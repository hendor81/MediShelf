# TECHNICAL_DOCUMENTATION.md

## Issue #30 — Onboarding essenziale

- `MainContent` attende le preferenze prima di scegliere fra SCH-08 e il grafo
  principale. Non viene creata una destinazione onboarding nello stack:
  dopo la chiusura BACK non può riaprirla.
- `OnboardingScreen` spiega inventario offline, scadenze, notifiche locali e
  confini organizzativi in una pagina scorrevole, con Preview chiara e scura.
  Inizia, Salta e BACK persistono la stessa chiusura e aprono Home, dove la CTA
  esistente guida al primo inserimento.
- `CompleteOnboardingUseCase` e `UserSettings.hasSeenOnboarding` riusano il
  repository delle impostazioni e SharedPreferences (`onboarding_seen`, default
  false). Nessuna migrazione Room. Una installazione preesistente senza chiave
  vede l'introduzione una volta; cancellare i dati ripristina il primo avvio.
- Il ViewModel root gestisce attesa, salvataggio e retry. Un errore conserva
  l'introduzione con feedback; gli input sono disabilitati durante il salvataggio.
- La richiesta iniziale del permesso notifiche attende la chiusura
  dell'introduzione; lo storico di concessione/rifiuto resta quello esistente.
- Test JVM: primo avvio, chiusura, ricreazione, attesa della prima emissione,
  errore e retry, conservazione della preferenza negli aggiornamenti successivi.
  Test Android: azioni Inizia/Salta e rilettura da SharedPreferences isolate.
  Gli smoke test di navigazione chiudono l'introduzione quando presente.
- Verifiche: 105 test JVM superati, build debug e compilazione dei test Android
  riuscite. Esecuzione dei tre test Android tentata sul dispositivo collegato,
  ma bloccata dall'installazione APK (`INSTALL_FAILED_USER_RESTRICTED`): nessun
  test strumentale risulta eseguito, anche se Gradle riporta BUILD SUCCESSFUL.
- Lint completato con zero errori e 127 avvisi complessivi, usando un processo
  isolato con un worker dopo un blocco dell'analizzatore nel primo tentativo.

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


## Stato attuale — Milestone 3: scadenze e notifiche

Questa sezione consolida il codice presente su `master` dopo la PR #88 e
costituisce il riferimento per lo stato corrente. Le sezioni successive sulle
Milestone 1 e 2 e sulle issue #27 e #28 conservano lo storico: le funzionalità
indicate lì come mancanti vanno confrontate con questo riepilogo aggiornato.

MediShelf Free è un inventario e scadenzario locale, offline-first, per un solo
armadietto. Dashboard, Scadenzario e Impostazioni sono collegati ai dati reali.
Le notifiche sono promemoria organizzativi sulle scadenze: non rappresentano
prescrizioni, consigli medici, diagnosi o reminder di assunzione di una terapia.

### Struttura e flusso dati

Il progetto resta monomodulo (`app`), con Kotlin, Compose/Material 3,
Navigation Compose, Hilt, Room e Coroutines/Flow. La toolchain usa JDK 21 e
Gradle 9.4.1; compile SDK 36.1, target SDK 36 e min SDK 24. La compatibilità
Java dei sorgenti resta 11, con desugaring per `java.time`.

| Responsabilità | Implementazione effettiva |
| --- | --- |
| Medicinali persistenti | `MedicineDao` → `LocalMedicineRepository` → `MedicineRepository` |
| Preferenze locali | `SharedPreferencesUserSettingsLocalDataSource` → `LocalUserSettingsRepository` → `UserSettingsRepository` |
| Regole condivise | `MedicineStatusCalculator`, `DateProvider`, `ExpiryNotificationSelector` |
| Riepilogo Home | `GetHomeSummaryUseCase`, `HomeSummary`, `HomeAttentionMedicine` |
| Presentazione | ViewModel → `StateFlow<UiState>` → schermate Compose stateless e callback |
| Tema globale | `MainViewModel` osserva le preferenze, `MainActivity` applica `MediShelfTheme` |
| Notifiche Android | `ExpiryNotificationScheduler`, `ExpiryNotificationJobService`, osservazione avviata da `MediShelfApplication` |

I package `feature.home`, `feature.expiry` e `feature.settings` contengono
schermata, route, ViewModel e UiState effettivi. `data.local.preferences`
gestisce le impostazioni senza aggiungere tabelle Room. Hilt collega i
repository nei moduli DI; i use case usano constructor injection.

### Regole di stato e soglia condivisa

[`MedicineStatusCalculator`](app/src/main/java/it/hendorsoftware/medishelf/domain/rules/MedicineStatusCalculator.kt)
usa `DateProvider.today()` e applica, nell'ordine:

