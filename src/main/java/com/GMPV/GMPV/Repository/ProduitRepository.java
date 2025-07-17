package com.GMPV.GMPV.Repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GMPV.GMPV.Entity.Produit;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
	
// find Produit By id de Categorie 
	List<Produit> findByCategorieId(Long categorieId);
	
 // Ajout de cette méthode pour obtenir le dernier produit
    Optional<Produit> findTopByOrderByIdDesc();
    
 // ✅ Produits dont la quantité en stock est > 0
    List<Produit> findByQuantityStockGreaterThan(Double quantityStock);

}
