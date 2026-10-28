package mx.edu.utez.libros.repository.prestamo;

import mx.edu.utez.libros.model.lector.Lector;
import mx.edu.utez.libros.model.prestamo.Estado;
import mx.edu.utez.libros.model.prestamo.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    long countByLectorAndEstadoIn(Lector lector, List<Estado> estados);
    List<Prestamo> findByLectorIdAndEstadoIn(Long lectorId, List<Estado> estados);
}