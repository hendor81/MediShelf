# SCREEN_CATALOG.md

## Scopo
Definire l'elenco delle schermate previste per la versione Free di MediShelf e il ruolo di ciascuna.

Questo documento è il riferimento principale quando bisogna:
- creare nuove schermate;
- verificare se una schermata è già prevista;
- collegare UI, navigazione, backlog e issue GitHub;
- evitare duplicazioni o schermate fuori scope.

La navigazione tra queste schermate è descritta in `NAVIGATION_MAP.md`.

## Schermate della versione Free

### SCH-01 – Home / Dashboard
Scopo: fornire una vista immediata dello stato dell'armadietto.

Contenuti principali:
- totale medicinali attivi;
- medicinali in scadenza;
- medicinali scaduti;
- medicinali con scorta bassa, solo quando la quantità è nota;
- accesso rapido ad aggiunta medicinale;
- accessi rapidi a Inventario e Scadenzario.

### SCH-02 – Inventario
Scopo: consultare, cercare e filtrare i medicinali attivi.

Contenuti principali:
- lista medicinali attivi;
- ricerca per nome;
- filtri per stato;
- ordinamenti base;
- accesso al dettaglio medicinale.

### SCH-03 – Dettaglio medicinale
Scopo: mostrare i dati di una voce e permettere azioni rapide.

Contenuti principali:
- nome;
- stato;
- scadenza, se presente;
- quantità, se presente;
- luogo di conservazione;
- note;
- azione modifica con icona a matita;
- azione archivia;
- azione elimina con icona a cestino e conferma;
- azioni rapide sulla quantità quando disponibile.

### SCH-04 – Inserimento / Modifica medicinale
Scopo: creare o aggiornare una voce di inventario.

Contenuti principali:
- nome, obbligatorio;
- formato/confezione, opzionale;
- quantità, opzionale;
- unità quantità, opzionale;
- scadenza, opzionale;
- luogo di conservazione, opzionale;
- note, opzionali;
- validazioni semplici e vicine ai campi.

Regola importante:
- la quantità non deve essere obbligatoria, perché l'utente deve poter censire una confezione senza aprirla o stimarne il contenuto.

### SCH-05 – Scadenzario
Scopo: concentrare la gestione delle scadenze.

Contenuti principali:
- medicinali in scadenza;
- medicinali scaduti;
- medicinali senza data;
- ordinamento cronologico;
- accesso al dettaglio.

### SCH-06 – Archivio
Scopo: consultare medicinali non più attivi.

Contenuti principali:
- lista medicinali archiviati;
- dettaglio medicinale archiviato;
- eventuale ripristino;
- eventuale eliminazione definitiva con conferma.

### SCH-07 – Impostazioni
Scopo: configurare il comportamento base dell'app.

Contenuti principali:
- soglia giorni per stato in scadenza;
- attivazione/disattivazione notifiche;
- preferenza archiviazione/cancellazione;
- informazioni essenziali sul perimetro dell'app;
- eventuale accesso futuro a upgrade Pro / Subscription.

### SCH-08 – Onboarding essenziale
Scopo: spiegare in modo leggero cosa fa MediShelf e guidare al primo inserimento.

Contenuti principali:
- valore dell'app: inventario e scadenzario;
- chiarimento che non è un'app medica o di terapia;
- CTA per aggiungere il primo medicinale;
- possibilità di saltare o chiudere.

## Schermate fuori scope nella versione Free
Sono previste per fasi successive, ma non devono essere implementate nella Free iniziale:
- gestione armadietti multipli;
- lista acquisti;
- storico movimenti completo;
- export/import;
- sync e condivisione;
- barcode scan;
- OCR;
- allegati.

## Relazione con gli altri documenti
- `PRODUCT_OVERVIEW.md`: definisce perché queste schermate esistono e quali confini rispettano.
- `UI_GUIDELINES.md`: definisce come devono apparire e comportarsi.
- `NAVIGATION_MAP.md`: definisce come sono collegate.
- `BACKLOG_FREE.md`: definisce in quale ordine implementarle.
