package br.com.phamtecnologia.apiclientes.entities;

import br.com.phamtecnologia.apiclientes.dtos.ClienteDto;
import br.com.phamtecnologia.apiclientes.enums.StatusCliente;
import br.com.phamtecnologia.apiclientes.enums.TipoCliente;
import br.com.phamtecnologia.apiclientes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("consultar")
    public List<Cliente> consultar() {
        try {
            return clienteRepository.findAll();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("atualizar/{id}")
    public String atualizar(@PathVariable int id, @RequestBody ClienteDto dto) {

        try {

            var cliente = new Cliente();

            cliente.setId(id);
            cliente.setNome(dto.getNome());
            cliente.setEmail(dto.getEmail());
            cliente.setTelefone(dto.getTelefone());
            cliente.setTipo(TipoCliente.valueOf((dto.getTipo())));

            if (clienteRepository.update(cliente)) {
                return "Cliente atualizado com sucesso";
            }
            else {
                return "Nenhum cliente foi encontrado para edfição. Verifique o ID informado";
            }
        }
        catch (Exception e)  {
            return "Erro ao atualizar o cliente: " + e.getMessage();
        }

    }

    @DeleteMapping("excluir/{id}")
    public String excluir(@PathVariable int id) {
        try {
            if (clienteRepository.delete(id)) {
                return "Cliente deletado com sucesso";
            }
            else {
                return "Nenhum cliente foi encontrado para exclusão. Verifique o ID informado.";
            }
        } catch (Exception e) {
            return "Erro ao excluir o cliente: " + e.getMessage();
        }
    }

    @GetMapping("obter/{id}")
    public Cliente obter(@PathVariable int id) {
        try {
            return clienteRepository.findById(id);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
