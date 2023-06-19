package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.*;
import es.ujaen.dae.hotel.excepciones.*;
import es.ujaen.dae.hotel.repositorios.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Validated
public class ServicioHotel {
    @Autowired
    RepositorioAdministrador repositorioAdministrador;
    @Autowired
    RepositorioCliente repositorioCliente;
    @Autowired
    RepositorioHotel repositorioHotel;
    @Autowired
    RepositorioReserva repositorioReserva;

    @Autowired
    RepositorioReservaCerradas repositorioReservaCerradas;

    //Damos de alta el cliente en el sistema
    public Cliente altaCliente(@NotNull @Valid Cliente cliente) throws ClienteNoRegistrado {
        if (repositorioCliente.buscarPorDNI(cliente.getDni()).isPresent()) {
            throw new ClienteYaRegistrado();
        } else {
            repositorioCliente.guardarCliente(cliente);
            return cliente;
        }
    }

    //Damos de alta el hotel en el sistema
    public Hotel altaHotel(@NotNull @Valid Hotel hotel, @Valid @NotNull Administrador administrador) throws AdministradorNoValido {
        Optional<Administrador> adminOptional = repositorioAdministrador.buscarAdminPorUserName(administrador.getUserName());
        if (adminOptional.isPresent()) {
            Administrador admin = adminOptional.get();
            if (admin.getContraseña().equals(administrador.getContraseña())) {
                Optional<Hotel> hotelExistente = repositorioHotel.buscarHotelPorId(hotel.getId());
                if (hotelExistente.isPresent()) {
                    throw new HotelYaExiste();
                } else {
                    repositorioHotel.guardarHotel(hotel);
                    return hotel;
                }
            }
        }
        throw new AdministradorNoValido();
    }

    //Damos de alta el administrador en el sistema
    public Administrador altaAdministrador(@NotNull @Valid Administrador administrador) throws AdministradorYaExiste {
        if (repositorioAdministrador.buscarAdminPorUserName(administrador.getUserName()).isPresent()) {
            throw new AdministradorYaExiste();
        } else {
            repositorioAdministrador.guardarAdministrador(administrador);
            return administrador;
        }
    }

    public void altaReserva(Reserva reserva, Hotel hotel) throws HotelNoExiste {
            //repositorioReserva.guardarReserva(reserva);
            hotel.addReserva(reserva);
            repositorioHotel.actualizarHotel(hotel);
    }


    //Hacemos el login del cliente
    public Optional<Cliente> loginCliente(@NotNull String dni, @NotNull String clave) {
        Optional<Cliente> clienteLogin = repositorioCliente.buscarPorDNI(dni)
                .filter((cliente) -> cliente.claveValida(clave));
        return clienteLogin;
    }

    public List<Hotel> buscarHoteles(String ciudad, LocalDate fechaIni, LocalDate fechaFin, int numHabitacionesSimp, int numHabitacionesDobl) {
        List<Hotel> hoteles = new ArrayList<>();
        List<Hotel> listaHoteles = repositorioHotel.buscarHotelesPorCiudad(ciudad);
        for (Hotel hotel : listaHoteles) {
            if (hotel.getDireccion().getCiudad().equals(ciudad) && hotel.hayDisponibilidad(fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numHabitacionesSimp, numHabitacionesDobl)) {
                hoteles.add(hotel);
            }
        }
        return hoteles;
    }

    public Optional<Hotel> buscarHotelPorId(int hotelId){
        return repositorioHotel.buscarHotelPorId(hotelId);
    }

    boolean hacerReserva(@NotNull @Valid Cliente cliente, int codigoHotel, LocalDate fechaIni, LocalDate fechaFin, int numDoble, int numSimple) {
        Optional<Cliente> optionalCliente = repositorioCliente.buscarPorDNI(cliente.getDni());
        Optional<Hotel> optionalHotel = repositorioHotel.buscarHotelPorId(codigoHotel);

        if (optionalCliente.isPresent() && optionalHotel.isPresent()) {
            Cliente clienteEncontrado = optionalCliente.get();
            Hotel hotel = optionalHotel.get();

            if (hotel.hayDisponibilidad(fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numSimple, numDoble)) {
                Reserva reserva = new Reserva(clienteEncontrado, fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numSimple, numDoble);
                hotel.addReserva(reserva);

                repositorioCliente.actualizarCliente(clienteEncontrado);

                return true; // la reserva se hizo correctamente
            }
        }

        return false; // no hay disponibilidad en el hotel o el cliente o el hotel no existen
    }

    //Ejercicio voluntario 1
    @Scheduled(cron = "0 0 3 * * *") // se ejecutará todos los días a las 3:00
    @Transactional
    public void moverReservasPasadasAHistorico() {

        List<Hotel> hoteles = repositorioHotel.buscarTodosLosHoteles();
        for (Hotel hotel : hoteles) {
            hotel.moverReservasPasadasAHistorico();
            repositorioHotel.actualizarHotel(hotel);
        }
    }

    //Ejercicio voluntario 2
    public void realizarTrasvaseReservasCerradas(int hotelId) {
        Optional<Hotel> hotelOptional = repositorioHotel.buscarHotelPorId(hotelId);
        if (hotelOptional.isPresent()) {
            Hotel hotel = hotelOptional.get();

            List<Reserva> reservasActuales = hotel.getReservasActuales() != null ? hotel.getReservasActuales() : new ArrayList<>();
            Set<Reserva> reservasPasadas = hotel.getReservasPasadas() != null ? hotel.getReservasPasadas() : new HashSet<>();
            Set<ReservaCerrada> reservasCerradas = new HashSet<>();

            List<Reserva> copiaReservasActuales = new ArrayList<>(reservasActuales);

            // Mover las reservas pasadas a la lista de reservas históricas y cerradas
            for (Reserva reserva : copiaReservasActuales) {
                if (reserva.getFechaFin() != null && reserva.getFechaFin().isBefore(LocalDateTime.now())) {
                    log.info("Reservas:" + reserva.toString());
                    Optional<Reserva> reservaOptional = repositorioReserva.buscarReservaPorClienteFechas(reserva.getCliente().getDni(), reserva.getFechaInicio(), reserva.getFechaFin());
                    if (reservaOptional.isPresent()) {
                        repositorioReserva.eliminarReserva(reserva.getId());  // Eliminar primero de la base de datos

                        reservasActuales.remove(reserva);
                        hotel.setNumSimp(reserva.getNumHabitacionesSimp());
                        hotel.setNumDobl(reserva.getNumHabitacionesDobl());
                        reservasPasadas.add(reserva);

                        ReservaCerrada reservaCerrada = new ReservaCerrada(reserva);
                        repositorioReservaCerradas.guardarReservaCerrada(reservaCerrada);
                        reservasCerradas.add(reservaCerrada);
                        hotel.setReservasCerradas(reservasCerradas);
                        hotel.setReservasActuales(reservasActuales);
                        repositorioHotel.actualizarHotel(hotel);
                    }
                }
            }
            //log.info("Reservas Actuales:" + reservasActuales);
            //log.info("Reservas Actuales Hotel:" + hotel.getReservasActuales());
            //log.info("Reservas Cerradas:" + reservasCerradas);
            //log.info("Reservas Cerradas Hotel:" + hotel.getReservasCerradas());
        } else {
            throw new HotelNoExiste();
        }
    }


}
