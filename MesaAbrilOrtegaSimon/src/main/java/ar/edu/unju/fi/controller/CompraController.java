package ar.edu.unju.fi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.service.CompraService;
import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.EventoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/compras")
public class CompraController {



    @Autowired
    private CompraService compraService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        return "compras/lista";
    }

    @GetMapping("/nueva/{eventoId}")
    public String mostrarFormularioCompraPorEvento(@PathVariable Long eventoId, Model model) {
        Compra compra = new Compra();
        Evento evento = eventoService.obtenerEventoPorId(eventoId);
        if (evento != null) {
            compra.setEvento(evento);
            model.addAttribute("evento", evento);
        }
        model.addAttribute("compra", compra);
        model.addAttribute("clientes", clienteService.listarClientes());
        return "compras/form";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCompra(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("eventos", eventoService.listarEventos());
        return "compras/form";
    }

    @PostMapping("/guardar")
    public String guardarCompra(@Valid @ModelAttribute("compra") Compra compra, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("eventos", eventoService.listarEventos());
            return "compras/form";
        }

        // Validar que el cliente exista
        if (compra.getCliente() == null || compra.getCliente().getId() == null) {
            model.addAttribute("error", "Debe seleccionar un cliente");
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("eventos", eventoService.listarEventos());
            return "compras/form";
        }

        // Validar que el evento exista y tenga suficientes tickets disponibles
        if (compra.getEvento() == null || compra.getEvento().getId() == null) {
            model.addAttribute("error", "Debe seleccionar un evento");
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("eventos", eventoService.listarEventos());
            return "compras/form";
        }

        Evento evento = eventoService.obtenerEventoPorId(compra.getEvento().getId());
        if (evento == null || eventoService.obtenerTicketsDisponibles(evento.getId()) < compra.getCantidadTickets()) {
            model.addAttribute("error", "No hay suficientes tickets disponibles");
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("eventos", eventoService.listarEventos());
            return "compras/form";
        }

        compra.setEvento(evento);
        compraService.realizarCompra(compra);
        return "redirect:/compras";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Compra compra = compraService.obtenerCompraPorId(id);
        if (compra == null) {
            return "redirect:/compras";
        }
        model.addAttribute("compra", compra);
        return "compras/detalle";
    }

    @GetMapping("/recaudacion")
    public String verRecaudacion(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        return "compras/recaudacion";
    }
}
