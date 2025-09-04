package model;
import com.toedter.calendar.JDateChooser;
import config.Utilities;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

@Getter
public class Compra {

    Utilities utilities = new Utilities();

    private final UUID id;
    private final String data;
    private final Produto produto;
    private final int quantidade;
    private final String fornecedor;

    public Compra(JDateChooser data, Produto produto, int quantidade, String fornecedor) {
        this.id = UUID.randomUUID();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        this.data = formato.format(data.getDate());
        this.produto = produto;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;

    }

    public Compra(UUID id, JDateChooser data, Produto produto, int quantidade, String fornecedor) {
        this.id = id;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        this.data = formato.format(data.getDate());
        this.produto = produto;
        this.quantidade = quantidade;
        this.fornecedor = fornecedor;

    }

    public Object[] linhaFormatada() {

        NumberFormat formatoMoeda = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setMaximumFractionDigits(2);

        BigDecimal valorTotal = new BigDecimal(String.valueOf(this.produto.getCusto())).multiply(BigDecimal.valueOf(this.quantidade));

        return new Object[]{
                this.id.toString(),
                utilities.formatarDataParaBR(this.data),
                this.produto.getCodigo(),
                this.produto.getNome(),
                this.quantidade,
                "R$ " + formatoMoeda.format(this.produto.getCusto()),
                "R$ " + formatoMoeda.format(valorTotal),
                this.fornecedor
        };
    }


}
