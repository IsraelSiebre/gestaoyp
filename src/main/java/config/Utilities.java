package config;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;

public class Utilities {
    public static final String NOME_BANCO_DE_DADOS = "databaseyp.db";
    public static final String CAMINHO_BANCO_DE_DADOS = System.getProperty("user.dir") + File.separator + NOME_BANCO_DE_DADOS;

    public void setNumericOnly(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new MonetaryDocumentFilter(campo));
    }

    // Filtro com suporte a placeholder
    private static class MonetaryDocumentFilter extends DocumentFilter {
        private final JTextField campo;

        public MonetaryDocumentFilter(JTextField campo) {
            this.campo = campo;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offset, string);
            if (isValidInput(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(atual);
            sb.replace(offset, offset + length, text);
            if (isValidInput(sb.toString()) || campo.getForeground().equals(Color.GRAY)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValidInput(String input) {
            if (input.isEmpty()) return true;
            return input.matches("^\\d*(\\.\\d{0,2})?$");
        }
    }

    public void addFocusListenerPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(Color.GRAY);
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    public void resetarCampo(JTextField campo, String placeholder) {
        campo.setText(placeholder);
        campo.setForeground(Color.GRAY);
    }

    public boolean verificaSeTemDB(String nomeArquivoBanco, String mensagemDeErro) {
        File arquivoOrigem = new File(nomeArquivoBanco);

        if (!arquivoOrigem.exists()) {
            JOptionPane.showMessageDialog(null, mensagemDeErro, "Erro", JOptionPane.ERROR_MESSAGE);

            return false;
        }
        return true;
    }

    public String formatarDataParaBR(String dataIso) {
        try {
            SimpleDateFormat formatoIso = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoBR = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date data = formatoIso.parse(dataIso);
            return formatoBR.format(data);
        } catch (Exception e) {
            return dataIso; // Se der erro, retorna a original mesmo
        }
    }


}
