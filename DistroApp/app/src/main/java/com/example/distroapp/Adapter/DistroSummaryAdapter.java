package com.example.distroapp.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.Entity.DistroSummaryDTO;
import com.example.distroapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter per visualizzare le distribuzioni summary nella CreaRichiestaActivity
 */
public class DistroSummaryAdapter extends RecyclerView.Adapter<DistroSummaryAdapter.DistroSummaryViewHolder> {

    private List<DistroSummaryDTO> distribuzioni;
    private List<DistroSummaryDTO> distribuzioniSelezionate;
    private OnDistribuzioniSelezionateListener listener;
    private static final int MAX_SELEZIONI = 5;

    public interface OnDistribuzioniSelezionateListener {
        void onDistribuzioniSelezionate(List<DistroSummaryDTO> distribuzioniSelezionate);
    }

    public DistroSummaryAdapter(List<DistroSummaryDTO> distribuzioni,
                                OnDistribuzioniSelezionateListener listener) {
        this.distribuzioni = distribuzioni != null ? distribuzioni : new ArrayList<>();
        this.distribuzioniSelezionate = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public DistroSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_distribuzione_summary, parent, false);
        return new DistroSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistroSummaryViewHolder holder, int position) {
        DistroSummaryDTO distro = distribuzioni.get(position);
        holder.bind(distro);
    }

    @Override
    public int getItemCount() {
        return distribuzioni.size();
    }

    public void updateDistribuzioni(List<DistroSummaryDTO> nuoveDistribuzioni) {
        this.distribuzioni.clear();
        if (nuoveDistribuzioni != null) {
            this.distribuzioni.addAll(nuoveDistribuzioni);
        }
        notifyDataSetChanged();
    }

    public List<DistroSummaryDTO> getDistribuzioniSelezionate() {
        return new ArrayList<>(distribuzioniSelezionate);
    }

    class DistroSummaryViewHolder extends RecyclerView.ViewHolder {

        private CardView cardDistribuzione;
        private CheckBox checkBoxDistribuzione;
        private TextView tvEmojiDistro;
        private TextView tvNomeDistribuzione;
        private TextView tvDescrizioneDistro;
        private TextView tvPopolarita;
        private TextView tvDifficolta;
        private LinearLayout layoutTags;

        public DistroSummaryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardDistribuzione = itemView.findViewById(R.id.cardDistribuzione);
            checkBoxDistribuzione = itemView.findViewById(R.id.checkBoxDistribuzione);
            tvEmojiDistro = itemView.findViewById(R.id.tvEmojiDistro);
            tvNomeDistribuzione = itemView.findViewById(R.id.tvNomeDistribuzione);
            tvDescrizioneDistro = itemView.findViewById(R.id.tvDescrizioneDistro);
            tvPopolarita = itemView.findViewById(R.id.tvPopolarita);
            tvDifficolta = itemView.findViewById(R.id.tvDifficolta);
            layoutTags = itemView.findViewById(R.id.layoutTags);
        }

        public void bind(DistroSummaryDTO distro) {
            // Nome e emoji
            tvNomeDistribuzione.setText(distro.getNomeDisplay());
            tvEmojiDistro.setText(distro.getIconaOrDefault());

            // Descrizione
            String descrizione = distro.getDescrizioneBreve();
            if (descrizione == null || descrizione.isEmpty()) {
                descrizione = "Distribuzione Linux " + distro.getLivelloDifficolta();
            }
            tvDescrizioneDistro.setText(descrizione);

            // Popolarità con stelle
            tvPopolarita.setText(distro.getStarsString());

            // Livello di difficoltà
            String livello = distro.getLivelloDifficolta();
            if (livello == null || livello.isEmpty()) {
                livello = "Generico";
            }
            tvDifficolta.setText(livello);

            // Colore della card basato sul colore della distro
            try {
                String colore = distro.getColoreHexOrDefault();
                if (!colore.startsWith("#")) {
                    colore = "#" + colore;
                }
                cardDistribuzione.setCardBackgroundColor(Color.parseColor(colore + "20")); // 20 = alpha per trasparenza
            } catch (IllegalArgumentException e) {
                // Fallback su colore predefinito
                cardDistribuzione.setCardBackgroundColor(Color.parseColor("#2196F320"));
            }

            // Setup tags (opzionale - per ora vuoto)
            layoutTags.removeAllViews();

            // Gestione selezione checkbox
            checkBoxDistribuzione.setOnCheckedChangeListener(null); // Rimuovi listener temporaneamente
            checkBoxDistribuzione.setChecked(distro.isSelected());

            checkBoxDistribuzione.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Controlla il limite massimo di selezioni
                    if (distribuzioniSelezionate.size() >= MAX_SELEZIONI) {
                        checkBoxDistribuzione.setChecked(false);
                        // Potresti mostrare un Toast qui se hai accesso al Context
                        return;
                    }

                    distro.setSelected(true);
                    distribuzioniSelezionate.add(distro);
                } else {
                    distro.setSelected(false);
                    distribuzioniSelezionate.remove(distro);
                }

                // Notifica il listener
                if (listener != null) {
                    listener.onDistribuzioniSelezionate(getDistribuzioniSelezionate());
                }
            });

            // Click sull'intera card per toggle della selezione
            cardDistribuzione.setOnClickListener(v -> {
                checkBoxDistribuzione.setChecked(!checkBoxDistribuzione.isChecked());
            });


        }
    }

    // Metodi helper per gestire le selezioni
    public void clearSelezioni() {
        for (DistroSummaryDTO distro : distribuzioni) {
            distro.setSelected(false);
        }
        distribuzioniSelezionate.clear();
        notifyDataSetChanged();

        if (listener != null) {
            listener.onDistribuzioniSelezionate(getDistribuzioniSelezionate());
        }
    }

    public void selezionaDistribuzione(int id) {
        for (DistroSummaryDTO distro : distribuzioni) {
            if (distro.getId() == id && !distro.isSelected() && distribuzioniSelezionate.size() < MAX_SELEZIONI) {
                distro.setSelected(true);
                distribuzioniSelezionate.add(distro);
                notifyDataSetChanged();

                if (listener != null) {
                    listener.onDistribuzioniSelezionate(getDistribuzioniSelezionate());
                }
                break;
            }
        }
    }

    public boolean isMaxSelezioniRaggiunto() {
        return distribuzioniSelezionate.size() >= MAX_SELEZIONI;
    }

    public int getNumeroSelezioni() {
        return distribuzioniSelezionate.size();
    }
}