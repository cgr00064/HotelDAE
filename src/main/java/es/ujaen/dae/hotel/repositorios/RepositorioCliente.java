package es.ujaen.dae.hotel.repositorios;

import es.ujaen.dae.hotel.entidades.Cliente;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioCliente {
    @PersistenceContext
    EntityManager em;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Cliente> buscarPorDNI(String dni) {
        return Optional.ofNullable(em.find(Cliente.class, dni));
    }

    public void guardarCliente(Cliente cliente) {
        em.persist(cliente);
    }

    public void actualizarCliente(Cliente cliente) {
        em.merge(cliente);
    }

}