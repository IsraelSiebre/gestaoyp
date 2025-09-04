package database;

import com.toedter.calendar.JDateChooser;
import config.Utilities;
import model.Cliente;
import model.Compra;
import model.Produto;
import model.Venda;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManagerVenda {

    Utilities utilities = new Utilities();

    //Metodo para adicionar uma venda no banco de dados
    public void registrarVenda(Venda venda) {
        String insertVendaSQL = "INSERT INTO vendas (id, data, id_produto, codigo_produto, nome_produto, quantidade, id_cliente, nome_cliente, adicional) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String selectEstoqueSQL = "SELECT quantidade FROM estoque WHERE codigo_produto = ?";
        String updateEstoqueSQL = "UPDATE estoque SET quantidade = quantidade - ? WHERE codigo_produto = ?";
        String insertEstoqueSQL = "INSERT INTO estoque (codigo_produto, nome_produto, quantidade, custo, id_produto) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {
            connection.setAutoCommit(false); // Início da transação

            try (
                    PreparedStatement insertCompraStmt = connection.prepareStatement(insertVendaSQL);
                    PreparedStatement selectEstoqueStmt = connection.prepareStatement(selectEstoqueSQL);
                    PreparedStatement updateEstoqueStmt = connection.prepareStatement(updateEstoqueSQL);
                    PreparedStatement insertEstoqueStmt = connection.prepareStatement(insertEstoqueSQL)
            ) {
                // Inserir na tabela de vendas
                insertCompraStmt.setString(1, String.valueOf(venda.getId()));
                insertCompraStmt.setString(2, String.valueOf(venda.getData()));
                insertCompraStmt.setString(3, String.valueOf(venda.getProduto().getId()));
                insertCompraStmt.setString(4, venda.getProduto().getCodigo());
                insertCompraStmt.setString(5, venda.getProduto().getNome());
                insertCompraStmt.setInt(6, venda.getQuantidade());
                insertCompraStmt.setString(7, String.valueOf(venda.getCliente().getId()));
                insertCompraStmt.setString(8, venda.getCliente().getNomeCompleto());
                insertCompraStmt.setBigDecimal(9, venda.getAdicional());
                insertCompraStmt.executeUpdate();

                // Verifica se o produto já está no estoque
                selectEstoqueStmt.setString(1, venda.getProduto().getCodigo());
                ResultSet resultSet = selectEstoqueStmt.executeQuery();

                // Produto já existe: atualizar a quantidade
                updateEstoqueStmt.setInt(1, venda.getQuantidade());
                updateEstoqueStmt.setString(2, venda.getProduto().getCodigo());
                updateEstoqueStmt.executeUpdate();

                connection.commit(); // Finaliza a transação
                System.out.println("Venda cadastrada e estoque atualizado com sucesso!");


            } catch (SQLException ex) {
                connection.rollback(); // Desfaz tudo se der erro
                throw new RuntimeException("Erro ao registrar venda e atualizar estoque: " + ex.getMessage(), ex);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + ex.getMessage(), ex);
        }
    }


    // Metodo para remover um produto do banco de dados
    public void removerVenda(UUID id) {
        String selectVendaSQL = "SELECT codigo_produto, quantidade FROM vendas WHERE id = ?";
        String deleteVendaSQL = "DELETE FROM vendas WHERE id = ?";
        String updateEstoqueSQL = "UPDATE estoque SET quantidade = quantidade + ? WHERE codigo_produto = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {
            connection.setAutoCommit(false); // Inicia transação

            try (
                    PreparedStatement selectStmt = connection.prepareStatement(selectVendaSQL);
                    PreparedStatement deleteStmt = connection.prepareStatement(deleteVendaSQL);
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

                    // Remover a venda
                    deleteStmt.setString(1, id.toString());
                    int rowsDeleted = deleteStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        connection.commit(); // Confirma a transação
                        System.out.println("Venda removida e estoque atualizado com sucesso!");
                    } else {
                        connection.rollback();
                        System.out.println("Nenhuma venda encontrada com o ID informado.");
                    }

                } else {
                    System.out.println("Venda não encontrada.");
                }

            } catch (SQLException ex) {
                connection.rollback();
                System.out.println("Erro ao remover a venda: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Erro ao remover a venda: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            System.out.println("Erro na conexão com o banco de dados: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Erro na conexão com o banco de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }




    //Metodo para listar todos os produtos
    public List<Venda> listarTodasVendas() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhuma venda foi encontrada!")) {
            return null;
        }

        List<Venda> vendas = new ArrayList<>();
        String selectSQL = "SELECT * FROM vendas";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String dataString = rs.getString("data");
                UUID idProduto = UUID.fromString(rs.getString("id_produto"));
                int quantidade = rs.getInt("quantidade");
                UUID idCliente = UUID.fromString(rs.getString("id_cliente"));
                BigDecimal adicional = rs.getBigDecimal("adicional");

                Produto produto = new DBManagerProduto().encontrarProdutoPorId(idProduto);
                Cliente cliente = new DBManagerCliente().encontrarClientePorId(idCliente);

                // Converte a data (String) para JDateChooser
                JDateChooser dataChooser = new JDateChooser();
                java.sql.Date dataSql = java.sql.Date.valueOf(dataString);
                dataChooser.setDate(dataSql);

                // Cria o objeto Compra
                Venda venda = new Venda(id, dataChooser, produto, quantidade, cliente, adicional);

                // Adiciona à lista
                vendas.add(venda);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar as vendas: " + e.getMessage());
        }

        return vendas;
    }



    public Venda encontrarVendaPorId(UUID id) {
        String selectSQL = "SELECT * FROM vendas WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, id.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String dataString = resultSet.getString("data");
                UUID idProduto = UUID.fromString(resultSet.getString("id_produto"));
                int quantidade = resultSet.getInt("quantidade");
                UUID idCliente = UUID.fromString(resultSet.getString("id_cliente"));
                BigDecimal adicional = resultSet.getBigDecimal("adicional");

                Produto produto = new DBManagerProduto().encontrarProdutoPorId(idProduto);
                Cliente cliente =  new DBManagerCliente().encontrarClientePorId(idCliente);

                // Converte data String para JDateChooser
                JDateChooser dataChooser = new JDateChooser();
                java.sql.Date dataSql = java.sql.Date.valueOf(dataString);
                dataChooser.setDate(dataSql);

                // Retorna a venda encontrada
                return new Venda(id, dataChooser, produto, quantidade, cliente, adicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar a venda: " + e.getMessage());
        }

        return null; // Caso não encontre a venda
    }

}
