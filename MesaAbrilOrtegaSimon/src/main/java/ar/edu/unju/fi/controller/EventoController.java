package ar.edu.unju.fi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.CompraService;
import ar.edu.unju.fi.service.EventoService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CompraService compraService;

    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.listarEventosDisponibles());
        return "eventos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Evento evento = new Evento();
        model.addAttribute("evento", evento);
        return "eventos/form";
    }

    @PostMapping("/nuevo")
    public String guardarEvento(@Valid @ModelAttribute("evento") Evento evento, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("evento", evento);
            return "eventos/form";
        }
        eventoService.guardarEvento(evento);
        return "redirect:/eventos";
    }

    @GetMapping("/detalle/{id}")
    public String mostrarEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        if (evento == null) {
            return "redirect:/eventos";
        }
        model.addAttribute("evento", evento);
        model.addAttribute("ticketsDisponibles", eventoService.obtenerTicketsDisponibles(id));
        model.addAttribute("compra", new Compra());
        return "eventos/detalle";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        model.addAttribute("evento", evento);
        return "eventos/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return "redirect:/eventos";
    }

    @PostMapping("/comprar/{eventoId}")
    public String comprarTickets(
            @PathVariable Long eventoId,
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("cantidadTickets") int cantidadTickets,
            Model model) {
        
        try {
            // Validar que el cliente exista
            Cliente cliente = clienteService.obtenerClientePorId(clienteId);
            if (cliente == null) {
                model.addAttribute("error", "Cliente no encontrado");
                return "redirect:/eventos/detalle/" + eventoId;
            }

            // Validar que el evento exista y tenga suficientes tickets disponibles
            Evento evento = eventoService.obtenerEventoPorId(eventoId);
            if (evento == null || eventoService.obtenerTicketsDisponibles(eventoId) < cantidadTickets) {
                model.addAttribute("error", "No hay suficientes tickets disponibles");
                return "redirect:/eventos/detalle/" + eventoId;
            }
            Compra compra = new Compra();
            compra.setCliente(cliente);
            compra.setEvento(evento);
            compra.setCantidadTickets(cantidadTickets);
            compra.setTotal(evento.getPrecio() * cantidadTickets);
            compra.setFechaCompra(LocalDateTime.now());
            
            compraService.realizarCompra(compra);
            
            return "redirect:/compra-exitosa";
        } catch (Exception e) {
            Evento evento = eventoService.obtenerEventoPorId(eventoId);
            model.addAttribute("evento", evento);
            model.addAttribute("ticketsDisponibles", eventoService.obtenerTicketsDisponibles(eventoId));
            model.addAttribute("compra", new Compra());
            model.addAttribute("error", e.getMessage());
            return "eventos/detalle";
        }
    }

    @GetMapping("/recaudacion")
    public String mostrarRecaudacion(Model model) {
        model.addAttribute("recaudacionPorEvento", eventoService.obtenerRecaudacionPorEvento());
        model.addAttribute("recaudacionPorTipo", eventoService.obtenerRecaudacionPorTipoEvento());
        return "eventos/recaudacion";
    }

    @GetMapping("/clientes-compras")
    public String mostrarClientesConCompras(Model model) {
        model.addAttribute("clientes", clienteService.listarClientesConCompras());
        return "clientes/compras";
    }

    @GetMapping("/compra-exitosa")
    public String mostrarCompraExitosa() {
        return "compra-exitosa";
    }
}
