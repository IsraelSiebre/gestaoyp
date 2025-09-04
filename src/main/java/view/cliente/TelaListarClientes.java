package view.cliente;

import database.DBManagerCliente;
import model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class TelaListarClientes extends JPanel {

    private DBManagerCliente dataBaseManager = new DBManagerCliente();
    private final JTable tabelaClientes;
    private final DefaultTableModel modeloTabela;
    private UUID clienteSelecionadoId; // Apenas o UUID do cliente selecionado

    public TelaListarClientes() {
        setLayout(new BorderLayout());

        // Criar a tabela
        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Nome Completo");
        modeloTabela.addColumn("Telefone");
        modeloTabela.addColumn("Email");

        tabelaClientes = new JTable(modeloTabela);

        // Centralizar conteúdo nas células
        DefaultTableCellRenderer centralizadoRenderer = new DefaultTableCellRenderer();
        centralizadoRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Aplica o renderer centralizado para todas as colunas
        for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
            tabelaClientes.getColumnModel().getColumn(i).setCellRenderer(centralizadoRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // Carregar os Clientes na tabela
        carregarClientes();

        // Painel com os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(_ -> excluirCliente());

        painelBotoes.add(botaoExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // Listener para seleção de linha (captura apenas o UUID)
        tabelaClientes.getSelectionModel().addListSelectionListener(_ -> {
            int selectedRow = tabelaClientes.getSelectedRow();
            if (selectedRow != -1) {
                clienteSelecionadoId = UUID.fromString(modeloTabela.getValueAt(selectedRow, 0).toString());
            }
        });
    }

    boolean carregarClientes() {
        List<Cliente> clientes = dataBaseManager.listarTodosClientes();

        // Limpa os dados existentes na tabela
        modeloTabela.setRowCount(0);

        // Adiciona os lançamentos na tabela
        for (Cliente cliente : clientes) {
            modeloTabela.addRow(cliente.linhaFormatada());
        }

        return true;
    }

    private void excluirCliente() {
        if (clienteSelecionadoId == null) {
            JOptionPane.showMessageDialog(null, "Selecione um cliente para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar exclusão
        int confirmacao = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir este cliente?", "Excluir cliente", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {

            DBManagerCliente dataBaseManager = new DBManagerCliente();
            Cliente cliente = dataBaseManager.encontrarClientePorId(clienteSelecionadoId);

            if (cliente == null) {
                JOptionPane.showMessageDialog(null, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Remover o cliente do banco de dados
            dataBaseManager.removerCliente(clienteSelecionadoId);

            // Remover o cliente da tabela
            carregarClientes();
            JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
        }
    }
}
