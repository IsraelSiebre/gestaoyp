package database;

import com.toedter.calendar.JDateChooser;
import config.Utilities;
import model.Cliente;
import model.Encomenda;
import model.Produto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBManagerEncomenda {

    Utilities utilities = new Utilities();

    public void registrarEncomenda(Encomenda encomenda) {
        String insertSQL = "INSERT INTO encomendas (id, data_pedido, data_entrega, id_produto, codigo_produto, nome_produto, quantidade, id_cliente, nome_cliente, adicional) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                stmt.setString(1, encomenda.getId().toString());
                stmt.setString(2, encomenda.getDataPedido());
                stmt.setString(3, encomenda.getDataEntrega());
                stmt.setString(4, encomenda.getProduto().getId().toString());
                stmt.setString(5, encomenda.getProduto().getCodigo());
                stmt.setString(6, encomenda.getProduto().getNome());
                stmt.setInt(7, encomenda.getQuantidade());
                stmt.setString(8, encomenda.getCliente().getId().toString());
                stmt.setString(9, encomenda.getCliente().getNomeCompleto());
                stmt.setBigDecimal(10, encomenda.getAdicional());
                stmt.executeUpdate();

                connection.commit();
                System.out.println("Encomenda registrada com sucesso!");

            } catch (SQLException ex) {
                connection.rollback();
                throw new RuntimeException("Erro ao registrar encomenda: " + ex.getMessage(), ex);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco: " + ex.getMessage(), ex);
        }
    }

    public boolean removerEncomenda(UUID id) {
        String deleteSQL = "DELETE FROM encomendas WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {

            stmt.setString(1, id.toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao remover encomenda: " + e.getMessage());
            return false;
        }
    }

    public Encomenda encontrarEncomendaPorId(UUID id) {
        String selectSQL = "SELECT * FROM encomendas WHERE id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement stmt = connection.prepareStatement(selectSQL)) {

            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UUID idProduto = UUID.fromString(rs.getString("id_produto"));
                UUID idCliente = UUID.fromString(rs.getString("id_cliente"));
                String dataPedido = rs.getString("data_pedido");
                String dataEntrega = rs.getString("data_entrega");
                int quantidade = rs.getInt("quantidade");
                BigDecimal adicional = rs.getBigDecimal("adicional");

                Produto produto = new DBManagerProduto().encontrarProdutoPorId(idProduto);
                Cliente cliente = new DBManagerCliente().encontrarClientePorId(idCliente);

                JDateChooser dataPedidoChooser = new JDateChooser();
                dataPedidoChooser.setDate(Date.valueOf(dataPedido));
                JDateChooser dataEntregaChooser = new JDateChooser();
                dataEntregaChooser.setDate(Date.valueOf(dataEntrega));

                return new Encomenda(id, dataPedidoChooser, dataEntregaChooser, produto, quantidade, cliente, adicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar encomenda: " + e.getMessage());
        }

        return null;
    }

    public List<Encomenda> listarTodasEncomendas() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhuma encomenda foi encontrada!")) {
            return null;
        }

        List<Encomenda> encomendas = new ArrayList<>();
        String selectSQL = "SELECT * FROM encomendas";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                UUID idProduto = UUID.fromString(rs.getString("id_produto"));
                UUID idCliente = UUID.fromString(rs.getString("id_cliente"));
                String dataPedido = rs.getString("data_pedido");
                String dataEntrega = rs.getString("data_entrega");
                int quantidade = rs.getInt("quantidade");
                BigDecimal adicional = rs.getBigDecimal("adicional");

                Produto produto = new DBManagerProduto().encontrarProdutoPorId(idProduto);
                Cliente cliente = new DBManagerCliente().encontrarClientePorId(idCliente);

                JDateChooser dataPedidoChooser = new JDateChooser();
                dataPedidoChooser.setDate(Date.valueOf(dataPedido));
                JDateChooser dataEntregaChooser = new JDateChooser();
                dataEntregaChooser.setDate(Date.valueOf(dataEntrega));

                Encomenda encomenda = new Encomenda(id, dataPedidoChooser, dataEntregaChooser, produto, quantidade, cliente, adicional);
                encomendas.add(encomenda);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar encomendas: " + e.getMessage());
        }

        return encomendas;
    }
}
