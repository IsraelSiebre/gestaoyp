package model;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

@Setter
@Getter
public class Produto {

    private UUID id;
    private String codigo;
    private String nome;
    private BigDecimal custo;
    private BigDecimal precoDeVenda;
    private BigDecimal margemDeLucro; //calculo automatico
    private int quantidadeEmEstoque;

    public Produto(String codigo, String nome, BigDecimal custo, BigDecimal precoDeVenda, BigDecimal margemDeLucro) {
        this.id = UUID.randomUUID();
        this.codigo = codigo;
        this.nome = nome;
        this.custo = custo;
        this.precoDeVenda = precoDeVenda;
        this.margemDeLucro = margemDeLucro;
    }

    public Produto(UUID id, String codigo, String nome, BigDecimal custo, BigDecimal precoDeVenda, BigDecimal margemDeLucro) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.custo = custo;
        this.precoDeVenda = precoDeVenda;
        this.margemDeLucro = margemDeLucro;
    }


    public Object[] linhaFormatada() {

        NumberFormat formatoMoeda = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setMaximumFractionDigits(2);

        return new Object[]{
                this.id.toString(),
                this.codigo,
                this.nome,
                "R$ " + formatoMoeda.format(this.custo),
                "R$ " + formatoMoeda.format(this.precoDeVenda),
                this.margemDeLucro + "%",
                this.quantidadeEmEstoque
        };
    }

    public Object[] linhaFormatadaEstoque() {

        NumberFormat formatoMoeda = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        formatoMoeda.setMinimumFractionDigits(2);
        formatoMoeda.setMaximumFractionDigits(2);

        return new Object[]{
                this.codigo,
                this.nome,
                this.quantidadeEmEstoque,
                "R$ " + formatoMoeda.format(this.custo),
                this.id.toString()
        };
    }

}
