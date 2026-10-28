package mx.edu.utez.libros.model.prestamo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoRequestDTO {
        @NotNull(message = "El id del lector es obligatorio")
        private Long lectorId;

        @NotNull(message = "El id del libro es obligatorio")
        private Long libroId;
}
