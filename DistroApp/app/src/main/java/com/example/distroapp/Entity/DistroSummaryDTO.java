package com.example.distroapp.Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DTO per la rappresentazione semplificata delle distribuzioni Linux
 * dal endpoint /distribuzioni-summary del server
 */
public class DistroSummaryDTO implements Parcelable {

    private int id;
    private int idDistribuzione;  // ID della distribuzione nel DB principale
    private String nomeDisplay;
    private String descrizioneBreve;
    private String descrizioneDettaglio;
    private String icona;  // Emoji o nome icona
    private String coloreHex;  // Per colorare le card
    private String livelloDifficolta;  // "Principianti", "Intermedio", "Avanzato", "Esperto"
    private int punteggioPopolarita;  // 1-5 stelle

    // Flag di selezione per l'adapter
    private boolean isSelected = false;

    // Costruttore vuoto
    public DistroSummaryDTO() {}

    // Costruttore completo
    public DistroSummaryDTO(int id, int idDistribuzione, String nomeDisplay,
                            String descrizioneBreve, String descrizioneDettaglio,
                            String icona, String coloreHex, String livelloDifficolta,
                            int punteggioPopolarita) {
        this.id = id;
        this.idDistribuzione = idDistribuzione;
        this.nomeDisplay = nomeDisplay;
        this.descrizioneBreve = descrizioneBreve;
        this.descrizioneDettaglio = descrizioneDettaglio;
        this.icona = icona;
        this.coloreHex = coloreHex;
        this.livelloDifficolta = livelloDifficolta;
        this.punteggioPopolarita = punteggioPopolarita;
    }

    // Costruttore da Parcel
    protected DistroSummaryDTO(Parcel in) {
        id = in.readInt();
        idDistribuzione = in.readInt();
        nomeDisplay = in.readString();
        descrizioneBreve = in.readString();
        descrizioneDettaglio = in.readString();
        icona = in.readString();
        coloreHex = in.readString();
        livelloDifficolta = in.readString();
        punteggioPopolarita = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<DistroSummaryDTO> CREATOR = new Creator<DistroSummaryDTO>() {
        @Override
        public DistroSummaryDTO createFromParcel(Parcel in) {
            return new DistroSummaryDTO(in);
        }

        @Override
        public DistroSummaryDTO[] newArray(int size) {
            return new DistroSummaryDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(idDistribuzione);
        dest.writeString(nomeDisplay);
        dest.writeString(descrizioneBreve);
        dest.writeString(descrizioneDettaglio);
        dest.writeString(icona);
        dest.writeString(coloreHex);
        dest.writeString(livelloDifficolta);
        dest.writeInt(punteggioPopolarita);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    // Metodi per generare stelle di popolarità
    public String getStarsString() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= punteggioPopolarita) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    // Metodo per ottenere emoji predefinite se mancano
    public String getIconaOrDefault() {
        if (icona != null && !icona.isEmpty()) {
            return icona;
        }

        // Emoji predefinite basate sul nome
        String nome = nomeDisplay.toLowerCase();
        if (nome.contains("ubuntu")) return "🐧";
        if (nome.contains("fedora")) return "⚡";
        if (nome.contains("arch")) return "⚙️";
        if (nome.contains("debian")) return "🌀";
        if (nome.contains("mint")) return "🌿";
        if (nome.contains("opensuse")) return "🦎";
        if (nome.contains("centos") || nome.contains("rhel")) return "🔴";
        if (nome.contains("manjaro")) return "💚";
        if (nome.contains("elementary")) return "🎨";
        if (nome.contains("kali")) return "🐉";

        return "🐧"; // Default
    }

    // Metodo per ottenere colore predefinito
    public String getColoreHexOrDefault() {
        if (coloreHex != null && !coloreHex.isEmpty()) {
            return coloreHex;
        }

        // Colori predefiniti
        String nome = nomeDisplay.toLowerCase();
        if (nome.contains("ubuntu")) return "#E95420";
        if (nome.contains("fedora")) return "#294172";
        if (nome.contains("arch")) return "#1793D1";
        if (nome.contains("debian")) return "#A81D33";
        if (nome.contains("mint")) return "#87CF3E";
        if (nome.contains("opensuse")) return "#73BA25";
        if (nome.contains("centos")) return "#932279";
        if (nome.contains("manjaro")) return "#35BF5C";

        return "#2196F3"; // Default blue
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdDistribuzione() { return idDistribuzione; }
    public void setIdDistribuzione(int idDistribuzione) { this.idDistribuzione = idDistribuzione; }

    public String getNomeDisplay() { return nomeDisplay; }
    public void setNomeDisplay(String nomeDisplay) { this.nomeDisplay = nomeDisplay; }

    public String getDescrizioneBreve() { return descrizioneBreve; }
    public void setDescrizioneBreve(String descrizioneBreve) { this.descrizioneBreve = descrizioneBreve; }

    public String getDescrizioneDettaglio() { return descrizioneDettaglio; }
    public void setDescrizioneDettaglio(String descrizioneDettaglio) { this.descrizioneDettaglio = descrizioneDettaglio; }

    public String getIcona() { return icona; }
    public void setIcona(String icona) { this.icona = icona; }

    public String getColoreHex() { return coloreHex; }
    public void setColoreHex(String coloreHex) { this.coloreHex = coloreHex; }

    public String getLivelloDifficolta() { return livelloDifficolta; }
    public void setLivelloDifficolta(String livelloDifficolta) { this.livelloDifficolta = livelloDifficolta; }

    public int getPunteggioPopolarita() { return punteggioPopolarita; }
    public void setPunteggioPopolarita(int punteggioPopolarita) { this.punteggioPopolarita = punteggioPopolarita; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    @Override
    public String toString() {
        return "DistroSummaryDTO{" +
                "id=" + id +
                ", idDistribuzione=" + idDistribuzione +
                ", nomeDisplay='" + nomeDisplay + '\'' +
                ", descrizioneBreve='" + descrizioneBreve + '\'' +
                ", livelloDifficolta='" + livelloDifficolta + '\'' +
                ", punteggioPopolarita=" + punteggioPopolarita +
                ", isSelected=" + isSelected +
                '}';
    }
}