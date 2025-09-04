package database;

import config.Utilities;
import model.Produto;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManagerProduto {

    Utilities utilities = new Utilities();

    //Metodo para adicionar um produto no banco de dados
    public void cadastrarProduto(Produto produto) {
        String insertSQL = "INSERT INTO produtos (id, codigo, nome, custo, preco_venda, margem_lucro) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {

            // Insere o novo produto
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setString(1, produto.getId().toString());
                statement.setString(2, produto.getCodigo());
                statement.setString(3, produto.getNome());
                statement.setString(4, produto.getCusto().toString());
                statement.setString(5, produto.getPrecoDeVenda().toString());
                statement.setBigDecimal(6, produto.getMargemDeLucro());

                statement.executeUpdate();
                System.out.println("Produto adicionado com sucesso!");

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Metodo para remover um produto do banco de dados
    public void removerProduto(UUID id) {
        String deleteSQL = "DELETE FROM produtos WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(deleteSQL)) {

            statement.setString(1, String.valueOf(id));


            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Produto removido com sucesso!");
            } else {
                System.out.println("Nenhum Produto encontrado com o ID informado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao remover o produto: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erro ao remover o produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Metodo para listar todos os produtos
    public List<Produto> listarTodosProdutos() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum produto foi encontrado!")) {
            return null;
        }

        List<Produto> produtos = new ArrayList<>();
        String selectSQL = "SELECT * FROM produtos";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = statement.executeQuery()) {

            // Percorrendo os resultados da consulta
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String codigo = resultSet.getString("codigo");
                String nome = resultSet.getString("nome");
                BigDecimal custo = resultSet.getBigDecimal("custo");
                BigDecimal precoDeVenda = resultSet.getBigDecimal("preco_venda");
                BigDecimal margemDeLucro = resultSet.getBigDecimal("margem_lucro");


                // Criando a instância do produto
                Produto produto = new Produto(id, codigo, nome, custo, precoDeVenda, margemDeLucro);


                // Adicionando o produto à lista
                produtos.add(produto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar os produtos: " + e.getMessage());
        }

        return produtos;
    }

    public Produto encontrarProdutoPorId(UUID id) {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum produto foi encontrado!")) {
            return null;
        }

        String selectSQL = "SELECT * FROM produtos WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String codigo = resultSet.getString("codigo");
                String nome = resultSet.getString("nome");
                BigDecimal custo = resultSet.getBigDecimal("custo");
                BigDecimal precoDeVenda = resultSet.getBigDecimal("preco_venda");
                BigDecimal margemDeLucro = resultSet.getBigDecimal("margem_lucro");

                // Criando e retornando o produto encontrado
                return new Produto(id, codigo, nome, custo, precoDeVenda, margemDeLucro);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o produto: " + e.getMessage());
        }

        return null; // Retorna null se o produto não for encontrado
    }

    public Produto encontrarProdutoPorCodigo(String codigo) {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum produto foi encontrado!")) {
            return null;
        }

        String selectSQL = "SELECT * FROM produtos WHERE codigo = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, codigo);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String nome = resultSet.getString("nome");
                BigDecimal custo = resultSet.getBigDecimal("custo");
                BigDecimal precoDeVenda = resultSet.getBigDecimal("preco_venda");
                BigDecimal margemDeLucro = resultSet.getBigDecimal("margem_lucro");

                return new Produto(id, codigo, nome, custo, precoDeVenda, margemDeLucro);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o produto por código: " + e.getMessage());
        }

        return null;
    }

    public Produto encontrarProdutoPorNome(String nome) {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum produto foi encontrado!")) {
            return null;
        }

        String selectSQL = "SELECT * FROM produtos WHERE nome = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String codigo = resultSet.getString("codigo");
                BigDecimal custo = resultSet.getBigDecimal("custo");
                BigDecimal precoDeVenda = resultSet.getBigDecimal("preco_venda");
                BigDecimal margemDeLucro = resultSet.getBigDecimal("margem_lucro");

                return new Produto(id, codigo, nome, custo, precoDeVenda, margemDeLucro);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o produto por nome: " + e.getMessage());
        }

        return null;
    }

    //Metodo para listar todos os produtos em estoque
    public List<Produto> listarTodosProdutosEmEstoque() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum produto foi encontrado!")) {
            return null;
        }

        List<Produto> produtos = new ArrayList<>();
        String selectSQL = "SELECT * FROM estoque";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = statement.executeQuery()) {

            // Percorrendo os resultados da consulta
            while (resultSet.next()) {
                String codigo = resultSet.getString("codigo_produto");
                String nome = resultSet.getString("nome_produto");
                int quantidade = resultSet.getInt("quantidade");
                BigDecimal custo = resultSet.getBigDecimal("custo");
                UUID id = UUID.fromString(resultSet.getString("id_produto"));

                if (quantidade > 0) {
                    // Criando a instância do produto
                    Produto produto = new DBManagerProduto().encontrarProdutoPorId(id);
                    produto.setQuantidadeEmEstoque(quantidade);

                    // Adicionando o produto à lista
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar os produtos em estoque: " + e.getMessage());
        }

        return produtos;
    }

}
