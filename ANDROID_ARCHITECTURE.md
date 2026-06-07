# ANDROID_ARCHITECTURE.md

## Scopo
Questo documento definisce l'architettura Android concreta di **MediShelf**.

Va usato come riferimento operativo per:
- creare lo scaffold del progetto;
- implementare le feature della versione Free;
- guidare GPT-Codex nella generazione del codice;
- mantenere coerenti package, layer, naming, test e documentazione tecnica.

Il documento non sostituisce `PRODUCT_OVERVIEW.md`, `DOMAIN_MODEL.md`, `DATA_MODEL.md`, `UI_GUIDELINES.md`, `SCREEN_CATALOG.md` e `BACKLOG_FREE.md`, ma li rende applicabili al codice Android.

## Principi architetturali
L'architettura deve essere:
- semplice;
- offline-first;
- testabile;
- incrementale;
- leggibile da sviluppatori umani e agenti IA;
- coerente con Jetpack Compose e Material 3;
- pronta a evolvere verso Pro e Subscription senza appesantire la versione Free.

Per la prima versione non va introdotta complessità non necessaria. Ogni astrazione deve avere uno scopo concreto.

## Stack tecnologico
Stack iniziale ufficiale:
- Kotlin;
- Jetpack Compose;
- Material 3;
- Navigation Compose;
- ViewModel;
- Room;
- Hilt;
- Coroutines;
- Flow;
- JUnit;
- Compose UI Test;
- GitHub Actions per automazione build e controlli base.

## Strategia moduli
Per la prima fase si usa un progetto **monomodulo** con solo modulo `app`.

La separazione avviene tramite package ordinati per responsabilità.

La multi-module architecture potrà essere valutata in futuro solo se il progetto cresce abbastanza da giustificarla.

## Package root
Package applicativo consigliato:

```text
it.hendorsoftware.medishelf
```

Struttura generale:

```text
it.hendorsoftware.medishelf
│
├── MediShelfApplication.kt
├── MainActivity.kt
│
├── core
│   ├── common
│   ├── designsystem
│   ├── notification
│   ├── resource
│   └── time
│
├── data
│   ├── local
│   │   ├── dao
│   │   ├── database
│   │   └── entity
│   ├── mapper
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   ├── rules
│   └── usecase
│
├── feature
│   ├── home
│   ├── inventory
│   ├── medicineform
│   ├── medicinedetail
│   ├── expiry
│   ├── archive
│   └── settings
│
├── navigation
│
└── di
```

## Responsabilità dei layer

### UI / Feature layer
Contiene schermate Compose, route, ViewModel, UiState ed eventuali modelli UI.

Responsabilità:
- mostrare lo stato ricevuto dal ViewModel;
- inoltrare eventi utente tramite callback;
- gestire layout, componenti visuali e feedback;
- non contenere regole di business complesse;
- non accedere direttamente a Room o repository concreti.

### Domain layer
Contiene il cuore applicativo indipendente da Android UI e da Room.

Responsabilità:
- modelli di dominio;
- regole di business;
- calcolo stato medicinale;
- use case;
- interfacce repository;
- validazioni centrali quando non puramente UI.

### Data layer
Contiene persistenza locale e implementazioni concrete dei repository.

Responsabilità:
- Room database;
- DAO;
- entity persistenti;
- mapper tra entity e domain model;
- repository locali;
- gestione delle query e dell'archiviazione logica.

### Core layer
Contiene elementi trasversali.

Responsabilità:
- design system;
- componenti UI riusabili;
- utility comuni;
- provider di data/ora;
- gestione notifiche;
- accesso centralizzato a risorse e costanti UI.

### DI layer
Contiene moduli Hilt.

Responsabilità:
- dichiarare database, DAO, repository, use case e provider;
- evitare creazioni manuali nei ViewModel e nelle Composable;
- rendere agevole la sostituzione con fake nei test.

## Regola fondamentale del flusso dati
Il flusso dati standard è:

