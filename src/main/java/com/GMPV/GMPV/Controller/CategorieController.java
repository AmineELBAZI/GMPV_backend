package com.GMPV.GMPV.Controller;

import com.GMPV.GMPV.Entity.Categorie;
import com.GMPV.GMPV.Service.CategorieService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {
	    "http://localhost:3000",
	    "https://gmpv-frontend-nu.vercel.app"
	})
public class CategorieController {

    @Autowired
    private CategorieService categorieService;

    @GetMapping
    public List<Categorie> getAllCategories() {
        return categorieService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Categorie getCategorieById(@PathVariable Long id) {
        return categorieService.getCategorieById(id);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Categorie createCategorie(@RequestBody Categorie categorie) {
        return categorieService.createCategorie(categorie);
    }






    @PutMapping("/{id}")
    public Categorie updateCategorie(@PathVariable Long id, @RequestBody Categorie categorie) {
        return categorieService.updateCategorie(id, categorie);
    }

    @DeleteMapping("/{id}")
    public void deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
    }
}
