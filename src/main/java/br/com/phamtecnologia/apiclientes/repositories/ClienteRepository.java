package br.com.phamtecnologia.apiclientes.repositories;

import br.com.phamtecnologia.apiclientes.entities.Cliente;
import br.com.phamtecnologia.apiclientes.factories.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClienteRepository {

    @Autowired
    private ConnectionFactory connectionFactory;

    public void create(Cliente cliente) throws Exception {

        try (var connection = connectionFactory.getConnection()) {

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
