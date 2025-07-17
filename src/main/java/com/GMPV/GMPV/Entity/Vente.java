package com.GMPV.GMPV.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateVente;

    private Double montantTotal;
    
    private double quantity;

    @ManyToOne
    @JoinColumn(name = "boutique_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Boutique boutique;


    @ManyToMany
    @JoinTable(
        name = "vente_produit",
        joinColumns = @JoinColumn(name = "vente_id"),
        inverseJoinColumns = @JoinColumn(name = "produit_id")
    )
    private List<Produit> produits;

    // Constructors
    public Vente() {}

    public Vente(LocalDateTime dateVente, Double montantTotal, Boutique boutique, List<Produit> produits) {
        this.dateVente = dateVente;
        this.montantTotal = montantTotal;
        this.boutique = boutique;
        this.produits = produits;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Boutique getBoutique() {
        return boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }
    
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }
}