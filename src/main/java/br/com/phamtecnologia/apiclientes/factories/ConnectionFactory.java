package br.com.phamtecnologia.apiclientes.factories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class ConnectionFactory {

    @Value("${database.host}")
    private String host;

    @Value("${database.user}")
    private String user;

    @Value("${database.pass}")
    private String pass;

    public Connection getConnection() throws Exception {

        return DriverManager.getConnection(host, user, pass);
    }
}
