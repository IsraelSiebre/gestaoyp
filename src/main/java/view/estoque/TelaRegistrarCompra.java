package view.estoque;

import com.toedter.calendar.JDateChooser;
import config.Utilities;
import database.DBManagerCompra;
import database.DBManagerProduto;
import model.Compra;
import model.Produto;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.UUID;

public class TelaRegistrarCompra extends JPanel {
    Utilities utilities = new Utilities();
    private DBManagerCompra dbManagerCompra = new DBManagerCompra();

    private final JDateChooser campoData;
    private final JComboBox<String> comboTipoInfoProduto;
    private final JTextField campoInfoProduto;
    private final JTextField campoQuantidade;
    private final JTextField campoFornecedor;


    public TelaRegistrarCompra() {
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels e campos de texto
        add(new JLabel("Data da Compra:"));
        campoData = new JDateChooser();
        campoData.setDateFormatString("dd/MM/yyyy");
        add(campoData);


        add(new JLabel("Buscar produto por:"));
        comboTipoInfoProduto = new JComboBox<>(new String[]{
                "ID",
                "Código",
                "Nome"
        });
        add(comboTipoInfoProduto);

        add(new JLabel("Informação do Produto:"));
        campoInfoProduto = new JTextField("Digite a informação do produto.");
        utilities.addFocusListenerPlaceholder(campoInfoProduto, "Digite a informação do produto.");
        add(campoInfoProduto);

        add(new JLabel("Quantidade:"));
        campoQuantidade = new JTextField("Digite a quantidade.");
        utilities.addFocusListenerPlaceholder(campoQuantidade, "Digite a quantidade.");
        utilities.setNumericOnly(campoQuantidade);
        add(campoQuantidade);

        add(new JLabel("Fornecedor:"));
        campoFornecedor = new JTextField("Digite o fornecedor.");
        utilities.addFocusListenerPlaceholder(campoFornecedor, "Digite o fornecedor.");
        add(campoFornecedor);

        // Botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(_ -> salvarCompra());
        painelBotoes.add(botaoSalvar);

        add(painelBotoes);
    }

    private void salvarCompra() {
        try {
            Date data = campoData.getDate();
            int quantidade = Integer.parseInt(campoQuantidade.getText());
            String fornecedor = campoFornecedor.getText();
            Produto produto = null;

            if (comboTipoInfoProduto.getSelectedItem() == "Código") {
                produto = new DBManagerProduto().encontrarProdutoPorCodigo(campoInfoProduto.getText());

            } else if (comboTipoInfoProduto.getSelectedItem() == "Nome") {
                produto = new DBManagerProduto().encontrarProdutoPorNome(campoInfoProduto.getText());

            } else {
                produto = new DBManagerProduto().encontrarProdutoPorId(UUID.fromString(campoInfoProduto.getText()));

            }

            if (produto == null) {
                JOptionPane.showMessageDialog(null, "Produto não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (data == null ||
                    quantidade == 0 ||
                    fornecedor.equals("Digite o fornecedor.") || fornecedor.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Compra compra = new Compra(campoData, produto, quantidade, fornecedor);
            dbManagerCompra.registrarCompra(compra);

            JOptionPane.showMessageDialog(null, "Compra registrada com sucesso!");
            limparCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao registrar compra: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoData.setDate(null);
        comboTipoInfoProduto.setSelectedIndex(0);
        utilities.resetarCampo(campoInfoProduto, "Digite o código do produto");
        utilities.resetarCampo(campoQuantidade, "Digite o nome do produto");
        utilities.resetarCampo(campoFornecedor, "Digite o custo");
    }

}
