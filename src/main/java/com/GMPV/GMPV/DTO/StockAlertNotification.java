package com.GMPV.GMPV.DTO;

public class StockAlertNotification {
    private String produitName;
    private String reference;
    private String boutiqueName;

    public StockAlertNotification(String produitName, String reference, String boutiqueName) {
        this.produitName = produitName;
        this.reference = reference;
        this.boutiqueName = boutiqueName;
    }

    // Getters
    public String getProduitName() { return produitName; }
    public String getReference() { return reference; }
    public String getBoutiqueName() { return boutiqueName; }

    @Override
    public String toString() {
        return "StockAlertNotification{" +
                "produitName='" + produitName + '\'' +
                ", reference='" + reference + '\'' +
                ", boutiqueName='" + boutiqueName + '\'' +
                '}';
    }
}
