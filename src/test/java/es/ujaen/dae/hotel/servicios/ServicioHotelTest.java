package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.*;
import es.ujaen.dae.hotel.excepciones.AdministradorYaExiste;
import es.ujaen.dae.hotel.excepciones.ClienteNoRegistrado;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest(classes = es.ujaen.dae.hotel.HotelDaeApp.class)
@ActiveProfiles(profiles = {"test"})
public class ServicioHotelTest {

    @Autowired
    ServicioHotel servicioHotel;

    //Accedemos al sistema
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

    //Damos de alta hotel y su dirección
    @Test
    public void testAltaHotel() throws AdministradorYaExiste {
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Hotel hotel = new Hotel(
                "hotel",
                direccion,
                20,
                30
        );

        //Creamos administrador
        Administrador administrador = new Administrador("mjmp", "clave1");
        Administrador administrador1 = servicioHotel.altaAdministrador(administrador);
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador1);
        Assertions.assertThat(hotel1).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testAltaYLoginCliente() {
        String clave = "manuel82";
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Cliente cliente = new Cliente(
                "21036345H",
                "Manuel Jesus",
                "mjmp0027",
                clave,
                direccion,
                "657550655",
                "mjmp@0027.es"
        );

        Cliente cliente1 = servicioHotel.altaCliente(cliente);

        Cliente clienteLogin = servicioHotel.loginCliente(cliente.getDni(), "manuel82")
                .orElseThrow(() -> new ClienteNoRegistrado());

        Assertions.assertThat(clienteLogin).isNotNull();
        Assertions.assertThat(clienteLogin.getDni()).isEqualTo(cliente1.getDni());
        Assertions.assertThat(clienteLogin.getNombre()).isEqualTo(cliente1.getNombre());
        Assertions.assertThat(clienteLogin.getUserName()).isEqualTo(cliente1.getUserName());
        Assertions.assertThat(clienteLogin.getContraseña()).isEqualTo(cliente1.getContraseña());
        Assertions.assertThat(clienteLogin.getDireccion()).isEqualTo(cliente1.getDireccion());

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testBuscarHoteles() throws AdministradorYaExiste {
        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);

        Hotel hotel = new Hotel(
                "hotel",
                direccion,
                2,
                2
        );

        // Damos de alta el hotel
        Administrador administrador = new Administrador("cgr0", "clave");
        Administrador administrador2 = servicioHotel.altaAdministrador(administrador);
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador2);

        Cliente cliente = new Cliente(
                "11111111A",
                "Juan",
                "juanito",
                "contraseña",
                direccion,
                "605092233",
                "juanito@gmail.com");
        Cliente cliente1 = servicioHotel.altaCliente(cliente);

        Cliente cliente2 = new Cliente(
                "11111114A",
                "Carlos",
                "carlosgr99",
                "1234",
                direccion,
                "605092233",
                "cgr00064@gmail.com");
        Cliente cliente3 = servicioHotel.altaCliente(cliente2);

        LocalDateTime fechaInicioReserva1 = LocalDateTime.of(2023, 05, 18, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva1 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva1 = new Reserva(
                cliente1,
                fechaInicioReserva1,
                fechaFinReserva1,
                1,
                1);
        // Guardar el hotel y la reserva
        servicioHotel.altaReserva(reserva1, hotel1);

        LocalDateTime fechaInicioReserva2 = LocalDateTime.of(2023, 05, 20, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva2 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva2 = new Reserva(
                cliente3,
                fechaInicioReserva2,
                fechaFinReserva2,
                0,
                1
        );
        servicioHotel.altaReserva(reserva2, hotel1);

        List<Reserva> reservasActuales = hotel1.getReservasActuales();
        Assertions.assertThat(reservasActuales).hasSize(2);

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
    public void testHacerReservaFalla() throws AdministradorYaExiste {
        // Crear hotel con habitaciones disponibles
        Direccion direccionHotel = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Hotel hotel = new Hotel(
                "hotel",
                direccionHotel,
                2,
                2
        );
        // Damos de alta el hotel
        Administrador administrador = new Administrador("cgr0", "clave");
        Administrador administrador2 = servicioHotel.altaAdministrador(administrador);
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador2);

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
                fechaInicioReserva1,
                fechaFinReserva1,
                1,
                1);
        servicioHotel.altaReserva(reserva1, hotel1);

        LocalDateTime fechaInicioReserva2 = LocalDateTime.of(2023, 05, 20, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva2 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva2 = new Reserva(
                cliente2,
                fechaInicioReserva2,
                fechaFinReserva2,
                0,
                1
        );
        servicioHotel.altaReserva(reserva2, hotel1);

        // Realizar la prueba para cuando no hay habitaciones porque están todas ocupadas
        boolean resultado = servicioHotel.hacerReserva(cliente1, hotel.getId(), LocalDate.of(2023, 5, 20), LocalDate.of(2023, 5, 21), 2, 1);
        // Verificar que la reserva no se realizó correctamente
        Assertions.assertThat(resultado).isFalse();

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testHacerReservaExito() throws AdministradorYaExiste {
        // Crear hotel con habitaciones disponibles
        Direccion direccionHotel = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);
        Hotel hotel = new Hotel(
                "hotel",
                direccionHotel,
                2,
                2
        );
        // Damos de alta el hotel
        Administrador administrador = new Administrador("cgr0", "clave");
        Administrador administrador2 = servicioHotel.altaAdministrador(administrador);
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador2);

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
                fechaInicioReserva1,
                fechaFinReserva1,
                1,
                1);
        servicioHotel.altaReserva(reserva1, hotel1);

