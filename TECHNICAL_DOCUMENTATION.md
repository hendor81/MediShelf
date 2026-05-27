# TECHNICAL_DOCUMENTATION.md

## Scopo
Raccogliere la documentazione tecnica viva del progetto Android man mano che l'implementazione procede.

Questo documento non sostituisce `ARCHITECTURES_RULES.md` o `CODING_CONVENTIONS.md`: li concretizza descrivendo le scelte effettivamente adottate nel codice.

## Quando aggiornarlo
Aggiornare questo documento:
- alla fine di ogni milestone;
- quando viene introdotta una decisione architetturale importante;
- quando cambia la struttura dei package;
- quando cambia lo schema dati Room;
- quando vengono introdotti nuovi pattern ricorrenti;
- quando viene rilevato debito tecnico significativo.

## Contenuti minimi

### Struttura progetto
Descrivere package, moduli o cartelle principali effettivamente presenti.

### Architettura adottata
Descrivere come sono separati UI, dominio, dati, dependency injection e navigazione.

### Modello di dominio implementato
Elencare entità, value object, enum di stato e use case disponibili.

### Persistenza locale
Documentare:
- entity Room;
- DAO;
- migrazioni;
- schema corrente;
- eventuali vincoli noti.

### Navigazione
Documentare route implementate, argomenti e flussi principali.

### Notifiche locali
Documentare strategia tecnica, permessi, scheduling e limiti Android rilevanti.

### Test
Documentare:
- test unitari presenti;
- test UI presenti;
- aree critiche coperte;
- aree ancora scoperte.

### Debito tecnico e decisioni aperte
Annotare compromessi, TODO rilevanti e decisioni da rivalutare.

## Regola operativa
Ogni milestone del backlog deve prevedere una issue o sotto-issue di aggiornamento documentazione tecnica, da chiudere solo dopo aver allineato questo file allo stato reale del progetto.


## Stato attuale - Milestone 1

La Milestone 1 ha prodotto le fondamenta tecniche dell'app Android, non le
feature complete di inventario. Il codice e' organizzato per sostenere la
versione Free offline-first con Compose, Room, Hilt, dominio separato e una
navigation shell navigabile.

### Struttura progetto
Il progetto e' monomodulo e contiene solo il modulo `app`, come previsto per la
prima fase.

Struttura principale effettiva:
- `it.hendorsoftware.medishelf`: application, activity e root composable;
- `core.designsystem`: tema, token e componenti Compose riusabili;
- `core.time`: provider testabile della data corrente;
- `core.notification`: package predisposto, senza scheduler concreti;
- `data.local.dao`: DAO Room dei medicinali;
- `data.local.database`: database Room e type converter temporali;
- `data.local.entity`: entity persistente `MedicineEntity`;
- `data.mapper`: mapping tra Room e domain;
- `data.repository`: repository locale basato su Room;
- `domain.model`: modelli e value object del dominio;
- `domain.repository`: contratto repository;
- `domain.rules`: calcolo dello stato del medicinale;
- `domain.usecase`: use case minimi gia disponibili;
- `di`: moduli Hilt per database, repository, use case, time e notifiche;
- `feature.*`: schermate Compose placeholder per le schermate Free;
- `navigation`: route e NavHost principali.

### Stack configurato
Stack effettivamente configurato:
- Kotlin `2.2.10`;
- Android Gradle Plugin `9.2.1`;
- compile SDK `36.1`, target SDK `36`, min SDK `24`;
- Java compatibility `11` con core library desugaring per `java.time`;
- Jetpack Compose con Compose BOM `2026.02.01`;
- Material 3;
- Navigation Compose `2.9.8`;
- Hilt `2.59.2` con KSP;
- Room `2.8.4` con KSP;
- Coroutines test `1.10.2`;
- JUnit 4, AndroidX Test, Espresso e Compose UI Test.

### Entry point applicativo
`MediShelfApplication` abilita Hilt tramite `@HiltAndroidApp`.

`MainActivity` e' annotata con `@AndroidEntryPoint`, abilita edge-to-edge e
monta `MediShelfApp` dentro `MediShelfTheme`.

