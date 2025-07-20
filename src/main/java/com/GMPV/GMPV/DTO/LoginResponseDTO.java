package com.GMPV.GMPV.DTO;

public class LoginResponseDTO {
    private Long id;
    private String username;
    private String role;
    private Long boutiqueId;

    public LoginResponseDTO(Long id, String username, String role, Long boutiqueId) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.boutiqueId = boutiqueId;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public Long getBoutiqueId() {
        return boutiqueId;
    }
}
