package br.com.phamtecnologia.apiclientes.repositories;

import br.com.phamtecnologia.apiclientes.entities.Cliente;
import br.com.phamtecnologia.apiclientes.enums.StatusCliente;
import br.com.phamtecnologia.apiclientes.enums.TipoCliente;
import br.com.phamtecnologia.apiclientes.factories.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<Cliente> findAll() throws Exception {

        try (var connection = connectionFactory.getConnection()) {
            var statement = connection.prepareStatement("""
                    SELECT * FROM clientes
                    WHERE status = 'ATIVO'
                    ORDER BY nome
            """);

            var result = statement.executeQuery();

            var lista = new ArrayList<Cliente>();
            while(result.next()) {

                var cliente = new Cliente();

                cliente.setId(result.getInt("id"));
                cliente.setNome(result.getString("nome"));
                cliente.setEmail(result.getString("email"));
                cliente.setTelefone(result.getString("telefone"));
                cliente.setStatus(StatusCliente.valueOf(result.getString("status")));
                cliente.setTipo(TipoCliente.valueOf(result.getString("tipo")));

                lista.add(cliente);

            }

            return lista;
        }
    }

    public boolean update(Cliente cliente) throws Exception {

        try (var connection = connectionFactory.getConnection()) {
            var statement = connection.prepareStatement("""
                    UPDATE clientes
                    SET nome = ?, email = ?, telefone = ?, tipo = ?
                    WHERE id = ?
            """);

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getTelefone());
            statement.setString(4, cliente.getTipo().toString());
            statement.setInt(5, cliente.getId());

            var rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean delete(Integer id) throws Exception {
        try (var connection = connectionFactory.getConnection()) {

            var statement = connection.prepareStatement("""
                    UPDATE clientes
                    SET status = 'INATIVO'
                    WHERE id=?
            """);
            statement.setInt(1, id);
            var rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public Cliente findById(Integer id) throws Exception {

        try (var connection = connectionFactory.getConnection()) {

            var statement = connection.prepareStatement("""
                    SELECT * FROM clientes
                    WHERE id = ?
                    AND status = 'ATIVO'
            """);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            Cliente cliente = null;

            if (result.next()) {
                cliente = new Cliente();

                cliente.setId(result.getInt("id"));
                cliente.setNome(result.getString("nome"));
                cliente.setEmail(result.getString("email"));
                cliente.setTelefone(result.getString("telefone"));
                cliente.setStatus(StatusCliente.valueOf(result.getString("status")));
                cliente.setTipo(TipoCliente.valueOf(result.getString("tipo")));

            }
            return cliente;
        }
    }
}
