package com.padelclubapp.dataclass;

public class Prenotazione {

    private String idPrenotazione;
    private String idCampo;
    private String data;
    private String ora;
    private Boolean disponibile;
    private Integer nPosti;

    public Prenotazione() {
    }

    public Prenotazione(String idPrenotazione, String idCampo, String data, String ora, Boolean disponibile) {
        this.idPrenotazione = idPrenotazione;
        this.idCampo = idCampo;
        this.data = data;
        this.ora = ora;
        this.disponibile = disponibile;
        this.nPosti = 1;
    }

    public String getIdPrenotazione() {
        return idPrenotazione;
    }

    public void setIdPrenotazione(String idPrenotazione) {
        this.idPrenotazione = idPrenotazione;
    }

    public String getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(String idCampo) {
        this.idCampo = idCampo;
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

    public Boolean getDisponibile() {
        return disponibile;
    }

    public void setDisponibile(Boolean disponibile) {
        this.disponibile = disponibile;
    }

    public Integer getnPosti() {
        return nPosti;
    }

    public void setnPosti() {
        nPosti++;
    }
}