        LocalDateTime fechaInicioReserva2 = LocalDateTime.of(2023, 05, 20, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva2 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva2 = new Reserva(
                cliente2,
                fechaInicioReserva2,
                fechaFinReserva2,
                0,
                1
        );
        servicioHotel.altaReserva(reserva2, hotel1);

        // Realizar la prueba para cuando no hay habitaciones porque están todas ocupadas
        boolean resultado = servicioHotel.hacerReserva(cliente1, hotel.getId(), LocalDate.of(2023, 8, 20), LocalDate.of(2023, 8, 21), 2, 1);
        // Verificar que la reserva no se realizó correctamente
        Assertions.assertThat(resultado).isTrue();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void moverReservasPasadasAHistorico() throws AdministradorYaExiste {

        Direccion direccion = new Direccion(
                "España",
                "Jaen",
                "SanJuan",
                19);

        Hotel hotel = new Hotel(
                "hotel",
                direccion,
                2,
                2
        );

        // Damos de alta el hotel
        Administrador administrador = new Administrador("cgr0", "clave");
        Administrador administrador2 = servicioHotel.altaAdministrador(administrador);
        Hotel hotel1 = servicioHotel.altaHotel(hotel, administrador2);

        Cliente cliente = new Cliente(
                "11111111A",
                "Juan",
                "juanito",
                "contraseña",
                direccion,
                "605092233",
                "juanito@gmail.com");
        Cliente cliente1 = servicioHotel.altaCliente(cliente);

        Cliente cliente2 = new Cliente(
                "11111114A",
                "Carlos",
                "carlosgr99",
                "1234",
                direccion,
                "605092233",
                "cgr00064@gmail.com");
        Cliente cliente3 = servicioHotel.altaCliente(cliente2);

        LocalDateTime fechaInicioReserva1 = LocalDateTime.of(2023, 05, 18, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva1 = LocalDateTime.of(2023, 05, 21, 00, 00, 00, 00);
        Reserva reserva1 = new Reserva(
                cliente1,
                fechaInicioReserva1,
                fechaFinReserva1,
                1,
                1);
        // Guardar el hotel y la reserva
        servicioHotel.altaReserva(reserva1, hotel1);

        LocalDateTime fechaInicioReserva2 = LocalDateTime.of(2024, 05, 20, 00, 00, 00, 00);
        LocalDateTime fechaFinReserva2 = LocalDateTime.of(2024, 05, 21, 00, 00, 00, 00);
        Reserva reserva2 = new Reserva(
                cliente3,
                fechaInicioReserva2,
                fechaFinReserva2,
                0,
                1
        );
        servicioHotel.altaReserva(reserva2, hotel1);

        List<Reserva> reservasActuales = hotel1.getReservasActuales();
        Assertions.assertThat(reservasActuales).hasSize(2);

        servicioHotel.moverReservasPasadasAHistorico();
        hotel1 = servicioHotel.buscarHotelPorId(hotel1.getId()).get();
        Hotel reservasPasadas =  servicioHotel.obtenerHotelConReservasPasadas(hotel1.getId());
        Assertions.assertThat(reservasPasadas.getReservasPasadas()).hasSize(1);
        // Verificar que las reservas estén en la lista de reservas cerradas del hotel
        Assertions.assertThat(hotel1.getReservasActuales()).hasSize(1);
    }


}


