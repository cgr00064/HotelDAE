package es.ujaen.dae.hotel.entidades;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Direccion {
    private String pais;
    private String ciudad;
    private String calle;
    private int num;
}