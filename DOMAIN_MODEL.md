# DOMAIN_MODEL.md

## Scopo del documento
Definire il modello di dominio della versione Free dell'app, cioè i concetti centrali, le entità, gli stati e le regole di business che devono restare indipendenti dalla UI, da Room e dai dettagli tecnici di persistenza.

Questo documento deve essere consultato prima di implementare entity, repository, use case, ViewModel o schermate che manipolano medicinali, quantità, scadenze o archivio.

## Principi guida del dominio
- Il dominio rappresenta il medicinale come oggetto organizzativo presente nell'armadietto, non come prescrizione o terapia.
- Le regole devono essere semplici, leggibili e testabili.
- Le regole di stato del medicinale non devono vivere nella UI.
- La versione Free deve funzionare offline-first e con un solo armadietto locale.
- Le funzionalità Pro e Subscription non devono contaminare il modello Free, ma il modello deve restare evolvibile.

## Entità principali della versione Free

### Medicine
Rappresenta una voce dell'inventario.

Campi principali consigliati:
- `id`: identificativo univoco locale.
- `name`: nome del medicinale o prodotto.
- `packageForm`: formato o confezione, ad esempio compresse, sciroppo, pomata, spray.
- `quantity`: quantità disponibile, opzionale nella creazione iniziale.
- `quantityUnit`: unità di misura o descrizione della quantità, opzionale e valorizzabile solo quando la quantità è nota.
- `expirationDate`: data di scadenza, opzionale.
- `storageLocation`: luogo di conservazione libero, ad esempio bagno, cucina, casa al mare.
- `notes`: note libere opzionali.
- `lowStockThreshold`: soglia sotto la quale il medicinale è considerato quasi terminato.
- `isArchived`: indica se la voce è uscita dall'inventario attivo.
- `createdAt`: data di creazione.
- `updatedAt`: data dell'ultima modifica.
- `archivedAt`: data di archiviazione, opzionale.

Campi deliberatamente esclusi dalla versione Free:
- più armadietti;
- profili persona;
- gestione lotti avanzata;
- confezioni multiple separate;
- barcode/OCR;
- allegati;
- sync cloud.

## Value object e tipi di supporto

### MedicineId
Identificativo stabile di un medicinale.

In prima versione può essere rappresentato da `Long` o `String`, ma nel dominio va trattato come concetto separato per evitare confusione con altri identificativi.

### Quantity
Rappresenta la quantità disponibile, quando l'utente decide di tracciarla.

La quantità **non è obbligatoria** in fase di inserimento di un medicinale.

Motivazione:
- l'utente potrebbe voler censire rapidamente una confezione senza aprirla;
- per alcune forme, ad esempio flaconi, pomate o spray, la quantità residua è difficile da stimare;
- rendere la quantità obbligatoria aumenterebbe l'attrito proprio nel momento più importante, cioè il primo inserimento;
- l'app deve restare utile anche solo come inventario e scadenzario.

Regole:
- può essere assente;
- se presente, non può essere negativa;
- se presente, può essere zero;
- quando arriva a zero il medicinale può risultare `OUT_OF_STOCK` se non archiviato;
- se assente, l'app non deve calcolare `OUT_OF_STOCK` o scorta bassa per quel medicinale;
- le operazioni di incremento e decremento devono essere disponibili solo quando la quantità è nota oppure devono prima chiedere di valorizzarla;
- la UI deve permettere all'utente di aggiungere o correggere la quantità in un secondo momento.

### ExpirationDate
Rappresenta la data di scadenza del medicinale.

Regole:
- può essere assente;
- se assente, il medicinale deve risultare nello stato `UNKNOWN_EXPIRATION`, salvo che sia esaurito o archiviato;
- se presente, viene confrontata con la data corrente locale.

### StorageLocation
Rappresenta il luogo di conservazione.

Nella versione Free può essere una stringa libera. In futuro potrà evolvere in entità strutturata per armadietti multipli, stanze, case o sottoposizioni.


