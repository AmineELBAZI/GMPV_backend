package com.GMPV.GMPV.Entity;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String reference;
    private String name;
    private double price_buy;
    private double price_sell;
    private double quantityStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    @JsonBackReference(value = "categorie-produit")
    private Categorie categorie;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    //@JsonManagedReference(value = "produit-stock")
    @JsonIgnore
    private List<Stock> stocks;



    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public double getPrice_buy() {
		return price_buy;
	}
	public void setPrice_buy(double price_buy) {
		this.price_buy = price_buy;
	}
	public double getPrice_sell() {
		return price_sell;
	}
	public void setPrice_sell(double price_sell) {
		this.price_sell = price_sell;
	}
	public String getName() { return name; }
    public void setName(String name) { this.name = name; }

  

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public List<Stock> getStocks() { return stocks; }
    
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }
    
	
	
	public double getQuantityStock() {
		return quantityStock;
	}
	public void setQuantityStock(double quantityStock) {
		this.quantityStock = quantityStock;
	}
	@PrePersist
	public void generateReference() {
	    if (this.reference == null || this.reference.isEmpty()) {
	        this.reference = "PROD" + String.format("%04d", System.currentTimeMillis() % 10000); // e.g., PROD8372
	    }
	}

}
