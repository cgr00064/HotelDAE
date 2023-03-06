package es.ujaen.dae.hotel.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ClienteTest {
    public ClienteTest(){
    }

    @Test
    void testValidacionCliente(){
        String clave = "manuel82";
        Direccion direccion = new Direccion(
               "España",
               "Jaen",
               "SanJuan",
               19);

        Cliente cliente = new Cliente(
                "12345678Q",
                "Manuel Jesus",
                "mjmp0027",
                clave,
                direccion,
                "657550655",
                "mjmp0027@ujaen.es"
        );
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Cliente>> violations = validator.validate(cliente);

        Assertions.assertThat(violations).isEmpty();

    }
    @Test
    void testComprobacionClave(){
        String clave = "manuel82";
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);

        Cliente cliente = new Cliente(
                "12345678Q",
                "Manuel Jesus",
                "mjmp0027",
                clave,
                direccion,
                "123456789",
                "mjmp0027@ujaen.es"
        );
        Assertions.assertThat(cliente.claveValida("manuel82")).isTrue();
    }
}