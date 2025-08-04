package com.GMPV.GMPV.DTO;

public class StockAlertNotification {
    private String produitName;
    private String reference;
    private String boutiqueName;
    private double quantity; // new field

    public StockAlertNotification(String produitName, String reference, String boutiqueName, double quantity) {
        this.produitName = produitName;
        this.reference = reference;
        this.boutiqueName = boutiqueName;
        this.quantity = quantity;
    }

    public String getProduitName() { return produitName; }
    public String getReference() { return reference; }
    public String getBoutiqueName() { return boutiqueName; }
    public double getQuantity() { return quantity; } // getter

    @Override
    public String toString() {
        return "StockAlertNotification{" +
                "produitName='" + produitName + '\'' +
                ", reference='" + reference + '\'' +
                ", boutiqueName='" + boutiqueName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