## Campi obbligatori nella creazione
Per ridurre l'attrito, la creazione manuale di un medicinale deve richiedere il minor numero possibile di dati.

Campi obbligatori nella versione Free:
- `name`.

Campi consigliati ma non obbligatori:
- `expirationDate`;
- `storageLocation`;
- `packageForm`;
- `quantity`;
- `quantityUnit`.

Il form può incoraggiare l'inserimento di scadenza, posizione e quantità, ma non deve bloccare il salvataggio se la quantità non è disponibile.

## Stati del medicinale

### MedicineStatus
Stati minimi:
- `VALID`: medicinale attivo, con quantità disponibile e scadenza futura oltre la soglia.
- `EXPIRING_SOON`: medicinale attivo, con quantità disponibile e scadenza entro la soglia configurata.
- `EXPIRED`: medicinale attivo, con data di scadenza già superata.
- `OUT_OF_STOCK`: medicinale attivo ma con quantità pari a zero.
- `UNKNOWN_EXPIRATION`: medicinale attivo con scadenza non indicata.
- `ARCHIVED`: medicinale archiviato, escluso dall'inventario attivo.

## Priorità degli stati
Quando più condizioni sono vere contemporaneamente, applicare questa priorità:

1. `ARCHIVED`
2. `OUT_OF_STOCK`
3. `UNKNOWN_EXPIRATION`
4. `EXPIRED`
5. `EXPIRING_SOON`
6. `VALID`

Motivazione:
- un medicinale archiviato non deve più comparire come problema attivo;
- un medicinale a quantità zero è prima di tutto esaurito;
- una scadenza assente va evidenziata separatamente;
- scaduto ha priorità su in scadenza;
- valido è lo stato residuale quando non esistono criticità.

## Regole di calcolo dello stato

Input necessari:
- medicinale;
- data corrente;
- soglia giorni per lo stato in scadenza.

Regole:
- se `isArchived == true`, stato `ARCHIVED`;
- altrimenti se `quantity == 0`, stato `OUT_OF_STOCK`;
- altrimenti se `expirationDate == null`, stato `UNKNOWN_EXPIRATION`;
- altrimenti se `expirationDate < today`, stato `EXPIRED`;
- altrimenti se `expirationDate <= today + expiringThresholdDays`, stato `EXPIRING_SOON`;
- altrimenti stato `VALID`.

La funzione di calcolo deve essere pura o quasi pura e facilmente testabile.

## Regole su creazione e modifica

### Creazione medicinale
Campi minimi richiesti:
- nome;
- quantità.

Campi consigliati ma opzionali:
- formato/confezione;
- data di scadenza;
- luogo di conservazione;
- note.

Regole:
- il nome non può essere vuoto;
- la quantità non può essere negativa;
- la soglia di scorta bassa, se presente, non può essere negativa;
- dopo il salvataggio deve essere calcolabile lo stato.

### Modifica medicinale
Regole:
- la modifica aggiorna `updatedAt`;
- l'identificativo non cambia;
- la modifica può cambiare lo stato derivato;
- l'archiviazione non deve cancellare fisicamente il record.

### Cancellazione medicinale
Nella versione Free la cancellazione definitiva deve essere secondaria rispetto all'archiviazione.

Regole:
- l'azione distruttiva richiede sempre conferma esplicita dell'utente;
- l'eliminazione fisica, se prevista, deve essere accessibile solo da aree secondarie come Archivio o Dettaglio;
- l'archiviazione è l'azione preferita per rimuovere un medicinale dall'inventario attivo.

## Regole su quantità

Operazioni minime:
- incremento;
- decremento;
- impostazione diretta;
- marcatura come terminato.

Regole:
- decrementare sotto zero non è consentito;
- impostare quantità a zero equivale a rendere il medicinale esaurito, salvo archiviazione;
- la UI può offrire azioni rapide, ma la validazione deve restare nel dominio o negli use case.

