package com.darquran.application.service.impl;

import com.darquran.application.dto.auth.login.LoginRequest;
import com.darquran.application.dto.auth.login.LoginResponse;
import com.darquran.application.dto.auth.register.RegisterRequest;
import com.darquran.application.dto.auth.register.RegisterResponse;
import com.darquran.application.dto.auth.resetPassword.ForgotPasswordRequest;
import com.darquran.application.dto.auth.resetPassword.ResetPasswordRequest;
import com.darquran.application.service.AuthenticationService;
import com.darquran.domain.exception.InvalidOTPException;
import com.darquran.domain.exception.UserNotFoundException;
import com.darquran.domain.model.entities.users.Student;
import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.entities.users.redis.RefreshToken;
import com.darquran.domain.model.enums.Role;
import com.darquran.domain.model.valueobjects.Adresse;
import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.UserRepository;
import com.darquran.infrastructure.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final com.darquran.application.service.RefreshTokenService refreshTokenService;
    private final com.darquran.application.service.BlacklistService blacklistService;
    private final com.darquran.application.service.EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final com.darquran.application.service.WhatsAppService whatsAppService;

    @Override
    public RegisterResponse register(RegisterRequest request) {


        var passwordVO = Password.create(request.getPassword(), passwordEncoder);

        User user = Student.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordVO)
                .role(Role.PUBLIC)
                .section(request.getSection())
                .telephone(request.getTelephone())
                .adresse(new Adresse("Rue Default", "Ville", "10000", "Maroc")) // Adresse par defaut
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return RegisterResponse.builder()
                .token(jwtToken)
                .build();
    }


    /*============== FONCTION LOGIN ==============*/

    @Override
    public LoginResponse authenticate(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("aucun utilisateur avec l 'email " + " " + request.getEmail()));

        String jwtToken = jwtService.generateToken(user);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .id(user.getId())
                .token(jwtToken)
                .refreshToken(refreshToken.getToken())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole().toString())
                .email(user.getEmail())
                .build();
    }

    /*========== REFRESH TOKEN ========*/

    @Override
    public LoginResponse refreshToken(String requestRefreshToken) {
        RefreshToken token = refreshTokenService.findByToken(requestRefreshToken);
        User user = userRepository.findByEmail(token.getUsername()).orElseThrow();

        if (!user.isEnabled()) throw new RuntimeException("User disabled");

        refreshTokenService.deleteByToken(requestRefreshToken);
        RefreshToken newToken = refreshTokenService.createRefreshToken(user.getEmail());

        String newJwtToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .id(user.getId())
                .token(newJwtToken)
                .refreshToken(newToken.getToken())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole().toString())
                .email(user.getEmail())
                .build();
    }

    /*========== LOGOUT ========*/

    @Override
    public void logout(String authHeader, String refreshToken) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            Date expiration = jwtService.extractExpiration(accessToken);
            long timeRemaining = expiration.getTime() - System.currentTimeMillis();

            if (timeRemaining > 0) {
                blacklistService.blackListToken(accessToken, timeRemaining);
            }
        }


        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }
    }


    /*============== FORGET PASSWORD ==============*/

    @Override
    public void requestPasswordReset(ForgotPasswordRequest request) {
        String contact = request.contact() != null ? request.contact().trim() : "";

        if (contact.contains("@")) {
            userRepository.findByEmail(contact)
                    .orElseThrow(() -> new UserNotFoundException(contact, true));
        } else {
            userRepository.findByTelephone(contact)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec ce numéro"));
        }

        String otp = String.valueOf(new java.util.Random().nextInt(900000) + 100000);

        redisTemplate.opsForValue().set("password_reset:" + contact, otp, 15, TimeUnit.MINUTES);

        if (contact.contains("@")) {
            emailService.sendEmail(contact, "Code de Réinitialisation", "Votre code est : " + otp);
        } else {
            whatsAppService.sendOTP(contact, otp);
        }
    }


    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String contact = request.contact().trim();
        String savedOtp = redisTemplate.opsForValue().get("password_reset:" + contact);

        if (savedOtp == null || !savedOtp.equals(request.otp())) {
            throw new InvalidOTPException("OTP invalide ou expiré");
        }

        User user = contact.contains("@")
                ? userRepository.findByEmail(contact).orElseThrow(() -> new UserNotFoundException(contact, true))
                : userRepository.findByTelephone(contact).orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec ce numéro"));

        user.setPassword(Password.create(request.newPassword(), passwordEncoder));
        userRepository.save(user);

        redisTemplate.delete("password_reset:" + contact);
    }
}

