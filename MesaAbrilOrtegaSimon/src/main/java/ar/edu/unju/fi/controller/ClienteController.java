package ar.edu.unju.fi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.service.ClienteService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("view", "clientes/lista");
        return "layout/nav";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("view", "clientes/form");
        return "layout/nav";
    }

    @PostMapping("/nuevo")
    public String guardarNuevoCliente(@ModelAttribute("cliente") Cliente cliente) {
        clienteService.guardarCliente(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute Cliente cliente, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("view", "clientes/form");
            return "layout/nav";
        }
        clienteService.guardarCliente(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.obtenerClientePorId(id));
        model.addAttribute("view", "clientes/form");
        return "layout/nav";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return "redirect:/clientes";
    }

    @GetMapping("/compradores")
    public String listarClientesConCompras(Model model) {
        model.addAttribute("clientes", clienteService.listarClientesConCompras());
        return "clientes/compradores";
    }
}
