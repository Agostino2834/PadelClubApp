package com.padelclubapp.dataclass;

public class Utente {

    private String userId;
    private String fotoUtente;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String bio;
    private Double ranking;
    private Integer countRanking;
    private Double reputazione;
    private boolean admin;
    private boolean firstLogin;

    private boolean sospeso;
    private Long tempoSospensione;
    private boolean bandito;

    public Utente() {

    }

    public Utente(String userId, String fotoUtente, String nome, String cognome, String email, String password, String bio, Double ranking, Double reputazione) {
        this.userId = userId;
        this.fotoUtente = fotoUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.ranking = ranking;
        this.countRanking = 0;
        this.reputazione = reputazione;
        this.admin = false;
        this.firstLogin = true;
        this.bandito = false;
        this.sospeso = false;
        this.tempoSospensione = Long.valueOf(0);
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

    public boolean getAdmin() {
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

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFotoUtente() {
        return fotoUtente;
    }

    public void setFotoUtente(String fotoUtente) {
        this.fotoUtente = fotoUtente;
    }

    public Double getRanking() {
        return ranking;
    }

    public void setRanking(Double ranking) {
        this.ranking = ranking;
    }

    public Integer getCountRanking() {
        return countRanking;
    }

    public void setCountRanking() {
        this.countRanking++;
    }

    public Double getReputazione() {
        return reputazione;
    }

    public void setReputazione(Double reputazione) {
        this.reputazione = reputazione;
    }

    public boolean isSospeso() {
        return sospeso;
    }

    public void setSospeso(boolean sospeso) {
        this.sospeso = sospeso;
    }

    public boolean isBandito() {
        return bandito;
    }

    public void setBandito(boolean bandito) {
        this.bandito = bandito;
    }

    public Long getTempoSospensione() {
        return tempoSospensione;
    }

    public void setTempoSospensione(Long tempoSospensione) {
        this.tempoSospensione = tempoSospensione;
    }
}
