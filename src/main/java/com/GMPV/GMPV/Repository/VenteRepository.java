package com.GMPV.GMPV.Repository;


import com.GMPV.GMPV.Entity.Vente;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    List<Vente> findByBoutiqueId(Long boutiqueId);
    boolean existsByBoutiqueId(Long boutiqueId);
    
}