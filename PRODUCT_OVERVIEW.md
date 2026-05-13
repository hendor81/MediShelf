# PRODUCT_OVERVIEW.md

## Nome del prodotto
Il nome definitivo dell'app è **MediShelf**.

Il nome richiama l'idea di uno scaffale o armadietto dei medicinali ordinato, semplice da consultare e adatto a un respiro internazionale.

## Scopo del prodotto
L'app consente all'utente di gestire il proprio armadietto dei medicinali in modo semplice, affidabile e rapido.

I due obiettivi centrali sono:
- avere sempre con sé un inventario aggiornato dei medicinali disponibili;
- avere uno scadenzario che evidenzi con chiarezza ciò che sta per scadere e ciò che è già scaduto.

## Principi alla base del prodotto
- **Semplicità prima di tutto**: le operazioni frequenti devono richiedere pochi tap.
- **Offline-first**: la versione Free deve funzionare bene senza dipendere dalla rete.
- **Affidabilità percepita**: stati, conteggi e notifiche devono risultare coerenti.
- **Basso attrito**: aggiungere o aggiornare un medicinale non deve essere faticoso.
- **Focus organizzativo**: l'app gestisce medicinali come oggetti presenti in casa, non terapie cliniche.
- **Crescita per strati**: prima il nucleo utile, poi funzioni avanzate, poi servizi continuativi.

## Strumenti di lavoro
### GPT-Codex
Usato per:
- scaffolding e implementazione incrementale;
- suggerimenti su architettura e refactoring;
- scrittura o aggiornamento di test;
- accelerazione di task tecnici ben definiti.

### Figma
Usato per:
- wireframe low fidelity e mid fidelity;
- mappa di navigazione;
- componenti UI condivisi;
- prototipi per validare i flussi principali.

### GitHub
Usato per:
- versionamento del codice;
- backlog e milestone;
- issue tecniche e funzionali;
- pull request, review e release.

## Target di utenti
### Utente singolo
Persona che vuole sapere cosa ha in casa e quando scade.

### Famiglia
Contesto in cui più persone usano lo stesso armadietto, anche se la condivisione reale arriverà in fasi successive.

### Caregiver
Persona che gestisce medicinali per figli, genitori o altre persone, sempre in una logica organizzativa.

## Confini di scope
### Dentro scope iniziale
- inventario medicinali;
- gestione quantità residue;
- scadenzario;
- notifiche locali di scadenza;
- archiviazione;
- consultazione rapida dell'inventario.

### Fuori scope iniziale
- reminder di assunzione terapia;
- suggerimenti medici o posologici;
- diagnosi o supporto clinico;
- percorsi terapeutici;
- raccomandazioni farmaceutiche automatiche.

## Tono del prodotto
Il tono deve essere:
- chiaro;
- rassicurante;
- pratico;
- non medico-clinico;
- non ansiogeno;
- sobrio.

L'app deve dare ordine e chiarezza, non allarmare l'utente.

## Percorso evolutivo sintetico
### Fase 1 – Free
- inventario base;
- dettaglio medicinale;
- inserimento e modifica;
- quantità residue;
- scadenzario;
- dashboard;
- impostazioni base;
- notifiche locali;
- archivio.

### Fase 2 – Pro
- armadietti multipli;
- categorie e tag;
- profili persona;
- reminder organizzativi avanzati;
- lista acquisti;
- storico;
- export/import;
- backup locale.

### Fase 3 – Subscription
- sync multi-device;
- backup cloud;
- condivisione familiare;
- barcode scan;
- OCR;
- allegati;
- insight intelligenti.

## Criterio guida per le decisioni
In caso di dubbio, preferire sempre la soluzione che:
- riduce la complessità;
- migliora chiarezza e velocità;
- preserva il focus organizzativo;
- evita di allargare troppo lo scope della versione Free.
