package com.GMPV.GMPV.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double quantity;
    
    @Enumerated(EnumType.STRING)
    private StockStatus status = StockStatus.NOT_VALIDATED;

    @ManyToOne
    @JoinColumn(name = "boutique_id")
    @JsonBackReference(value = "boutique-stock")
    private Boutique boutique;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties("stocks")
    //@JsonBackReference(value = "produit-stock")
    private Produit produit;




    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    
    public StockStatus getStatus() { return status; }
    public void setStatus(StockStatus status) { this.status = status; }
    
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public Boutique getBoutique() { return boutique; }
    public void setBoutique(Boutique boutique) { this.boutique = boutique; }
}
