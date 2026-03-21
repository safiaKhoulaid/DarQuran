package com.darquran.infrastructure.config.security;

import com.darquran.domain.model.valueobjects.Password;
import com.darquran.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true) // Hsan nkhliwha ReadOnly bach tkon performante
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        // Hna khassna n-castiw UserDetails l User Entity bach nqdro nsta3mlo setters
        var user = repository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword( new Password(newPassword));
        return repository.save(user);
    }
}