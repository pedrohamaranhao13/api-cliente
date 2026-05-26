package br.com.phamtecnologia.apiclientes.entities;

import br.com.phamtecnologia.apiclientes.dtos.ClienteDto;
import br.com.phamtecnologia.apiclientes.enums.StatusCliente;
import br.com.phamtecnologia.apiclientes.enums.TipoCliente;
import br.com.phamtecnologia.apiclientes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("criar")
    public String criar(@RequestBody ClienteDto dto) {
        try {
            var cliente = new Cliente();
            cliente.setNome(dto.getNome());
            cliente.setEmail(dto.getEmail());
            cliente.setTelefone(dto.getTelefone());
            cliente.setTipo(TipoCliente.valueOf((dto.getTipo())));
            cliente.setStatus(StatusCliente.ATIVO);

            clienteRepository.create(cliente);

            return "Cliente cadastrado com sucesso";
        }
        catch (Exception e) {
            return "Erro ao cadastrado o cliente: " + e.getMessage();
        }
    }
}
