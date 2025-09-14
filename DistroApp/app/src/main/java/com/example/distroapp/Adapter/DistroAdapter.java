package com.example.distroapp.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.Entity.Distro;
import com.example.distroapp.R;

import java.util.List;

/**
 * Adapter per la RecyclerView che mostra la lista di distribuzioni consigliate.
 * Prende una List<Distro> e la adatta per essere visualizzata*/
public class DistroAdapter extends RecyclerView.Adapter<DistroAdapter.DistroViewHolder> {

    private List<Distro> distroList;

    public DistroAdapter(List<Distro> distroList) {
        this.distroList = distroList;
    }

    /**
     *  ON CREATE VIEW HOLDER
     *  chiamato dalla RecyclerView quando ha bisogno di creare
     * una nuova riga. Accade solo per le prime righe visibili sullo schermo.
     */
    @NonNull
    @Override
    public DistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Infla" (crea) la vista dal file XML item_distro.xml
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_distro, parent, false);

        // Restituisce un nuovo ViewHolder che contiene questa vista
        return new DistroViewHolder(itemView);
    }

    public static class DistroViewHolder extends RecyclerView.ViewHolder {

        public TextView nomeDistro;
        public TextView anno;
        public TextView ramMin;
        public TextView gestorePacchetti;
        public TextView numeroPacchetti;
        public TextView beginnerFriendly;
        public Button buttonScaricaGuida;


        // Il costruttore del ViewHolder, cache per la costruzione delle visite si utilizza qua una volta sola
        public DistroViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeDistro = itemView.findViewById(R.id.textViewNomeDistro);
            anno = itemView.findViewById(R.id.textViewAnno);
            ramMin = itemView.findViewById(R.id.textViewRamMin);
            gestorePacchetti = itemView.findViewById(R.id.textViewGestorePacchetti);
            numeroPacchetti = itemView.findViewById(R.id.textViewNumeroPacchetti);
            beginnerFriendly = itemView.findViewById(R.id.textViewBeginnerFriendly);
            buttonScaricaGuida = itemView.findViewById(R.id.buttonScaricaGuida);

        }
    }


    // collega i dati dell elemento alla vista , chiamato per ogni riga da popolare
    @Override
    public void onBindViewHolder(@NonNull DistroViewHolder holder, int position) {

        // Prendi l'oggetto Distro dalla lista alla posizione corrente
        Distro distroCorrente = distroList.get(position);

        holder.nomeDistro.setText(distroCorrente.getNome());

        String annoText = "Fondata nel: " + distroCorrente.getAnnoFondazione();
        holder.anno.setText(annoText);

        String ramText = "RAM Minima: " + distroCorrente.getMinRamMB() + "MB";
        holder.ramMin.setText(ramText);

        String gestoreText = "Gestore Pacchetti: " + distroCorrente.getGestorePacchetti();
        holder.gestorePacchetti.setText(gestoreText);

        String numeroPacchettiText = "N. Pacchetti: " + distroCorrente.getNumeroPacchetti();
        holder.numeroPacchetti.setText(numeroPacchettiText);

        String beginnerFriendlyText = "Adatta a Principianti: " + (distroCorrente.isBeginnerFriendly() ? "Sì" : "No");
        holder.beginnerFriendly.setText(beginnerFriendlyText);


        holder.buttonScaricaGuida.setOnClickListener(view -> {
            // Recupera l'URL della guida per questa specifica distro
            String urlGuida = distroCorrente.getGuidaPdf();

            // Controlla che l'URL non sia vuoto o nullo
            if (urlGuida != null && !urlGuida.isEmpty()) {
                // Crea un Intent per aprire l'URL
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlGuida));

                // Chiedi al sistema di avviare l'attività appropriata (il browser)
                view.getContext().startActivity(intent);
            } else {
                // Se l'URL non è disponibile, mostra un messaggio all'utente
                Toast.makeText(view.getContext(), "Guida non disponibile.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        if(!distroList.isEmpty()){
            return distroList.size();
        }else{
            return 0;
        }
    }



}