```text
Room DAO
   ↓
Repository concreto
   ↓
Use case
   ↓
ViewModel
   ↓
UiState
   ↓
Composable Screen
```

Gli eventi utente seguono il percorso inverso:

```text
Composable Screen
   ↓
ViewModel event function
   ↓
Use case
   ↓
Repository
   ↓
Room DAO
```

Le Composable non devono accedere direttamente a repository, DAO o use case.

## Domain model principale
Il modello di dominio iniziale ruota intorno a `Medicine`.

Esempio orientativo:

```kotlin
data class Medicine(
    val id: MedicineId,
    val name: String,
    val packageDescription: String?,
    val quantity: QuantityInfo?,
    val expirationDate: LocalDate?,
    val storageLocation: String?,
    val notes: String?,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

Regole principali:
- `name` è obbligatorio;
- `quantity` è opzionale;
- `expirationDate` è opzionale;
- `storageLocation` è opzionale;
- `notes` è opzionale;
- `isArchived` gestisce l'archiviazione logica.

## Quantità opzionale
La quantità non deve essere obbligatoria in inserimento.

Motivazioni:
- l'utente può voler censire una confezione senza aprirla;
- per flaconi, pomate, spray e prodotti già iniziati la quantità può essere difficile da stimare;
- rendere obbligatoria la quantità aumenta l'attrito;
- l'inventario e lo scadenzario restano utili anche senza quantità.

Se la quantità non è indicata:
- il medicinale è comunque valido come voce di inventario;
- può avere scadenza e posizione;
- non viene classificato come `OUT_OF_STOCK`;
- non partecipa al calcolo di scorta bassa.

## Stati del medicinale
Gli stati minimi sono:

```kotlin
enum class MedicineStatus {
    VALID,
    EXPIRING,
    EXPIRED,
    OUT_OF_STOCK,
    NO_EXPIRATION_DATE
}
```

Lo stato non va salvato nel database nella prima versione. Va calcolato dal dominio.

Ordine logico consigliato:
1. se quantità presente e quantità minore o uguale a zero: `OUT_OF_STOCK`;
2. se la scadenza non è indicata: `NO_EXPIRATION_DATE`;
3. se la scadenza è precedente a oggi: `EXPIRED`;
4. se la scadenza è entro la soglia configurata: `EXPIRING`;
5. altrimenti: `VALID`.

La regola deve vivere in `MedicineStatusCalculator` o equivalente, non nella UI.

## Gestione data corrente
Tutte le regole basate sulla data corrente devono usare un provider testabile.

Esempio:

```kotlin
interface DateProvider {
    fun today(): LocalDate
}
```

Implementazione di produzione:

```kotlin
class SystemDateProvider @Inject constructor() : DateProvider {
    override fun today(): LocalDate = LocalDate.now()
}
```

Nei test si userà un fake provider con data fissa.

## Data model Room
Il modello Room iniziale deve essere coerente con `DATA_MODEL.md`.

Entity principale:

```kotlin
@Entity(
    tableName = "medicines",
    indices = [
        Index(value = ["name"]),
        Index(value = ["expirationDate"]),
        Index(value = ["isArchived"])
    ]
)
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val packageDescription: String?,
    val quantityValue: Double?,
    val quantityUnit: String?,
    val lowStockThreshold: Double?,
    val expirationDate: LocalDate?,
    val storageLocation: String?,
    val notes: String?,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

Room richiede converter per:
- `LocalDate`;
- `Instant`.

## DAO iniziale
DAO orientativo:

```kotlin
@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines WHERE isArchived = 0 ORDER BY name ASC")
    fun observeActiveMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun observeArchivedMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: String): MedicineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMedicine(entity: MedicineEntity)

    @Query("UPDATE medicines SET isArchived = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun archiveMedicine(id: String, updatedAt: Instant)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicine(id: String)
}
```

L'archiviazione è il comportamento preferito. La cancellazione fisica resta disponibile, ma deve essere usata solo dopo conferma esplicita dell'utente.

## Repository
Nel domain:

