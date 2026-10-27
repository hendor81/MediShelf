# MediShelf Free — note di release interne

26 ottobre 2026 · Issue #35 · Milestone 4 — Hardening e release Free.
Build: **1.0.0-alpha03**, codice **4**, Android 7.0/API 24 o successivo.

Aggiornamento del 27 ottobre 2026: interfaccia disponibile in italiano e inglese,
incluse notifiche, tutorial e accessibilità. Lingua dell'app selezionabile dalle
impostazioni Android da Android 13. Formati delle confezioni localizzati senza
modificare i valori persistiti. Traduzione provata dall'utente su emulatore;
116 test unitari e 68 test strumentali superati prima dell'incremento versione.
L'AAB firmato alpha03 è destinato al caricamento manuale in Play Console;
questa preparazione non pubblica una release sullo store.
Stato: alpha interna per installazione diretta; nessuna pubblicazione sullo store.

## Cosa include

- Un inventario locale offline, senza account: aggiunta manuale con solo nome
  obbligatorio, modifica, formato, quantità/unità, soglia di scorta, scadenza,
  posizione e note opzionali. Calendario per selezionare o rimuovere la data.
- Ricerca per nome e filtri per stato, Scorta bassa e Da controllare.
- Dettaglio con incremento/decremento della quantità quando nota, senza valori
  negativi. Quantità assente e quantità zero restano distinte.
- Home con riepiloghi e fino a tre voci da controllare; «Vedi tutti» apre
  l'Inventario filtrato senza questo limite.
- Scadenzario con in scadenza, scaduti e senza data. Soglia iniziale 60 giorni,
  configurabile 0–365. Oggi è ancora in scadenza, ieri è scaduto.
  Esauriti e archiviate sono esclusi dalle sezioni.
- Scorta bassa per quantità positiva entro la soglia individuale; zero è
  esaurito ed escluso dal conteggio scorte basse. Scorta e scadenza possono
  coesistere.
- Archivio con ripristino dal Dettaglio, eliminazione definitiva e svuotamento
  Archivio con conferma.
- Tema chiaro/scuro/sistema, preferenza Archivia/Elimina, privacy e disclaimer.
- Introduzione e guida Home in cinque passi, con possibilità di saltare.
- Notifiche locali riepilogative delle scadenze e percorso per riabilitarle
  dalle impostazioni Android; il tap apre Home.

MediShelf è uno strumento organizzativo. **Reminder di assunzione o terapia,
prescrizioni, consigli posologici, diagnosi e supporto clinico sono esclusi.**

## Cosa resta escluso

Armadietti multipli, profili persona, gestione avanzata di lotti/confezioni,
storico movimenti, lista acquisti, export/import, backup gestito dall'app,
account, sincronizzazione, condivisione, barcode/OCR e allegati.
Materiali store completi e pubblicazione pubblica non fanno parte della #35.

## Limiti noti

- Controllo notifiche giornaliero affidato ad Android, senza orario garantito.
  Doze, risparmio energetico, arresto forzato, permesso negato o canale
  disabilitato possono ritardare o impedire il riepilogo. Salvare una voce
  non genera subito una nuova notifica.
- Nessun aggiornamento automatico a mezzanotte degli stati di una schermata
  rimasta aperta. Il Dettaglio rilegge le preferenze durante caricamento e
  operazioni, senza osservarle continuamente.
- Svuotamento Archivio non atomico: un errore può lasciare un'eliminazione
  parziale. Gestione degli errori Impostazioni ancora incompleta.
- Nessuna funzione applicativa per esportare o recuperare l'inventario.
  Il backup di sistema Android è consentito dal manifest con regole iniziali:
  non è un servizio cloud MediShelf né una garanzia di ripristino.
  La disinstallazione elimina i dati locali.
- Restano avvisi lint, schema Room non esportato e nessuna migrazione evolutiva.
  Le prove manuali su emulatore Android 16/API 36.1 non certificano tutti i
  dispositivi supportati. L'AAB non è ancora validato da Play Console.

## Verifiche disponibili

La #33 ha registrato **116 test JVM e 65 test strumentali** superati su
emulatore Android 16/API 36.1: dominio, quantità opzionale, CRUD, filtri,
conteggi, archivio/ripristino, Room, schermate e navigazione.
La CI include un emulatore API 29 isolato; gli esiti remoti vanno letti nei
report della singola esecuzione.

Per alpha02, il 26 ottobre 2026 sono registrati 116 test JVM superati,
lint release senza errori (121 avvisi), firma APK/AAB verificata e prove
manuali di inserimento/modifica, quantità, ricerca/filtri, scadenze, archivio,
preferenze persistenti, offline, permessi, notifica con job forzato e
persistenza del job dopo riavvio. Aggiornamento firmato alpha01 → alpha02
senza perdita di inventario o preferenze.

Salta non è stato ripetuto manualmente sull'APK firmato, pur essendo coperto
dai test automatici. Il job forzato non dimostra la cadenza naturale.
Questi sono esiti già registrati nelle #33/#34: la #35 verifica i documenti
contro i sorgenti, senza dichiarare nuove esecuzioni dei test.

## Installazione e riferimenti

La [checklist alpha](ALPHA_RELEASE_CHECKLIST.md) descrive firma, build,
installazione, aggiornamento e risultati manuali. Gli APK debug CI hanno firma
distinta dall'alpha. Conservare i dati con un aggiornamento richiede stesso
application ID, firma compatibile e codice versione crescente.

La [documentazione tecnica](TECHNICAL_DOCUMENTATION.md) riporta architettura,
schema, navigazione e debito residuo;
la [strategia test](TESTING_STRATEGY.md) elenca suite e comandi riproducibili.
