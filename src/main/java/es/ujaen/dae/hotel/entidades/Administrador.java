package es.ujaen.dae.hotel.entidades;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Administrador {

    @NotBlank
    private String userName;

    @NotBlank
    private String contraseña;
}