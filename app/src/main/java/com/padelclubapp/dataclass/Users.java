package com.padelclubapp.dataclass;

public class Users {
    private String userId;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Boolean admin;

    public Users() {

    }

    public Users(String userId, String nome, String cognome, String email, String password, Boolean admin) {
        this.userId = userId;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
