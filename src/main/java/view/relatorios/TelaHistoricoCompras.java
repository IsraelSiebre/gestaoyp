package view.relatorios;

import database.DBManagerCompra;
import model.Compra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.table.TableColumnModel;
import java.util.List;
import java.util.UUID;

public class TelaHistoricoCompras extends JPanel {

    private DBManagerCompra dbManagerCompra = new DBManagerCompra();
    private final JTable tabelaCompras;
    private final DefaultTableModel modeloTabela;
    private UUID compraSelecionadaId; // Apenas o UUID da compra selecionada

    public TelaHistoricoCompras() {
        setLayout(new BorderLayout());

        // Criar a tabela
        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Data");
        modeloTabela.addColumn("Codigo do Produto");
        modeloTabela.addColumn("Nome do Produto");
        modeloTabela.addColumn("Quantidade");
        modeloTabela.addColumn("Valor Unitário");
        modeloTabela.addColumn("Valor Total");
        modeloTabela.addColumn("Fornecedor");

        tabelaCompras = new JTable(modeloTabela);

        // Centralizar conteúdo nas células
        DefaultTableCellRenderer centralizadoRenderer = new DefaultTableCellRenderer();
        centralizadoRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Aplica o renderer centralizado para todas as colunas
        for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
            tabelaCompras.getColumnModel().getColumn(i).setCellRenderer(centralizadoRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaCompras);
        add(scrollPane, BorderLayout.CENTER);

        // Carregar as compras na tabela
        carregarCompras();

        // Painel com os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(_ -> excluirCompra());

        painelBotoes.add(botaoExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // Listener para seleção de linha (captura apenas o UUID)
        tabelaCompras.getSelectionModel().addListSelectionListener(_ -> {
            int selectedRow = tabelaCompras.getSelectedRow();
            if (selectedRow != -1) {
                compraSelecionadaId = UUID.fromString(modeloTabela.getValueAt(selectedRow, 0).toString());
            }
        });
    }

    boolean carregarCompras() {
        List<Compra> compras = dbManagerCompra.listarTodasCompras();

        // Limpa os dados existentes na tabela
        modeloTabela.setRowCount(0);

        // Adiciona as compras na tabela
        for (Compra compra : compras) {
            modeloTabela.addRow(compra.linhaFormatada());
        }

        return true;
    }

    private void excluirCompra() {
        if (compraSelecionadaId == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma compra para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar exclusão
        int confirmacao = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esta compra?", "Excluir compra", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {

            Compra compra = dbManagerCompra.encontrarCompraPorId(compraSelecionadaId);

            if (compra == null) {
                JOptionPane.showMessageDialog(null, "Compra não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Remover a compra do banco de dados
            dbManagerCompra.removerCompra(compraSelecionadaId);

            // Remover a compra da tabela
            carregarCompras();
            JOptionPane.showMessageDialog(this, "Compra excluída com sucesso!");
        }
    }
}
