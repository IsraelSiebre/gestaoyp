package model;

import lombok.Getter;

import java.util.UUID;


@Getter
public class Cliente {

    private UUID id;
    private String nomeCompleto;
    private String telefone;
    private String email;

    public Cliente(String nomeCompleto, String telefone, String email) {
        this.id = UUID.randomUUID();
        this.nomeCompleto = nomeCompleto;
        this.telefone = telefone;
        this.email = email;
    }

    public Cliente(UUID id ,String nomeCompleto, String telefone, String email) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.telefone = telefone;
        this.email = email;
    }

    public Object[] linhaFormatada() {

        return new Object[]{
                this.id.toString(),
                this.nomeCompleto,
                this.telefone,
                this.email
        };
    }
}
