package com.GMPV.GMPV.Repository;

import com.GMPV.GMPV.Entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    
}