1. quantità nota pari a zero → `OUT_OF_STOCK`;
2. scadenza assente → `NO_EXPIRATION_DATE`;
3. scadenza precedente a oggi → `EXPIRED`;
4. scadenza entro oggi più la soglia, estremi inclusi → `EXPIRING`;
5. altrimenti → `VALID`.

Quantità assente e quantità zero sono casi diversi: solo la seconda determina
esaurito. Con soglia zero, la scadenza odierna è ancora `EXPIRING`. Le voci
archiviate sono escluse dagli stream attivi del repository; il selettore delle
notifiche le esclude anche esplicitamente. `ARCHIVED` non è un valore di
`MedicineStatus`: l'archiviazione resta una proprietà persistita separata.

Il default reale è **60 giorni**, definito in
[`MediShelfDefaults`](app/src/main/java/it/hendorsoftware/medishelf/core/common/MediShelfDefaults.kt),
con intervallo ammesso **0–365**. I 30 giorni citati come proposta nei documenti
iniziali e usati nei fake dei test non sono il default applicativo attuale.
Home, Inventario e Scadenzario osservano la soglia persistita e ricalcolano le
viste quando cambiano dati o impostazioni.

### Dashboard Home

[`GetHomeSummaryUseCase`](app/src/main/java/it/hendorsoftware/medishelf/domain/usecase/GetHomeSummaryUseCase.kt)
combina inventario attivo e impostazioni. `HomeViewModel` converte il riepilogo
in `HomeUiState`, senza calcolare gli stati nelle Composable.

- Conteggi: totale attivi, in scadenza, scaduti, scorta bassa o esaurita.
- Scorta bassa: quantità nota a zero oppure minore o uguale alla soglia
  individuale, se presente. Può sovrapporsi ai conteggi di scadenza; i quattro
  indicatori non sono categorie da sommare tra loro.
- La lista «Da tenere d'occhio» include criticità di stato o di quantità ed è
  limitata a tre voci. L'ordine è scaduti, esauriti, in scadenza, senza data,
  validi con scorta bassa; poi data e nome. I conteggi usano l'intero inventario,
  senza essere troncati dal limite della lista.
- Una voce `VALID` ma sotto soglia usa il badge Home `LowStock`; questo badge
  non introduce un nuovo stato nel dominio o nello Scadenzario.
- Ricerca dalla Home apre l'Inventario; «Vedi tutti» apre lo Scadenzario;
  aggiunta e tap sulle voci aprono form e dettaglio con identificativi reali.

### Scadenzario e Inventario

[`ExpiryViewModel`](app/src/main/java/it/hendorsoftware/medishelf/feature/expiry/ExpiryViewModel.kt)
combina `GetActiveMedicinesUseCase` e `ObserveUserSettingsUseCase` e produce tre
sezioni: in scadenza, scaduti e senza data. Le prime due sono ordinate per data,
poi nome; la terza per nome. I medicinali validi e quelli esauriti sono esclusi,
anche se questi ultimi hanno una data passata o assente. Sono previsti stato di
caricamento e liste vuote; il tap passa l'id reale al dettaglio.

`InventoryViewModel` applica lo stesso classificatore e la stessa soglia,
combinandoli con ricerca per nome e filtro di stato. I filtri disponibili restano
tutti, valido, in scadenza, scaduto, esaurito e senza data; non esiste un filtro
specifico per scorta bassa. Lo stato derivato non viene persistito.

### Impostazioni e persistenza

[`SettingsViewModel`](app/src/main/java/it/hendorsoftware/medishelf/feature/settings/SettingsViewModel.kt)
usa `ObserveUserSettingsUseCase` e i use case `UpdateThemeModeUseCase`,
`UpdateExpiringThresholdDaysUseCase`, `UpdateNotificationsEnabledUseCase` e
`UpdatePreferArchiveOverDeleteUseCase`.

| Preferenza | Default | Comportamento implementato |
| --- | --- | --- |
| Tema | `LIGHT` | Chiaro, scuro o sistema; applicato dalla root dell'app |
| Soglia scadenza | 60 giorni | Intero 0–365; dialog con errori per vuoto, numero non valido o fuori intervallo |
| Notifiche | `true` | Preferenza locale distinta dal permesso Android; recupero tramite il toggle |
| Preferenza rimozione | Archiviazione | Salvata e visualizzata nelle Impostazioni; non cambia ancora le azioni del Dettaglio |