```kotlin
interface MedicineRepository {
    fun observeActiveMedicines(): Flow<List<Medicine>>
    fun observeArchivedMedicines(): Flow<List<Medicine>>
    suspend fun getMedicineById(id: MedicineId): Medicine?
    suspend fun saveMedicine(medicine: Medicine)
    suspend fun archiveMedicine(id: MedicineId)
    suspend fun deleteMedicine(id: MedicineId)
}
```

Nel data layer:

```kotlin
class LocalMedicineRepository @Inject constructor(
    private val medicineDao: MedicineDao,
    private val mapper: MedicineMapper
) : MedicineRepository
```

## Use case iniziali
Use case minimi consigliati:

```text
AddMedicineUseCase
UpdateMedicineUseCase
ArchiveMedicineUseCase
DeleteMedicineUseCase
GetActiveMedicinesUseCase
GetArchivedMedicinesUseCase
GetMedicineByIdUseCase
GetExpiringMedicinesUseCase
GetExpiredMedicinesUseCase
UpdateMedicineQuantityUseCase
GetHomeSummaryUseCase
ObserveSettingsUseCase
UpdateExpiryThresholdUseCase
```

I use case devono restare piccoli e orientati ad azioni concrete.

## Feature package standard
Ogni feature principale segue lo stesso schema.

Esempio:

```text
feature/inventory/
  InventoryRoute.kt
  InventoryScreen.kt
  InventoryViewModel.kt
  InventoryUiState.kt
  InventoryFilter.kt
  InventorySort.kt
```

Regole:
- `Route` collega navigazione, ViewModel e Screen;
- `Screen` riceve `UiState` e callback;
- `ViewModel` espone stato e gestisce eventi;
- `UiState` rappresenta tutto ciò che serve alla UI;
- eventuali classi `Action` o `Event` devono avere nomi chiari.

## ViewModel
Ogni schermata principale deve avere il proprio ViewModel.

Pattern consigliato:

```kotlin
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getActiveMedicinesUseCase: GetActiveMedicinesUseCase
) : ViewModel() {
    // Stato ed eventi
}
```

Regole:
- non usare repository concreti direttamente se esiste un use case;
- non conoscere dettagli di Room;
- non contenere rendering UI;
- esporre `StateFlow<UiState>`;
- usare funzioni evento con nomi espliciti, ad esempio `onSearchQueryChanged`, `onArchiveClick`, `onDeleteConfirmed`.

## UiState
Ogni schermata deve avere un proprio `UiState`.

Esempio:

```kotlin
data class InventoryUiState(
    val isLoading: Boolean = true,
    val medicines: List<MedicineListItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: InventoryFilter = InventoryFilter.All,
    val errorMessage: String? = null
)
```

Regole:
- lo stato deve essere immutabile;
- evitare proprietà calcolate complesse dentro la Composable;
- preferire mapping nel ViewModel o in mapper UI dedicati.

## Navigation
La navigazione è gestita con Navigation Compose.

Package:

```text
navigation/
  MediShelfNavHost.kt
  MediShelfRoute.kt
```

Route principali:

```kotlin
sealed class MediShelfRoute(val route: String) {
    data object Home : MediShelfRoute("home")
    data object Inventory : MediShelfRoute("inventory")
    data object Expiry : MediShelfRoute("expiry")
    data object Archive : MediShelfRoute("archive")
    data object Settings : MediShelfRoute("settings")
    data object AddMedicine : MediShelfRoute("medicine/add")
    data object EditMedicine : MediShelfRoute("medicine/{medicineId}/edit")
    data object MedicineDetail : MediShelfRoute("medicine/{medicineId}")
}
```

Regole:
- `Home` è il punto di ingresso;
- `Inventory` e `Expiry` sono accessi principali;
- `MedicineDetail` è raggiungibile da più schermate ma deve restare un'esperienza unica;
- `MedicineForm` gestisce sia inserimento sia modifica;
- le schermate ricevono identificativi minimi, non oggetti complessi serializzati.

## Navigation shell
La navigation shell è lo scheletro navigabile dell'app.

