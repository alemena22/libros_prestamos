package mx.edu.utez.libros.repository.lector;

import mx.edu.utez.libros.model.lector.Lector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectorRepository extends JpaRepository<Lector, Long> {
}