Le preferenze sono salvate nel file SharedPreferences `medishelf_user_settings`
con chiavi `theme_mode`, `expiring_threshold_days`, `notifications_enabled` e
`prefer_archive_over_delete`. Il datasource espone un Flow tramite listener,
limita i valori di soglia letti a 0–365 e ripiega su `LIGHT` per temi sconosciuti.
Repository e modello `UserSettings` validano la soglia in scrittura/costruzione.

Le Impostazioni includono privacy/disclaimer e lo svuotamento dell'Archivio
previa conferma. `ClearArchiveUseCase` legge le voci archiviate e le elimina
singolarmente tramite repository, restituendo il numero rimosso; non è una
transazione atomica di cancellazione collettiva.

Il database `medishelf.db` resta alla versione **1**, con sola tabella
`medicines`, `exportSchema = false` e nessuna migrazione introdotta nella
Milestone 3. Quantità, scadenza e gli altri campi opzionali restano nullable;
`LocalDate` è salvato come stringa ISO e `Instant` come epoch millis.

### Notifiche locali: selezione, esecuzione e permessi

[`ExpiryNotificationSelector`](app/src/main/java/it/hendorsoftware/medishelf/domain/rules/ExpiryNotificationSelector.kt)
riusa il classificatore e la soglia utente. Se la preferenza è disattivata non
seleziona nulla; altrimenti include solo attivi `EXPIRING` o `EXPIRED`, anche
senza quantità. Esclude archiviati, esauriti, validi e voci senza data.

[`ExpiryNotificationScheduler`](app/src/main/java/it/hendorsoftware/medishelf/core/notification/ExpiryNotificationScheduler.kt)
programma tramite `JobScheduler` un job periodico giornaliero persistente
(`JOB_ID = 27`, `setPersisted(true)`). Il manifest dichiara
`RECEIVE_BOOT_COMPLETED` e il servizio protetto da `BIND_JOB_SERVICE`.
Non sono richiesti rete o allarmi esatti e non viene fissata un'ora del giorno:
la finestra effettiva dipende da Android, Doze e risparmio energetico.
Il primo controllo avviene nella finestra periodica, non immediatamente dopo il
salvataggio di un medicinale. Arresto forzato dell'app e restrizioni del sistema
possono ritardare o impedire l'esecuzione.

`MediShelfApplication` osserva impostazioni e inventario per la durata del
processo. Con notifiche attive programma il job solo se manca; le modifiche a
dati o soglia aggiornano o rimuovono un riepilogo già visibile. Se non c'è una
notifica visibile non ne generano subito una nuova. Disattivare la preferenza
annulla job e riepilogo.

`ExpiryNotificationJobService` rilegge medicinali e preferenze a ogni esecuzione,
mostra i conteggi aggiornati e chiede la riprogrammazione in caso di errore;
annulla la coroutine quando Android interrompe il job.

La pubblicazione usa una sola notifica (`NOTIFICATION_ID = 27`) sul canale
`medicine_expiry`, creato da Android 8 con importanza predefinita. Il contenuto
mostra conteggi aggregati di in scadenza/scaduti, senza nomi di medicinali;
il tap apre `MainActivity` e la Home, non una destinazione diretta allo
Scadenzario. Il riepilogo vuoto viene rimosso. Il controllo globale delle
notifiche e la gestione di `SecurityException` proteggono la pubblicazione,
ma un canale disabilitato nel sistema può comunque impedirne la visualizzazione.

Su Android 13+ `MainActivity` richiede `POST_NOTIFICATIONS` una sola volta se la
preferenza è attiva. Lo storico è condiviso con il toggle nel file
`notification_permission`, chiave `requested`. Il rifiuto iniziale non modifica
automaticamente la preferenza locale e il job può restare programmato senza
poter mostrare notifiche.

[`NotificationPermissionFlow`](app/src/main/java/it/hendorsoftware/medishelf/feature/settings/NotificationPermissionFlow.kt)
e `NotificationPermissionState` gestiscono l'abilitazione esplicita dal toggle:
richiesta iniziale, spiegazione del rifiuto temporaneo o accesso alle impostazioni
di sistema se il permesso è bloccato. Al ritorno rileggono permesso e blocco
globale, senza interpretare il result code come concessione; la preferenza è
abilitata solo quando entrambi consentono le notifiche. Lo stato di attesa
sopravvive alla ricreazione della schermata. Da Android 8 si apre la pagina
notifiche dell'app; su Android 7 si usa la pagina dettagli dell'app.

### Navigazione e verifica dello stato implementato