`MediShelfApp` ospita il grafo `MediShelfNavHost` e ha una Preview Compose.

### Design system iniziale
La base visuale Compose e' centralizzata nel package `core.designsystem`.

Elementi introdotti:
- `MediShelfTheme`, applicato da `MainActivity`;
- palette Material 3 chiara/scura in `Color.kt` e `Theme.kt`;
- colori semantici per stati medicinali tramite `MediShelfStatusColors`;
- scala tipografica base in `Type.kt`;
- shape condivise in `Shape.kt`;
- token dimensionali riusabili in `Dimens.kt`;
- risorse colore in `colors.xml`;
- stringhe UI e stringhe di Preview in `strings.xml`.

Componenti riusabili disponibili:
- `MedicineStatusBadge`;
- `EmptyState`;
- `ConfirmDeleteDialog`;
- `MediShelfTopAppBar`;
- `MediShelfPlaceholderScreen`.

Ogni componente introdotto ha almeno una Preview Compose locale e usa risorse o
token centralizzati per testi, colori e dimensioni.

### Navigation shell
La navigazione e' gestita con Navigation Compose nel package `navigation`.

Route implementate:
- `home`;
- `inventory`;
- `expiry`;
- `archive`;
- `settings`;
- `medicine/add`;
- `medicine/{medicineId}`;
- `medicine/{medicineId}/edit`.

`Home` e' la destinazione iniziale. Le schermate con argomento ricevono solo
`medicineId` come stringa di route. La navigation shell collega i placeholder
principali e copre i passaggi essenziali:
- Home -> Inserimento;
- Home -> Inventario;
- Home -> Scadenzario;
- Home -> Impostazioni;
- Inventario/Scadenzario/Archivio -> Dettaglio esempio;
- Dettaglio -> Modifica;
- Dettaglio -> Archivio;
- Inserimento completato -> Inventario.

Limite noto: in assenza di dati reali, Inventario, Scadenzario e Archivio
aprono il dettaglio usando un id dimostrativo interno al NavHost.

### Schermate implementate
Le feature package esistono per:
- `home`;
- `inventory`;
- `medicineform`;
- `medicinedetail`;
- `expiry`;
- `archive`;
- `settings`.

Le schermate sono placeholder navigabili, con `Scaffold`, top app bar, testi da
`strings.xml`, callback esplicite e Preview. Non sono ancora presenti ViewModel,
UiState reali, form funzionanti, liste persistenti o azioni dati collegate a
Room.

### Modello di dominio implementato
Il dominio ruota intorno a `Medicine`.

Modelli disponibili:
- `Medicine`;
- `MedicineId`, value class basata su `Long`;
- `QuantityInfo`;
- `MedicineStatus`.

Campi principali di `Medicine`:
- `id`;
- `name`;
- `packageForm`;
- `quantity`;
- `expirationDate`;
- `storageLocation`;
- `notes`;
- `isArchived`;
- `createdAt`;
- `updatedAt`;
- `archivedAt`.

Regole gia enforce nel modello:
- il nome non puo essere vuoto;
- la quantita nota non puo essere negativa;
- la soglia di scorta bassa non puo essere negativa;
- `archivedAt` puo essere valorizzato solo se `isArchived` e' true.

`MedicineStatusCalculator` calcola lo stato derivato senza persisterlo. Stati
effettivamente presenti:
- `VALID`;
- `EXPIRING`;
- `EXPIRED`;
- `OUT_OF_STOCK`;
- `NO_EXPIRATION_DATE`.

La regola applicata considera, nell'ordine: quantita nota a zero, scadenza
assente, scadenza passata, scadenza entro soglia inclusiva, validita residua.
Il calcolo usa `DateProvider`, quindi e' testabile con una data fissa.

Nota importante: il modello `Medicine` contiene `isArchived`, ma lo enum
`MedicineStatus` non contiene ancora uno stato `ARCHIVED`. L'archiviazione e'
gestita come filtro e stato persistente, non come stato visuale/domain
calcolato.

