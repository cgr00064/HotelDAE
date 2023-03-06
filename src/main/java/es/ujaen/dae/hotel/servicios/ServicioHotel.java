package es.ujaen.dae.hotel.servicios;

import es.ujaen.dae.hotel.entidades.*;
import es.ujaen.dae.hotel.excepciones.ClienteNoRegistrado;
import es.ujaen.dae.hotel.excepciones.ClienteYaRegistrado;
import es.ujaen.dae.hotel.excepciones.HotelYaExiste;
import es.ujaen.dae.hotel.excepciones.ReservaNoDisponible;
import lombok.extern.slf4j.Slf4j;
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
    Map<Integer, Cliente> clientes;
    Map<Integer, Hotel> hoteles;
    //Mapa de administradores para la comprobacion en altaHotel
    Map<String, Administrador> administradores;
    private int numClientes;
    private int numHoteles;

    @PostConstruct
    private void init() {
        clientes = new TreeMap<>();
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
        if (clientes.containsKey(cliente.getId())) {
            throw new ClienteYaRegistrado();
        } else {
            cliente.setId(numClientes++);
            clientes.put(cliente.getId(), cliente);
            log.info("Cliente con datos: " + cliente + " registrado");
            return cliente;
        }
    }

    public Hotel altaHotel(@NotNull @Valid Hotel hotel, @Valid @NotNull Administrador administrador) throws Exception {
        for (Map.Entry<String, Administrador> administradores : administradores.entrySet()) {
            if (administradores.getValue().getUserName().equals(administrador.getUserName())
                    && administradores.getValue().getContraseña().equals(administrador.getContraseña())) {
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
        for (Map.Entry<Integer, Cliente> clientes : clientes.entrySet()) {
            if (clientes.getValue().getUserName().equals(userName) && clientes.getValue().claveValida(clave))
                cliente = Optional.of(clientes.getValue());
        }
        return cliente;
//        return Optional
//                .ofNullable(clientes.get(userName))
//                .filter((cliente)
//                        -> cliente.claveValida(clave));
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
    
    boolean hacerReserva(@NotNull @Valid Cliente cliente, Direccion direccion, LocalDateTime fechaIni, LocalDateTime fechaFin, int numDoble, int numSimple) {

        List<Hotel> listaHoteles = buscarHoteles(direccion, fechaIni, fechaFin);
        //Suponemos que se queda con el primer hotel que hay en esa direccion
        if(clientes.containsKey(cliente.getId()))
        if (listaHoteles.get(0).getNumDobl() >= numDoble && listaHoteles.get(0).getNumSimp() >= numSimple) {
            Reserva reserva = new Reserva(direccion, fechaIni, fechaFin, numSimple, numDoble);
            cliente.addReserva(reserva);
            listaHoteles.get(0).setNumSimp(numSimple);
            listaHoteles.get(0).setNumDobl(numDoble);
            return true;
        }
        return false;
    }
}