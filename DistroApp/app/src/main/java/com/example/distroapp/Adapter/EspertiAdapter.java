package com.example.distroapp.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.distroapp.Entity.EspertoSelezionatoDTO;
import com.example.distroapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter per visualizzare gli esperti selezionabili nella CreaRichiestaActivity
 */
public class EspertiAdapter extends RecyclerView.Adapter<EspertiAdapter.EspertoViewHolder> {

    private List<EspertoSelezionatoDTO> esperti;
    private List<EspertoSelezionatoDTO> espertiSelezionati;
    private OnEspertiSelezionatiListener listener;
    private static final int MAX_SELEZIONI = 3;  // Massimo 3 esperti per richiesta

    public interface OnEspertiSelezionatiListener {
        void onEspertiSelezionati(List<EspertoSelezionatoDTO> espertiSelezionati);
    }

    public EspertiAdapter(List<EspertoSelezionatoDTO> esperti,
                          OnEspertiSelezionatiListener listener) {
        this.esperti = esperti != null ? esperti : new ArrayList<>();
        this.espertiSelezionati = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public EspertoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_esperto, parent, false);
        return new EspertoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EspertoViewHolder holder, int position) {
        EspertoSelezionatoDTO esperto = esperti.get(position);
        holder.bind(esperto);
    }

    @Override
    public int getItemCount() {
        return esperti.size();
    }

    public void updateEsperti(List<EspertoSelezionatoDTO> nuoviEsperti) {
        this.esperti.clear();
        if (nuoviEsperti != null) {
            this.esperti.addAll(nuoviEsperti);
        }
        notifyDataSetChanged();
    }

    public List<EspertoSelezionatoDTO> getEspertiSelezionati() {
        return new ArrayList<>(espertiSelezionati);
    }

    class EspertoViewHolder extends RecyclerView.ViewHolder {

        private CardView cardEsperto;
        private CheckBox checkBoxEsperto;
        private TextView tvIconaSpecializzazione;
        private TextView tvNomeEsperto;
        private TextView tvSpecializzazione;
        private TextView tvEsperienza;
        private TextView tvFeedback;
        private TextView tvStelle;
        private TextView tvStatoDisponibilita;
        private TextView tvBio;

        public EspertoViewHolder(@NonNull View itemView) {
            super(itemView);

            cardEsperto = itemView.findViewById(R.id.cardEsperto);
            checkBoxEsperto = itemView.findViewById(R.id.checkBoxEsperto);
            tvIconaSpecializzazione = itemView.findViewById(R.id.tvIconaSpecializzazione);
            tvNomeEsperto = itemView.findViewById(R.id.tvNomeEsperto);
            tvSpecializzazione = itemView.findViewById(R.id.tvSpecializzazione);
            tvEsperienza = itemView.findViewById(R.id.tvEsperienza);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
            tvStelle = itemView.findViewById(R.id.tvStelle);
            tvStatoDisponibilita = itemView.findViewById(R.id.tvStatoDisponibilita);
            tvBio = itemView.findViewById(R.id.tvBio);
        }

        public void bind(EspertoSelezionatoDTO esperto) {
            // Nome completo
            tvNomeEsperto.setText(esperto.getNomeCompleto());

            // Icona specializzazione
            tvIconaSpecializzazione.setText(esperto.getIconaSpecializzazione());

            // Specializzazione
            String specializzazione = esperto.getSpecializzazione();
            if (specializzazione == null || specializzazione.isEmpty()) {
                specializzazione = "Esperto Linux";
            }
            tvSpecializzazione.setText(specializzazione);

            // Anni di esperienza
            tvEsperienza.setText(esperto.getEsperienzaFormatted() + " di esperienza");

            // Feedback formattato
            if (esperto.getNumeroValutazioni() > 0) {
                String feedbackText = esperto.getFeedbackFormatted() +
                        " (" + esperto.getNumeroValutazioni() + " valutazioni)";
                tvFeedback.setText(feedbackText);
                tvStelle.setText(esperto.getFeedbackStars());
                tvStelle.setVisibility(View.VISIBLE);
            } else {
                tvFeedback.setText("Nessuna valutazione ancora");
                tvStelle.setVisibility(View.GONE);
            }

            // Stato disponibilità
            tvStatoDisponibilita.setText(esperto.getStatoDisponibilita());
            try {
                tvStatoDisponibilita.setTextColor(Color.parseColor(esperto.getColoreStato()));
            } catch (IllegalArgumentException e) {
                tvStatoDisponibilita.setTextColor(Color.parseColor("#4CAF50"));
            }

            // Bio (se presente)
            if (esperto.getBio() != null && !esperto.getBio().isEmpty()) {
                tvBio.setText(esperto.getBio());
                tvBio.setVisibility(View.VISIBLE);
            } else {
                tvBio.setVisibility(View.GONE);
            }

            // Disabilita se non disponibile
            boolean isClickable = esperto.isDisponibile();
            checkBoxEsperto.setEnabled(isClickable);
            cardEsperto.setEnabled(isClickable);

            if (!isClickable) {
                cardEsperto.setAlpha(0.6f);
            } else {
                cardEsperto.setAlpha(1.0f);
            }

            // Gestione selezione checkbox
            checkBoxEsperto.setOnCheckedChangeListener(null); // Rimuovi listener temporaneamente
            checkBoxEsperto.setChecked(esperto.isSelected());

            if (isClickable) {
                checkBoxEsperto.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Controlla il limite massimo di selezioni
                        if (espertiSelezionati.size() >= MAX_SELEZIONI) {
                            checkBoxEsperto.setChecked(false);
                            // Potresti mostrare un Toast qui se hai accesso al Context
                            return;
                        }

                        esperto.setSelected(true);
                        espertiSelezionati.add(esperto);
                    } else {
                        esperto.setSelected(false);
                        espertiSelezionati.remove(esperto);
                    }

                    // Notifica il listener
                    if (listener != null) {
                        listener.onEspertiSelezionati(getEspertiSelezionati());
                    }
                });

                // Click sull'intera card per toggle della selezione
                cardEsperto.setOnClickListener(v -> {
                    if (esperto.isDisponibile()) {
                        checkBoxEsperto.setChecked(!checkBoxEsperto.isChecked());
                    }
                });
            }

        }
    }

    // Metodi helper per gestire le selezioni
    public void clearSelezioni() {
        for (EspertoSelezionatoDTO esperto : esperti) {
            esperto.setSelected(false);
        }
        espertiSelezionati.clear();
        notifyDataSetChanged();

        if (listener != null) {
            listener.onEspertiSelezionati(getEspertiSelezionati());
        }
    }

    public void selezionaEsperto(int id) {
        for (EspertoSelezionatoDTO esperto : esperti) {
            if (esperto.getId() == id && !esperto.isSelected() &&
                    esperto.isDisponibile() && espertiSelezionati.size() < MAX_SELEZIONI) {
                esperto.setSelected(true);
                espertiSelezionati.add(esperto);
                notifyDataSetChanged();

                if (listener != null) {
                    listener.onEspertiSelezionati(getEspertiSelezionati());
                }
                break;
            }
        }
    }

    public boolean isMaxSelezioniRaggiunto() {
        return espertiSelezionati.size() >= MAX_SELEZIONI;
    }

    public int getNumeroSelezioni() {
        return espertiSelezionati.size();
    }

    public void filtraPerDisponibilita(boolean soloDisponibili) {
        // Implementazione del filtro se necessaria
        notifyDataSetChanged();
    }

    public void filtraPerSpecializzazione(String specializzazione) {
        // Implementazione del filtro se necessaria
        notifyDataSetChanged();
    }
}