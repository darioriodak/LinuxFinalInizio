package enumerazioni;

public enum TipoNotificaUtente {
	
	RICHIESTA_INVIATA,        // Quando utente crea richiesta
    PRIMA_VALUTAZIONE,        // Prima valutazione ricevuta
    VALUTAZIONE_PARZIALE,     // Valutazioni intermedie (2/3, 3/4, ecc.)
    VALUTAZIONE_COMPLETATA,   // Tutti esperti hanno valutato
    SCADENZA_IMMINENTE,       // 24h prima della scadenza
    RICHIESTA_SCADUTA,        // Richiesta scaduta senza completamento
    NUOVO_ESPERTO_ASSEGNATO   // Esperto aggiunto alla richiesta

}
