package br.com.phamtecnologia.apiclientes.entities;

import br.com.phamtecnologia.apiclientes.enums.StatusCliente;
import br.com.phamtecnologia.apiclientes.enums.TipoCliente;
import lombok.Data;

import java.util.UUID;

@Data
public class Cliente {

    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private TipoCliente tipo;
    private StatusCliente status;

}
