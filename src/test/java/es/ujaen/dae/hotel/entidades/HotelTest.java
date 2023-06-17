package es.ujaen.dae.hotel.entidades;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class HotelTest {
    public HotelTest(){
    }

    private Hotel hotel;
    @BeforeEach
    void setUp() {
        Direccion direccion = new Direccion("España", "Jaen", "SanJuan", 19);
        hotel = new Hotel("hotel", direccion, 20, 30);
    }

    @Test
    void testValidacionHotel(){

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);

        Assertions.assertThat(violations).isEmpty();
    }

    @Test
    void testAddReserva() {
        Reserva reserva = new Reserva(); // Crea una instancia de Reserva adecuada para el test

        hotel.addReserva(reserva);

        List<Reserva> reservasActuales = hotel.getReservasActuales();
        Assertions.assertThat(1).isEqualTo(reservasActuales.size());
        Assertions.assertThat(reserva).isEqualTo(reservasActuales.get(0));
        Assertions.assertThat(1).isEqualTo(hotel.getTotalReservasActuales());
    }

    @Test
    void testMoverReservasPasadasAHistorico() {
        // Crea algunas reservas para que haya reservas pasadas
        Reserva reservaPasada1 = new Reserva();
        reservaPasada1.setFechaFin(LocalDateTime.now().minusDays(1));
        hotel.addReserva(reservaPasada1);

        Reserva reservaPasada2 = new Reserva();
        reservaPasada2.setFechaFin(LocalDateTime.now().minusDays(2));
        hotel.addReserva(reservaPasada2);

        hotel.moverReservasPasadasAHistorico();

        List<Reserva> reservasActuales = hotel.getReservasActuales();
        Set<Reserva> reservasPasadas = hotel.getReservasPasadas();

        Assertions.assertThat(0).isEqualTo(reservasActuales.size());
        Assertions.assertThat(2).isEqualTo(reservasPasadas.size());
        Assertions.assertThat(reservasPasadas.contains(reservaPasada1)).isTrue();
        Assertions.assertThat(reservasPasadas.contains(reservaPasada2)).isTrue();
    }

    @Test
    void testHayDisponibilidad() {
        // Crea algunas reservas para ocupar algunas habitaciones
        Reserva reserva1 = new Reserva();
        reserva1.setFechaInicio(LocalDateTime.now().minusDays(2));
        reserva1.setFechaFin(LocalDateTime.now().minusDays(1));
        reserva1.setNumHabitacionesDobl(5);
        reserva1.setNumHabitacionesSimp(5);
        hotel.addReserva(reserva1);

        Reserva reserva2 = new Reserva();
        reserva2.setFechaInicio(LocalDateTime.now().plusDays(1));
        reserva2.setFechaFin(LocalDateTime.now().plusDays(2));
        reserva2.setNumHabitacionesDobl(10);
        reserva2.setNumHabitacionesSimp(10);
        hotel.addReserva(reserva2);

        // Comprueba la disponibilidad para diferentes fechas y cantidades de habitaciones
        boolean disponibilidad1 = hotel.hayDisponibilidad(
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(5),
                3,
                3
        );
        Assertions.assertThat(disponibilidad1).isTrue();

        boolean disponibilidad2 = hotel.hayDisponibilidad(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                10,
                10
        );
        Assertions.assertThat(disponibilidad2).isTrue();

        boolean disponibilidad3 = hotel.hayDisponibilidad(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                10,
                10
        );
        Assertions.assertThat(disponibilidad3).isTrue();
    }
}