Include:
- `MainActivity`;
- `MediShelfNavHost`;
- definizione route principali;
- schermate placeholder iniziali;
- navigazione tra Home, Inventario, Scadenzario, Archivio e Impostazioni;
- passaggio `medicineId` verso Dettaglio e Modifica.

Non richiede feature complete. Serve a impostare il telaio dell'app prima dell'implementazione funzionale.

## Design system e risorse centralizzate
Il codice non deve mai contenere stringhe, dimensioni, colori o valori visuali sparsi direttamente nelle Composable o nei ViewModel.

### Stringhe
Tutte le stringhe visibili all'utente devono stare in:

```text
app/src/main/res/values/strings.xml
```

Esempi:
- titoli schermata;
- label campi;
- messaggi errore;
- testi empty state;
- conferme dialog;
- descrizioni accessibilità.

### Dimensioni
Le dimensioni UI devono essere centralizzate.

Per Compose si usa un file Kotlin dedicato, ad esempio:

```text
core/designsystem/theme/Dimens.kt
```

Esempio:

```kotlin
object MediShelfDimens {
    val SpacingSmall = 8.dp
    val SpacingMedium = 16.dp
    val SpacingLarge = 24.dp
    val CardCornerRadius = 16.dp
}
```

Non usare valori come `16.dp`, `24.dp`, `8.dp` direttamente in modo ripetuto dentro le schermate, salvo casi locali davvero eccezionali e motivati.

### Colori
I colori devono essere centralizzati nel tema.

Risorse XML:

```text
app/src/main/res/values/colors.xml
```

Tema Compose:

```text
core/designsystem/theme/Color.kt
```

Le Composable devono usare i colori del tema o token semantici del design system, non valori hardcoded.

### Shape e tipografia
Anche shape e tipografia devono essere centralizzate nei file del tema:

```text
core/designsystem/theme/Shape.kt
core/designsystem/theme/Type.kt
```

### Numeri e soglie
I valori di business non devono essere dispersi nel codice.

Esempi:
- soglia default in scadenza;
- soglia default scorta bassa;
- limiti input;
- valori di default impostazioni.

Devono stare in oggetti dedicati, ad esempio:

```text
core/common/MediShelfDefaults.kt
```

oppure nel relativo package domain se sono regole di dominio.

## Regola sulle Preview Compose
Ogni Composable renderizzabile prodotto nel progetto deve avere almeno una `@Preview` significativa.

Regole:
- ogni `Screen` deve avere una Preview con stato realistico;
- ogni componente riusabile del design system deve avere una Preview;
- ogni helper Composable privato che disegna UI deve avere una Preview dedicata o una Preview locale che lo isoli chiaramente;
- i wrapper tecnici non renderizzabili o non preview-safe, ad esempio `Route` con `hiltViewModel()`, Composable che richiedono `NavController`, getter di tema o lambda `@Composable` passate come parametro, possono essere esclusi se la schermata o il componente stateless sottostante ha Preview;
- quando utile, aggiungere Preview per empty state, stato errore e stato con dati;
- le Preview non devono dipendere da ViewModel, Hilt, Room o dati reali;
- le Preview devono usare dati fake locali e stabili.

Esempio:

```kotlin
@Preview(showBackground = true)
@Composable
private fun InventoryScreenPreview() {
    MediShelfTheme {
        InventoryScreen(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = sampleMedicineItems
            ),
            onMedicineClick = {},
            onAddClick = {},
            onSearchQueryChanged = {}
        )
    }
}
```

## Regole UI su modifica e cancellazione
Per ogni entità modificabile:
- la modifica si attiva con icona standard a matita;
- la cancellazione si attiva con icona standard a cestino;
- la cancellazione richiede sempre conferma esplicita;
- il dialog di conferma deve spiegare cosa accadrà;
- quando possibile, preferire archiviazione a cancellazione definitiva.

## Hilt
Package:

```text
di/
  DatabaseModule.kt
  RepositoryModule.kt
  UseCaseModule.kt
  TimeModule.kt
  NotificationModule.kt
```

