package com.djcode.fitness.fitnessapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Serves the single-page application index.html for non-API routes so that
 * client-side routing (React Router) works when the user refreshes or deep-links.
 */
@Controller
public class SpaController {

    @RequestMapping({"/", "/login", "/register", "/dashboard"})
    public String forwardToIndex() {
        // Forward (not redirect) so /index.html static resource is served
        return "forward:/index.html";
    }
}