### Use case e repository
Contratto domain:
- `MedicineRepository`.

Implementazione data:
- `LocalMedicineRepository`.

Use case disponibili:
- `AddMedicineUseCase`;
- `UpdateMedicineUseCase`;
- `ArchiveMedicineUseCase`;
- `DeleteMedicineUseCase`;
- `GetActiveMedicinesUseCase`;
- `GetArchivedMedicinesUseCase`;
- `GetMedicineByIdUseCase`.

`MedicineNameValidator` normalizza il nome con trim e rifiuta valori vuoti.
`AddMedicineUseCase` usa `MedicineId(0L)` come sentinel per nuove voci, poi
delega al repository. `UpdateMedicineUseCase` aggiorna `updatedAt` e mantiene
la validazione del nome nel domain.

### Persistenza locale Room
Room e' la fonte dati locale configurata per la versione Free.

Database:
- classe `MediShelfDatabase`;
- nome file `medishelf.db`;
- versione schema `1`;
- `exportSchema = false`.

Tabella implementata:
- `medicines`.

Entity:
- `MedicineEntity`;
- primary key `id: Long` autogenerata;
- campi nullable per `packageForm`, `quantity`, `quantityUnit`,
  `expirationDate`, `storageLocation`, `notes`, `lowStockThreshold`,
  `archivedAt`;
- campi obbligatori per `name`, `isArchived`, `createdAt`, `updatedAt`.

Indici presenti:
- `name`;
- `expirationDate`;
- `isArchived`;
- `storageLocation`.

DAO disponibile:
- `observeActiveMedicines()`;
- `observeArchivedMedicines()`;
- `getMedicineById(id)`;
- `insertMedicine(entity)`;
- `updateMedicine(entity)`;
- `archiveMedicine(id, archivedAt)`;
- `deleteMedicine(entity)`;
- `deleteMedicineById(id)`.

I converter temporali salvano `LocalDate` come stringa ISO-8601 e `Instant`
come epoch millis.

Non sono ancora presenti migrazioni, coerentemente con schema v1 iniziale.

### Mapping
`MedicineMapper` centralizza le conversioni:
- `MedicineEntity -> Medicine`;
- `Medicine -> MedicineEntity`.

La quantita e' costruita come `QuantityInfo` solo quando `MedicineEntity.quantity`
e' valorizzata. In assenza di quantita, anche `quantityUnit` e
`lowStockThreshold` restano fuori dal modello domain.

### Dependency Injection
Hilt e' configurato con:
- `DatabaseModule`, per `MediShelfDatabase` e `MedicineDao`;
- `RepositoryModule`, per collegare `MedicineRepository` a
  `LocalMedicineRepository`;
- `TimeModule`, per collegare `DateProvider` a `SystemDateProvider`;
- `UseCaseModule`, attualmente vuoto perche i use case usano constructor
  injection;
- `NotificationModule`, predisposto ma senza provider concreti.

### Notifiche locali
La Milestone 1 non implementa notifiche locali. Esistono solo:
- package `core.notification`;
- `NotificationModule` vuoto come punto di estensione DI.

La selezione dei medicinali da notificare, lo scheduling Android e la gestione
permessi restano fuori dallo stato corrente.

### Test presenti
Test unitari JVM:
- `MedicineModelTest`, per validazioni di modello, quantita opzionale,
  scadenza opzionale e stati minimi;
- `MedicineStatusCalculatorTest`, per validita, in scadenza, scaduto,
  esaurito, scadenza assente, quantita assente e soglia inclusiva;
- `MedicineMapperTest`, per mapping completo, mapping con campi opzionali null
  e campi di archivio;
- `MedicineUseCaseTest`, per validazione/trim del nome, update, archiviazione,
  cancellazione e stream attivi/archiviati;
- `FakeMedicineRepository`, `FakeDateProvider` e `MainDispatcherRule` come
  supporto ai test.

Test strumentali Android:
- `MedicineDaoTest`, per insert/read, campi nullable, filtro attivi,
  archiviazione logica, filtro archiviati e cancellazione fisica;
