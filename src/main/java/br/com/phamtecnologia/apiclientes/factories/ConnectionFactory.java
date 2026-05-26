package br.com.phamtecnologia.apiclientes.factories;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

    public static Connection getConnection() throws Exception {

        var host = "jdbc:postgresql://localhost:5432/bd-api-clientes";
        var user = "postgres";
        var pass = "root";

        return DriverManager.getConnection(host, user, pass);
    }
}
