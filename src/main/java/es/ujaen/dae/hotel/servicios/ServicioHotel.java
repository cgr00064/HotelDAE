package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.Administrador;
import es.ujaen.dae.hotel.entidades.Cliente;
import es.ujaen.dae.hotel.entidades.Hotel;
import es.ujaen.dae.hotel.entidades.Reserva;
import es.ujaen.dae.hotel.excepciones.*;
import es.ujaen.dae.hotel.repositorios.RepositorioAdministrador;
import es.ujaen.dae.hotel.repositorios.RepositorioCliente;
import es.ujaen.dae.hotel.repositorios.RepositorioHotel;
import es.ujaen.dae.hotel.repositorios.RepositorioReserva;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        repositorioReserva.guardarReserva(reserva);

        // Comprobar que el hotel existe
        Optional<Hotel> hotelExistente = repositorioHotel.buscarHotelPorId(hotel.getId());
        if (hotelExistente.isEmpty()) {
            throw new HotelNoExiste();
        }

        hotelExistente.get().addReserva(reserva);
        repositorioHotel.actualizarHotel(hotelExistente.get());
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

    @Scheduled(cron = "0 0 3 * * *") // se ejecutará todos los días a las 3:00
    @Transactional
    public void moverReservasPasadasAHistorico() {
        List<Hotel> hoteles = repositorioHotel.buscarTodosLosHoteles();
        for (Hotel hotel : hoteles) {
            hotel.moverReservasPasadasAHistorico();
            repositorioHotel.actualizarHotel(hotel);
        }
    }
}
