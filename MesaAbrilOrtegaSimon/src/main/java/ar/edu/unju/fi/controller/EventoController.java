package ar.edu.unju.fi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.exception.EventoNoEncontradoException;
import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.CompraService;
import ar.edu.unju.fi.service.EventoService;

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
    public String listarEventos(Model model, RedirectAttributes redirectAttributes) {
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
        
        try {
            eventoService.guardarEvento(evento);
            return "redirect:/eventos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar el evento: " + e.getMessage());
            return "eventos/form";
        }
    }

    @GetMapping("/evento/detalle/{id}")
    public String mostrarEvento(@PathVariable Long id, Model model) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            if (evento == null) {
                throw new EventoNoEncontradoException("El evento no existe.");
            }
            
            int ticketsDisponibles = eventoService.obtenerTicketsDisponibles(id);
            if (ticketsDisponibles == 0) {
                model.addAttribute("eventoAgotado", true);
            }
            
            model.addAttribute("evento", evento);
            model.addAttribute("ticketsDisponibles", ticketsDisponibles);
            model.addAttribute("compra", new Compra());
            model.addAttribute("clientes", clienteService.listarClientes());
            return "eventos/detalle";
        } catch (EventoNoEncontradoException e) {
            model.addAttribute("error", e.getMessage());
            return "eventos/error";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al cargar el evento: " + e.getMessage());
            return "eventos/error";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarEvento(@PathVariable Long id, Model model) {
        try {
            Evento evento = eventoService.obtenerEventoPorId(id);
            model.addAttribute("evento", evento);
            return "eventos/form";
        } catch (EventoNoEncontradoException e) {
            return "redirect:/eventos";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String mostrarConfirmacionEliminacion(@PathVariable Long id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id);
        if (evento == null) {
            return "redirect:/eventos?error=Evento no encontrado";
        }
        model.addAttribute("evento", evento);
        return "eventos/confirmar-eliminacion";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Evento eliminado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el evento.");
        }
        return "redirect:/eventos";
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
