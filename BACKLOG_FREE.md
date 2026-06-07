# BACKLOG_FREE.md

## Scopo
Fornire una roadmap operativa iniziale per la versione Free, utile a milestone, issue e pianificazione incrementale.

## Milestone 1 – Fondazioni
- setup progetto Android
- architettura base
- persistenza locale
- catalogo schermate e route principali allineate a `SCREEN_CATALOG.md`
- navigation shell
  - struttura minima di navigazione dell'app: `NavHost`, route principali, destinazione iniziale `Home`, collegamenti base tra Home, Inventario, Dettaglio, Inserimento/Modifica, Scadenzario, Archivio e Impostazioni. Non richiede schermate complete, ma placeholder navigabili e back behavior coerente.
- design system iniziale
- test foundation
- automazione build con GitHub Actions
- aggiornamento documentazione tecnica Milestone 1

## Milestone 2 – Inventario base
- modello Medicinale MVP
- schermata Inventario
- schermata Dettaglio medicinale
- schermata Inserimento / Modifica
- ricerca per nome
- filtro stato base
- archiviazione
- aggiornamento quantità
- aggiornamento documentazione tecnica Milestone 2

## Milestone 3 – Scadenze e notifiche
- classificazione stato medicinale
- schermata Scadenzario
- schermata Home / Dashboard
- impostazioni base
- notifiche locali
- coerenza conteggi e viste derivate
- aggiornamento documentazione tecnica Milestone 3

## Milestone 4 – Hardening e release Free
- empty states
- error handling
- onboarding essenziale
  - prima esperienza minima per spiegare in poche schermate o messaggi cosa fa l'app, cosa non fa, e come iniziare ad aggiungere il primo medicinale. Deve essere leggero, saltabile e non trasformarsi in tutorial lungo.
- polishing UX
- test regressivi
- build alpha/internal testing
- aggiornamento documentazione tecnica Milestone 4

## Ordine consigliato di implementazione
1. setup progetto e persistenza locale
2. modello Medicinale
3. Inserimento / Modifica
4. Inventario
5. Dettaglio
6. Quantità
7. Archiviazione
8. Regole di stato
9. Scadenzario
10. Home
11. Impostazioni
12. Notifiche
13. Hardening e release
14. Aggiornamento documentazione tecnica finale della versione Free

## Issue documentali obbligatorie
Ogni milestone deve includere una issue o sotto-issue dedicata all'aggiornamento della documentazione tecnica.

La issue deve verificare almeno:
- coerenza tra codice implementato e `TECHNICAL_DOCUMENTATION.md`;
- aggiornamento di eventuali decisioni architetturali;
- aggiornamento dello schema dati, se modificato;
- aggiornamento di navigation map, backlog o linee guida se la milestone ha introdotto cambiamenti rilevanti;
- presenza di note su debito tecnico o limiti noti.

## Chiarimenti terminologici
### Navigation shell
Per `navigation shell` si intende l'impalcatura minima di navigazione dell'app: route, `NavHost`, destinazione iniziale, collegamenti principali e placeholder delle schermate. Serve a validare subito il flusso generale senza aspettare che tutte le schermate siano complete.

### Onboarding essenziale
Per `onboarding essenziale` si intende una prima esperienza molto leggera che aiuta l'utente a capire rapidamente il valore dell'app e il primo passo da compiere. Deve comunicare che l'app è un inventario/scadenzario organizzativo, non uno strumento medico o un reminder di terapia.


## Issue documentali ricorrenti
Alla fine di ogni milestone deve essere prevista una issue di documentazione.

Esempi:
- aggiornare `TECHNICAL_DOCUMENTATION.md` con decisioni architetturali e schema corrente;
- aggiornare `DATA_MODEL.md` se cambia lo schema persistente;
- aggiornare `TESTING_STRATEGY.md` se cambiano livelli o convenzioni di test;
- aggiornare `SCREEN_CATALOG.md` e `NAVIGATION_MAP.md` se cambiano schermate o flussi;
- verificare che le issue create rispettino `ISSUE_TEMPLATE.md`.

## Template issue
Le issue GitHub devono seguire `ISSUE_TEMPLATE.md`, includendo sempre:
- contesto;
- obiettivo;
- scope incluso;
- scope escluso;
- criteri di accettazione;
- test richiesti;
- documenti da consultare;
- note di documentazione.


## Attività trasversali da includere nelle issue
- Ogni nuovo Composable renderizzabile deve includere la relativa Preview; i wrapper tecnici non preview-safe sono ammessi solo se il Composable stateless sottostante ha Preview.
- Le risorse UI devono essere centralizzate: stringhe in `strings.xml`, dimensioni in `Dimens.kt`, colori in `colors.xml` e tema Compose.
- Ogni milestone deve chiudersi con una issue di aggiornamento della documentazione tecnica.
- La Milestone 1 deve includere una issue dedicata alla configurazione di GitHub Actions per build, test e lint.