Le route restano `home`, `inventory`, `expiry`, `archive`, `settings`,
`medicine/add`, `medicine/{medicineId}` e `medicine/{medicineId}/edit`.
[`MediShelfNavHost`](app/src/main/java/it/hendorsoftware/medishelf/navigation/MediShelfNavHost.kt)
collega Home, Inventario, Scadenzario e Archivio al dettaglio con id reali:
il precedente limite `SAMPLE_MEDICINE_ID` è superato. Inserimento torna
all'Inventario, modifica torna al Dettaglio, archiviazione apre Archivio e
cancellazione torna indietro. Non sono state aggiunte schermate Pro o cloud.

La copertura JVM comprende `HomeViewModelTest`, `ExpiryViewModelTest`,
`InventoryViewModelTest`, `SettingsViewModelTest`,
`LocalUserSettingsRepositoryTest`, `ExpiryNotificationSelectorTest` e
`NotificationPermissionStateTest`, oltre ai test di dominio e dati esistenti.
`DerivedViewsConsistencyTest` verifica insieme le tre viste con data fissa,
fake condivisi e aspettative esplicite su conteggi, filtri e aggiornamenti.

Verifica automatica di riferimento della issue #28, sul codice incorporato
nella PR #88: **91 test JVM superati**, nessun fallimento o test saltato,
`assembleDebug` riuscita, `lintDebug` senza errori e con **83 avvisi**. Questa
issue documentale verifica manualmente la corrispondenza con sorgenti e
struttura; non aggiunge test e non dichiara una nuova esecuzione della suite.

```shell
bash gradlew testDebugUnitTest assembleDebug lintDebug
```

I test strumentali presenti includono DAO, navigazione, Home, Inventario,
form, Dettaglio e `NotificationPermissionDialogTest`. La presenza di questi test
non equivale all'esecuzione su dispositivo. Le verifiche storiche della PR #87
riguardano due dialoghi Compose su Android 16; il percorso completo attraverso
le impostazioni di sistema dopo quella correzione resta da verificare manualmente.

Su un dispositivo con dati di prova e permessi concessi, il job può essere
forzato per controllare selezione, conteggi e apertura dalla notifica:

```shell
adb shell cmd jobscheduler run -f it.hendorsoftware.medishelf 27
```

Ripetere con permesso negato, rifiuto ripetuto e recupero dalle impostazioni,
rotazione durante il passaggio al sistema, variazione della soglia,
archiviazione, disattivazione notifiche e riavvio. Il comando forzato non
dimostra il rispetto della cadenza naturale del job periodico.

### Limiti e debito tecnico residuo

- I Flow delle viste ricalcolano al cambiamento di dati o preferenze: non c'è
  un segnale temporale a mezzanotte che aggiorni automaticamente gli stati di
  una schermata rimasta aperta. Il job rilegge la data quando viene eseguito.
- Il Dettaglio legge le impostazioni al caricamento e dopo azioni sulla quantità;
  non osserva continuamente gli aggiornamenti come le tre viste principali.
- L'Archivio è consultivo: manca il ripristino. La preferenza archivia/elimina è
  persistita ma non modifica ancora il comportamento delle azioni del Dettaglio.
- La campanella nella Home è nascosta finché non sarà disponibile una destinazione notifiche.
- Gli errori nelle operazioni delle Impostazioni non hanno ancora una gestione
  UI completa; lo svuotamento Archivio non è atomico e può interrompersi a metà.
- Lo scheduler registra gli errori di programmazione, ma non li espone nella UI;
  un errore nell'osservazione avviata dall'Application viene registrato senza
  un ciclo automatico di riavvio dell'osservazione.
- Restano la verifica manuale completa del recupero permessi e la copertura
  automatizzata Android di scheduling, riavvio e pubblicazione effettiva.
- La CI su `master` esegue `build` e `test` con JDK 21; non ha step dedicati a
  `lintDebug` né un dispositivo/emulatore per i test strumentali.
- Restano `exportSchema = false`, assenza di migrazioni evolutive, test template
  dello scaffold e divergenze terminologiche dei documenti guida (`ARCHIVED`,
  `EXPIRING_SOON`, `UNKNOWN_EXPIRATION`) rispetto al codice corrente.

## Storico — Milestone 1

Le descrizioni e i limiti che seguono si riferiscono alla conclusione della
Milestone 1, non allo stato attuale consolidato sopra.

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
La pipeline `.github/workflows/android-ci.yml` si avvia sui push a `master`
e ai branch `codex/**`, sulle pull request verso `master` e tramite
`workflow_dispatch`.

Job attuale:
- checkout repository;
- setup JDK Temurin 21;
- setup Gradle;
- `chmod +x gradlew`;
- `./gradlew build`;
- `./gradlew test`;
- caricamento degli APK debug con `actions/upload-artifact@v4`, conservati per
  14 giorni; il job fallisce se il file atteso non è presente.

