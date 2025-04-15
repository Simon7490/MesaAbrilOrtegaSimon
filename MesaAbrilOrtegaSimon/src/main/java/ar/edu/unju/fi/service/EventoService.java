package ar.edu.unju.fi.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.model.TipoEvento;
import ar.edu.unju.fi.repository.EventoRepository;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Lista todos los eventos disponibles ordenados por fecha
     */
    public List<Evento> listarEventos() {
        return eventoRepository.findByFechaAfterOrderByFechaAsc(LocalDateTime.now());
    }

    /**
     * Lista todos los eventos con tickets disponibles
     */
    public List<Evento> listarEventosDisponibles() {
        return eventoRepository.findEventosDisponibles(LocalDateTime.now());
    }

    /**
     * Lista eventos por tipo
     */
    public List<Evento> listarEventosPorTipo(TipoEvento tipoEvento) {
        return eventoRepository.findByTipoEventoAndFechaAfterOrderByFechaAsc(tipoEvento, LocalDateTime.now());
    }

    /**
     * Guarda un nuevo evento o actualiza uno existente
     */
    @Transactional
    public Evento guardarEvento(Evento evento) {
        if (evento.getCapacidadInicial() == null) {
            evento.setCapacidadInicial(evento.getCapacidad());
        }
        if (evento.getId() != null) {
            // Si es una actualización, validar que no tenga compras si se reduce la capacidad
            Evento eventoExistente = obtenerEventoPorId(evento.getId());
            if (evento.getCapacidad() < eventoExistente.getCapacidad()) {
                int ticketsVendidos = eventoRepository.getTicketsVendidos(evento.getId());
                if (evento.getCapacidad() < ticketsVendidos) {
                    throw new RuntimeException("No se puede reducir la capacidad por debajo de los tickets vendidos");
                }
            }
        }
        return eventoRepository.save(evento);
    }

    /**
     * Obtiene un evento por su ID
     */
    public Evento obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }

    /**
     * Elimina un evento verificando que no tenga compras
     */
    @Transactional
    public void eliminarEvento(Long id) {
        Evento evento = obtenerEventoPorId(id);
        if (!evento.getCompras().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un evento con compras realizadas");
        }
        eventoRepository.delete(evento);
    }

    /**
     * Obtiene la cantidad de tickets disponibles para un evento
     */
    public int obtenerTicketsDisponibles(Long eventoId) {
        Evento evento = obtenerEventoPorId(eventoId);
        int ticketsVendidos = eventoRepository.getTicketsVendidos(eventoId);
        return evento.getCapacidad() - ticketsVendidos;
    }

    /**
     * Obtiene la recaudación total por evento
     */
    public Map<String, Double> obtenerRecaudacionPorEvento() {
        List<Object[]> resultados = eventoRepository.getRecaudacionPorEvento();
        Map<String, Double> recaudacion = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String nombreEvento = (String) resultado[0];
            Double total = (Double) resultado[1];
            recaudacion.put(nombreEvento, total);
        }
        
        return recaudacion;
    }

    /**
     * Obtiene la recaudación total por tipo de evento
     */
    public Map<TipoEvento, Double> obtenerRecaudacionPorTipoEvento() {
        List<Object[]> resultados = eventoRepository.getRecaudacionPorTipoEvento();
        Map<TipoEvento, Double> recaudacion = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            TipoEvento tipo = (TipoEvento) resultado[0];
            Double total = (Double) resultado[1];
            recaudacion.put(tipo, total);
        }
        
        return recaudacion;
    }

    /**
     * Busca eventos por nombre
     */
    public List<Evento> buscarEventosPorNombre(String nombre) {
        return eventoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
