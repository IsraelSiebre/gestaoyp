package view;

import database.*;
import view.cliente.TelaCadastrarCliente;
import view.cliente.TelaListarClientes;
import view.estoque.TelaCadastrarProduto;
import view.estoque.TelaCatalogoProdutos;
import view.estoque.TelaRegistrarCompra;
import view.estoque.TelaRegistrarVenda;
import view.pedidos.TelaListarEncomendas;
import view.pedidos.TelaRegistrarEncomendas;
import view.relatorios.TelaEstoque;
import view.relatorios.TelaHistoricoCompras;
import view.relatorios.TelaHistoricoVendas;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class TelaPrincipal extends JFrame {

    private JPanel painelPrincipal; // painel onde as telas aparecerão

    public TelaPrincipal() {
        setTitle("Sistema de Gestão - YP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Carregar o ícone
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.ico")));
        setIconImage(icon.getImage());

        // Criar o menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuEstoque = new JMenu("Estoque");
        JMenu menuClientes = new JMenu("Clientes");
        JMenu menuRelatorios = new JMenu("Relatórios");
        JMenu menuPedidos = new JMenu("Pedidos");
        JMenu menuBackup = new JMenu("Backup");

        JMenuItem itemCadastroProduto = new JMenuItem("Cadastro de Produto");
        JMenuItem itemCatalogoProduto = new JMenuItem("Catálogo de Produtos");
        JMenuItem itemRegistrarCompra = new JMenuItem("Registrar Compra");
        JMenuItem itemRegistrarVenda = new JMenuItem("Registrar Venda");
        menuEstoque.add(itemCadastroProduto);
        menuEstoque.add(itemCatalogoProduto);
        menuEstoque.add(itemRegistrarCompra);
        menuEstoque.add(itemRegistrarVenda);

        JMenuItem itemCadastrarCliente = new JMenuItem("Cadastrar Cliente");
        JMenuItem itemListarCliente = new JMenuItem("Listar Clientes");
        menuClientes.add(itemCadastrarCliente);
        menuClientes.add(itemListarCliente);

        JMenuItem itemRelatorioEstoque = new JMenuItem("Relação de Estoque");
        JMenuItem itemHistoricoVendas = new JMenuItem("Histórico de Vendas");
        JMenuItem itemHistoricoCompras = new JMenuItem("Histórico de Compras");
        menuRelatorios.add(itemRelatorioEstoque);
        menuRelatorios.add(itemHistoricoVendas);
        menuRelatorios.add(itemHistoricoCompras);

        JMenuItem itemRegistrarEncomenda = new JMenuItem("Registrar Encomenda");
        JMenuItem itemListarEncomenda = new JMenuItem("Listar Encomendas");
        menuPedidos.add(itemRegistrarEncomenda);
        menuPedidos.add(itemListarEncomenda);

        JMenuItem itemFazerBackup = new JMenuItem("Fazer Backup");
        JMenuItem itemCarregarBackup = new JMenuItem("Carregar Backup");
        menuBackup.add(itemFazerBackup);
        menuBackup.add(itemCarregarBackup);

        menuBar.add(menuEstoque);
        menuBar.add(menuClientes);
        menuBar.add(menuRelatorios);
        menuBar.add(menuPedidos);
        menuBar.add(menuBackup);
        setJMenuBar(menuBar);

        // Painel principal que vai trocar de tela
        painelPrincipal = new JPanel(new BorderLayout());

        // Encapsular o painel principal em um JScrollPane
        JScrollPane scrollPane = new JScrollPane(painelPrincipal);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // Ação do item "Cadastro de Produto"
        itemCadastroProduto.addActionListener(_ -> abrirTela(new TelaCadastrarProduto()));

        // Ação do item "Catalogo de Produtos"
        itemCatalogoProduto.addActionListener(_ -> {if (!new DBManagerProduto().listarTodosProdutos().isEmpty()) {abrirTela(new TelaCatalogoProdutos());}
        else { JOptionPane.showMessageDialog(this, "Não há Produtos Cadastrados!", "Erro", JOptionPane.ERROR_MESSAGE);}});

        // Ação do item "Cadastro de Cliente"
        itemCadastrarCliente.addActionListener(_ -> abrirTela(new TelaCadastrarCliente()));

        // Ação do item "Lista de Clientes"
        itemListarCliente.addActionListener(_ -> {if (!new DBManagerCliente().listarTodosClientes().isEmpty()) {abrirTela(new TelaListarClientes());}
        else { JOptionPane.showMessageDialog(this, "Não há Clientes Cadastrados!", "Erro", JOptionPane.ERROR_MESSAGE);}});

        // Ação do item "Registrar Compra"
        itemRegistrarCompra.addActionListener(_ -> abrirTela(new TelaRegistrarCompra()));

        // Ação do item "Historico de Compras"
        itemHistoricoCompras.addActionListener(_ -> {if (!new DBManagerCompra().listarTodasCompras().isEmpty()) {abrirTela(new TelaHistoricoCompras());}
        else { JOptionPane.showMessageDialog(this, "Não há Compras Registradas!", "Erro", JOptionPane.ERROR_MESSAGE);}});

        // Ação do item "Relatorio Estoque"
        itemRelatorioEstoque.addActionListener(_ -> {if (!new DBManagerProduto().listarTodosProdutosEmEstoque().isEmpty()) {abrirTela(new TelaEstoque());}
        else { JOptionPane.showMessageDialog(this, "Não há Produtos em Estoque!", "Erro", JOptionPane.ERROR_MESSAGE);}});

        // Ação do item "Registro de Venda"
        itemRegistrarVenda.addActionListener(_ -> abrirTela(new TelaRegistrarVenda()));

        // Ação do item "Historico de Vendas"
        itemHistoricoVendas.addActionListener(_ -> {if (!new DBManagerVenda().listarTodasVendas().isEmpty()) {abrirTela(new TelaHistoricoVendas());}
        else { JOptionPane.showMessageDialog(this, "Não há Vendas Registradas!", "Erro", JOptionPane.ERROR_MESSAGE);}});

        // Ação do item "Registro de Encomenda"
        itemRegistrarEncomenda.addActionListener(_ -> abrirTela(new TelaRegistrarEncomendas()));

        // Ação do item "Listar Encomendas"
        itemListarEncomenda.addActionListener(_ -> {if (!new DBManagerEncomenda().listarTodasEncomendas().isEmpty()) {abrirTela(new TelaListarEncomendas());}
        else { JOptionPane.showMessageDialog(this, "Não há Encomendas Registradas!", "Erro", JOptionPane.ERROR_MESSAGE);}});

        // Ação do item "Fazer Backup"
        itemFazerBackup.addActionListener(_ -> new DBManager().fazerBackup());

        // Ação do item "Carregar"
        itemCarregarBackup.addActionListener(_ -> new DBManager().carregarBackup());

        setVisible(true);
    }

    private void abrirTela(JPanel novaTela) {
        painelPrincipal.removeAll();
        painelPrincipal.add(novaTela);
        painelPrincipal.revalidate();
        painelPrincipal.repaint();
    }
}
