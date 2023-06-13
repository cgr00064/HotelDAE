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
import java.time.LocalDate;
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

    public List<Hotel> buscarHoteles(String ciudad, LocalDate fechaIni, LocalDate fechaFin, int numHabitacionesSimp, int numHabitacionesDobl) {
        List<Hotel> listaHoteles = new ArrayList<>();
        for (Map.Entry<Integer, Hotel> hoteles : hoteles.entrySet()) {

            Hotel hotel = hoteles.getValue();
            // A la hora de buscar lo que mas sentido tiene es por ciudad
            if (hotel.getDireccion().getCiudad().equals(ciudad) && hotel.hayDisponibilidad(fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numHabitacionesSimp, numHabitacionesDobl)) {
                listaHoteles.add(hotel);
            }
        }
        return listaHoteles;
    }

    /*boolean hacerReserva(@NotNull @Valid Cliente cliente, Direccion direccion, LocalDate fechaIni, LocalDate fechaFin, int numDoble, int numSimple) {
        if (!clientes.containsKey(cliente.getDni())) {
            return false; // el cliente no está registrado
        }
        List<Hotel> listaHoteles = new ArrayList<>();
        for (Map.Entry<Integer, Hotel> hoteles : hoteles.entrySet()) {
            Hotel hotel = hoteles.getValue();
            // Sin embargo, cuando reservas lo que mas sentido tiene es por direccion.
            if (hotel.getDireccion().equals(direccion) && hotel.hayDisponibilidad(fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numSimple, numDoble)) {
                Reserva reserva = new Reserva(cliente, direccion, fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numSimple, numDoble);
                cliente.addReserva(reserva);
                hotel.addReserva(reserva);
                return true; // la reserva se hizo correctamente
            }
        }
        return false; // no hay disponibilidad en el hotel
    }
    */

    boolean hacerReserva(@NotNull @Valid Cliente cliente, int codigoHotel, LocalDate fechaIni, LocalDate fechaFin, int numDoble, int numSimple) {
        if (!clientes.containsKey(cliente.getDni())) {
            return false; // el cliente no está registrado
        }
        Hotel hotel = hoteles.get(codigoHotel);
        //System.out.println("Id Hotel: " + hoteles.get(codigoHotel));
        if (hotel != null && hotel.hayDisponibilidad(fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numSimple, numDoble)) {
            Reserva reserva = new Reserva(cliente, hotel.getDireccion(), fechaIni.atStartOfDay(), fechaFin.atStartOfDay(), numSimple, numDoble);
            cliente.addReserva(reserva);
            hotel.addReserva(reserva);
            return true; // la reserva se hizo correctamente
        }
        return false; // no hay disponibilidad en el hotel o el hotel no existe
    }

    @Scheduled(cron = "0 0 3 * * *") // se ejecutará todos los días a las 3:00
    public void moverReservasPasadasAHistorico() {
        for (Hotel hotel : hoteles.values()) {
            hotel.moverReservasPasadasAHistorico();
        }
    }
}