package es.ujaen.dae.hotel.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

public class ReservaTest {
    public ReservaTest() {

    }

    @Test
    void TestValidacionReserva() {
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        LocalDateTime fechaInicio = LocalDateTime.of(2022, 10, 10, 10, 10, 10, 10);
        LocalDateTime fechaFin = LocalDateTime.of(2022, 11, 11, 11, 11, 11, 11);
        Reserva reserva = new Reserva(
                direccion,
                fechaInicio,
                fechaFin,
                1,
                2);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Reserva>> violations = validator.validate(reserva);

        Assertions.assertThat(violations).isEmpty();
    }
}