package br.com.phamtecnologia.apiclientes.repositories;

import br.com.phamtecnologia.apiclientes.entities.Cliente;
import br.com.phamtecnologia.apiclientes.factories.ConnectionFactory;

public class ClienteRepository {

    public void create(Cliente cliente) throws Exception {

        try (var connection = ConnectionFactory.getConnection()) {

            var statement = connection.prepareStatement("""  
                    INSERT INTO clientes (nome, email, telefone, tipo, status)
                    VALUES (?, ?, ?, ?, ?)
                """);
            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getTelefone());
            statement.setString(4, cliente.getTipo().toString());
            statement.setString(5, cliente.getStatus().toString());
            statement.execute();

            statement.close();

        }

    }
}
