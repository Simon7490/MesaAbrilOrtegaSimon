package ar.edu.unju.fi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.service.ICompraService;
import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.EventoService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/compras")
public class CompraController {



    @Autowired
    private ICompraService compraService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String listarCompras(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        return "compras/lista";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("_csrf", new Object() {
            public String getParameterName() { return "_csrf"; }
            public String getToken() { return ""; }
        });
    }

    @GetMapping("/nuevo/{eventoId}")
    public String mostrarFormularioCompraPorEvento(@PathVariable Long eventoId, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(eventoId);
        if (evento == null) {
            model.addAttribute("error", "El evento no existe");
            return "redirect:/eventos";
        }
        
        if (evento.getFecha().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "El evento ya ha finalizado");
            return "redirect:/eventos";
        }

        Compra compra = new Compra();
        compra.setEvento(evento);

        model.addAttribute("compra", compra);
        model.addAttribute("evento", evento);
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("ticketsDisponibles", compraService.obtenerTicketsDisponibles(evento.getId()));

        return "compras/form";
    }

    @GetMapping("/nueva/{eventoId}")
    public String nuevaCompraDesdeEvento(@PathVariable Long eventoId, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(eventoId);
        if (evento == null) {
            return "redirect:/eventos?error=Evento no encontrado";
        }
        
        Compra compra = new Compra();
        compra.setEventoId(eventoId);
        model.addAttribute("compra", compra);
        model.addAttribute("eventos", eventoService.listarEventos());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "compras/form";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("eventos", eventoService.listarEventos());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "compras/form";
    }

    @PostMapping("/guardar")
    public String guardarCompra(@Valid @ModelAttribute Compra compra, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("eventos", eventoService.listarEventos());
            model.addAttribute("clientes", clienteService.listarClientes());
            return "compras/form";
        }

        try {
            if (compra.getId() != null) {
                compraService.actualizarCompra(compra);
            } else {
                compraService.guardar(compra);
            }
            redirectAttributes.addFlashAttribute("success", "Compra guardada exitosamente.");
        } catch (RuntimeException e) {
            model.addAttribute("eventos", eventoService.listarEventos());
            model.addAttribute("clientes", clienteService.listarClientes());
            model.addAttribute("error", e.getMessage());
            return "compras/form";
        }

        return "redirect:/compras";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCompra(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            compraService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Compra eliminada con éxito.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la compra: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al eliminar la compra.");
        }
        return "redirect:/compras";
    }

    @GetMapping("/compra/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Compra compra = compraService.obtenerCompraPorId(id);
        if (compra == null) {
            return "redirect:/compras";
        }
        model.addAttribute("compra", compra);
        return "compras/detalle";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Compra compra = compraService.obtenerCompraPorId(id);
        if (compra == null) {
            return "redirect:/compras";
        }
        model.addAttribute("compra", compra);
        model.addAttribute("eventos", eventoService.listarEventos());
        model.addAttribute("clientes", clienteService.listarClientes());
        return "compras/form";
    }

    @GetMapping("/recaudacion")
    public String verRecaudacion(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        return "compras/recaudacion";
    }
}
