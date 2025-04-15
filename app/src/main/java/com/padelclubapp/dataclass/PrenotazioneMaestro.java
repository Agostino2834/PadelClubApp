package com.padelclubapp.dataclass;

public class PrenotazioneMaestro {

    private String idPrenotazione;
    private String idMaestro;
    private String idCampo;
    private String idUtente;
    private String data;
    private String ora;


    public PrenotazioneMaestro() {
    }

    public PrenotazioneMaestro(String idPrenotazione, String idMaestro, String idCampo, String idUtente, String data, String ora) {
        this.idPrenotazione = idPrenotazione;
        this.idMaestro = idMaestro;
        this.idCampo = idCampo;
        this.idUtente = idUtente;
        this.data = data;
        this.ora = ora;
    }

    public String getIdMaestro() {
        return idMaestro;
    }

    public void setIdMaestro(String idMaestro) {
        this.idMaestro = idMaestro;
    }

    public String getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(String idCampo) {
        this.idCampo = idCampo;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(String idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }


}