Regole:
- nessuna creazione manuale di repository dentro ViewModel;
- nessuna creazione manuale di DAO fuori dai moduli DI;
- dipendenze dichiarate in modo esplicito;
- provider testabili e sostituibili.

## Notifiche locali
Le notifiche locali sono parte della versione Free, ma possono essere implementate dopo il nucleo inventario/scadenzario.

Componenti previsti:

```text
core/notification/
  ExpiryNotificationScheduler.kt
  NotificationConstants.kt
```

Regole:
- la logica di selezione dei medicinali da notificare deve dipendere dal dominio;
- la parte Android-specific deve restare isolata;
- le notifiche devono rispettare le impostazioni dell'utente;
- le notifiche non devono includere consigli medici o posologici.

## GitHub Actions
La Milestone 1 deve includere l'automatizzazione della build tramite GitHub Actions.

Workflow minimo consigliato:

```text
.github/workflows/android-ci.yml
```

Controlli minimi:
- checkout repository;
- setup JDK;
- setup Gradle;
- build debug;
- esecuzione test unitari;
- eventualmente lint Android.

Esempio orientativo:

```yaml
name: Android CI

on:
  pull_request:
  push:
    branches:
      - main
      - develop

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Build debug
        run: ./gradlew assembleDebug

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Run lint
        run: ./gradlew lintDebug
```

La pipeline deve essere mantenuta semplice all'inizio. Potrà essere estesa con test strumentali e signing solo in fasi successive.

## Testing
I test devono seguire `TESTING_STRATEGY.md`.

Priorità iniziale:
1. test unitari su `MedicineStatusCalculator`;
2. test mapping entity/domain;
3. test use case principali;
4. test ViewModel;
5. test DAO Room;
6. test Compose UI per schermate principali.

Le regole su quantità opzionale, scadenza opzionale e archiviazione devono essere coperte presto.

## Commenti e documentazione codice
Ogni classe e ogni metodo devono avere commenti KDoc/Javadoc adeguati.

I commenti devono spiegare:
- scopo;
- parametri di input;
- valore restituito;
- eccezioni o condizioni di errore rilevanti;
- sintesi dell'algoritmo se non banale.

L'implementazione dei metodi deve contenere commenti inline nei passaggi delicati.

Non servono commenti ovvi o ridondanti, ma il codice generato dagli agenti IA deve essere comprensibile e manutenibile.

## Documentazione tecnica durante lo sviluppo
Ogni milestone deve prevedere una issue finale di aggiornamento documentazione tecnica.

La documentazione tecnica deve riflettere:
- decisioni architetturali prese;
- package effettivamente creati;
- flussi principali implementati;
- compromessi e limiti noti;
- eventuali divergenze motivate dai documenti iniziali.

## Sequenza implementativa della Milestone 1
Ordine consigliato:

1. scaffold progetto Android;
2. configurazione Gradle e dipendenze;
3. setup Hilt;
4. setup Compose Material 3;
5. design system minimo;
6. centralizzazione stringhe, dimensioni e colori;
7. setup Navigation Compose;
8. navigation shell con schermate placeholder;
9. setup Room;
10. entity e DAO iniziali;
11. domain model minimo;
12. `MedicineStatusCalculator` con test;
13. repository locale minimo;
14. setup GitHub Actions;
15. aggiornamento documentazione tecnica di fine milestone.

## Decisioni architetturali consolidate
- progetto monomodulo nella prima fase;
- package separati per layer e feature;
- domain indipendente da Room e Compose;
- Room come fonte dati locale della versione Free;
- stato medicinale calcolato, non persistito;
- quantità opzionale;
- scadenza opzionale;
- archiviazione logica;
- ViewModel per schermata;
- UiState immutabile;
- Composable preferibilmente stateless;
- Preview obbligatorie per Screen e componenti;
- risorse centralizzate, niente valori hardcoded sparsi;
- build automatizzata con GitHub Actions;
- documentazione tecnica aggiornata a ogni milestone.
