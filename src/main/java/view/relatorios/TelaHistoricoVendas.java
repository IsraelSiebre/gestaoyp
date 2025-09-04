package view.relatorios;

import database.DBManagerVenda;
import model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import javax.swing.table.TableColumnModel;
import java.util.List;
import java.util.UUID;

public class TelaHistoricoVendas extends JPanel {

    private DBManagerVenda dbManagerVenda = new DBManagerVenda();
    private final JTable tabelaVendas;
    private final DefaultTableModel modeloTabela;
    private UUID vendaSelecionadaId; // Apenas o UUID da compra selecionada

    public TelaHistoricoVendas() {
        setLayout(new BorderLayout());

        // Criar a tabela
        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Data");
        modeloTabela.addColumn("Codigo do Produto");
        modeloTabela.addColumn("Nome do Produto");
        modeloTabela.addColumn("Quantidade");
        modeloTabela.addColumn("Valor Unitário");
        modeloTabela.addColumn("Adicional/Juros");
        modeloTabela.addColumn("Valor Total");
        modeloTabela.addColumn("Nome do Cliente");
        modeloTabela.addColumn("ID do Cliente");

        tabelaVendas = new JTable(modeloTabela);

        // Centralizar conteúdo nas células
        DefaultTableCellRenderer centralizadoRenderer = new DefaultTableCellRenderer();
        centralizadoRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Aplica o renderer centralizado para todas as colunas
        for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
            tabelaVendas.getColumnModel().getColumn(i).setCellRenderer(centralizadoRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaVendas);
        add(scrollPane, BorderLayout.CENTER);

        // Carregar as vendas na tabela
        carregarVendas();

        // Painel com os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(_ -> excluirVenda());

        painelBotoes.add(botaoExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // Listener para seleção de linha (captura apenas o UUID)
        tabelaVendas.getSelectionModel().addListSelectionListener(_ -> {
            int selectedRow = tabelaVendas.getSelectedRow();
            if (selectedRow != -1) {
                vendaSelecionadaId = UUID.fromString(modeloTabela.getValueAt(selectedRow, 0).toString());
            }
        });
    }

    boolean carregarVendas() {
        List<Venda> vendas = dbManagerVenda.listarTodasVendas();

        // Limpa os dados existentes na tabela
        modeloTabela.setRowCount(0);

        // Adiciona as vendas na tabela
        for (Venda venda : vendas) {
            modeloTabela.addRow(venda.linhaFormatada());
        }

        return true;
    }

    private void excluirVenda() {
        if (vendaSelecionadaId == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma venda para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar exclusão
        int confirmacao = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esta venda?", "Excluir venda", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {

            Venda compra = dbManagerVenda.encontrarVendaPorId(vendaSelecionadaId);

            if (compra == null) {
                JOptionPane.showMessageDialog(null, "Venda não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Remover a venda do banco de dados
            dbManagerVenda.removerVenda(vendaSelecionadaId);

            // Remover a venda da tabela
            carregarVendas();
            JOptionPane.showMessageDialog(this, "Venda excluída com sucesso!");
        }

    }
}
