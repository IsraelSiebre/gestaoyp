package database;


import com.toedter.calendar.JDateChooser;
import config.Utilities;
import model.Compra;
import model.Produto;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManagerCompra {

    Utilities utilities = new Utilities();

    //Metodo para adicionar uma compra no banco de dados
    public void registrarCompra(Compra compra) {
        String insertCompraSQL = "INSERT INTO compras (id, data, id_produto, codigo_produto, nome_produto, quantidade, fornecedor) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String selectEstoqueSQL = "SELECT quantidade FROM estoque WHERE codigo_produto = ?";
        String updateEstoqueSQL = "UPDATE estoque SET quantidade = quantidade + ? WHERE codigo_produto = ?";
        String insertEstoqueSQL = "INSERT INTO estoque (codigo_produto, nome_produto, quantidade, custo, id_produto) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {
            connection.setAutoCommit(false); // Início da transação

            try (
                    PreparedStatement insertCompraStmt = connection.prepareStatement(insertCompraSQL);
                    PreparedStatement selectEstoqueStmt = connection.prepareStatement(selectEstoqueSQL);
                    PreparedStatement updateEstoqueStmt = connection.prepareStatement(updateEstoqueSQL);
                    PreparedStatement insertEstoqueStmt = connection.prepareStatement(insertEstoqueSQL)
            ) {
                // Inserir na tabela de compras
                insertCompraStmt.setString(1, String.valueOf(compra.getId()));
                insertCompraStmt.setString(2, String.valueOf(compra.getData()));
                insertCompraStmt.setString(3, String.valueOf(compra.getProduto().getId()));
                insertCompraStmt.setString(4, compra.getProduto().getCodigo());
                insertCompraStmt.setString(5, compra.getProduto().getNome());
                insertCompraStmt.setInt(6, compra.getQuantidade());
                insertCompraStmt.setString(7, compra.getFornecedor());
                insertCompraStmt.executeUpdate();

                // Verifica se o produto já está no estoque
                selectEstoqueStmt.setString(1, compra.getProduto().getCodigo());
                ResultSet resultSet = selectEstoqueStmt.executeQuery();

                if (resultSet.next()) {
                    // Produto já existe: atualizar a quantidade
                    updateEstoqueStmt.setInt(1, compra.getQuantidade());
                    updateEstoqueStmt.setString(2, compra.getProduto().getCodigo());
                    updateEstoqueStmt.executeUpdate();
                } else {
                    // Produto não existe: inserir no estoque
                    insertEstoqueStmt.setString(1, compra.getProduto().getCodigo());
                    insertEstoqueStmt.setString(2, compra.getProduto().getNome());
                    insertEstoqueStmt.setInt(3, compra.getQuantidade());
                    insertEstoqueStmt.setBigDecimal(4, compra.getProduto().getCusto()); // Assumindo que o Produto tem `getCusto()`
                    insertEstoqueStmt.setString(5, String.valueOf(compra.getProduto().getId()));
                    insertEstoqueStmt.executeUpdate();
                }

                connection.commit(); // Finaliza a transação
                System.out.println("Compra cadastrada e estoque atualizado com sucesso!");

            } catch (SQLException ex) {
                connection.rollback(); // Desfaz tudo se der erro
                throw new RuntimeException("Erro ao cadastrar produto e atualizar estoque: " + ex.getMessage(), ex);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + ex.getMessage(), ex);
        }
    }




    // Metodo para remover um produto do banco de dados
    public void removerCompra(UUID id) {
        String selectCompraSQL = "SELECT codigo_produto, quantidade FROM compras WHERE id = ?";
        String deleteCompraSQL = "DELETE FROM compras WHERE id = ?";
        String updateEstoqueSQL = "UPDATE estoque SET quantidade = quantidade - ? WHERE codigo_produto = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {
            connection.setAutoCommit(false); // Inicia transação

            try (
                    PreparedStatement selectStmt = connection.prepareStatement(selectCompraSQL);
                    PreparedStatement deleteStmt = connection.prepareStatement(deleteCompraSQL);
                    PreparedStatement updateEstoqueStmt = connection.prepareStatement(updateEstoqueSQL)
            ) {
                // Buscar dados da compra antes de deletar
                selectStmt.setString(1, id.toString());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String codigoProduto = rs.getString("codigo_produto");
                    int quantidade = rs.getInt("quantidade");

                    // Atualizar o estoque (subtrair a quantidade)
                    updateEstoqueStmt.setInt(1, quantidade);
                    updateEstoqueStmt.setString(2, codigoProduto);
                    updateEstoqueStmt.executeUpdate();

                    // Remover a compra
                    deleteStmt.setString(1, id.toString());
                    int rowsDeleted = deleteStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        connection.commit(); // Confirma a transação
                        System.out.println("Compra removida e estoque atualizado com sucesso!");
                    } else {
                        connection.rollback();
                        System.out.println("Nenhuma compra encontrada com o ID informado.");
                    }

                } else {
                    System.out.println("Compra não encontrada.");
                }

            } catch (SQLException ex) {
                connection.rollback();
                System.out.println("Erro ao remover a compra: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Erro ao remover a compra: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            System.out.println("Erro na conexão com o banco de dados: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }




    //Metodo para listar todos os produtos
    public List<Compra> listarTodasCompras() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhuma compra foi encontrada!")) {
            return null;
        }

        List<Compra> compras = new ArrayList<>();
        String selectSQL = "SELECT * FROM compras";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String dataString = rs.getString("data");
                UUID idProduto = UUID.fromString(rs.getString("id_produto"));
                String codigo = rs.getString("codigo_produto");
                String nome = rs.getString("nome_produto");
                int quantidade = rs.getInt("quantidade");
                String fornecedor = rs.getString("fornecedor");

                // Cria o produto com valores nulos/defaults para custo, preco e margem (não estão disponíveis na tabela de compras)
                Produto produto = new DBManagerProduto().encontrarProdutoPorId(idProduto);

                // Converte a data (String) para JDateChooser
                JDateChooser dataChooser = new JDateChooser();
                java.sql.Date dataSql = java.sql.Date.valueOf(dataString);
                dataChooser.setDate(dataSql);

                // Cria o objeto Compra
                Compra compra = new Compra(id, dataChooser, produto, quantidade, fornecedor);

                // Adiciona à lista
                compras.add(compra);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar as compras: " + e.getMessage());
        }

        return compras;
    }



    public Compra encontrarCompraPorId(UUID id) {
        String selectSQL = "SELECT * FROM compras WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String dataString = resultSet.getString("data");
                UUID idProduto = UUID.fromString(resultSet.getString("id_produto"));
                String codigo = resultSet.getString("codigo_produto");
                String nome = resultSet.getString("nome_produto");
                int quantidade = resultSet.getInt("quantidade");
                String fornecedor = resultSet.getString("fornecedor");

                // Produto (valores nulos para campos não salvos na tabela de compras)
                Produto produto = new DBManagerProduto().encontrarProdutoPorId(idProduto);

                // Converte data String para JDateChooser
                JDateChooser dataChooser = new JDateChooser();
                java.sql.Date dataSql = java.sql.Date.valueOf(dataString);
                dataChooser.setDate(dataSql);

                // Retorna a compra encontrada
                return new Compra(id, dataChooser, produto, quantidade, fornecedor);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar a compra: " + e.getMessage());
        }

        return null; // Caso não encontre a compra
    }

}


