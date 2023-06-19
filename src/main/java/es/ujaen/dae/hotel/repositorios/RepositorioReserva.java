package es.ujaen.dae.hotel.repositorios;


import es.ujaen.dae.hotel.entidades.Reserva;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioReserva {
    @PersistenceContext
    EntityManager em;

    public void guardarReserva(Reserva reserva) {
        em.persist(reserva);
    }

    @Transactional
    public void eliminarReserva(int reservaId) {
        Reserva reserva = em.find(Reserva.class, reservaId);
        if (reserva != null) {
            em.remove(reserva);
        }
    }

    public List<Reserva> buscarReservasAbiertasAnteriores(LocalDateTime fecha) {
        Query query = em.createQuery("SELECT r FROM Reserva r WHERE r.fechaFin < :fecha");
        query.setParameter("fecha", fecha);
        List<Reserva> reservasAbiertas = query.getResultList();

        for (Reserva reserva : reservasAbiertas) {
            em.remove(reserva);
        }

        return reservasAbiertas;
    }

    public Optional<Reserva> buscarReservaPorClienteFechas(String clienteDNI, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Query query = em.createQuery("SELECT r FROM Reserva r WHERE r.cliente.dni = :clienteDNI AND r.fechaInicio = :fechaInicio AND r.fechaFin = :fechaFin");
        query.setParameter("clienteDNI", clienteDNI);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);

        List<Reserva> reservas = query.getResultList();

        return Optional.ofNullable(reservas.get(0)); // Devuelve la primera reserva encontrada

    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Reserva> buscarReservaPorId(int id){
        return Optional.ofNullable(em.find(Reserva.class, id));
    }
    public void actualizarReserva(Reserva reserva){
        em.merge(reserva);
    }
}