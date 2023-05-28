package es.ujaen.dae.hotel.entidades;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Reserva {

    int id;
    @NotNull
    private final Cliente cliente; // referencia al cliente que hace la reserva

    @NotNull
    private final Direccion direccion;

    @DateTimeFormat
    private final LocalDateTime fechaInicio;

    @DateTimeFormat
    private final LocalDateTime fechaFin;

    @PositiveOrZero
    private final int numHabitacionesSimp;

    @PositiveOrZero
    private final int numHabitacionesDobl;

    public boolean solapa(LocalDateTime dia) {
        //System.out.println("Salida: "+ ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                //(dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin))));
        return ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                (dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin)));


    }

}
