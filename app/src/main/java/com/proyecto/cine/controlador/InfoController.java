package com.proyecto.cine.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Devuelve el nombre del contenedor/pod y del nodo que atendio la peticion.
 * Sirve para demostrar en el video que Kubernetes reparte las peticiones entre replicas.
 */
@RestController
public class InfoController {

    @GetMapping("/api/info")
    public Map<String, Object> info() throws Exception {
        return Map.of(
                "aplicacion", "cine-api",
                "atendidoPor", InetAddress.getLocalHost().getHostName(),
                "nodo", System.getenv().getOrDefault("NODO", "no-definido"),
                "hora", LocalDateTime.now().toString());
    }
}
