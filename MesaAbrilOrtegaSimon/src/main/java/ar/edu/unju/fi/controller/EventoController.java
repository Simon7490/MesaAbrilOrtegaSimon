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
        model.addAttribute("view", "eventos/lista");
        return "layout/nav";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("view", "eventos/form");
        return "layout/nav";
    }

    @PostMapping("/nuevo")
    public String guardarNuevoEvento(@Valid @ModelAttribute("evento") Evento evento, BindingResult result, Model model) {
        if (result.hasErrors()) {
        System.out.println("Errores de binding: " + result.getAllErrors());
            model.addAttribute("view", "eventos/form");
            return "layout/nav";
        }
        eventoService.guardarEvento(evento);
        return "redirect:/eventos";
    }

    @GetMapping("/evento/{id}")
public String mostrarEvento(@PathVariable Long id, Model model) {
    Evento evento = eventoService.obtenerEventoPorId(id);
    model.addAttribute("evento", evento);
    model.addAttribute("ticketsDisponibles", eventoService.obtenerTicketsDisponibles(id));
    model.addAttribute("compra", new Compra());
    model.addAttribute("view", "eventos/detalle");
    return "layout/nav";
}

@GetMapping("/editar/{id}")
public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    Evento evento = eventoService.obtenerEventoPorId(id);
    model.addAttribute("evento", evento);
    model.addAttribute("view", "eventos/form");
    return "layout/nav";
}

@PostMapping("/eliminar/{id}")
public String eliminarEvento(@PathVariable Long id) {
    eventoService.eliminarEvento(id);
    return "redirect:/eventos";
}


    @PostMapping("/guardar")
    public String guardarEvento(@Valid @ModelAttribute Evento evento, BindingResult result, Model model) {
        if (result.hasErrors()) {
        System.out.println("Errores de binding: " + result.getAllErrors());
            model.addAttribute("view", "eventos/form");
            return "layout/nav";
        }
        eventoService.guardarEvento(evento);
        return "redirect:/eventos";
    }

    @PostMapping("/comprar/{eventoId}")
    public String comprarTickets(
            @PathVariable Long eventoId,
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("cantidadTickets") int cantidadTickets,
            Model model) {
        
        try {
            Cliente cliente = clienteService.obtenerClientePorId(clienteId);
            Evento evento = eventoService.obtenerEventoPorId(eventoId);
            
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
            model.addAttribute("view", "eventos/detalle");
            return "layout/nav";
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
