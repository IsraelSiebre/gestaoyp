package database;

import config.Utilities;
import model.Cliente;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManagerCliente {

    Utilities utilities = new Utilities();

    //Metodo para adicionar um cliente no banco de dados
    public void cadastrarCliente(Cliente cliente) {
        String insertSQL = "INSERT INTO clientes (id, nome_completo, telefone, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {

            // Insere o novo cliente
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setString(1, cliente.getId().toString());
                statement.setString(2, cliente.getNomeCompleto());
                statement.setString(3, cliente.getTelefone());
                statement.setString(4, cliente.getEmail());

                statement.executeUpdate();
                System.out.println("Cliente cadastrado com sucesso!");

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Metodo para remover um cliente do banco de dados
    public void removerCliente(UUID id) {
        String deleteSQL = "DELETE FROM clientes WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {

            statement.setString(1, String.valueOf(id));


            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Cliente removido com sucesso!");
            } else {
                System.out.println("Nenhum vliente encontrado com o ID informado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao remover o cliente: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erro ao remover o cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Metodo para listar todos os clientes
    public List<Cliente> listarTodosClientes() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum cliente foi encontrado!")) {
            return null;
        }

        List<Cliente> clientes = new ArrayList<>();
        String selectSQL = "SELECT * FROM clientes";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = statement.executeQuery()) {

            // Percorrendo os resultados da consulta
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String nomeCompleto = resultSet.getString("nome_completo");
                String telefone = resultSet.getString("telefone");
                String email = resultSet.getString("email");

                // Criando a instância do cliente
                Cliente cliente = new Cliente(id, nomeCompleto, telefone, email);

                // Adicionando o cliente à lista
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar os clientes: " + e.getMessage());
        }

        return clientes;
    }


    public Cliente encontrarClientePorId(UUID id) {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum cliente foi encontrado!")) {
            return null;
        }

        String selectSQL = "SELECT * FROM clientes WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nomeCompleto = resultSet.getString("nome_completo");
                String telefone = resultSet.getString("telefone");
                String email = resultSet.getString("email");

                // Criando e retornando o produto encontrado
                return new Cliente(id, nomeCompleto, telefone, email);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o cliente: " + e.getMessage());
        }

        return null; // Retorna null se o cliente não for encontrado
    }

    public Cliente encontrarClientePorNome(String nome) {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum cliente foi encontrado!")) {
            return null;
        }

        String selectSQL = "SELECT * FROM clientes WHERE nome_completo = ? COLLATE NOCASE";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String telefone = resultSet.getString("telefone");
                String email = resultSet.getString("email");

                // Criando e retornando o produto encontrado
                return new Cliente(id, nome, telefone, email);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o cliente: " + e.getMessage());
        }

        return null; // Retorna null se o cliente não for encontrado
    }

}