## Regole su scadenze

### Scadenza presente
Una data di scadenza presente consente il calcolo di `VALID`, `EXPIRING_SOON` o `EXPIRED`.

### Scadenza assente
La scadenza assente è ammessa, ma deve essere visibile come informazione incompleta.

Regole:
- i medicinali senza data non devono essere confusi con medicinali validi;
- devono poter comparire in una sezione dedicata dello scadenzario;
- non generano notifica di scadenza specifica, salvo futura scelta esplicita.

## Regole su archivio

Un medicinale archiviato:
- non compare nella lista inventario attiva;
- non viene conteggiato nella Home come medicinale attivo;
- non produce notifiche di scadenza;
- resta consultabile nell'Archivio;
- può essere ripristinato, se previsto dalla UI.

## Configurazione Free

### UserSettings
Impostazioni minime:
- `expiringThresholdDays`: soglia giorni per considerare un medicinale in scadenza.
- `notificationsEnabled`: abilita o disabilita le notifiche locali.
- `preferArchiveOverDelete`: preferenza comportamentale o copy di prodotto che orienta l'utente verso l'archiviazione.

Valori iniziali consigliati:
- `expiringThresholdDays = 30`;
- `notificationsEnabled = true`, compatibilmente con permessi Android;
- `preferArchiveOverDelete = true`.

## Use case di dominio iniziali

Use case consigliati per la versione Free:
- `AddMedicineUseCase`
- `UpdateMedicineUseCase`
- `GetMedicineDetailUseCase`
- `GetActiveMedicinesUseCase`
- `SearchMedicinesUseCase`
- `ArchiveMedicineUseCase`
- `RestoreArchivedMedicineUseCase`
- `DeleteMedicineUseCase`
- `IncrementMedicineQuantityUseCase`
- `DecrementMedicineQuantityUseCase`
- `MarkMedicineAsOutOfStockUseCase`
- `CalculateMedicineStatusUseCase`
- `GetExpiringMedicinesUseCase`
- `GetExpiredMedicinesUseCase`
- `GetDashboardSummaryUseCase`
- `UpdateSettingsUseCase`

## Dashboard summary
La Home non deve duplicare logica di calcolo nelle Composable.

Il dominio o un use case dedicato deve produrre un riepilogo con almeno:
- numero medicinali attivi;
- numero medicinali in scadenza;
- numero medicinali scaduti;
- numero medicinali quasi terminati o esauriti;
- numero medicinali senza data, se scelto come indicatore utile.

## Confini Free / Pro / Subscription

### Free
Il dominio Free copre:
- un solo inventario locale;
- medicinali attivi e archiviati;
- quantità;
- scadenze;
- stato derivato;
- impostazioni minime;
- notifiche locali.

### Pro
Il dominio potrà essere esteso con:
- armadietti multipli;
- categorie e tag;
- profili persona;
- confezioni multiple dello stesso medicinale;
- storico movimenti completo;
- lista acquisti;
- export/import.

### Subscription
Il dominio potrà essere esteso con:
- account;
- sincronizzazione;
- condivisione familiare;
- barcode/OCR;
- allegati;
- insight intelligenti.

## Criteri di test del dominio
Le regole di dominio devono essere coperte da test unitari.

Test minimi consigliati:
- calcolo stato valido;
- calcolo stato in scadenza;
- calcolo stato scaduto;
- calcolo stato senza data;
- calcolo stato esaurito;
- priorità archiviato su tutti gli altri stati;
- decremento quantità non sotto zero;
- archiviazione esclusa dai conteggi dashboard;
- medicinali senza scadenza esclusi dalle notifiche di scadenza.

## Regola finale
Se una nuova funzionalità richiede di modificare il dominio, prima verificare che:
- non introduca logica medico-clinica;
- non complichi il flusso Free oltre il necessario;
- sia coerente con la roadmap Free / Pro / Subscription;
- sia descrivibile con regole semplici e testabili.