- `NavigationSmokeTest`, per apertura Home e navigazione Home -> Inventario,
  Home -> Scadenzario, Home -> Impostazioni.

Restano presenti i test template `ExampleUnitTest` ed
`ExampleInstrumentedTest`, non ancora rimossi.

### GitHub Actions
La pipeline `.github/workflows/android-ci.yml` e' configurata su push e pull
request verso `master`.

Job attuale:
- checkout repository;
- setup JDK Temurin 21;
- setup Gradle;
- `chmod +x gradlew`;
- `./gradlew build`;
- `./gradlew test`.

Limite noto: la pipeline non esegue ancora esplicitamente `lintDebug` o test
strumentali. Il comando `build` copre la build locale prevista da Gradle, mentre
i test strumentali richiedono un ambiente Android dedicato non configurato in
CI.

### Decisioni tecniche consolidate
- Il progetto resta monomodulo nella Milestone 1.
- I layer sono separati per package, non per moduli Gradle.
- Il domain non dipende da Compose, Room o Android UI.
- Room e' la fonte dati locale offline-first.
- Lo stato del medicinale e' derivato e non persistito.
- Quantita e scadenza sono opzionali.
- L'archiviazione e' logica tramite `isArchived` e `archivedAt`.
- La cancellazione fisica e' distinta dall'archiviazione.
- `DateProvider` astrae la data corrente per rendere testabile il dominio.
- I testi visibili sono centralizzati in `strings.xml`.
- Colori, shape, tipografia e dimensioni sono centralizzati nel design system.
- Le schermate della Milestone 1 sono placeholder navigabili, non feature
  complete.

### Deviazioni e allineamenti da monitorare
- `DOMAIN_MODEL.md` descrive anche lo stato `ARCHIVED`; il codice attuale non lo
  include in `MedicineStatus`. L'archiviazione e' comunque rappresentata nel
  modello e nello schema dati.
- Alcuni documenti usano nomi stato come `EXPIRING_SOON` o
  `UNKNOWN_EXPIRATION`; il codice corrente usa `EXPIRING` e
  `NO_EXPIRATION_DATE`.
- `ANDROID_ARCHITECTURE.md` mostra esempi con id stringa in alcuni snippet, ma
  l'implementazione reale usa `Long` autogenerato da Room e `MedicineId(Long)`
  nel dominio.
- La CI reale usa branch `master` e JDK 21, mentre gli esempi documentali
  citavano anche `main`/`develop` e JDK 17.
- `lintDebug` non e' ancora uno step esplicito della pipeline.

### Debito tecnico noto
- Implementare ViewModel, UiState e collegamento reale tra UI, use case e Room.
- Sostituire gli id dimostrativi della navigation shell con id reali provenienti
  dalle liste.
- Decidere se introdurre `ARCHIVED` in `MedicineStatus` oppure mantenere
  l'archiviazione come dimensione separata e documentarlo nei modelli guida.
- Allineare definitivamente il naming degli stati tra documenti e codice.
- Aggiungere restore da archivio quando la feature Archivio diventera'
  funzionale.
- Implementare ricerca, filtri, ordinamenti e dashboard summary.
- Implementare notifiche locali e gestione permessi Android.
- Valutare `exportSchema = true` e cartella schema Room quando inizieranno le
  migrazioni.
- Estendere la CI con lint esplicito ed eventuale esecuzione di test
  strumentali su emulatore o servizio dedicato.
- Rimuovere o sostituire i test template Android generati dallo scaffold.


## Stato attuale - Milestone 2

La Milestone 2 ha reso funzionale il nucleo di inventario base. Le aree
Inventario, Inserimento/Modifica, Dettaglio medicinale e Archivio non sono piu
solo placeholder: osservano o modificano i dati locali tramite ViewModel, use
case e repository Room. Home, Scadenzario e Impostazioni restano invece
placeholder navigabili.

