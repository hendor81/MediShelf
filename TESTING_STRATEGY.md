# TESTING_STRATEGY.md

## Scopo
Definire la strategia di test di **MediShelf** per garantire stabilità, affidabilità e sviluppo incrementale.

La priorità iniziale è coprire bene il nucleo Free: dominio, persistenza locale, ViewModel, navigazione essenziale e flussi UI principali.

## Principi guida
- Testare prima le regole che possono generare errori percepiti dall'utente.
- Privilegiare test piccoli, leggibili e mantenibili.
- Evitare test fragili troppo legati a dettagli grafici interni.
- Ogni issue funzionale rilevante deve prevedere almeno un criterio di verifica.
- Il comportamento atteso deve essere più importante dell'implementazione specifica.

## Livelli di test
### Test di dominio
Sono i test più importanti nella fase iniziale.

Devono coprire:

- classificazione valido / in scadenza / scaduto;
- gestione scadenza assente;
- gestione quantità assente;
- gestione quantità pari a zero;
- gestione scorta bassa;
- archiviazione logica;
- ordinamenti e filtri se implementati nel dominio.

Questi test devono essere rapidi, senza Android framework e senza Room.

### Test dei use case
Devono verificare il coordinamento tra repository e logica applicativa.

Esempi:

- inserire un medicinale valido;
- aggiornare un medicinale;
- archiviare un medicinale;
- recuperare solo medicinali attivi;
- calcolare riepiloghi per Home;
- recuperare medicinali in scadenza.

### Test DAO / Room
Devono verificare che le query persistenti funzionino correttamente.

Coprire almeno:

- insert e read by id;
- update;
- archiviazione logica;
- filtro attivi / archiviati;
- ricerca per nome;
- ordinamento per scadenza;
- gestione campi nullable, in particolare `quantity` e `expirationDate`.

### Test repository
Devono verificare il mapping tra entity e domain e il comportamento del repository concreto.

Coprire almeno:

- mapping completo;
- mapping con campi opzionali null;
- propagazione dati da DAO a Flow;
- operazioni di modifica principali.

### Test ViewModel
Devono verificare stato, eventi e trasformazioni verso UI.

Coprire almeno:

- caricamento iniziale;
- empty state;
- salvataggio con dati minimi;
- validazione nome obbligatorio;
- ricerca inventario;
- archiviazione;
- aggiornamento quantità quando presente;
- comportamento quando quantità assente.

### Test UI Compose
Devono coprire i flussi utente principali, senza diventare troppo granulari.

Coprire almeno:

- apertura Home;
- navigazione a Inventario;
- apertura form inserimento;
- inserimento medicinale con solo nome;
- visualizzazione in lista;
- apertura dettaglio;
- azione modifica tramite icona matita;
- azione cancellazione tramite icona cestino e conferma;
- navigazione a Scadenzario;
- empty state principali.

### Test di navigazione
Devono verificare i passaggi principali definiti in `NAVIGATION_MAP.md` e `SCREEN_CATALOG.md`.

Coprire almeno:

- Home → Inventario;
- Home → Scadenzario;
- Inventario → Dettaglio;
- Dettaglio → Modifica;
- Dettaglio → Archivio, se previsto dal flusso;
- Home → Impostazioni.

## Test sulle notifiche locali
Le notifiche locali vanno testate con attenzione, ma possono essere introdotte dopo la stabilizzazione di dominio e persistenza.

Coprire almeno:

- programmazione notifica per medicinali in scadenza;
- nessuna notifica per medicinale senza scadenza;
- aggiornamento notifiche dopo modifica soglia;
- cancellazione o disattivazione notifiche per medicinale archiviato.

## Convenzioni di naming dei test
Usare nomi descrittivi.

Formato consigliato:

```kotlin
fun shouldReturnExpiredWhenExpirationDateIsBeforeToday()
fun shouldAllowMedicineWithoutQuantity()
fun shouldNotMarkLowStockWhenQuantityIsMissing()
```

Oppure, se il team preferisce nomi in italiano:

```kotlin
fun shouldAllowMedicineWithOnlyName()
```

La lingua dei test può essere inglese per coerenza con codice e naming tecnico, mentre commit e documentazione restano in italiano.

## Dati di test
Creare factory o builder per dati di test ricorrenti.

Esempi:

- `MedicineTestData.validMedicine()`;
- `MedicineTestData.expiredMedicine()`;
- `MedicineTestData.medicineWithoutQuantity()`;
- `MedicineTestData.medicineWithoutExpirationDate()`.

Evitare duplicazione eccessiva nei test.

## Cosa non testare nella prima fase
Nella prima fase evitare o rimandare:

- test pixel-perfect della UI;
- test su feature Pro o Subscription;
- test su sync cloud;
- test su OCR/barcode;
- test su dettagli interni temporanei;
- test troppo rigidi su testi che potrebbero cambiare frequentemente.

## Copertura minima attesa per Milestone
### Milestone 1
- test foundation configurata;
- primi test di dominio;
- eventuali test DAO base.

### Milestone 2
- test CRUD inventario;
- test form inserimento/modifica;
- test ricerca;
- test archiviazione.

### Milestone 3
- test classificazione stato;
- test scadenzario;
- test riepiloghi Home;
- test impostazioni soglia;
- test notifiche base.

### Milestone 4
- test regressivi sui flussi principali;
- test UI sui percorsi Free;
- verifica empty state ed error handling.

## Regola finale
Una feature non è completa se non è verificabile. Quando una issue introduce comportamento utente o regole di dominio, deve includere criteri di accettazione e almeno una forma di test o verifica esplicita.
