package com.padelclubapp.item;

public class GiornoItem {
    private String giornoSettimanaNumero;
    private String giornoSettimana;
    private String mese;

    private String data;

    public GiornoItem(String data, String giornoSettimanaNumero, String giornoSettimana, String mese) {
        this.data = data;
        this.giornoSettimanaNumero = giornoSettimanaNumero;
        this.giornoSettimana = giornoSettimana;
        this.mese = mese;
    }

    public String getData() {
        return this.data;
    }

    public String getGiornoSettimanaNumero() {
        return this.giornoSettimanaNumero;
    }

    public String getGiornoSettimana() {
        return this.giornoSettimana;
    }

    public String getMese() {
        return this.mese;
    }
}
