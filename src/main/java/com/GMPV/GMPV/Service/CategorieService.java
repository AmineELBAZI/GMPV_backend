package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.Entity.Categorie;
import com.GMPV.GMPV.Repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id).orElse(null);
    }

    public Categorie createCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public Categorie updateCategorie(Long id, Categorie updatedCategorie) {
        Optional<Categorie> existing = categorieRepository.findById(id);
        if (existing.isPresent()) {
            Categorie categorie = existing.get();
            categorie.setNom(updatedCategorie.getNom());
            return categorieRepository.save(categorie);
        }
        return null;
    }

    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}
