package br.com.phamtecnologia.apiclientes.entities;

import br.com.phamtecnologia.apiclientes.enums.StatusCliente;
import br.com.phamtecnologia.apiclientes.enums.TipoCliente;
import br.com.phamtecnologia.apiclientes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @PostMapping("criar")
    public String criar(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String tipo
    ) {
        try {
            var cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setTipo(TipoCliente.valueOf(tipo));
            cliente.setStatus(StatusCliente.ATIVO);

            var clienteRepository = new ClienteRepository();
            clienteRepository.create(cliente);

            return "Cliente cadastrado com sucesso";
        }
        catch (Exception e) {
            return "Erro ao cadastrado o cliente: " + e.getMessage();
        }
    }
}
