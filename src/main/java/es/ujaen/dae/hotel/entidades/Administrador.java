package es.ujaen.dae.hotel.entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Administrador {

    @NotBlank
    @Id
    private String userName;

    @NotBlank
    private String contraseña;
}