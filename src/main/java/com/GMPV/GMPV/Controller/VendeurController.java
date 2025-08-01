package com.GMPV.GMPV.Controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {
	    "http://localhost:3000",
	    "https://gmpv-frontend-nu.vercel.app"
	})
@RequestMapping("/vendeur")
public class VendeurController {
    
    @GetMapping("/dashboard")
    public String vendeurDashboard(Principal principal) {
        return "Vendeur Dashboard for " + principal.getName();
    }
}