Gli artefatti `MediShelf-debug-<run_number>-<run_attempt>` contengono un APK
installabile dopo aver estratto lo ZIP. La firma debug del runner non è una
chiave stabile di distribuzione. Le istruzioni per il dispositivo sono nel README.

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


## Storico — Milestone 2

Le descrizioni e i limiti di questa sezione si riferiscono alla conclusione
della Milestone 2; dashboard, scadenzario, impostazioni e notifiche sono stati
successivamente implementati nella Milestone 3.

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

## Issue #27 — Notifiche locali di scadenza

- `ExpiryNotificationSelector` riusa la classificazione di dominio e la soglia
  corrente: include scaduti e in scadenza, anche senza quantità; esclude
  archiviate, esaurite e voci senza data. Preferenza disattivata: lista vuota.
- `ExpiryNotificationScheduler` usa JobScheduler Android senza nuove dipendenze:
  un job periodico giornaliero persistente dopo il riavvio, senza rete né allarmi
  esatti. Android decide la finestra effettiva; il primo controllo avviene nella
  prima finestra periodica, non immediatamente dopo il salvataggio.
- `MediShelfApplication` osserva preferenze e inventario: disattivare annulla il
  job e la notifica; cambiare dati o soglia aggiorna un riepilogo già visibile.
- `ExpiryNotificationJobService` rilegge repository e impostazioni a ogni
  esecuzione e gestisce cancellazione Android ed errori con riprogrammazione.
- Canale `medicine_expiry`, icona monocromatica e unica notifica con conteggi
  aggregati, senza nomi di medicinali. Il tap apre la Home, da cui si accede allo
  Scadenzario. Nessun consiglio medico o reminder di terapia.
- Android 13+: richiesta del permesso al primo avvio con preferenza attiva,
  ulteriori tentativi espliciti tramite il toggle delle Impostazioni. Il rifiuto
  di sistema impedisce la pubblicazione anche se la preferenza locale è attiva.
- Nessuna modifica allo schema Room. Test unitari dedicati a selezione, soglia
  inclusiva/modificata, preferenza disattivata ed esclusioni.
- Verifica manuale: aggiungere voci scadute/in scadenza, concedere il permesso,
  eseguire `adb shell cmd jobscheduler run -f it.hendorsoftware.medishelf 27`,
  verificare il riepilogo e il tap. Cambiare soglia/archiviare e controllare il
  riepilogo visibile; disattivare e verificare che il job sia rimosso. Ripetere
  con permesso negato e dopo riavvio. Questa verifica richiede un dispositivo.
- Verifica automatica eseguita: `testDebugUnitTest assembleDebug lintDebug`
  completati con successo; 76 test passati (4 nuovi), lint senza errori con
  95 avvisi complessivi. Verifica manuale completata dall'utente con esito
  positivo, come confermato prima della pubblicazione della pull request.

### Correzione review PR #87 — recupero permesso notifiche

- Il toggle distingue primo tentativo, rifiuto temporaneo e permesso bloccato
  tramite una fotografia testabile `NotificationPermissionState`.
- `NotificationPermissionFlow` mostra un dialogo con spiegazione e nuovo tentativo
  oppure il pulsante per aprire `ACTION_APP_NOTIFICATION_SETTINGS`.
- La richiesta iniziale e quella delle Impostazioni condividono lo storico locale.
  Lo stato di attesa del ritorno dalle impostazioni Android sopravvive alla
  ricreazione della schermata. Il resultCode non è interpretato come permesso:
  al ritorno si rileggono permesso runtime e blocco globale delle notifiche.
- La preferenza viene abilitata solo se entrambi i controlli lo consentono;
  altrimenti resta disattivata e viene spiegato come recuperare.
- Otto test unitari coprono prima richiesta, rifiuto temporaneo, blocco,
  concessione o mancata concessione al ritorno e versioni precedenti ad Android 13.
- Verifica manuale aggiuntiva: negare ripetutamente il permesso su Android 13+,
  provare ad attivare il toggle, aprire le impostazioni dal dialogo e tornare
  prima senza concessione, poi dopo averla concessa. Verificare rispettivamente
  toggle disattivato e attivato. Verificare anche Annulla e rotazione mentre le
  impostazioni di sistema sono aperte. Il precedente test manuale positivo
  riguarda la versione prima di questa correzione.
- Verifiche della correzione: build debug, 84 test unitari e due test Compose
  mirati sul dispositivo Android 16 collegato completati con successo.
  Lint: zero errori, 94 avvisi. Il flusso completo nelle impostazioni di sistema
  resta da verificare manualmente; i test UI esercitano i dialoghi dell'app.
