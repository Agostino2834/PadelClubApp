package com.padelclubapp.dataclass;

public class Campo {
    private String id;
    private String nome;
    private String tipoCampo;

    public Campo() {
    }

    public Campo(String id, String nome, String tipoCampo) {
        this.id = id;
        this.nome = nome;
        this.tipoCampo = tipoCampo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(String tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
