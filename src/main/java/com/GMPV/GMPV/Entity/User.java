package com.GMPV.GMPV.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "boutique_id")
    private Boutique boutique; // only used for Vendeur

    // Constructors
    public User() {}

    public User(String username, String password, Role role, Boutique boutique) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.boutique = boutique;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }

    public void setRole(Role role) { this.role = role; }

    public Boutique getBoutique() { return boutique; }

    public void setBoutique(Boutique boutique) { this.boutique = boutique; }
}
