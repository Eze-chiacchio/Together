package com.chiacchio.together.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    @GetMapping("/viajes")
    public String mostrarViajes() {
        return "viajes";
    }
    @GetMapping("/viajes/{id}")
    public String verDetalleViaje() {
        return "viaje-detalle";
    }
    @GetMapping("/crear-viaje")
    public String crearViaje() {
        return "crear-viaje";
    }

    @GetMapping("/solicitudes-recibidas")
    public String solicitudesRecibidas() {
        return "solicitudes-recibidas";
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes() {
        return "mis-solicitudes";
    }
}