- Android 7 usa la pagina dettagli dell'app come alternativa alla pagina notifiche,
  disponibile tramite intent pubblico da Android 8.

## Issue #28 — Coerenza dei conteggi e delle viste derivate

- `DerivedViewsConsistencyTest` esercita insieme Home, Inventario e Scadenzario
  tramite i ViewModel e i use case reali, con repository fake condivisi e
  `FakeDateProvider` fissato al 18 maggio 2026.
- Gli identificativi attesi sono espliciti per ogni stato: i test confrontano
  conteggi Home, sezioni dello Scadenzario e tutti i filtri dell'Inventario,
  senza costruire le aspettative con il classificatore di produzione.
- La copertura include soglia inclusiva e zero, aumento e riduzione della soglia
  sui ViewModel già attivi, quantità e scadenza assenti, priorità di esaurito,
  scorta bassa, inventario vuoto e sole voci archiviate. Aggiornamento quantità,
  archiviazione e cancellazione verificano anche la propagazione dei dati.
- I conteggi restano indipendenti dal limite di tre voci della lista attenzione.
  La Home può evidenziare con `LowStock` una voce altrimenti `VALID`:
  questa distinzione visuale non cambia le sezioni basate sulla scadenza.
- Sono test JVM, senza Room, dispositivo, rete, attese reali o test end-to-end.
  I 7 nuovi test passano; la suite completa conta 91 test superati, senza errori
  o test saltati. Build debug e lint completati con successo; lint con zero
  errori e 83 avvisi. Nessuna modifica al codice applicativo o allo schema Room.

Esecuzione mirata:

```shell
bash gradlew testDebugUnitTest --tests it.hendorsoftware.medishelf.feature.DerivedViewsConsistencyTest
```

### Uniformità delle schermate principali (issue #90)

- Lo scaffold principale consuma gli inset applicati al NavHost, evitando spazi di sistema duplicati nelle schermate figlie; la barra di navigazione ha un divisore superiore.
- Home, Inventario e Scadenzario condividono `AddMedicineActionBar` e `MedicineListCard`, con icona confezione, metadati e badge adattato alla larghezza disponibile.
- Inventario e Scadenzario usano il titolo maggiorato del 50%, lo sfondo del tema e l'icona `archivio` già presente nelle Impostazioni. Lo Scadenzario espone anche aggiunta e archivio.
- Ogni stato del badge ha un'icona; `scorta_bassa` distingue le scorte ridotte dall'esaurimento.
- Il form di aggiunta e modifica espone una freccia indietro che chiude senza salvare.

### Rifiniture UI e navigazione (issue #92)

- Il badge delle card di attenzione Home affianca il nome, allineato a destra.
- `MedicineListCard` associa il formato salvato alla relativa icona tramite `MedicinePackageForms`; Archivio riusa la stessa card. Valori liberi preesistenti restano leggibili e quelli sconosciuti usano la compressa. Spray usa la risorsa spray orale disponibile.
- Il form propone i 17 formati richiesti in un menu opzionale; la selezione può essere rimossa. Il calendario Material 3 sostituisce l'input manuale e permette di rimuovere la data. Le conversioni del calendario usano UTC, come previsto dall'API, mantenendo il formato ISO per il ViewModel.
- Dopo un salvataggio riuscito il form chiude immediatamente con `popBackStack`, ripristinando la schermata chiamante anche in aggiunta.
- Archivio, Scadenzario e Dettaglio espongono il ritorno alla schermata precedente. Nel Dettaglio le superfici e l'elevazione seguono le card Home e le icone usano le risorse richieste.
- Nuovi test Compose verificano selezione del formato, rimozione della data e ritorno a Home/Scadenzario dopo il salvataggio. I test strumentali richiedono esecuzione su un dispositivo di test.
- Nessuna modifica a dominio o schema Room.


### Verifica di coerenza e correzioni approvate (issue #92)