### Scope funzionale implementato
Feature collegate a dati reali:
- inserimento di un medicinale con solo nome obbligatorio;
- modifica di un medicinale esistente;
- consultazione dell'inventario attivo;
- ricerca per nome nell'inventario;
- filtro inventario per stato calcolato;
- apertura del dettaglio da Inventario e Archivio usando id reali;
- incremento e decremento rapido della quantita quando gia indicata;
- archiviazione logica dal dettaglio;
- cancellazione definitiva dal dettaglio dopo conferma;
- consultazione dell'Archivio con lista di medicinali non piu attivi.

Feature ancora placeholder o fuori dalla Milestone 2:
- Home / Dashboard con riepilogo reale;
- Scadenzario con sezioni in scadenza, scaduti e senza data;
- Impostazioni reali;
- notifiche locali;
- ripristino da archivio;
- ordinamenti configurabili;
- stato o filtro dedicato alla scorta bassa.

### Feature Inventario
`InventoryRoute` usa `InventoryViewModel` tramite Hilt e passa a
`InventoryScreen` uno `InventoryUiState` immutabile.

Il ViewModel:
- osserva `GetActiveMedicinesUseCase`;
- combina stream medicinali, query di ricerca e filtro selezionato;
- calcola lo stato con `MedicineStatusCalculator`;
- formatta data, quantita e id per la UI;
- espone `hasActiveMedicines` per distinguere inventario vuoto e ricerca senza
  risultati.

La schermata Inventario mostra:
- top app bar con accesso all'Archivio;
- campo ricerca testuale;
- chip di filtro per tutti gli stati domain implementati;
- FAB di aggiunta medicinale;
- lista di card compatte con nome, formato, badge stato, scadenza, quantita e
  luogo quando presenti;
- empty state dedicato per inventario vuoto e nessun risultato.

I filtri disponibili sono:
- tutti;
- valido;
- in scadenza;
- scaduto;
- esaurito;
- senza scadenza.

La ricerca e il filtro vivono nel ViewModel, non nella Composable. Non esiste
ancora un `SearchMedicinesUseCase` separato: per ora la scelta e locale alla
feature, coerente con lo scope piccolo della Milestone 2.

### Feature Inserimento / Modifica
`MedicineFormRoute` usa `MedicineFormViewModel` e abilita la modalita modifica
quando riceve un `medicineId` dalla route `medicine/{medicineId}/edit`.

Il form supporta:
- nome obbligatorio;
- formato/confezione opzionale;
- quantita opzionale;
- unita quantita opzionale;
- soglia scorta bassa opzionale;
- data scadenza opzionale in formato ISO `yyyy-MM-dd`;
- luogo conservazione opzionale;
- note opzionali.

Validazioni implementate:
- nome non vuoto;
- quantita numerica opzionale e non negativa;
- soglia scorta bassa numerica opzionale e non negativa;
- data opzionale valida secondo `LocalDate.parse`.

Valori numerici con virgola vengono normalizzati sostituendo `,` con `.` prima
del parsing. I campi vuoti restano assenti nel domain model. La quantita resta
realmente opzionale: salvare una voce con solo nome e' supportato e testato.

In modifica il ViewModel carica la voce tramite `GetMedicineByIdUseCase`,
precompila i campi, mantiene id e `createdAt`, aggiorna `updatedAt` tramite
`UpdateMedicineUseCase` e mostra uno stato dedicato se l'id non e valido o non
esiste piu.

### Feature Dettaglio medicinale
`MedicineDetailRoute` carica il dato con `MedicineDetailViewModel` partendo
dall'id di navigazione.

Il dettaglio mostra:
- nome;
- formato/confezione;
- badge stato;
- quantita o testo neutro quando assente;
- scadenza o testo neutro quando assente;
- luogo o testo neutro quando assente;
- note o testo neutro quando assenti;
- icona matita per modifica;
- icona cestino per cancellazione definitiva;
- pulsante di archiviazione.

Azioni implementate:
- modifica tramite route `medicine/{medicineId}/edit`;
- aggiunta quantita mancante tramite apertura del form di modifica;
- incremento quantita di 1 quando la quantita e nota;
- decremento quantita di 1 senza scendere sotto zero;
- feedback discreto per quantita aggiornata, quantita assente e quantita gia a
  zero;
