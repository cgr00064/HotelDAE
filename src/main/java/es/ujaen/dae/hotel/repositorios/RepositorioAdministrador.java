package es.ujaen.dae.hotel.repositorios;

import es.ujaen.dae.hotel.entidades.Administrador;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioAdministrador {

    @PersistenceContext
    EntityManager em;

    public void guardarAdministrador(Administrador administrador){
        em.persist(administrador);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Administrador> buscarAdminPorUserName(String userName){
        return Optional.ofNullable(em.find(Administrador.class, userName));
    }
}