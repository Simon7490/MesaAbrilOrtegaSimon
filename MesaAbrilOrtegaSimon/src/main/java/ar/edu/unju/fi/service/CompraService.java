package ar.edu.unju.fi.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.repository.CompraRepository;
import ar.edu.unju.fi.repository.EventoRepository;

@Service
public class CompraService implements ICompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Lista todas las compras realizadas
     */
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    /**
     * Realiza una nueva compra validando disponibilidad y calculando el total
     */
    @Transactional
    public Compra guardar(Compra compra) {
        Evento evento = compra.getEvento();
        if (evento == null || evento.getId() == null) {
            throw new RuntimeException("Evento no válido");
        }
        
        if (evento.getFecha().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El evento ya ha finalizado");
        }
        
        if (evento.getCapacidad() < compra.getCantidadTickets()) {
            throw new RuntimeException("No hay suficientes tickets disponibles");
        }
        
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(evento.getPrecio() * compra.getCantidadTickets());
        compraRepository.save(compra);
        
        // Actualizar la capacidad del evento
        evento.setCapacidad(evento.getCapacidad() - compra.getCantidadTickets());
        eventoRepository.save(evento);

        return compra;
    }

    /**
     * Actualiza una compra existente validando disponibilidad y calculando el total
     */
    @Transactional
    public void actualizarCompra(Compra compra) {
        Evento evento = compra.getEvento();
        if (evento == null || evento.getId() == null) {
            throw new RuntimeException("Evento no válido");
        }
        
        if (evento.getFecha().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El evento ya ha finalizado");
        }
        
        // Obtener la compra existente para calcular la diferencia de tickets
        Compra compraExistente = compraRepository.findById(compra.getId())
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        // Calcular la diferencia de tickets
        int diferenciaTickets = compra.getCantidadTickets() - compraExistente.getCantidadTickets();

        // Validar capacidad del evento
        if (evento.getCapacidad() + compraExistente.getCantidadTickets() < compra.getCantidadTickets()) {
            throw new RuntimeException("No hay suficientes tickets disponibles");
        }

        // Actualizar la compra
        compra.setFechaCompra(LocalDateTime.now());
        compra.setTotal(evento.getPrecio() * compra.getCantidadTickets());
        compraRepository.save(compra);

        // Actualizar la capacidad del evento
        evento.setCapacidad(evento.getCapacidad() + diferenciaTickets);
        eventoRepository.save(evento);
    }

    /**
     * Obtiene una compra por su ID
     */
    public Compra obtenerCompraPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }

    /**
     * Calcula la recaudación total por evento
     */
    public Map<String, Double> obtenerRecaudacionPorEvento() {
        List<Object[]> resultados = compraRepository.findRecaudacionPorEvento();
        Map<String, Double> recaudacion = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String nombreEvento = (String) resultado[0];
            Double total = (Double) resultado[1];
            
            // Verificar si el evento ya ha finalizado
            List<Evento> eventos = eventoRepository.findByNombreContainingIgnoreCase(nombreEvento);
            if (eventos.isEmpty()) {
                throw new RuntimeException("Evento no encontrado: " + nombreEvento);
            }
            
            Evento evento = eventos.get(0);
            if (evento.getFecha().isAfter(LocalDateTime.now())) {
                recaudacion.put(nombreEvento, total);
            }
        }
        
        return recaudacion;
    }

    /**
     * Obtiene las compras de un cliente específico
     */
    public List<Compra> obtenerComprasPorCliente(Cliente cliente) {
        return compraRepository.findByClienteOrderByFechaCompraDesc(cliente);
    }

    /**
     * Obtiene las compras de un evento específico
     */
    public List<Compra> obtenerComprasPorEvento(Evento evento) {
        return compraRepository.findByEventoOrderByFechaCompraDesc(evento);
    }











    /**
     * Elimina una compra existente
     */
    @Transactional
    public void eliminar(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
        
        Evento evento = compra.getEvento();
        // Devolver los tickets a la capacidad del evento
        evento.setCapacidad(evento.getCapacidad() + compra.getCantidadTickets());
        eventoRepository.save(evento);
        
        compraRepository.delete(compra);
    }

    /**
     * Verifica la disponibilidad de tickets para un evento
     */
    public int obtenerTicketsDisponibles(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        
        int ticketsVendidos = compraRepository.countTicketsVendidosPorEvento(eventoId);
        return evento.getCapacidad() - ticketsVendidos;
    }
}
