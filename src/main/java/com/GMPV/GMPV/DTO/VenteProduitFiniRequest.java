package com.GMPV.GMPV.DTO;

public class VenteProduitFiniRequest {
    private Long boutiqueId;
    private Long bouteilleId;
    private Long huileId;
    private Long alcoolId;
    private Double montantTotal;
    private String taille;
    
    public Long getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(Long boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public Long getBouteilleId() {
        return bouteilleId;
    }

    public void setBouteilleId(Long bouteilleId) {
        this.bouteilleId = bouteilleId;
    }

    public Long getHuileId() {
        return huileId;
    }

    public void setHuileId(Long huileId) {
        this.huileId = huileId;
    }

    public Long getAlcoolId() {
        return alcoolId;
    }

    public void setAlcoolId(Long alcoolId) {
        this.alcoolId = alcoolId;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getTaille() {
        return taille;
    }

    public void setTailleBouteille(String taille) {
        this.taille = taille;
    }
}
