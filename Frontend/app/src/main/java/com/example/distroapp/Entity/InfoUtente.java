package com.example.distroapp.Entity;



import org.json.JSONException;
import org.json.JSONObject;
public class InfoUtente {

        private String architetturaCpu;
        private int ramGB;
        private String cpuTier;
        private int spazioArchiviazione;
        private String tipoArchiviazione;
        private String produttoreGpu;
        private String modelloGpu;
        private String livelloEsperienza;
        private String useCase;
        private String ambienteDesktopPreferito;

        //costruttore con info essenziali
        public InfoUtente (String architetturaCpu,int ramMB,String cpuTier,String livelloEsperienza)  {
            this.architetturaCpu = architetturaCpu;
            this.ramGB = ramMB;
            this.cpuTier = cpuTier;
            this.livelloEsperienza = livelloEsperienza;
            this.spazioArchiviazione = -1;
        }

    public String getArchitetturaCpu() {
        return architetturaCpu;
    }

    public void setArchitetturaCpu(String architetturaCpu) {
        this.architetturaCpu = architetturaCpu;
    }

    public int getRamMB() {
        return ramGB;
    }

    public void setRamMB(int ramMB) {
        this.ramGB = ramMB;
    }

    public String getCpuTier() {
        return cpuTier;
    }

    public void setCpuTier(String cpuTier) {
        this.cpuTier = cpuTier;
    }

    public int getSpazioArchiviazione() {
        return spazioArchiviazione;
    }

    public void setSpazioArchiviazione(int spazioArchiviazione) {
        this.spazioArchiviazione = spazioArchiviazione;
    }

    public String getTipoArchiviazione() {
        return tipoArchiviazione;
    }

    public void setTipoArchiviazione(String tipoArchiviazione) {
        this.tipoArchiviazione = tipoArchiviazione;
    }

    public String getProduttoreGpu() {
        return produttoreGpu;
    }

    public void setProduttoreGpu(String produttoreGpu) {
        this.produttoreGpu = produttoreGpu;
    }

    public String getModelloGpu() {
        return modelloGpu;
    }

    public void setModelloGpu(String modelloGpu) {
        this.modelloGpu = modelloGpu;
    }

    public String getLivelloEsperienza() {
        return livelloEsperienza;
    }

    public void setLivelloEsperienza(String livelloEsperienza) {
        this.livelloEsperienza = livelloEsperienza;
    }

    public String getUseCase() {
        return useCase;
    }

    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }

    public String getAmbienteDesktopPreferito() {
        return ambienteDesktopPreferito;
    }

    public void setAmbienteDesktopPreferito(String ambienteDesktopPreferito) {
        this.ambienteDesktopPreferito = ambienteDesktopPreferito;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();

        try {
            // 2. Aggiungi ogni campo usando il metodo .put("chiave", valore).
            //    Le chiavi devono corrispondere a quelle che il backend si aspetta.

            // Campi obbligatori
            jsonObject.put("architetturaCpu", getArchitetturaCpu());
            jsonObject.put("ramMB", getRamMB());
            jsonObject.put("cpuTier", getCpuTier());
            jsonObject.put("livelloEsperienza", getLivelloEsperienza());

            // Campi opzionali: controlliamo che non siano nulli prima di aggiungerli.
            // Se un campo è null, la sua chiave non verrà inclusa nel JSON finale.
            if (getSpazioArchiviazione() != -1) {
                jsonObject.put("spazioArchiviazione", getSpazioArchiviazione());
            }
            if (getTipoArchiviazione() != null && !getTipoArchiviazione().isEmpty()) {
                jsonObject.put("tipoArchiviazione", getTipoArchiviazione());
            }
            if (getUseCase() != null && !getUseCase().isEmpty()) {
                jsonObject.put("useCase", getUseCase());
            }
            if (getAmbienteDesktopPreferito() != null && !getAmbienteDesktopPreferito().equalsIgnoreCase("Nessuna preferenza")) {
                jsonObject.put("ambienteDesktopPreferito", getAmbienteDesktopPreferito());
            }
            if (getProduttoreGpu() != null && !getProduttoreGpu().isEmpty()) {
                jsonObject.put("produttoreGpu", getProduttoreGpu());
            }
            if (getModelloGpu() != null && !getModelloGpu().isEmpty()) {
                jsonObject.put("modelloGpu", getModelloGpu());
            }

        } catch (JSONException e) {
            // È buona norma gestire l'eccezione
            e.printStackTrace();
            // In caso di errore, restituisci un oggetto JSON vuoto per evitare crash
            return "{}";
        }

        // 3. Converti l'oggetto JSON finale in una stringa
        return jsonObject.toString();
    }

}
