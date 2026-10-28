package mx.edu.utez.libros.service.prestamo;

import mx.edu.utez.libros.exception.CustomExceptions.CustomBadRequestException;
import mx.edu.utez.libros.model.lector.Lector;
import mx.edu.utez.libros.model.libro.Libro;
import mx.edu.utez.libros.model.prestamo.Estado;
import mx.edu.utez.libros.model.prestamo.Prestamo;
import mx.edu.utez.libros.model.prestamo.dto.PrestamoRequestDTO;
import mx.edu.utez.libros.repository.lector.LectorRepository;
import mx.edu.utez.libros.repository.libro.LibroRepository;
import mx.edu.utez.libros.repository.prestamo.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LectorRepository lectorRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           LectorRepository lectorRepository,
                           LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.lectorRepository = lectorRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public Prestamo solicitarPrestamo(PrestamoRequestDTO dto) {
        Optional<Lector> lectorOptional = lectorRepository.findById(dto.getLectorId());
        if (!lectorOptional.isPresent()) {
            throw new CustomBadRequestException("El lector no existe");
        }
        Lector lector = lectorOptional.get();

        if (!lector.isActivo()) {
            throw new CustomBadRequestException("El lector se encuentra inactivo");
        }

        Optional<Libro> libroOptional = libroRepository.findById(dto.getLibroId());
        if (!libroOptional.isPresent()) {
            throw new CustomBadRequestException("El libro no existe");
        }
        Libro libro = libroOptional.get();

        if (libro.getEjemplaresDisponibles() < 1) {
            throw new CustomBadRequestException("No hay ejemplares disponibles de este libro");
        }

        int limitePrestamos = lector.isMembresiaPremium() ? 5 : 3;
        long prestamosActivos = prestamoRepository.countByLectorAndEstadoIn(
                lector, Arrays.asList(Estado.PRESTADO, Estado.VENCIDO)
        );

        if (prestamosActivos >= limitePrestamos) {
            throw new CustomBadRequestException("El lector ha alcanzado el límite máximo de préstamos activos (" + limitePrestamos + ")");
        }

        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = new Prestamo();
        prestamo.setLector(lector);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionMaxima(LocalDate.now().plusDays(14));
        prestamo.setEstado(Estado.PRESTADO);

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public Prestamo devolverLibro(Long prestamoId) {
        Optional<Prestamo> prestamoOptional = prestamoRepository.findById(prestamoId);
        if (!prestamoOptional.isPresent()) {
            throw new CustomBadRequestException("El préstamo no existe");
        }
        Prestamo prestamo = prestamoOptional.get();

        if (prestamo.getEstado() == Estado.DEVUELTO) {
            throw new CustomBadRequestException("El préstamo ya fue devuelto anteriormente");
        }

        prestamo.setEstado(Estado.DEVUELTO);

        Libro libro = prestamo.getLibro();
        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() + 1);
        libroRepository.save(libro);

        if (LocalDate.now().isAfter(prestamo.getFechaDevolucionMaxima())) {
            Lector lector = prestamo.getLector();
            lector.setActivo(false);
            lectorRepository.save(lector);
        }

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public Prestamo renovarPrestamo(Long prestamoId) {
        Optional<Prestamo> prestamoOptional = prestamoRepository.findById(prestamoId);
        if (!prestamoOptional.isPresent()) {
            throw new CustomBadRequestException("El préstamo no existe");
        }
        Prestamo prestamo = prestamoOptional.get();

        if (prestamo.getEstado() == Estado.VENCIDO || prestamo.getEstado() == Estado.DEVUELTO) {
            throw new CustomBadRequestException("No se puede renovar un préstamo en estado VENCIDO o DEVUELTO");
        }

        prestamo.setFechaDevolucionMaxima(prestamo.getFechaDevolucionMaxima().plusDays(7));
        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public Lector rehabilitarLector(Long lectorId) {
        Optional<Lector> lectorOptional = lectorRepository.findById(lectorId);
        if (!lectorOptional.isPresent()) {
            throw new CustomBadRequestException("El lector no existe");
        }
        Lector lector = lectorOptional.get();

        lector.setActivo(true);
        return lectorRepository.save(lector);
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPrestamosActivosLector(Long lectorId) {
        Optional<Lector> lectorOptional = lectorRepository.findById(lectorId);
        if (!lectorOptional.isPresent()) {
            throw new CustomBadRequestException("El lector no existe");
        }

        return prestamoRepository.findByLectorIdAndEstadoIn(
                lectorId, Arrays.asList(Estado.PRESTADO, Estado.VENCIDO)
        );
    }
}