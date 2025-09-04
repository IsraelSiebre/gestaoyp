package view.cliente;

import config.Utilities;
import database.DBManagerCliente;
import model.Cliente;

import javax.swing.*;
import java.awt.*;

public class TelaCadastrarCliente extends JPanel{

    Utilities utilities = new Utilities();
    private DBManagerCliente dataBaseManager = new DBManagerCliente();

    private final JTextField campoNomeCompleto;
    private final JTextField campoTelefone;
    private final JTextField campoEmail;

    public TelaCadastrarCliente() {
        setLayout(new GridLayout(6, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Nome Completo do Cliente:"));
        campoNomeCompleto = new JTextField("Digite o nome do cliente.");
        utilities.addFocusListenerPlaceholder(campoNomeCompleto, "Digite o nome completo do cliente.");
        add(campoNomeCompleto);

        add(new JLabel("Telefone do Cliente:"));
        campoTelefone = new JTextField("Digite o telefone do cliente.");
        utilities.addFocusListenerPlaceholder(campoTelefone, "Digite o telefone do cliente.");
        add(campoTelefone);

        add(new JLabel("Email do Cliente:"));
        campoEmail = new JTextField("Digite o email do cliente.");
        utilities.addFocusListenerPlaceholder(campoEmail, "Digite o email do cliente.");
        add(campoEmail);


        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.addActionListener(e -> salvarCliente());
        painelBotoes.add(botaoSalvar);

        add(painelBotoes);

    }


    private void salvarCliente() {
        try {
            String nomeCompleto = campoNomeCompleto.getText();
            String telefone = campoTelefone.getText();
            String email = campoEmail.getText();

            if (nomeCompleto.isEmpty() || nomeCompleto.equals("Digite o nome completo do cliente.") ||
                    telefone.isEmpty() || telefone.equals("Digite o telefone do cliente.") ||
                    email.isEmpty() || email.equals("Digite o email do cliente.")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente cliente = new Cliente(nomeCompleto, telefone, email);
            dataBaseManager.cadastrarCliente(cliente);

            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
            limparCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar o cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        utilities.resetarCampo(campoNomeCompleto, "Digite o nome completo do cliente.");
        utilities.resetarCampo(campoTelefone, "Digite o telefone do cliente.");
        utilities.resetarCampo(campoEmail, "Digite o email do cliente.");
    }


}
