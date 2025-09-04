package view.estoque;

import com.toedter.calendar.JDateChooser;
import config.Utilities;
import database.DBManagerCliente;
import database.DBManagerEstoque;
import database.DBManagerProduto;
import database.DBManagerVenda;
import model.Cliente;
import model.Produto;
import model.Venda;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class TelaRegistrarVenda extends JPanel{

    Utilities utilities = new Utilities();
    private DBManagerVenda dbManagerVenda = new DBManagerVenda();

    private final JDateChooser campoData;
    private final JComboBox<String> comboTipoInfoProduto;
    private final JTextField campoInfoProduto;
    private final JTextField campoQuantidade;
    private final JTextField campoAdicional;
    private final JComboBox<String> comboTipoInfoCliente;
    private final JTextField campoInfoCliente;

    public TelaRegistrarVenda() {
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels e campos de texto
        add(new JLabel("Data da Venda:"));
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

        add(new JLabel("Adicional/Juros:"));
        campoAdicional = new JTextField("Digite o adicional.");
        utilities.addFocusListenerPlaceholder(campoAdicional, "Digite o adicional.");
        utilities.setNumericOnly(campoAdicional);
        add(campoAdicional);

        add(new JLabel("Buscar cliente por:"));
        comboTipoInfoCliente = new JComboBox<>(new String[]{
                "ID",
                "Nome Completo",
        });
        add(comboTipoInfoCliente);

        add(new JLabel("Informação do Cliente:"));
        campoInfoCliente = new JTextField("Digite a informação do cliente.");
        utilities.addFocusListenerPlaceholder(campoInfoCliente, "Digite a informação do cliente.");
        add(campoInfoCliente);

        // Botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(_ -> salvarVenda());
        painelBotoes.add(botaoSalvar);

        add(painelBotoes);
    }

    private void salvarVenda() {
        try {
            Date data = campoData.getDate();
            int quantidade = Integer.parseInt(campoQuantidade.getText());
            String adicional = campoAdicional.getText();
            Produto produto = null;
            Cliente cliente = null;

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

            if (comboTipoInfoCliente.getSelectedItem() == "ID") {
                cliente = new DBManagerCliente().encontrarClientePorId(UUID.fromString(campoInfoCliente.getText()));

            } else {
                cliente = new DBManagerCliente().encontrarClientePorNome(campoInfoCliente.getText());

            }

            if (cliente == null) {
                JOptionPane.showMessageDialog(null, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (data == null ||
                    quantidade == 0) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (adicional.equals("0") || adicional.equals("Digite o adicional.") || adicional.isEmpty()) {
                adicional = "0";
            }

            Venda venda = new Venda(campoData, produto, quantidade, cliente, new BigDecimal(adicional));
            if (!new DBManagerEstoque().produtoDisponivelParaVenda(venda.getProduto().getId(), venda.getQuantidade())) {
                JOptionPane.showMessageDialog(null, "Quantidade em estoque insuficiente para venda!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dbManagerVenda.registrarVenda(venda);

            JOptionPane.showMessageDialog(null, "Venda registrada com sucesso!");
            limparCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao registrar venda: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoData.setDate(null);
        comboTipoInfoProduto.setSelectedIndex(0);
        comboTipoInfoCliente.setSelectedIndex(0);
        utilities.resetarCampo(campoInfoProduto, "Digite a informação do produto.");
        utilities.resetarCampo(campoInfoCliente, "Digite a informação do cliente.");
        utilities.resetarCampo(campoQuantidade, "Digite a quantidade.");
    }

}
