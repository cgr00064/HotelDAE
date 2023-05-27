package es.ujaen.dae.hotel.entidades;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
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

    /*
     verifica si la reserva actual se solapa con otra reserva recibida como parámetro.
     Si se solapa, retorna true, de lo contrario, retorna false.
     */
    /*public boolean solapa(Reserva otraReserva) {
        return (fechaInicio.isBefore(otraReserva.getFechaFin())
                && otraReserva.getFechaInicio().isBefore(fechaFin));
    }
    public boolean solapaEnDia(LocalDateTime dia) {

        System.out.println("fecha: "+ fechaInicio +" dia.plusDays(1): "+dia.plusDays(1)+ " dia: "+ dia);

        return (dia.isBefore(fechaInicio) && dia.isBefore(fechaInicio) || dia.isAfter(fechaFin) && dia.isAfter(fechaFin));

    }*/
    /*public boolean solapa(LocalDateTime dia) {
        System.out.println("dia: "+ dia +" fechaInicio: "+fechaInicio+ " fechaFin: "+ fechaFin);
        System.out.println("Salida: "+ ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                (dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin))));
        String mes =(dia.getMonthValue() > 9) ? ""+dia.getMonthValue():"0"+dia.getMonthValue();
        String fecha = ""+dia.getYear()+ mes + dia.getDayOfMonth();

        System.out.println(Integer.parseInt(fecha));

        return ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                (dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin)));


    }*/

    public boolean solapa(LocalDateTime dia) {

        System.out.println("Salida: "+ ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                (dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin))));

        return ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                (dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin)));


    }

}
