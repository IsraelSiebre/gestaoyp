package database;

import config.Utilities;

import javax.swing.*;
import java.io.*;

public class DBManager {

    public void fazerBackup() {
        File arquivoOrigem = new File(Utilities.NOME_BANCO_DE_DADOS);

        if (!verificaSeTemDB(Utilities.NOME_BANCO_DE_DADOS, "O arquivo do banco de dados não foi encontrado!")) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Escolha onde salvar o backup do banco de dados");

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File arquivoDestino = fileChooser.getSelectedFile();

            // Adiciona extensão .db se o usuário não colocar
            if (!arquivoDestino.getName().endsWith(".db")) {
                arquivoDestino = new File(arquivoDestino.getAbsolutePath() + ".db");
            }

            try (InputStream in = new FileInputStream(arquivoOrigem);
                 OutputStream out = new FileOutputStream(arquivoDestino)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                JOptionPane.showMessageDialog(null, "Backup salvo com sucesso!");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar o backup: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void carregarBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o arquivo de backup do banco de dados");

        int opcao = fileChooser.showOpenDialog(null);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();

            File pastaDestino = new File(Utilities.CAMINHO_BANCO_DE_DADOS);

            // Define o caminho completo do arquivo de destino
            File arquivoDestino = new File(pastaDestino, Utilities.NOME_BANCO_DE_DADOS);

            try (InputStream in = new FileInputStream(arquivoSelecionado);
                 OutputStream out = new FileOutputStream(arquivoDestino)) {

                byte[] buffer = new byte[1024];
                int bytesLidos;
                while ((bytesLidos = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesLidos);
                }

                JOptionPane.showMessageDialog(null,
                        "Backup restaurado com sucesso em:\n" + arquivoDestino.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao restaurar o backup: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public boolean verificaSeTemDB(String nomeArquivoBanco, String mensagemErro) {
        File arquivoOrigem = new File(nomeArquivoBanco);

        if (!arquivoOrigem.exists()) {
            JOptionPane.showMessageDialog(null, mensagemErro,
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

}
