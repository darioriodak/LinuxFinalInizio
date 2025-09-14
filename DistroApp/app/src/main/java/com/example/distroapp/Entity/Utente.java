package com.example.distroapp.Entity;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

public class Utente implements Parcelable {
    private int id;
    private String email;
    private String livelloEsperienza;
    private boolean isEsperto;

    // Campi esperto
    private String areaEspertise;
    private int anniEsperienza;
    private String certificazioni;

    //  utente normale
    public Utente(int id, String email, String livelloEsperienza) {
        this.id = id;
        this.email = email;
        this.livelloEsperienza = livelloEsperienza;
        this.isEsperto = false;
    }

    //  esperto
    public Utente(int id, String email, String livelloEsperienza,
                  String areaEspertise, int anniEsperienza) {
        this(id, email, livelloEsperienza);
        this.isEsperto = true;
        this.areaEspertise = areaEspertise;
        this.anniEsperienza = anniEsperienza;
        this.certificazioni = certificazioni;
    }

    protected Utente(Parcel in) {
        id = in.readInt();
        email = in.readString();
        livelloEsperienza = in.readString();
        isEsperto = in.readByte() != 0;
        areaEspertise = in.readString();
        anniEsperienza = in.readInt();
        certificazioni = in.readString();
    }

    public static final Creator<Utente> CREATOR = new Creator<Utente>() {
        @Override
        public Utente createFromParcel(Parcel in) {
            return new Utente(in);
        }

        @Override
        public Utente[] newArray(int size) {
            return new Utente[size];
        }
    };

    // Parsing da risposta login
    public static Utente parseFromLoginResponse(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);

        int id = json.getInt("id");
        String email = json.getString("email");
        String nome = json.getString("nome");
        String cognome = json.getString("cognome");
        String livelloEsperienza = json.getString("livelloEsperienza");

        if (json.has("areaEspertise")) {
            // È un esperto
            String areaEspertise = json.getString("areaEspertise");
            int anniEsperienza = json.getInt("anniEsperienza");
            String certificazioni = json.optString("certificazioni", "");

            return new Utente(id, email, livelloEsperienza,
                    areaEspertise, anniEsperienza);
        } else {
            // È un utente normale
            return new Utente(id, email, livelloEsperienza);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(livelloEsperienza);
        dest.writeByte((byte) (isEsperto ? 1 : 0));
        dest.writeString(areaEspertise);
        dest.writeInt(anniEsperienza);
        dest.writeString(certificazioni);
    }

    // Getters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getLivelloEsperienza() { return livelloEsperienza; }
    public boolean isEsperto() { return isEsperto; }
    public String getAreaEspertise() { return areaEspertise; }
    public int getAnniEsperienza() { return anniEsperienza; }
    public String getCertificazioni() { return certificazioni; }

}

