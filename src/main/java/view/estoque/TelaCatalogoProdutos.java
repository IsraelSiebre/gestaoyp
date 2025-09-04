package view.estoque;

import database.DBManagerProduto;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class TelaCatalogoProdutos extends JPanel {

    private DBManagerProduto dbManagerProduto = new DBManagerProduto();
    private final JTable tabelaProdutos;
    private final DefaultTableModel modeloTabela;
    private UUID produtoSelecionadoId; // Apenas o UUID do produto selecionado

    public TelaCatalogoProdutos() {
        setLayout(new BorderLayout());

        // Criar a tabela
        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Código");
        modeloTabela.addColumn("Nome");
        modeloTabela.addColumn("Custo");
        modeloTabela.addColumn("Preço de Venda");
        modeloTabela.addColumn("Margem de Lucro");

        tabelaProdutos = new JTable(modeloTabela);

        // Centralizar conteúdo nas células
        DefaultTableCellRenderer centralizadoRenderer = new DefaultTableCellRenderer();
        centralizadoRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Aplica o renderer centralizado para todas as colunas
        for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
            tabelaProdutos.getColumnModel().getColumn(i).setCellRenderer(centralizadoRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        add(scrollPane, BorderLayout.CENTER);

        // Carregar os Produtos na tabela
        carregarProdutos();

        // Painel com os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(_ -> excluirProduto());

        painelBotoes.add(botaoExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // Listener para seleção de linha (captura apenas o UUID)
        tabelaProdutos.getSelectionModel().addListSelectionListener(_ -> {
            int selectedRow = tabelaProdutos.getSelectedRow();
            if (selectedRow != -1) {
                produtoSelecionadoId = UUID.fromString(modeloTabela.getValueAt(selectedRow, 0).toString());
            }
        });
    }

    boolean carregarProdutos() {
        List<Produto> produtos = new DBManagerProduto().listarTodosProdutos();

        // Limpa os dados existentes na tabela
        modeloTabela.setRowCount(0);

        // Adiciona os produtos na tabela
        for (Produto produto : produtos) {
            modeloTabela.addRow(produto.linhaFormatada());
        }

        return true;
    }

    private void excluirProduto() {
        if (produtoSelecionadoId == null) {
            JOptionPane.showMessageDialog(null, "Selecione um produto para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar exclusão
        int confirmacao = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir este produto?", "Excluir produto", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {

            Produto produto = dbManagerProduto.encontrarProdutoPorId(produtoSelecionadoId);

            if (produto == null) {
                JOptionPane.showMessageDialog(null, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Remover o produto do banco de dados
            dbManagerProduto.removerProduto(produtoSelecionadoId);

            // Remover o produto da tabela
            carregarProdutos();
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
        }
    }
}
