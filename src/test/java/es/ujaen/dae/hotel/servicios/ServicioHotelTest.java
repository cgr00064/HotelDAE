package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest(classes = es.ujaen.dae.hotel.HotelDaeApp.class)
public class ServicioHotelTest {

    @Autowired
    ServicioHotel servicioHotel;

    @Test
    public void testAccesoServicioHotel() {
        Assertions.assertThat(servicioHotel).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaClienteInvalido() {
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
                "mjmp0027gmail.com"
        );

        Assertions.assertThatThrownBy(() -> servicioHotel.altaCliente(cliente))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaHotel() throws Exception {
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Hotel hotel = new Hotel(
                2,
                "hotel",
                direccion,
                2,
                2
        );
        
        Administrador administrador = new Administrador("mjmp", "clave1");
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador);
        Assertions.assertThat(hotel1).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaYLoginCliente() throws Exception {
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
                "mjmp@0027.es"
        );

        Cliente cliente1 = servicioHotel.altaCliente(cliente);
        Cliente clienteLogin = servicioHotel.loginCliente(cliente.getUserName(), "manuel82")
                .orElseThrow(() -> new Exception("Cliente vacio"));

        Assertions.assertThat(clienteLogin).isNotNull();
        Assertions.assertThat(clienteLogin).isEqualTo(cliente1);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testBusarHoteles() throws Exception {
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Hotel hotel = new Hotel(
                2,
                "hotel",
                direccion,
                2,
                2
        );
        //Damos de alta el hotel
        Administrador administrador = new Administrador("mjmp", "clave1");
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador);

        Cliente cliente1 = new Cliente(
                "11111111A",
                "Juan",
                "juanito",
                "contraseña",
                direccion,
                "605092233",
                "juanito@gmail.com");
        servicioHotel.altaCliente(cliente1);
        Cliente cliente2 = new Cliente(
                "11111114A",
                "Carlos",
                "carlosgr99",
                "1234",
                direccion,
                "605092233",
                "cgr00064@gmail.com");
        servicioHotel.altaCliente(cliente2);

        LocalDateTime fechaInicioReserva1 = LocalDateTime.of(2023, 05, 18, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva1 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva1 = new Reserva(
                cliente1,
                direccion,
                fechaInicioReserva1,
                fechaFinReserva1,
                1,
                1);
        hotel1.addReserva(reserva1);

        LocalDateTime fechaInicioReserva2 = LocalDateTime.of(2023, 05, 20, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva2 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva2 = new Reserva(
                cliente2,
                direccion,
                fechaInicioReserva2,
                fechaFinReserva2,
                0,1
        );
        hotel1.addReserva(reserva2);

        List<Hotel> hotelesDisponibles = servicioHotel.buscarHoteles(
                "Jaen",
                LocalDate.of(2023, 5, 20),
                LocalDate.of(2023, 5, 21),
                2,
                1
        );
        Assertions.assertThat(hotelesDisponibles).isEmpty();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testHacerReserva() throws Exception {
        // Crear hotel con habitaciones disponibles
        Direccion direccionHotel = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Hotel hotel = new Hotel(
                2,
                "hotel",
                direccionHotel,
                2, // 2 habitaciones dobles disponibles
                2 // 2 habitaciones simples disponibles
        );
        //Damos de alta el hotel
        Administrador administrador = new Administrador("mjmp", "clave1");
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador);

        // Crear cliente y agregarlo al servicio
        Direccion direccionCliente = new Direccion(
                "España",
                "Malaga",
                "SanJuan",
                19);
        Cliente cliente1 = new Cliente(
                "12345678Q",
                "Manuel Jesus",
                "mjmp0027",
                "clave",
                direccionCliente,
                "657550655",
                "mjmp0027@ujaen.es"
        );
        servicioHotel.altaCliente(cliente1);
        Cliente cliente2 = new Cliente(
                "11111115A",
                "Carlos",
                "carlosgr",
                "1234",
                direccionCliente,
                "605092233",
                "cgr00064@gmail.com");
        servicioHotel.altaCliente(cliente2);
        LocalDateTime fechaInicioReserva1 = LocalDateTime.of(2023, 05, 18, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva1 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva1 = new Reserva(
                cliente1,
                direccionHotel,
                fechaInicioReserva1,
                fechaFinReserva1,
                1,
                1);
        hotel1.addReserva(reserva1);

        LocalDateTime fechaInicioReserva2 = LocalDateTime.of(2023, 05, 20, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva2 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva2 = new Reserva(
                cliente2,
                direccionHotel,
                fechaInicioReserva2,
                fechaFinReserva2,
                0,1
        );
        hotel1.addReserva(reserva2);

        // Realizar la prueba para cuando no hay habitaciones porque estan todas ocupadas
        boolean resultado = servicioHotel.hacerReserva(cliente1, hotel.getId(), LocalDate.of(2023, 5, 20), LocalDate.of(2023, 5, 21), 2, 1);
        // Verificar que la reserva no se realizó correctamente
        Assertions.assertThat(resultado).isFalse();

        // Realizar la prueba para cuando si hay disponibilidad y se puede realizar
        boolean resultado2 = servicioHotel.hacerReserva(cliente1, hotel.getId(), LocalDate.of(2023, 6, 20), LocalDate.of(2023, 6, 21), 2, 1);
        // Verificar que la reserva no se realizó correctamente
        Assertions.assertThat(resultado2).isTrue();
        //assertTrue(resultado2);
    }

}