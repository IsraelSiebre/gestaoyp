package view.relatorios;

import database.DBManagerProduto;
import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.io.File;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;


public class TelaEstoque extends JPanel {

    private DBManagerProduto dbManagerProduto = new DBManagerProduto();
    private final JTable tabelaEstoque;
    private final DefaultTableModel modeloTabela;

    public TelaEstoque() {
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("Código do Produto");
        modeloTabela.addColumn("Nome do Produto");
        modeloTabela.addColumn("Quantidade em Estoque");
        modeloTabela.addColumn("Valor Custo");
        modeloTabela.addColumn("ID do Produto");

        tabelaEstoque = new JTable(modeloTabela);

        DefaultTableCellRenderer centralizadoRenderer = new DefaultTableCellRenderer();
        centralizadoRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < modeloTabela.getColumnCount(); i++) {
            tabelaEstoque.getColumnModel().getColumn(i).setCellRenderer(centralizadoRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabelaEstoque);
        add(scrollPane, BorderLayout.CENTER);

        carregarEstoque();

        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton botaoExportar = new JButton("Exportar PDF");
        botaoExportar.addActionListener(_ -> exportarRelatorioEstoquePDF());
        painelBotoes.add(botaoExportar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    boolean carregarEstoque() {
        List<Produto> produtos = dbManagerProduto.listarTodosProdutosEmEstoque();
        modeloTabela.setRowCount(0);
        for (Produto produto : produtos) {
            modeloTabela.addRow(produto.linhaFormatadaEstoque());
        }
        return true;
    }

    private void exportarRelatorioEstoquePDF() {
        List<Produto> produtos = dbManagerProduto.listarTodosProdutosEmEstoque();

        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum produto em estoque para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar relatório de estoque");
        fileChooser.setSelectedFile(new File("relatorio_estoque.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDType1Font font = PDType1Font.HELVETICA;
            float fontSize = 10;

            float margin = 50;
            float yStart = 750;
            float rowHeight = 20;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;

            float[] columnWidths = {tableWidth * 0.25f, tableWidth * 0.45f, tableWidth * 0.15f, tableWidth * 0.15f};
            String[] headers = {"Código", "Nome", "Quantidade", "Valor Custo"};

            float yPosition = yStart;
            contentStream.setFont(font, fontSize);

            // Cabeçalho
            float nextX = margin;
            for (int i = 0; i < headers.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(nextX + 2, yPosition - 15);
                contentStream.showText(headers[i]);
                contentStream.endText();
                nextX += columnWidths[i];
            }

            nextX = margin;
            for (float colWidth : columnWidths) {
                contentStream.addRect(nextX, yPosition - rowHeight, colWidth, rowHeight);
                nextX += colWidth;
            }

            yPosition -= rowHeight;

            for (Produto produto : produtos) {
                String[] valores = {
                        produto.getCodigo(),
                        produto.getNome(),
                        String.valueOf(produto.getQuantidadeEmEstoque()),
                        String.format("%.2f", produto.getCusto())
                };

                nextX = margin;
                for (int i = 0; i < valores.length; i++) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(nextX + 2, yPosition - 15);
                    contentStream.showText(valores[i]);
                    contentStream.endText();

                    contentStream.addRect(nextX, yPosition - rowHeight, columnWidths[i], rowHeight);
                    nextX += columnWidths[i];
                }

                yPosition -= rowHeight;

                if (yPosition < 50) {
                    contentStream.stroke();
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
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
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
