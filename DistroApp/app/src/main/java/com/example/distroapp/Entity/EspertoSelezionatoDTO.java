package com.example.distroapp.Entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DTO per rappresentare un esperto selezionabile per la valutazione delle richieste
 */
public class EspertoSelezionatoDTO implements Parcelable {

    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String specializzazione;  // "Server", "Desktop", "Gaming", "Sicurezza", etc.
    private int anniEsperienza;
    private double feedbackMedio;  // Media dei feedback ricevuti (1.0 - 5.0)
    private int numeroValutazioni;  // Numero totale di valutazioni effettuate
    private String livelloEsperienza;  // "INTERMEDIO", "ESPERTO", "AVANZATO"
    private boolean isDisponibile;  // Se è attualmente disponibile per nuove valutazioni
    private String bio;  // Breve biografia/descrizione

    // Flag di selezione per l'adapter
    private boolean isSelected = false;

    // Costruttore vuoto
    public EspertoSelezionatoDTO() {}

    // Costruttore completo
    public EspertoSelezionatoDTO(int id, String nome, String cognome, String email,
                                 String specializzazione, int anniEsperienza,
                                 double feedbackMedio, int numeroValutazioni,
                                 String livelloEsperienza, boolean isDisponibile, String bio) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.specializzazione = specializzazione;
        this.anniEsperienza = anniEsperienza;
        this.feedbackMedio = feedbackMedio;
        this.numeroValutazioni = numeroValutazioni;
        this.livelloEsperienza = livelloEsperienza;
        this.isDisponibile = isDisponibile;
        this.bio = bio;
    }

    // Costruttore da Parcel
    protected EspertoSelezionatoDTO(Parcel in) {
        id = in.readInt();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        specializzazione = in.readString();
        anniEsperienza = in.readInt();
        feedbackMedio = in.readDouble();
        numeroValutazioni = in.readInt();
        livelloEsperienza = in.readString();
        isDisponibile = in.readByte() != 0;
        bio = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<EspertoSelezionatoDTO> CREATOR = new Creator<EspertoSelezionatoDTO>() {
        @Override
        public EspertoSelezionatoDTO createFromParcel(Parcel in) {
            return new EspertoSelezionatoDTO(in);
        }

        @Override
        public EspertoSelezionatoDTO[] newArray(int size) {
            return new EspertoSelezionatoDTO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
        dest.writeString(specializzazione);
        dest.writeInt(anniEsperienza);
        dest.writeDouble(feedbackMedio);
        dest.writeInt(numeroValutazioni);
        dest.writeString(livelloEsperienza);
        dest.writeByte((byte) (isDisponibile ? 1 : 0));
        dest.writeString(bio);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    // Metodi utili
    public String getNomeCompleto() {
        if (cognome != null && !cognome.isEmpty()) {
            return nome + " " + cognome;
        }
        return nome;
    }

    public String getFeedbackStars() {
        int stelle = (int) Math.round(feedbackMedio);
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= stelle) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public String getFeedbackFormatted() {
        return String.format("%.1f", feedbackMedio);
    }

    public String getEsperienzaFormatted() {
        if (anniEsperienza == 1) {
            return "1 anno";
        }
        return anniEsperienza + " anni";
    }

    public String getIconaSpecializzazione() {
        if (specializzazione == null) return "👨‍💻";

        String spec = specializzazione.toLowerCase();
        if (spec.contains("server") || spec.contains("cloud")) return "🖥️";
        if (spec.contains("desktop") || spec.contains("ui")) return "🖨️";
        if (spec.contains("gaming") || spec.contains("grafica")) return "🎮";
        if (spec.contains("sicurezza") || spec.contains("security")) return "🔒";
        if (spec.contains("sviluppo") || spec.contains("dev")) return "💻";
        if (spec.contains("network") || spec.contains("rete")) return "🌐";
        if (spec.contains("database") || spec.contains("db")) return "🗄️";
        if (spec.contains("embedded") || spec.contains("iot")) return "🔧";

        return "👨‍💻"; // Default
    }

    public String getStatoDisponibilita() {
        return isDisponibile ? "Disponibile" : "Occupato";
    }

    public String getColoreStato() {
        return isDisponibile ? "#4CAF50" : "#FF9800"; // Verde per disponibile, arancione per occupato
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecializzazione() { return specializzazione; }
    public void setSpecializzazione(String specializzazione) { this.specializzazione = specializzazione; }

    public int getAnniEsperienza() { return anniEsperienza; }
    public void setAnniEsperienza(int anniEsperienza) { this.anniEsperienza = anniEsperienza; }

    public double getFeedbackMedio() { return feedbackMedio; }
    public void setFeedbackMedio(double feedbackMedio) { this.feedbackMedio = feedbackMedio; }

    public int getNumeroValutazioni() { return numeroValutazioni; }
    public void setNumeroValutazioni(int numeroValutazioni) { this.numeroValutazioni = numeroValutazioni; }

    public String getLivelloEsperienza() { return livelloEsperienza; }
    public void setLivelloEsperienza(String livelloEsperienza) { this.livelloEsperienza = livelloEsperienza; }

    public boolean isDisponibile() { return isDisponibile; }
    public void setDisponibile(boolean disponibile) { isDisponibile = disponibile; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    @Override
    public String toString() {
        return "EspertoSelezionatoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", specializzazione='" + specializzazione + '\'' +
                ", anniEsperienza=" + anniEsperienza +
                ", feedbackMedio=" + feedbackMedio +
                ", isDisponibile=" + isDisponibile +
                ", isSelected=" + isSelected +
                '}';
    }
}