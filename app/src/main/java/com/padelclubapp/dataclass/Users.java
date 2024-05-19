package com.padelclubapp.dataclass;

public class Users {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String confermaPassword;
    private Boolean admin;

    public Users(String nome, String cognome, String email, String password, String confermaPassword, Boolean admin) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.confermaPassword = confermaPassword;
        this.admin = admin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfermaPassword() {
        return confermaPassword;
    }

    public void setConfermaPassword(String confermaPassword) {
        this.confermaPassword = confermaPassword;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
