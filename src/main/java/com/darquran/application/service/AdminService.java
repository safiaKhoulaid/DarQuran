package com.darquran.application.service;

import com.darquran.application.dto.users.admin.AdminRequest;
import com.darquran.application.dto.users.admin.AdminResponse;
import com.darquran.domain.model.enums.Section;

import java.util.List;

/**
 * Service de gestion des administrateurs de section.
 */
public interface AdminService {

    AdminResponse create(AdminRequest request);

    AdminResponse getById(String id);

    /**
     * Récupère tous les admins, éventuellement filtrés par section.
     *
     * @param section section à filtrer (optionnelle, peut être null)
     */
    List<AdminResponse> getAllBySection(Section section);

    AdminResponse update(String id, AdminRequest request);

    void delete(String id);
}

