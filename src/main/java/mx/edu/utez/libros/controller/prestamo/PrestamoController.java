package mx.edu.utez.libros.controller.prestamo;

import jakarta.validation.Valid;
import mx.edu.utez.libros.model.lector.Lector;
import mx.edu.utez.libros.model.prestamo.Prestamo;
import mx.edu.utez.libros.model.prestamo.dto.PrestamoRequestDTO;
import mx.edu.utez.libros.service.prestamo.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping("/solicitar")
    public ResponseEntity<Prestamo> solicitarPrestamo(@Valid @RequestBody PrestamoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.solicitarPrestamo(dto));
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<Prestamo> devolverLibro(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.devolverLibro(id));
    }

    @PutMapping("/{id}/renovar")
    public ResponseEntity<Prestamo> renovarPrestamo(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.renovarPrestamo(id));
    }

    @PutMapping("/lectores/{id}/rehabilitar")
    public ResponseEntity<Lector> rehabilitarLector(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.rehabilitarLector(id));
    }

    @GetMapping("/lectores/{id}/activos")
    public ResponseEntity<List<Prestamo>> listarPrestamosActivosLector(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.listarPrestamosActivosLector(id));
    }
}