# README.md

## Nome progetto
MediShelf – Android

## Nome definitivo dell'app
**MediShelf**

## Cos'è
Applicazione Android pensata per aiutare l'utente a gestire il proprio armadietto dei medicinali.

Il cuore del prodotto è semplice:
- mantenere un inventario aggiornato dei medicinali presenti;
- sapere cosa sta per scadere e cosa è già scaduto;
- ridurre sprechi, dimenticanze e acquisti duplicati.

## Obiettivo della prima fase
Realizzare una versione Free, offline-first, solida e chiara, utile per un singolo utente.

La versione iniziale deve permettere almeno di:
- aggiungere un medicinale;
- consultare l'inventario;
- aggiornare quantità residue;
- visualizzare le scadenze;
- ricevere notifiche locali di scadenza;
- archiviare medicinali non più attivi.

## Posizionamento del prodotto
L'app è uno strumento organizzativo personale o familiare.

Non è:
- uno strumento di diagnosi;
- un sistema di supporto clinico;
- un sostituto di medico o farmacista;
- un gestore di terapia nella fase iniziale.

## Modello di prodotto
Il prodotto è pensato a livelli:
- **Free**: nucleo essenziale utile da subito;
- **Pro**: funzionalità avanzate locali, sbloccabili con acquisto una tantum;
- **Subscription**: funzionalità continuative come cloud, sync, condivisione e smart input.

## Strumenti previsti
- **GPT-Codex**: supporto alla generazione e rifinitura del codice, test, refactoring e task tecnici.
- **Figma**: progettazione di wireframe, navigation map, componenti e UI.
- **GitHub**: repository, backlog, milestone, issue, review e release workflow.

## Documenti chiave
- `PRODUCT_OVERVIEW.md` – intento di prodotto e confini
- `DOMAIN_MODEL.md` – entità, stati e regole di dominio
- `DATA_MODEL.md` – modello dati persistente, Room, entity, DAO e mapping
- `ARCHITECTURES_RULES.md` – regole architetturali e di implementazione
- `ANDROID_ARCHITECTURE.md` – architettura Android concreta, package, layer e flusso dati
- `CODING_CONVENTIONS.md` – convenzioni codice, commenti KDoc/Javadoc e documentazione tecnica
- `UI_GUIDELINES.md` – principi UX/UI e regole visuali
- `VISUAL_DESIGN_BRIEF.md` – direzione visuale, palette, componenti, tono e layout
- `SCREEN_CATALOG.md` – elenco ufficiale delle schermate della versione Free
- `NAVIGATION_MAP.md` – flusso tra schermate
- `BACKLOG_FREE.md` – roadmap operativa della versione Free
- `TESTING_STRATEGY.md` – strategia di test per dominio, dati, ViewModel e UI
- `ISSUE_TEMPLATE.md` – template standard per issue GitHub
- `COMMIT_GUIDELINES.md` – regole per commit e riferimenti alle issue
- `TECHNICAL_DOCUMENTATION.md` – documentazione tecnica viva del progetto

## Stato attuale
Questa documentazione rappresenta una base iniziale per guidare agenti IA e sviluppo umano.

È un set di working documents: può evolvere, ma ogni aggiornamento deve preservare coerenza e semplicità.
