package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.*;
import es.ujaen.dae.hotel.excepciones.ClienteNoRegistrado;
import es.ujaen.dae.hotel.excepciones.ClienteYaRegistrado;
import es.ujaen.dae.hotel.excepciones.HotelYaExiste;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Validated
public class ServicioHotel {
    Map<String, Cliente> clientes;
    Map<Integer, Hotel> hoteles;
    //Mapa de administradores para la comprobacion en altaHotel
    Map<String, Administrador> administradores;
    private int numClientes;
    private int numHoteles;

    @PostConstruct
    private void init() {
        clientes = new TreeMap<String, Cliente>();
        hoteles = new TreeMap<>();
        administradores = new TreeMap<>();
        numClientes = 0;
        numHoteles = 0;
        //Solo vamos a ser tres administradores
        Administrador manuel = new Administrador("mjmp", "clave1");
        Administrador carlos = new Administrador("cgr", "clave2");
        Administrador maria = new Administrador("mhm", "clave3");
        administradores.put(manuel.getUserName(), manuel);
        administradores.put(carlos.getUserName(), carlos);
        administradores.put(maria.getUserName(), maria);
    }

    public Cliente altaCliente(@NotNull @Valid Cliente cliente) throws ClienteNoRegistrado {
        log.info("Cliente con datos: " + cliente + " registrandose");
        if (clientes.containsKey(cliente.getDni())) {
            throw new ClienteYaRegistrado();
        } else {
            clientes.put(cliente.getDni(), cliente);
            log.info("Cliente con datos: " + cliente + " registrado");
            return cliente;
        }
    }

    public Hotel altaHotel(@NotNull @Valid Hotel hotel, @Valid @NotNull Administrador administrador) throws Exception {
        if (administradores.containsKey(administrador.getUserName())) {
            Administrador admin = administradores.get(administrador.getUserName());
            if (admin.getContraseña().equals(administrador.getContraseña())) {
                log.info("Hotel con datos: " + hotel + " registrandose");
                if (hoteles.containsKey(hotel.getId())) {
                    throw new HotelYaExiste();
                } else {
                    hotel.setId(numHoteles++);
                    hoteles.put(hotel.getId(), hotel);
                    log.info("Hotel con datos: " + hotel + " registrado");
                    return hotel;
                }
            }
        }
        throw new Exception("Administrador no valido");
    }

    public Optional<Cliente> loginCliente(@NotNull String userName, @NotNull String clave) {
        Optional<Cliente> cliente = Optional.empty();
        for (Map.Entry<String, Cliente> clientes : clientes.entrySet()) {
            if (clientes.getValue().getUserName().equals(userName) && clientes.getValue().claveValida(clave))
                cliente = Optional.of(clientes.getValue());
        }
        return cliente;
    }

    /*
    Este método itera sobre todos los hoteles y comprueba si la dirección coincide con la dirección dada y
    si hay habitaciones disponibles en el hotel. Luego, verifica si hay alguna reserva en el hotel que se
    solape con las fechas dadas. Si no hay solapamiento, agrega el hotel a la lista de hoteles disponibles.

    public List<Hotel> buscarHoteles(Direccion direccion, LocalDateTime fechaIni, LocalDateTime fechaFin) {
        List<Hotel> listaHoteles = new ArrayList<>();
        for (Map.Entry<Integer, Hotel> hoteles : hoteles.entrySet()) {
            Hotel hotel = hoteles.getValue();
            if (hotel.getDireccion().equals(direccion) && hotel.numHabitacionesSimp() > 0 && hotel.numDoblDisponibles() > 0) {
            /*
            se realiza una búsqueda en las reservas históricas del hotel para verificar si hay alguna reserva existente
            que se superponga con las fechas de la reserva nueva.
                boolean reservaDisponible = hotel.getReservasActuales()
                        .stream()
                        .noneMatch(r -> r.solapa(new Reserva(null, direccion, fechaIni, fechaFin, 0, 0)));
                if (reservaDisponible) {
                    listaHoteles.add(hotel);
                }
            }
        }
        return listaHoteles;
    }
    */

    public List<Hotel> buscarHoteles(Direccion direccion, LocalDateTime fechaIni, LocalDateTime fechaFin, int numHabitacionesSimp, int numHabitacionesDobl) {
        List<Hotel> listaHoteles = new ArrayList<>();
        for (Map.Entry<Integer, Hotel> hoteles : hoteles.entrySet()) {
            Hotel hotel = hoteles.getValue();
            if (hotel.getDireccion().equals(direccion) && hotel.numHabitacionesSimp() > 0 && hotel.numDoblDisponibles() > 0) {
                if (hotel.hayDisponibilidad(fechaIni, fechaFin, numHabitacionesSimp, numHabitacionesDobl)) {
                    listaHoteles.add(hotel);
                }
            }
        }
        return listaHoteles;
    }

    boolean hacerReserva(@NotNull @Valid Cliente cliente, Direccion direccion, LocalDateTime fechaIni, LocalDateTime fechaFin, int numDoble, int numSimple, Hotel hotel) {
        if (!clientes.containsKey(cliente.getDni())) {
            return false; // el cliente no está registrado
        }
        if (hotel.hayDisponibilidad(fechaIni, fechaFin, numSimple, numDoble)) {
            Reserva reserva = new Reserva(cliente, direccion, fechaIni, fechaFin, numSimple, numDoble);
            cliente.addReserva(reserva);
            hotel.addReserva(reserva);
            return true; // la reserva se hizo correctamente
        }
        return false; // no hay disponibilidad en el hotel
    }


    @Scheduled(cron = "0 0 3 * * *") // se ejecutará todos los días a las 3:00
    public void moverReservasPasadasAHistorico() {
        for (Hotel hotel : hoteles.values()) {
            hotel.moverReservasPasadasAHistorico();
        }
    }
}