- `MedicineStockRules` centralizza la scorta bassa positiva entro soglia, distinta da esaurito. Home, Inventario, Scadenzario e Dettaglio condividono il flag e mantengono visibile lo stato di scadenza.
- Inventario aggiunge Scorta bassa e Da controllare; Vedi tutti nella Home apre il secondo filtro tramite argomento opzionale, preservando le successive scelte dell'utente.
- `RestoreArchivedMedicineUseCase` riusa il repository per ripristinare la voce e aggiornare il timestamp. Nessuna migrazione Room.
- Il Dettaglio rispetta la preferenza di rimozione, espone il ripristino e consuma gli eventi di navigazione. Archiviazione rimuove il Dettaglio dallo stack prima di aprire Archivio.
- Il form valida la dipendenza quantità/unità/soglia. Errori di salvataggio conservano gli input e riabilitano il tentativo; anche gli errori delle operazioni nel Dettaglio liberano lo stato occupato. Le cancellazioni delle coroutine restano propagate.
- Lo scaffold applicativo ospita la conferma del salvataggio dopo il ritorno alla schermata chiamante.
- Menu formati alfabetico con icone, compresse e spray distinti; titoli delle app bar uniformati; testi italiani e dicitura Senza data aggiornati.
- `UiRefinementsTest` copre stock e filtri, ripristino, preferenza Elimina con conferma, consumo evento archivio, dipendenze del form ed errori recuperabili. Aggiornati i test di consistenza alla nuova semantica.
- Restano intenzionalmente invariati il colore del titolo Home nel tema scuro, il troncamento dei nomi, il layout dei riepiloghi e il calendario con formato ISO nel form.

Verifica delle correzioni: 101 test unitari superati; build debug, lint e compilazione dei test UI completati. Test strumentali non eseguiti su dispositivo.

### Palette scura e titolo Dettaglio (issue #92)
Titolo della schermata abbreviato in Dettaglio. Palette scura campionata dallo screenshot: aggiornati token Material, superfici dei menu/dialog/barre, accenti, testi, separatori e badge. La Home usa onBackground per il testo del marchio, evitando il nero fisso nel tema scuro. Layout e regole di dominio restano invariati.

## Issue #94 — Onboarding e guida della Home

- SCH-08 distingue Avanti e Salta: entrambi persistono `hasSeenOnboarding`,
  ma solo Avanti apre la Home con la guida. BACK equivale a Salta.
- `MainViewModel` coordina cinque passi, uscita anticipata e completamento.
  L’errore di persistenza mantiene l’introduzione e consente un nuovo tentativo.
  Lo stato osservato non apre la Home prima che sia disponibile la scelta della guida.
- `HomeTutorialOverlay` evidenzia gli elementi reali tramite coordinate Compose:
  riepiloghi, ricerca, sezione medicinali da controllare (anche vuota), pulsante
  aggiunta e barra delle quattro destinazioni. Il bersaglio viene portato in vista;
  l’overlay intercetta i tap e nasconde la semantica della UI sottostante.
- Ogni passo espone Avanti/Fine, Salta, progresso testuale e supporto BACK.
  I testi scorrono con font grandi; risorse e token visuali sono centralizzati.
  Preview disponibili anche a larghezza ridotta e font ingranditi.
- Il passo corrente resta nel ViewModel durante la rotazione. Dopo la morte del
  processo o un nuovo avvio la guida non viene riproposta: l’onboarding è già
  persistito come concluso. Nessuna modifica Room e nessuna nuova dipendenza.
- La richiesta iniziale del permesso notifiche attende la chiusura della guida.
- Test JVM: percorso Salta, ordine e chiusura dei cinque passi, uscita anticipata,
  riapertura ed errore recuperabile. Test Compose: callback distinte per Avanti e
  Salta, ordine dei tooltip, Fine e uscita anticipata.

Verifica #94: build debug, 107 test JVM, lint (zero errori, 127 avvisi) e
5 test strumentali del pacchetto onboarding sul dispositivo Android 16 collegato
completati con successo (4 Compose e 1 sulle preferenze persistenti).

### Rifiniture visuali della guida (#94)

I messaggi cambiano con una breve dissolvenza e il ritaglio evidenziato si sposta
con una transizione delle coordinate. Il ritaglio usa angoli arrotondati.
Il riquadro descrittivo usa primaryContainer/onPrimaryContainer del tema, bordo
primario e ombra per restare distinto dalle card, anche quando le sovrappone.
Durata, bordo ed elevazione sono token locali; nessuna nuova dipendenza.

### Correzioni della review Copilot sulla PR #96

- La guida usa una finestra Popup focusable dimensionata e posizionata sul root:
  isola tastiera e accessibilità dalla Home, conserva le coordinate dei bersagli
  e assegna il focus iniziale al contenitore del messaggio. BACK e Salta chiudono la guida.
- Il messaggio sceglie il lato con più spazio rispetto alla geometria del bersaglio.
  La scelta resta stabile durante lo scroll per evitare oscillazioni. L’ingombro
  misurato riserva spazio nel viewport Home, quindi bringIntoView considera anche
  il tooltip. L’altezza del messaggio tiene conto del viewport e il testo scorre
  quando lo spazio o i caratteri grandi lo richiedono.
