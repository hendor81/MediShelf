# ARCHITECTURES_RULES.md

## Obiettivo del documento
Definire le regole architetturali e implementative del progetto Android, in modo che agenti IA e sviluppatori umani producano codice coerente, leggibile, testabile e incrementale.

## Stack tecnologico
Stack iniziale consigliato:
- **Kotlin**
- **Jetpack Compose** per UI
- **Material 3** come base design system Android
- **Navigation Compose** per navigazione
- **ViewModel** per state handling delle schermate
- **Room** per persistenza locale
- **Hilt** per Dependency Injection
- **Coroutines + Flow** per asincronia e stream di stato
- **JUnit** per test unitari
- **Compose UI Test** per test UI

## Struttura del progetto
Struttura logica consigliata:
- `app/`
- `core/`
- `feature/`
- `domain/`
- `data/`

In una prima versione semplificata può andare bene anche una struttura per package ben separata, purché mantenga le responsabilità chiare.

### Separazione minima raccomandata
- `ui` / `presentation`
- `domain`
- `data`
- `di`
- `navigation`
- `core/designsystem`
- `core/common`

## Regole UI
- Le Composable devono restare il più possibile semplici e dichiarative.
- Nessuna logica di business complessa dentro le schermate.
- Le schermate leggono uno `UiState` esposto dal ViewModel.
- Gli eventi utente vengono inoltrati al ViewModel tramite callback esplicite.
- Evitare side effect sparsi e non tracciabili.
- Preferire componenti piccoli, riusabili e testabili.

## Regole ViewModel
- Ogni schermata principale ha il proprio ViewModel.
- Il ViewModel coordina use case, repository e trasformazione in `UiState`.
- Il ViewModel non deve conoscere dettagli di rendering della UI.
- Gli eventi devono passare tramite funzioni chiare, ad esempio `onSearchQueryChanged`, `onArchiveClick`, `onSaveClick`.
- Il ViewModel deve essere facilmente testabile in isolamento.

## Regole di dominio
- Il dominio contiene le regole di business centrali.
- Le regole sullo stato del medicinale devono vivere fuori dalla UI.
- Use case e regole di classificazione devono essere puri o quasi puri quando possibile.
- Le entità di dominio non devono dipendere da Compose o framework UI.

## Regole sui dati
- Il layer dati gestisce persistenza locale, mapping e repository concreti.
- Room è la fonte primaria per la versione Free.
- Il modello dati persistente è definito in `DATA_MODEL.md`.
- Gli oggetti di persistenza non devono propagarsi direttamente in UI se è utile un mapping intermedio.
- Le query devono riflettere chiaramente la distinzione tra elementi attivi e archiviati.
- L'archiviazione iniziale è logica, non fisica.
- Quantità e scadenza devono restare opzionali, salvo diversa decisione documentata.

## Regole di navigazione
- La navigazione deve essere semplice e prevedibile.
- Punto di ingresso standard: `Home`.
- Le route devono avere naming coerente e stabile.
- Le schermate di dettaglio ricevono identificativi minimi, non oggetti complessi serializzati.
- Il back behavior deve essere naturale e non sorprendere l'utente.
- L'elenco delle schermate è definito in `SCREEN_CATALOG.md`.
- La navigation map di riferimento è definita in `NAVIGATION_MAP.md`.

## Regole di Dependency Injection
- Hilt è il meccanismo standard di DI.
- Nessuna creazione manuale di repository o datasource dentro ViewModel o Composable.
- Le dipendenze vanno dichiarate in modo esplicito e centralizzato.
- I fake/test double devono essere facilmente sostituibili nei test.

## Regole di naming
- Nomi espliciti e leggibili, evitare abbreviazioni ambigue.
- Le schermate usano nomi coerenti con la documentazione di prodotto.
- I ViewModel seguono il pattern `<ScreenName>ViewModel`.
- Gli `UiState` seguono il pattern `<ScreenName>UiState`.
- Gli eventi possono usare pattern come `<ScreenName>Action` o funzioni `onXxx`.
- I use case usano verbi chiari, ad esempio `GetActiveMedicinesUseCase`, `ArchiveMedicineUseCase`.

## Regole di riuso
- Estrarre componenti riusabili solo quando il riuso è reale o imminente.
- Non creare astrazioni premature.
- Riutilizzare pattern coerenti per card, badge, field e feedback.
- Il design system iniziale deve coprire gli elementi ripetuti, non ogni singolo dettaglio.

## Regole su gestione errori
- Gli errori devono essere gestiti in modo esplicito e leggibile.
- La UI deve prevedere empty state, errore e success feedback dove necessario.
- Gli errori non devono lasciare la schermata in stato incoerente.
- Preferire messaggi chiari e non tecnici per l'utente.
- Tenere separati errori tecnici, validazioni e stati vuoti.

## Regole di testing
Le regole dettagliate sono definite in `TESTING_STRATEGY.md`.

In sintesi:
- testare prima di tutto le regole di business critiche;
- avere test unitari su classificazione stato, quantità opzionale, scadenza opzionale e archiviazione;
- avere test DAO/Room sulle query persistenti principali;
- avere test ViewModel sui flussi di schermata;
- avere UI test sui flussi principali della versione Free;
- ogni feature importante dovrebbe avere almeno un criterio di verifica automatizzabile;
- evitare test fragili troppo legati a dettagli di implementazione interna.

## Regole addizionali utili
### Incrementalità
- Preferire PR piccole.
- Preferire feature complete ma limitate, invece di implementazioni molto ampie e incomplete.

### Tracciabilità
- Ogni modifica significativa deve poter essere ricondotta a schermata, use case, milestone o issue.

### Compatibilità con agenti IA
- Scrivere codice e strutture facili da capire e modificare.
- Evitare scorciatoie che aumentano confusione o coupling.


## Regole di documentazione tecnica
La documentazione tecnica deve essere prodotta e aggiornata durante lo sviluppo, non ricostruita solo alla fine.

Ogni milestone deve prevedere una issue o sotto-issue dedicata all'aggiornamento di `TECHNICAL_DOCUMENTATION.md`.

Gli aggiornamenti devono descrivere almeno:
- struttura package o moduli effettiva;
- decisioni architetturali prese;
- classi e use case principali introdotti;
- schema Room corrente;
- flussi di navigazione implementati;
- test presenti e limiti noti;
- eventuale debito tecnico.

## Regole su commenti nel codice
Le regole dettagliate su KDoc/Javadoc, commenti inline e convenzioni di scrittura sono definite in `CODING_CONVENTIONS.md`.

In sintesi:
- ogni classe rilevante deve avere un commento KDoc/Javadoc;
- ogni metodo pubblico o rilevante deve avere un commento che ne spieghi scopo, input, output, eccezioni e logica;
- i passaggi implementativi delicati devono avere commenti inline;
- i commenti devono chiarire il perché delle scelte, non ripetere meccanicamente il codice.


## Regole operative aggiuntive
- L'architettura Android concreta è descritta in `ANDROID_ARCHITECTURE.md`.
- La Milestone 1 deve includere una pipeline GitHub Actions per build debug, test unitari e lint.
- Ogni Composable di schermata o componente riusabile deve avere almeno una `@Preview` significativa.
- Il codice non deve contenere stringhe, dimensioni, colori o valori visuali hardcoded: usare `strings.xml`, tema Compose, `Dimens.kt`, `colors.xml` e costanti centralizzate.
