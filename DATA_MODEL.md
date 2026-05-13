# DATA_MODEL.md

## Scopo
Questo documento definisce il modello dati persistente iniziale di **MediShelf** per la versione Free.

Il documento guida l'implementazione con Room, il mapping verso il dominio e le future migrazioni. Non sostituisce `DOMAIN_MODEL.md`: lo traduce in strutture persistenti, query e regole tecniche.

## Principi guida
- La versione Free è **offline-first**.
- Room è la fonte dati primaria locale.
- Il modello dati deve supportare bene inventario, scadenze, quantità opzionale e archiviazione logica.
- Le entity Room non devono diventare automaticamente modelli UI.
- Le decisioni devono restare semplici, evolvibili e compatibili con future funzioni Pro/Subscription.

## Entità persistenti iniziali
Per la versione Free è sufficiente una singola tabella principale:

- `medicines`

Entità future, non obbligatorie nella Free iniziale:

- `medicine_movements`
- `cabinets`
- `locations`
- `categories`
- `person_profiles`
- `shopping_items`
- `attachments`

## Tabella `medicines`
Rappresenta una voce dell'inventario locale.

### Campi consigliati
| Campo | Tipo Kotlin | Tipo Room indicativo | Obbligatorio | Note |
|---|---:|---:|---:|---|
| `id` | `Long` | `INTEGER PRIMARY KEY AUTOINCREMENT` | sì | Identificativo tecnico locale. |
| `name` | `String` | `TEXT NOT NULL` | sì | Unico campo realmente obbligatorio in inserimento. |
| `form` | `String?` | `TEXT` | no | Esempi: compresse, sciroppo, pomata, spray. |
| `quantity` | `Double?` | `REAL` | no | Quantità opzionale. Se assente, non si calcola scorta bassa/esaurito. |
| `quantityUnit` | `String?` | `TEXT` | no | Esempi: confezioni, compresse, ml, g, unità. |
| `expirationDate` | `LocalDate?` | `TEXT` o `INTEGER` via converter | no | Se assente, stato legato alla scadenza = senza data. |
| `storageLocation` | `String?` | `TEXT` | no | Luogo libero: bagno, cucina, casa al mare. |
| `notes` | `String?` | `TEXT` | no | Note utente. |
| `lowStockThreshold` | `Double?` | `REAL` | no | Soglia opzionale per scorta bassa. |
| `isArchived` | `Boolean` | `INTEGER NOT NULL` | sì | Archiviazione logica. Default `false`. |
| `createdAt` | `Instant` | `INTEGER` o `TEXT` | sì | Data creazione record. |
| `updatedAt` | `Instant` | `INTEGER` o `TEXT` | sì | Data ultimo aggiornamento. |
| `archivedAt` | `Instant?` | `INTEGER` o `TEXT` | no | Valorizzato quando `isArchived = true`. |

## Campo obbligatorio minimo
In creazione, il solo dato obbligatorio è:

- `name`

Tutti gli altri campi possono essere compilati subito o in un secondo momento.

Questa scelta riduce l'attrito di inserimento: l'utente può censire rapidamente un medicinale anche senza aprire la confezione o stimare quantità difficili da misurare.

## Quantità opzionale
La quantità deve essere opzionale.

### Se `quantity` è valorizzata
Il sistema può:

- mostrare la quantità residua;
- consentire incremento/decremento rapido;
- valutare stato `ESaurito` se quantità pari o inferiore a zero;
- valutare scorta bassa se è presente una soglia.

### Se `quantity` non è valorizzata
Il sistema deve:

- mostrare l'informazione come non indicata;
- non classificare il medicinale come esaurito;
- non classificare il medicinale come quasi terminato;
- continuare a gestire normalmente scadenza, posizione e archivio.

## Scadenza opzionale
La scadenza deve essere opzionale.

### Se `expirationDate` è valorizzata
Il dominio calcola:

- valido;
- in scadenza;
- scaduto.

### Se `expirationDate` non è valorizzata
Il dominio assegna uno stato informativo di tipo:

- senza data.

Questo stato non deve bloccare l'uso dell'app, ma deve essere distinguibile in inventario, dettaglio e scadenzario.

## Stato del medicinale
Lo stato del medicinale non deve essere persistito come fonte primaria, almeno nella versione iniziale.

Lo stato va calcolato dal dominio a partire da:

- `quantity`;
- `expirationDate`;
- soglia giorni di scadenza imminente;
- `lowStockThreshold`;
- `isArchived`.

Persistire lo stato rischierebbe incoerenze se cambiano data corrente, soglie o quantità.

## Archiviazione logica
L'archiviazione iniziale è logica.

Un medicinale archiviato:

- resta nella tabella `medicines`;
- ha `isArchived = true`;
- ha preferibilmente `archivedAt` valorizzato;
- non compare nelle liste principali attive;
- può comparire nella schermata Archivio;
- può essere ripristinato.

La cancellazione fisica è un'azione distinta, secondaria, da confermare sempre.

## Query Room minime
Il DAO iniziale dovrebbe supportare almeno:

- inserimento medicinale;
- aggiornamento medicinale;
- recupero per `id`;
- lista medicinali attivi;
- lista medicinali archiviati;
- ricerca per nome tra attivi;
- filtro per posizione;
- filtro grezzo per scadenza, quando utile;
- archiviazione logica;
- ripristino da archivio;
- cancellazione fisica confermata.

## Indici consigliati
Indici iniziali consigliati:

- `name`, per ricerca e ordinamento;
- `expirationDate`, per scadenzario;
- `isArchived`, per separare inventario attivo e archivio;
- opzionale: `storageLocation`, per filtro luogo.

## Mapping Entity → Domain
Le entity Room devono essere convertite in modelli domain.

Esempio concettuale:

```text
MedicineEntity -> Medicine
Medicine -> MedicineEntity
```

Il mapping deve:

- evitare che dettagli Room entrino nella UI;
- normalizzare campi testuali se necessario;
- mantenere chiara la differenza tra dati persistiti e stato calcolato;
- gestire conversioni di date e numeri in modo centralizzato.

## Type converter
Room richiederà converter per tipi non primitivi, ad esempio:

- `LocalDate`;
- `Instant`.

Scelta consigliata:

- salvare `LocalDate` come stringa ISO `yyyy-MM-dd`;
- salvare `Instant` come epoch millis o stringa ISO, scegliendo una sola convenzione e mantenendola stabile.

## Migrazioni
Anche se la prima versione può partire con database semplice, lo schema deve essere pensato per migrazioni future.

Regole:

- non rinominare campi senza migrazione esplicita;
- non cambiare significato a un campo già usato;
- preferire aggiunte nullable o con default;
- documentare ogni cambio schema in `TECHNICAL_DOCUMENTATION.md`.

## Evoluzioni future
### Pro
Possibili nuove tabelle:

- `cabinets` per armadietti multipli;
- `categories` e/o `tags`;
- `person_profiles`;
- `medicine_batches` per confezioni multiple con scadenze diverse;
- `medicine_movements` per storico.

### Subscription
Possibili campi o tabelle aggiuntive:

- identificativi remoti;
- stato sincronizzazione;
- timestamp di ultima sync;
- allegati;
- ownership e condivisione familiare.

## Regola finale
Il modello dati deve favorire l'inserimento rapido e la consultazione affidabile. Ogni campo obbligatorio aggiunto deve essere giustificato da un reale bisogno del nucleo Free.
