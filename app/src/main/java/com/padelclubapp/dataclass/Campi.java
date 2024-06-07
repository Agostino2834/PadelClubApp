package com.padelclubapp.dataclass;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Campi {

    private String nome;
    private String tipoCampo;
    private boolean disponibile;
    private int nPostiOccupati;
    private ArrayList<String> idUsers;

    public Campi(){}

    public Campi(String nome, String tipoCampo, boolean disponibile, int nPostiOccupati) {
        this.nome = nome;
        this.tipoCampo = tipoCampo;
        this.disponibile = disponibile;
        this.nPostiOccupati = nPostiOccupati;
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

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public int getnPostiOccupati() {
        return nPostiOccupati;
    }

    public void setnPostiOccupati(int nPostiOccupati) {
        this.nPostiOccupati = nPostiOccupati;
    }

    public ArrayList<String> getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(ArrayList<String> idUsers) {
        this.idUsers = idUsers;
    }
}
