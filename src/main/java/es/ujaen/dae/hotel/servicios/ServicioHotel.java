package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.*;
import es.ujaen.dae.hotel.excepciones.ClienteNoRegistrado;
import es.ujaen.dae.hotel.excepciones.ClienteYaRegistrado;
import es.ujaen.dae.hotel.excepciones.HotelYaExiste;
import es.ujaen.dae.hotel.excepciones.ReservaNoDisponible;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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
    
    public List<Hotel> buscarHoteles(Direccion direccion, LocalDateTime fechaIni, LocalDateTime fechaFin) {
        List<Hotel> listaHoteles = new ArrayList<>();
        for (Map.Entry<Integer, Hotel> hoteles : hoteles.entrySet()) {
            for (int i = 0; i < hoteles.getValue().getReservasActuales().size(); i++) {
                if (hoteles.getValue().getDireccion() == direccion) {
                    if (fechaIni.isBefore(hoteles.getValue().getReservasActuales().get(i).getFechaInicio())
                            && fechaFin.isBefore(hoteles.getValue().getReservasActuales().get(i).getFechaInicio())
                            || fechaIni.isAfter(hoteles.getValue().getReservasActuales().get(i).getFechaFin())) {
                        listaHoteles.add(hoteles.getValue());
                    } else {
                        throw new ReservaNoDisponible();
                    }
                } else {
                    throw new ReservaNoDisponible();
                }
            }
        }
        return listaHoteles;
    }

    boolean hacerReserva(@NotNull @Valid Cliente cliente, Direccion direccion, LocalDateTime fechaIni, LocalDateTime fechaFin, int numDoble, int numSimple, Hotel hotel) {
        if (clientes.containsKey(cliente.getDni())) {
            if (hotel.getNumDobl() >= numDoble && hotel.getNumSimp() >= numSimple) {
                Reserva reserva = new Reserva(cliente, direccion, fechaIni, fechaFin, numSimple, numDoble);
                cliente.addReserva(reserva);
                hotel.setNumSimp(numSimple);
                hotel.setNumDobl(numDoble);
                return true;
            }
        }
        return false;
    }

    @Scheduled(cron = "0 0 3 * * *") // se ejecutará todos los días a las 3:00
    public void moverReservasPasadasAHistorico() {
        for (Hotel hotel : hoteles.values()) {
            List<Reserva> reservasActuales = hotel.getReservasActuales();
            List<Reserva> reservasPasadas = hotel.getReservasPasadas();

            // Mover las reservas pasadas a la lista de reservas históricas
            Iterator<Reserva> iterator = reservasActuales.iterator();
            while (iterator.hasNext()) {
                Reserva reserva = iterator.next();
                if (reserva.getFechaFin().isBefore(LocalDate.now().atStartOfDay())) {
                    iterator.remove();
                    hotel.setNumSimp(reserva.getNumHabitacionesSimp());
                    hotel.setNumDobl(reserva.getNumHabitacionesDobl());
                    reservasPasadas.add(reserva);
                }
            }
        }
    }
}