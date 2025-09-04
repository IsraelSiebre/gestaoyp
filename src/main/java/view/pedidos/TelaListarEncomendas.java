package view.pedidos;

import database.DBManagerEncomenda;
import model.Encomenda;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class TelaListarEncomendas extends JPanel{

    private DBManagerEncomenda dbManagerEncomenda = new DBManagerEncomenda();
    private final JTable tabelaVendas;
    private final DefaultTableModel modeloTabela;
    private UUID encomendaSelecionadaId;

    public TelaListarEncomendas() {
        setLayout(new BorderLayout());

        // Criar a tabela
        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Data do Pedido");
        modeloTabela.addColumn("Data da Entrega");
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
        carregarEncomendas();

        // Painel com os botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        JButton botaoExcluir = new JButton("Excluir");
        botaoExcluir.addActionListener(_ -> excluirEncomenda());

        JButton botaoExportar = new JButton("Exportar Relatório");
        botaoExportar.addActionListener(_ -> exportarRelatorioPDF());

        painelBotoes.add(botaoExportar);

        painelBotoes.add(botaoExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // Listener para seleção de linha (captura apenas o UUID)
        tabelaVendas.getSelectionModel().addListSelectionListener(_ -> {
            int selectedRow = tabelaVendas.getSelectedRow();
            if (selectedRow != -1) {
                encomendaSelecionadaId = UUID.fromString(modeloTabela.getValueAt(selectedRow, 0).toString());
            }
        });
    }

    boolean carregarEncomendas() {
        List<Encomenda> encomendas = dbManagerEncomenda.listarTodasEncomendas();

        // Limpa os dados existentes na tabela
        modeloTabela.setRowCount(0);

        // Adiciona as encomendas na tabela
        for (Encomenda encomenda : encomendas) {
            modeloTabela.addRow(encomenda.linhaFormatada());
        }

        return true;
    }

    private void excluirEncomenda() {
        if (encomendaSelecionadaId == null) {
            JOptionPane.showMessageDialog(null, "Selecione uma encomenda para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmar exclusão
        int confirmacao = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esta encomenda?", "Excluir encomenda", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {

            Encomenda encomenda = dbManagerEncomenda.encontrarEncomendaPorId(encomendaSelecionadaId);

            if (encomenda == null) {
                JOptionPane.showMessageDialog(null, "Encomenda não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Remover a encomenda do banco de dados
            dbManagerEncomenda.removerEncomenda(encomendaSelecionadaId);

            // Remover a encomenda da tabela
            carregarEncomendas();
            JOptionPane.showMessageDialog(this, "Encomenda excluída com sucesso!");
        }

    }

    private void exportarRelatorioPDF() {
        List<Encomenda> encomendas = dbManagerEncomenda.listarTodasEncomendas();

        if (encomendas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma encomenda encontrada para exportar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Escolha onde salvar o relatório PDF");
        fileChooser.setSelectedFile(new java.io.File("relatorio_encomendas.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".pdf");
        }

        try (org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDType1Font font = PDType1Font.HELVETICA;
            float fontSize = 10;

            float margin = 50;
            float yStart = 750;
            float rowHeight = 20;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

            float[] columnWidths = {tableWidth * 0.4f, tableWidth * 0.4f, tableWidth * 0.2f};
            String[] headers = {"Produto", "Cliente", "Quantidade"};

            float yPosition = yStart;

            contentStream.setFont(font, fontSize);

            // Desenhar cabeçalho
            float nextX = margin;
            for (int i = 0; i < headers.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(nextX + 2, yPosition - 15);
                contentStream.showText(headers[i]);
                contentStream.endText();
                nextX += columnWidths[i];
            }

            // Desenhar bordas do cabeçalho
            nextX = margin;
            for (float colWidth : columnWidths) {
                contentStream.addRect(nextX, yPosition - rowHeight, colWidth, rowHeight);
                nextX += colWidth;
            }

            yPosition -= rowHeight;

            // Desenhar linhas
            for (Encomenda encomenda : encomendas) {
                String[] values = {
                        encomenda.getProduto().getNome(),
                        encomenda.getCliente().getNomeCompleto(),
                        String.valueOf(encomenda.getQuantidade())
                };

                nextX = margin;
                for (int i = 0; i < values.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(nextX + 2, yPosition - 15);
                    contentStream.showText(values[i]);
                    contentStream.endText();

                    contentStream.addRect(nextX, yPosition - rowHeight, columnWidths[i], rowHeight);
                    nextX += columnWidths[i];
                }

                yPosition -= rowHeight;

                if (yPosition < 50) {
                    // Nova página
                    contentStream.stroke();
                    contentStream.close();

                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(font, fontSize);
                    yPosition = yStart;
                }
            }

            contentStream.stroke();
            contentStream.close();

            document.save(fileToSave);
            JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso para: " + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }



}