- archiviazione logica;
- cancellazione fisica solo dopo dialog di conferma.

`UpdateMedicineQuantityUseCase` mantiene la quantita opzionale: se il valore non
e stato impostato non crea una quantita fittizia, ma restituisce
`MissingQuantity`. Il decremento a zero restituisce `AlreadyZero` quando non
puo applicare ulteriori riduzioni.

### Feature Archivio
`ArchiveRoute` usa `ArchiveViewModel` e osserva `GetArchivedMedicinesUseCase`.

La schermata Archivio mostra:
- stato di caricamento;
- empty state quando non ci sono medicinali archiviati;
- copy che la qualifica come area secondaria;
- lista di card con badge `Archiviato`;
- dati opzionali mostrati solo quando presenti;
- apertura del dettaglio con id reale della voce archiviata.

L'Archivio e attualmente consultivo: non esiste ancora un use case o un'azione UI
per ripristinare una voce nell'inventario attivo.

### Dominio e use case
I modelli domain restano quelli introdotti nella Milestone 1:
- `Medicine`;
- `MedicineId`;
- `QuantityInfo`;
- `MedicineStatus`.

Use case disponibili nello stato corrente:
- `AddMedicineUseCase`;
- `UpdateMedicineUseCase`;
- `ArchiveMedicineUseCase`;
- `DeleteMedicineUseCase`;
- `GetActiveMedicinesUseCase`;
- `GetArchivedMedicinesUseCase`;
- `GetMedicineByIdUseCase`;
- `UpdateMedicineQuantityUseCase`.

`MedicineNameValidator` continua a normalizzare il nome tramite trim.
`MedicineStatusCalculator` continua a calcolare lo stato derivato senza
persistenza. La soglia in scadenza usata dalle feature e
`MediShelfDefaults.ExpiringThresholdDays`.

### Persistenza locale
Lo schema Room non e cambiato nella Milestone 2:
- database `MediShelfDatabase`;
- file `medishelf.db`;
- versione schema `1`;
- `exportSchema = false`;
- tabella `medicines`;
- id `Long` autogenerato;
- archiviazione logica con `isArchived` e `archivedAt`;
- `LocalDate` salvato come stringa ISO;
- `Instant` salvato come epoch millis.

Le query DAO attualmente usate dalle feature sono:
- `observeActiveMedicines`, ordinata per nome case-insensitive;
- `observeArchivedMedicines`, ordinata per data di archiviazione/aggiornamento
  decrescente e poi per nome;
- `getMedicineById`;
- `insertMedicine`;
- `updateMedicine`;
- `archiveMedicine`;
- `deleteMedicineById`.

Non sono state introdotte migrazioni, perche lo schema persistente e ancora
alla versione iniziale.

### Navigazione aggiornata
Le route restano:
- `home`;
- `inventory`;
- `expiry`;
- `archive`;
- `settings`;
- `medicine/add`;
- `medicine/{medicineId}`;
- `medicine/{medicineId}/edit`.

Differenze rispetto alla Milestone 1:
- Inventario apre il dettaglio con l'id reale della card selezionata;
- Archivio apre il dettaglio con l'id reale della card archiviata;
- il form di inserimento salva e torna all'Inventario;
- il form di modifica salva e torna al Dettaglio;
- il dettaglio archiviato naviga verso Archivio;
- il dettaglio cancellato torna indietro.

Limite residuo: lo Scadenzario e ancora placeholder e continua ad aprire il
dettaglio con `SAMPLE_MEDICINE_ID`, quindi non e collegato a medicinali reali.

### UI e design system
La Milestone 2 riusa il design system esistente:
- `MediShelfTopAppBar`;
- `MedicineStatusBadge`;
- `EmptyState`;
- `ConfirmDeleteDialog`;
- tema, shape e dimensioni centralizzate.

Le stringhe visibili introdotte per form, inventario, dettaglio e archivio sono
in `strings.xml`. Le schermate hanno Preview per stati pieni, vuoti, mancanti o
non trovati quando rilevante. I test UI usano test tag stabili per elementi
interattivi di Inventario, Dettaglio e Archivio.