- Il campo Cerca per nome conserva un’etichetta semantica anche con query presente,
  mantenendo il placeholder e lo stile visuale approvati.
- Il test di Fine usa stato Compose e verifica la scomparsa del tooltip. Nuovi test
  coprono focus da tastiera, assenza di sovrapposizioni in una finestra bassa con
  lista vuota/popolata e font grandi, e l’etichetta accessibile con testo digitato.

Verifica delle correzioni: 107 test JVM superati; build debug e lint completati
con zero errori e 127 avvisi. Tutti i 31 test strumentali delle feature sono
passati sul dispositivo Android 16, inclusi focus da tastiera, geometria con font
grandi, chiusura della guida ed etichetta della ricerca con query presente.
Adeguati anche due test preesistenti: selezione del riepilogo Home tramite tag
per evitare testi duplicati e scroll esplicito al pulsante Salva del form.

### Focus iniziale dei messaggi lunghi (review PR #97)

Il focus iniziale appartiene al contenitore del messaggio, esterno allo scroll,
anziché ad Avanti/Fine. Ogni passo azzera lo scroll: titolo e inizio della
spiegazione restano visibili; Tab raggiunge poi le azioni, riportandole in vista
solo quando richiesto dall’utente. Il Popup continua a isolare il focus dalla Home.
Un test combinato esercita i cinque passi da tastiera, con font al 160% e finestra
alta 420 dp: verifica overflow reale, scroll iniziale zero, titolo/spiegazione
visibili, avanzamento con Tab/Invio e chiusura finale.

Verifica del focus iniziale: build debug e lint superati (zero errori, 127 avvisi);
9 test strumentali onboarding superati su Android 16, incluso il nuovo caso
combinato di tastiera e messaggi con overflow.

## Issue #33 — Regressioni per la release Free

- `FreeReleaseRegressionTest` collega form, dettaglio, inventario, Home,
  Scadenzario, Archivio e Impostazioni agli stessi fake, con classificazione
  reale e data fissa. Copre inserimento con solo nome, modifica senza cambio
  identità, ricerca, soglia inclusiva, esaurimento senza quantità negative,
  archivio/ripristino e conferme/annullamenti di eliminazione e svuotamento.
- `SettingsViewModelTest` verifica input incompleti, overflow numerico, limiti
  0/365, annullamento senza scrittura e rilettura della preferenza salvata.
  `MedicineStatusCalculatorTest` aggiunge passaggi sul giorno bisestile e sul
  cambio anno. Le aspettative non dipendono dalla data di esecuzione.
- `LocalMedicineRepositoryRegressionTest` esercita repository concreto, mapper
  e Room in memoria: id distinti per nuovi record, update senza duplicazione,
  campi opzionali, archivio/ripristino/cancellazione e invalidazione di un
  osservatore già attivo. `MedicineDaoTest` copre ordinamento senza distinzione
  maiuscole, data archivio, fallback legacy e nome come criterio secondario.
- I test di navigazione selezionano i tab interattivi per etichetta e distinguono
  i titoli dai tab omonimi. Prima dell'avvio dell'Activity concedono il permesso
  notifiche sui sistemi che lo richiedono, evitando dialoghi Android nei test.
- Il controllo di accessibilità delle azioni Dettaglio misura l'area tattile
  Material di 48 dp invece del solo contenitore visuale dell'icona. Gli errori
  di overflow riportano testo e dimensioni per rendere la diagnosi riproducibile.
- La regressione al 200% ha rilevato un paragrafo centrato più largo del nodo
  dell'etichetta Home durante le misure di FlowRow. Le etichette occupano ora
  tutta la larghezza disponibile della card, conservando font e centratura.
- `Android CI` aggiunge un job strumentale su emulatore API 29 x86_64 separato
  dal job JVM/build. Entrambi caricano report anche in caso di fallimento.
  Comandi e matrice della copertura minima sono in `TESTING_STRATEGY.md`.
- Nessuna modifica allo schema Room, ai repository applicativi o ai flussi
  di prodotto; i test del grafo principale vanno eseguiti su installazioni
  di test perché inseriscono voci reali nel database dell'app emulata.

Verifica locale #33: `testDebugUnitTest assembleDebug connectedDebugAndroidTest
lintDebug --offline` completati con successo. 116 test JVM e 65 test strumentali
su emulatore Android 16/API 36.1, senza fallimenti, errori o test saltati.
La modifica aggiunge 6 test JVM e 5 strumentali, oltre agli adeguamenti dei
test esistenti. Il nuovo job GitHub Actions API 29 richiede la prima esecuzione
remota dopo la pubblicazione del branch; non è stato eseguito da questa sessione.
