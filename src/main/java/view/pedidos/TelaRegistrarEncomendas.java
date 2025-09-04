package view.pedidos;

import com.toedter.calendar.JDateChooser;
import config.Utilities;
import database.*;
import model.Cliente;
import model.Encomenda;
import model.Produto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class TelaRegistrarEncomendas extends JPanel {

    Utilities utilities = new Utilities();
    private DBManagerEncomenda dbManagerEncomenda = new DBManagerEncomenda();

    private final JDateChooser campoDataPedido;
    private final JDateChooser campoDataEntrega;
    private final JComboBox<String> comboTipoInfoProduto;
    private final JTextField campoInfoProduto;
    private final JTextField campoQuantidade;
    private final JTextField campoAdicional;
    private final JComboBox<String> comboTipoInfoCliente;
    private final JTextField campoInfoCliente;

    public TelaRegistrarEncomendas() {
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Labels e campos de texto
        add(new JLabel("Data do Pedido:"));
        campoDataPedido = new JDateChooser();
        campoDataPedido.setDateFormatString("dd/MM/yyyy");
        add(campoDataPedido);

        add(new JLabel("Data da Entrega:"));
        campoDataEntrega = new JDateChooser();
        campoDataEntrega.setDateFormatString("dd/MM/yyyy");
        add(campoDataEntrega);


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
        botaoSalvar.addActionListener(_ -> salvarEncomenda());
        painelBotoes.add(botaoSalvar);

        add(painelBotoes);
    }

    private void salvarEncomenda() {
        try {
            Date dataPedido = campoDataPedido.getDate();
            Date dataEntrega = campoDataEntrega.getDate();
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

            int quantidade = Integer.parseInt(campoQuantidade.getText());

            if (dataPedido == null ||
                    dataEntrega == null ||
                    quantidade == 0) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (adicional.equals("0") || adicional.equals("Digite o adicional.") || adicional.isEmpty()) {
                adicional = "0";
            }

            Encomenda encomenda = new Encomenda(campoDataPedido, campoDataEntrega, produto, quantidade, cliente, new BigDecimal(adicional));

            dbManagerEncomenda.registrarEncomenda(encomenda);

            JOptionPane.showMessageDialog(null, "Encomenda registrada com sucesso!");
            limparCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Preencha os campos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.out.println("Erro ao registrar encomenda: " + e.getMessage());
        }
    }

    private void limparCampos() {
        campoDataPedido.setDate(null);
        campoDataEntrega.setDate(null);
        comboTipoInfoProduto.setSelectedIndex(0);
        comboTipoInfoCliente.setSelectedIndex(0);
        utilities.resetarCampo(campoInfoProduto, "Digite a informação do produto.");
        utilities.resetarCampo(campoInfoCliente, "Digite a informação do cliente.");
        utilities.resetarCampo(campoQuantidade, "Digite a quantidade.");
    }

}