### Test presenti dopo la Milestone 2
Test unitari JVM aggiunti o estesi:
- `InventoryViewModelTest`, per caricamento attivi, ricerca, filtro stato,
  campi opzionali e reazione all'archiviazione;
- `MedicineFormViewModelTest`, per salvataggio con solo nome, validazioni,
  conversione quantita, caricamento modifica, update e not found;
- `MedicineDetailViewModelTest`, per caricamento dettaglio, campi opzionali,
  increment/decrement quantita, quantita assente, zero, archiviazione e
  cancellazione confermata;
- `ArchiveViewModelTest`, per caricamento soli archiviati, empty state e campi
  opzionali;
- test di dominio, mapper, use case e DAO gia presenti dalla Milestone 1.

Test strumentali/Compose presenti:
- `InventoryScreenTest`, per empty state, dettagli item, tap verso dettaglio,
  ricerca e filtro;
- `MedicineFormScreenTest`, per campo nome e inserimento testuale;
- `MedicineDetailScreenTest`, per dati principali, modifica, archiviazione e
  conferma cancellazione;
- `NavigationSmokeTest`, per navigazione principale da Home.

Restano presenti i test template `ExampleUnitTest` ed
`ExampleInstrumentedTest`.

### Deviazioni e allineamenti da monitorare
- Il domain enum non contiene `ARCHIVED`; lo stato archiviato e rappresentato
  come proprieta `isArchived` e come badge UI in Dettaglio/Archivio.
- Il naming reale degli stati resta `EXPIRING` e `NO_EXPIRATION_DATE`, mentre
  alcuni documenti usano `EXPIRING_SOON` e `UNKNOWN_EXPIRATION`.
- `lowStockThreshold` e persistito e modificabile dal form, ma non produce
  ancora uno stato visuale o un filtro dedicato di scorta bassa.
- La ricerca e implementata nel `InventoryViewModel`, non come use case
  autonomo.
- L'Archivio non consente ancora il ripristino, anche se la documentazione di
  prodotto lo prevede come evoluzione naturale.
- Lo Scadenzario non e ancora collegato ai dati reali e usa ancora una route di
  esempio.
- Home non mostra ancora riepiloghi reali dell'armadietto.
- Non ci sono ancora impostazioni per cambiare soglia in scadenza o notifiche:
  le feature usano il default centralizzato.

### Debito tecnico noto aggiornato
- Implementare Home / Dashboard con riepilogo reale dei medicinali attivi.
- Implementare Scadenzario reale con sezioni in scadenza, scaduti e senza data.
- Implementare impostazioni reali per soglia scadenza e notifiche.
- Decidere se introdurre uno stato domain `ARCHIVED` o mantenere stabilmente
  archiviazione come dimensione separata.
- Allineare il naming degli stati tra documentazione e codice.
- Introdurre ripristino da Archivio quando previsto dalla milestone dedicata.
- Valutare un use case dedicato alla ricerca se la logica uscira dai soli
  requisiti dell'Inventario.
- Introdurre stato/filtro di scorta bassa se la soglia deve avere impatto UI.
- Collegare lo Scadenzario a id reali e rimuovere `SAMPLE_MEDICINE_ID`.
- Rimuovere i test template generati dallo scaffold.
- Estendere la CI con lint esplicito ed eventuali test strumentali.
- Valutare `exportSchema = true` quando inizieranno migrazioni Room.


## Collegamento con gli altri documenti
La documentazione tecnica deve restare allineata con:

- `DOMAIN_MODEL.md`, per regole di dominio e stati;
- `DATA_MODEL.md`, per schema Room, entity, DAO, indici e migrazioni;
- `ARCHITECTURES_RULES.md`, per struttura e responsabilità;
- `TESTING_STRATEGY.md`, per test implementati e lacune note;
- `SCREEN_CATALOG.md` e `NAVIGATION_MAP.md`, per schermate e flussi effettivamente realizzati.
