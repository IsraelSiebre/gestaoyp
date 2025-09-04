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

public class DBManagerEstoque {

    Utilities utilities = new Utilities();

    // Método para listar todos os produtos em estoque
    public List<Produto> listarTodosProdutosEmEstoque() {
        if (!utilities.verificaSeTemDB(Utilities.CAMINHO_BANCO_DE_DADOS, "Nenhum produto foi encontrado!")) {
            return null;
        }

        List<Produto> produtos = new ArrayList<>();
        String selectSQL = "SELECT * FROM estoque";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String codigo = resultSet.getString("codigo_produto");
                String nome = resultSet.getString("nome_produto");
                int quantidade = resultSet.getInt("quantidade");
                BigDecimal custo = resultSet.getBigDecimal("custo");
                UUID id = UUID.fromString(resultSet.getString("id_produto"));

                if (quantidade > 0) {
                    Produto produto = new DBManagerProduto().encontrarProdutoPorId(id);
                    produto.setQuantidadeEmEstoque(quantidade);
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar os produtos em estoque: " + e.getMessage());
        }

        return produtos;
    }

    // Novo método: Verifica se o produto existe e tem quantidade suficiente em estoque
    public boolean produtoDisponivelParaVenda(UUID idProduto, int quantidadeRequerida) {
        String selectSQL = "SELECT quantidade FROM estoque WHERE id_produto = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Utilities.NOME_BANCO_DE_DADOS);
             PreparedStatement statement = connection.prepareStatement(selectSQL)) {

            statement.setString(1, idProduto.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int quantidadeEmEstoque = resultSet.getInt("quantidade");
                    return quantidadeEmEstoque >= quantidadeRequerida;
                } else {
                    // Produto não encontrado no estoque
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar disponibilidade do produto: " + e.getMessage());
            return false;
        }
    }
}
