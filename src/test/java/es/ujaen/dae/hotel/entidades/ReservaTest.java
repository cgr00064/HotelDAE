package es.ujaen.dae.hotel.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

public class ReservaTest {
    public ReservaTest() {

    }
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        Direccion direccion = new Direccion("España", "Jaen", "SanJuan", 19);
        Cliente cliente = new Cliente("11111111A", "Juan", "juanito", "contraseña", direccion, "123456789", "juanito@gmail.com");
        LocalDateTime fechaInicio = LocalDateTime.of(2023, 10, 1, 0, 0, 0, 0);
        LocalDateTime fechaFin = LocalDateTime.of(2023, 12, 1, 0, 0, 0, 0);
        reserva = new Reserva(cliente, fechaInicio, fechaFin, 1, 2);
    }

    @Test
    void testValidacionReserva() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Reserva>> violations = validator.validate(reserva);

        Assertions.assertThat(violations).isEmpty();
    }

    @Test
    void testSolapa() {
        LocalDateTime dia1 = LocalDateTime.of(2023, 10, 1, 0, 0, 0, 0);
        LocalDateTime dia2 = LocalDateTime.of(2023, 11, 1, 0, 0, 0, 0);
        LocalDateTime dia3 = LocalDateTime.of(2023, 12, 2, 10, 0, 0, 0);
        LocalDateTime dia4 = LocalDateTime.of(2023, 9, 30, 0, 0, 0, 0);

        boolean solapa1 = reserva.solapa(dia1);
        boolean solapa2 = reserva.solapa(dia2);
        boolean solapa3 = reserva.solapa(dia3);
        boolean solapa4 = reserva.solapa(dia4);

        Assertions.assertThat(solapa1).isTrue();
        Assertions.assertThat(solapa2).isTrue();
        Assertions.assertThat(solapa3).isFalse();
        Assertions.assertThat(solapa4).isFalse();
    }

}