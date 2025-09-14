package com.example.distroapp.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Distro implements Parcelable {

    private String nome ;
    private int id ;
    private int numeroPacchetti ;
    private int minRamMB;
    private int annoFondazione;
    private String gestorePacchetti;
    private boolean beginnerFriendly ;
    private String guidaPdf;


    public Distro(String nome, int id, int numeroPacchetti, int minRamMB, int annoFondazione, String gestorePacchetti, boolean beginnerFriendly,String guidaPdf){
        this.nome = nome;
        this.id = id;
        this.numeroPacchetti = numeroPacchetti;
        this.minRamMB = minRamMB;
        this.annoFondazione = annoFondazione;
        this.gestorePacchetti = gestorePacchetti;
        this.beginnerFriendly = beginnerFriendly;
        this.guidaPdf = guidaPdf;
    }

    public Distro(String nome, int id, int numeroPacchetti, int minRamMB, int annoFondazione, String gestorePacchetti, boolean beginnerFriendly){
        this.nome = nome;
        this.id = id;
        this.numeroPacchetti = numeroPacchetti;
        this.minRamMB = minRamMB;
        this.annoFondazione = annoFondazione;
        this.gestorePacchetti = gestorePacchetti;
        this.beginnerFriendly = beginnerFriendly;

    }


    protected Distro(Parcel in) {
        nome = in.readString();
        id = in.readInt();
        numeroPacchetti = in.readInt();
        minRamMB = in.readInt();
        annoFondazione = in.readInt();
        gestorePacchetti = in.readString();
        beginnerFriendly = in.readByte() != 0;
        guidaPdf = in.readString();
    }

    public static final Creator<Distro> CREATOR = new Creator<Distro>() {
        @Override
        public Distro createFromParcel(Parcel in) {
            return new Distro(in);
        }

        @Override
        public Distro[] newArray(int size) {
            return new Distro[size];
        }
    };

    public static List<Distro> parsificaArrayDistro(String jsonString) throws JSONException {

        List<Distro> distroList = new ArrayList<Distro>();
        JSONArray jsonArray = new JSONArray(jsonString);

        for( int i = 0; i < jsonArray.length(); i++){

            JSONObject DistroJsonArray = jsonArray.getJSONObject(i);
            Distro d;

            int id = DistroJsonArray.getInt("id");
            String nome = DistroJsonArray.getString("nome");
            int numeroPacchetti = DistroJsonArray.getInt("numeroPacchetti");
            int minRamMB = DistroJsonArray.getInt("minRamMB");
            int annoFondazione = DistroJsonArray.getInt("annoFondazione");
            String gestorePacchetti = DistroJsonArray.getString("gestorePacchetti");
            boolean beginnerFriendly = DistroJsonArray.getBoolean("beginnerFriendly");
            if(DistroJsonArray.has("guidaPdf")){
                String guidaPdf = DistroJsonArray.getString("guidaPdf");
                d = new Distro(nome,id,numeroPacchetti,minRamMB,annoFondazione,gestorePacchetti,beginnerFriendly,guidaPdf);

            }else{
                d = new Distro(nome,id,numeroPacchetti,minRamMB,annoFondazione,gestorePacchetti,beginnerFriendly);

            }
            distroList.add(d);
        }
        return distroList;
    }

    public static Distro parsificaDistro(String jsonString) throws JSONException {
        JSONObject DistroJsonObject = null;
        Distro d;

        try {
            DistroJsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        int id = DistroJsonObject.getInt("id");
        String nome = DistroJsonObject.getString("nome");
        int numeroPacchetti = DistroJsonObject.getInt("numeroPacchetti");
        int minRamMB = DistroJsonObject.getInt("minRamMB");
        int annoFondazione = DistroJsonObject.getInt("annoFondazione");
        boolean beginnerFriendly = DistroJsonObject.getBoolean("beginnerFriendly");
        String gestorePacchetti = DistroJsonObject.getString("gestorePacchetti");
        if(DistroJsonObject.has("guidaPdf")){
            String guidaPdf = DistroJsonObject.getString("guidaPdf");
            d = new Distro(nome,id,numeroPacchetti,minRamMB,annoFondazione,gestorePacchetti,beginnerFriendly,guidaPdf);

        }else{
            d = new Distro(nome,id,numeroPacchetti,minRamMB,annoFondazione,gestorePacchetti,beginnerFriendly);

        }


        return d;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeInt(id);
        dest.writeInt(numeroPacchetti);
        dest.writeInt(minRamMB);
        dest.writeInt(annoFondazione);
        dest.writeString(gestorePacchetti);
        dest.writeByte((byte) (beginnerFriendly ? 1 : 0));
        dest.writeString(guidaPdf);
    }

    public String getNome() {
        return this.nome;
    }

    public int getId(){
        return this.id;
    }

    public String getMinRamMB() {
        id = this.minRamMB;
        return Integer.toString(id);
    }

    public String getAnnoFondazione() {
        return Integer.toString(this.annoFondazione);
    }

    public String getNumeroPacchetti() {
        return Integer.toString(this.numeroPacchetti);
    }

    public String getGestorePacchetti() {
        return this.gestorePacchetti;
    }

    public boolean isBeginnerFriendly() {
        return this.beginnerFriendly;
    }

    public String getGuidaPdf(){ return this.guidaPdf; }

}
