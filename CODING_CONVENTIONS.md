# CODING_CONVENTIONS.md

## Scopo
Definire convenzioni di scrittura del codice Kotlin/Android del progetto, con particolare attenzione a leggibilità, commenti, documentazione tecnica e manutenzione nel tempo.

## Principi generali
- Scrivere codice semplice, esplicito e leggibile.
- Preferire nomi chiari a commenti compensativi.
- Evitare astrazioni premature.
- Mantenere separazione netta tra UI, dominio, dati e navigazione.
- Ogni modifica deve essere piccola, testabile e coerente con la issue di riferimento.

## Lingua del codice e dei commenti
- Nomi di classi, metodi, proprietà, package e variabili: inglese.
- Commenti KDoc/Javadoc e commenti inline: italiano, salvo termini tecnici consolidati.
- Messaggi utente nella UI: italiano nella prima versione, con struttura pronta a futura localizzazione.

## Documentazione KDoc/Javadoc obbligatoria
Ogni classe pubblica o rilevante deve avere un commento KDoc.
Ogni metodo pubblico o rilevante deve avere un commento KDoc.

Il commento deve indicare, quando applicabile:
- scopo della classe o del metodo;
- parametri di input;
- valore restituito;
- eccezioni o casi di errore rilevanti;
- sunto dell'algoritmo o della regola applicata;
- eventuali assunzioni o limiti.

Esempio:

```kotlin
/**
 * Calcola lo stato corrente di un medicinale in base a quantità, archiviazione e scadenza.
 *
 * @param medicine medicinale da valutare.
 * @param today data corrente usata come riferimento per il calcolo.
 * @param expiringThresholdDays numero di giorni entro cui una scadenza è considerata vicina.
 * @return stato derivato del medicinale.
 *
 * L'algoritmo applica una priorità esplicita: archiviato, esaurito,
 * scadenza sconosciuta, scaduto, in scadenza, valido.
 */
fun calculateStatus(
    medicine: Medicine,
    today: LocalDate,
    expiringThresholdDays: Int
): MedicineStatus
```

## Commenti inline
L'implementazione dei metodi deve contenere commenti inline nei passaggi più delicati.

Sono considerati passaggi delicati:
- regole di business non banali;
- mapping tra layer;
- gestione date e scadenze;
- gestione permessi e notifiche;
- transazioni o aggiornamenti dati;
- workaround tecnici;
- codice che potrebbe sembrare controintuitivo.

Evitare commenti inline ovvi, ad esempio:

```kotlin
// Incrementa i di 1
index++
```

Preferire commenti che spiegano il perché:

```kotlin
// Un medicinale archiviato non deve generare stati di attenzione nella Home.
if (medicine.isArchived) return MedicineStatus.ARCHIVED
```


## Convenzioni di naming
- Usare `camelCase` per nomi di variabili, proprietà, parametri e funzioni/metodi.
- Usare `PascalCase` per classi, interfacce, enum, sealed class/interface e Composable principali.
- Usare `UPPER_SNAKE_CASE` solo per costanti realmente globali o statiche.
- I package devono essere in minuscolo, senza underscore, salvo casi eccezionali.
- I nomi devono essere in inglese, espliciti e coerenti con il dominio.

Esempi:
- `medicineName`
- `expirationDate`
- `calculateMedicineStatus`
- `MedicineDetailViewModel`
- `LOW_STOCK_DEFAULT_THRESHOLD`

## Convenzioni Kotlin
- Usare `data class` per modelli immutabili.
- Preferire `val` a `var` quando possibile.
- Evitare valori magici: usare costanti nominate.
- Usare sealed interface/class per stati UI complessi, se utile.
- Gestire nullability in modo esplicito.
- Evitare `!!` salvo casi eccezionali e documentati.
- Preferire funzioni pure per regole di dominio.

## Convenzioni Compose
- Le Composable devono essere piccole e dichiarative.
- Le schermate ricevono `UiState` e callback.
- Nessuna logica di business complessa nelle Composable.
- Preview utili dove il componente è riusabile o visivamente rilevante.
- I componenti condivisi devono stare nel design system o in package coerenti.

## Convenzioni ViewModel
- I ViewModel espongono stato osservabile, idealmente tramite `StateFlow`.
- Gli eventi utente devono avere funzioni chiare, ad esempio `onSaveClick` o `onQuantityIncrementClick`.
- Il ViewModel coordina use case e mapping verso UI, non implementa regole profonde di dominio.
- Ogni ViewModel rilevante deve avere test unitari quando contiene logica di stato.

## Convenzioni dominio
- Le regole di business devono essere indipendenti da Android framework e Compose.
- Le funzioni di dominio devono essere testabili senza emulatori o device.
- Lo stato del medicinale deve essere calcolato in dominio/use case, non in UI.

## Convenzioni dati
- Room entity e modelli di dominio devono restare separati quando il mapping migliora chiarezza e stabilità.
- I DAO non devono essere usati direttamente dai ViewModel.
- I repository concreti stanno nel layer data.
- Le query devono distinguere chiaramente elementi attivi e archiviati.

## Documentazione tecnica durante lo sviluppo
Ogni milestone deve chiudersi con un aggiornamento della documentazione tecnica.

La documentazione tecnica deve includere almeno:
- struttura package/moduli effettivamente adottata;
- decisioni architetturali prese;
- principali classi introdotte;
- use case disponibili;
- schema Room corrente;
- flussi di navigazione implementati;
- note su test e copertura;
- limiti noti e debito tecnico.

Gli aggiornamenti documentali devono essere tracciati con issue dedicate o sotto-task della milestone.

## Regola finale
Il codice deve essere comprensibile da uno sviluppatore che entra nel progetto mesi dopo. Commenti, KDoc, nomi e documentazione tecnica non sono accessori: fanno parte della qualità minima attesa del progetto.


## Risorse e valori hardcoded
- Non inserire stringhe visibili all'utente direttamente nel codice Kotlin: usare `strings.xml`.
- Non disseminare dimensioni come `8.dp`, `16.dp`, `24.dp` nelle schermate: usare `Dimens.kt` o token equivalenti.
- Non usare colori hardcoded nelle Composable: usare tema Compose, `colors.xml` e token semantici.
- Non duplicare soglie o valori di default: centralizzarli in oggetti dedicati, ad esempio `MediShelfDefaults`.

## Preview Compose
- Ogni Composable di schermata deve avere almeno una `@Preview`.
- Ogni componente riusabile deve avere almeno una `@Preview`.
- Le Preview devono usare dati fake locali e stabili.
