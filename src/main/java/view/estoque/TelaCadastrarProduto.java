package view.estoque;

import config.Utilities;
import database.DBManagerProduto;
import model.Produto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class TelaCadastrarProduto extends JPanel {

    Utilities utilities = new Utilities();
    private DBManagerProduto dbManagerProduto = new DBManagerProduto();

    private final JTextField campoCodigo;
    private final JTextField campoNome;
    private final JTextField campoCusto;
    private final JTextField campoPrecoVenda;
    private final JTextField campoMargemLucro;

    public TelaCadastrarProduto() {
        setLayout(new GridLayout(6, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Código do Produto:"));
        campoCodigo = new JTextField("Digite o código do produto");
        utilities.addFocusListenerPlaceholder(campoCodigo, "Digite o código do produto");
        add(campoCodigo);

        add(new JLabel("Nome do Produto:"));
        campoNome = new JTextField("Digite o nome do produto");
        utilities.addFocusListenerPlaceholder(campoNome, "Digite o nome do produto");
        add(campoNome);

        add(new JLabel("Custo:"));
        campoCusto = new JTextField("Digite o custo");
        utilities.setNumericOnly(campoCusto);
        utilities.addFocusListenerPlaceholder(campoCusto, "Digite o custo");
        add(campoCusto);

        add(new JLabel("Preço de Venda:"));
        campoPrecoVenda = new JTextField("Digite o preço de venda");
        utilities.setNumericOnly(campoPrecoVenda);
        utilities.addFocusListenerPlaceholder(campoPrecoVenda, "Digite o preço de venda");
        add(campoPrecoVenda);

        add(new JLabel("Margem de Lucro (%):"));
        campoMargemLucro = new JTextField();
        campoMargemLucro.setEditable(false);
        campoMargemLucro.setForeground(Color.BLUE);
        add(campoMargemLucro);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(e -> salvarProduto());
        painelBotoes.add(botaoSalvar);

        add(painelBotoes);

        campoCusto.addCaretListener(e -> calcularMargem());
        campoPrecoVenda.addCaretListener(e -> calcularMargem());
    }

    private void calcularMargem() {
        try {
            BigDecimal custo = new BigDecimal(campoCusto.getText());
            BigDecimal preco = new BigDecimal(campoPrecoVenda.getText());

            if (custo.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal margem = preco.subtract(custo).divide(custo, 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("100"));
                campoMargemLucro.setText(margem.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString());
            } else {
                campoMargemLucro.setText("0.00");
            }
        } catch (Exception e) {
            campoMargemLucro.setText("0.00");
        }
    }

    private void salvarProduto() {
        try {
            String codigo = campoCodigo.getText();
            String nome = campoNome.getText();
            String custo = campoCusto.getText();
            String preco = campoPrecoVenda.getText();
            String margem = campoMargemLucro.getText();

            if (codigo.isEmpty() || codigo.equals("Digite o código do produto") ||
                    nome.isEmpty() || nome.equals("Digite o nome do produto") ||
                    custo.equals("0") || custo.equals("Digite o custo") || custo.isEmpty() ||
                    preco.equals("0") || preco.equals("Digite o custo") || preco.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Produto produto = new Produto(codigo.trim(), nome.trim(), new BigDecimal(custo), new BigDecimal(preco), new BigDecimal(margem));
            dbManagerProduto.cadastrarProduto(produto);

            JOptionPane.showMessageDialog(null, "Produto salvo com sucesso!");
            limparCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        utilities.resetarCampo(campoCodigo, "Digite o código do produto");
        utilities.resetarCampo(campoNome, "Digite o nome do produto");
        utilities.resetarCampo(campoCusto, "Digite o custo");
        utilities.resetarCampo(campoPrecoVenda, "Digite o preço de venda");
        campoMargemLucro.setText("");
    }


}
