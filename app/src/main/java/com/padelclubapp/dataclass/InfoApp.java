package com.padelclubapp.dataclass;

public class InfoApp {
    private String nome;
    private String logo;
    private int nCampi;

    public InfoApp() {

    }

    public InfoApp(String logo, String nome, int nCampi) {
        this.nome = nome;
        this.logo = logo;
        this.nCampi = nCampi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getnCampi() {
        return nCampi;
    }

    public void setnCampi(int nCampi) {
        this.nCampi = nCampi;
    }
}
