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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Evento.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String id) {
                if (id == null || id.isEmpty()) {
                    setValue(null);
                } else {
                    Evento evento = eventoService.obtenerEventoPorId(Long.parseLong(id));
                    setValue(evento);
                }
            }
        });
    }


    @Autowired
    private CompraService compraService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        model.addAttribute("view", "compras/lista");
        return "layout/nav";
    }

    @GetMapping("/nueva/{eventoId}")
    public String mostrarFormularioCompraPorEvento(@PathVariable Long eventoId, Model model) {
        Compra compra = new Compra();
        Evento evento = eventoService.obtenerEventoPorId(eventoId);
        compra.setEvento(evento);
        model.addAttribute("compra", compra);
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("evento", evento);
        model.addAttribute("view", "compras/form");
        return "compras/form";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioCompra(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("eventos", eventoService.listarEventos());
        // No hay un evento específico, pero para evitar errores en el formulario, agregamos un evento vacío o null
        model.addAttribute("evento", null);
        model.addAttribute("view", "compras/form");
        return "layout/nav";
    }

    @PostMapping("/guardar")
    public String guardarCompra(@Valid @ModelAttribute Compra compra, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("eventos", eventoService.listarEventos());
            model.addAttribute("view", "compras/form");
            return "layout/nav";
        }
        try {
            compraService.realizarCompra(compra);
            return "redirect:/compras";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("eventos", eventoService.listarEventos());
            model.addAttribute("view", "compras/form");
            return "layout/nav";
        }
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("compra", compraService.obtenerCompraPorId(id));
        model.addAttribute("view", "compras/detalle");
        return "layout/nav";
    }

    @GetMapping("/recaudacion")
    public String verRecaudacion(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        model.addAttribute("view", "compras/recaudacion");
        return "layout/nav";
    }
}
