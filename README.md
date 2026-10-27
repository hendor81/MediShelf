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
Il nucleo Free è implementato come alpha interna **1.0.0-alpha03** (codice 4):
inventario offline, quantità opzionali, scadenzario, dashboard, archivio con
ripristino, impostazioni, onboarding e notifiche locali di scadenza.
I reminder di assunzione/terapia sono esclusi.

L'interfaccia è disponibile in italiano e inglese e segue la lingua del
dispositivo. Da Android 13 è possibile scegliere la lingua di MediShelf dalle
impostazioni di sistema dedicate all'app. I dati inseriti dall'utente restano
nella lingua originale.

Le [note interne della release Free](FREE_RELEASE_NOTES.md) riportano funzioni,
esclusioni, limiti e verifiche. La prima sezione della
[documentazione tecnica](TECHNICAL_DOCUMENTATION.md) consolida lo stato corrente;
le sezioni storiche descrivono le milestone precedenti.
La pubblicazione su Play Store resta un'attività successiva.

## Provare l'app su Android tramite GitHub Actions

La pipeline **Android CI** verifica il progetto e conserva l'APK debug per 14
giorni. Si avvia sui push a `master` e ai branch `codex/**`, sulle pull request
verso `master` e manualmente tramite **Run workflow** (disponibile dopo che il
workflow è presente nel branch predefinito).

1. Aprire [Actions → Android CI](https://github.com/hendor81/MediShelf/actions/workflows/android-ci.yml)
   nel browser, accedendo a GitHub.
2. Selezionare l'esecuzione completata per il branch e il commit da provare.
3. Nella sezione **Artifacts**, scaricare `MediShelf-debug-<numero>-<tentativo>`.
   Da smartphone, se la sezione non appare, usare la modalità sito desktop.
4. Estrarre lo ZIP e aprire `app-debug.apk` dal gestore file Android.
5. Consentire, se richiesto, l'installazione di app dalla fonte utilizzata.

L'APK è una build di test firmata con la chiave debug del runner. La firma può
cambiare tra esecuzioni o differire da quella della versione già installata:
in tal caso Android può rifiutare l'aggiornamento. La disinstallazione elimina
l'inventario locale; non disinstallare una versione con dati da conservare.

## Preparare APK e AAB alpha firmati

La versione alpha è `1.0.0-alpha03` (`versionCode = 4`). Per usare la chiave
stabile locale, compilare `keystore.properties` dal modello
`keystore.properties.example` e avviare:

```powershell
.\gradlew.bat :app:assembleRelease -PsignRelease=true
```

L'APK firmato è `app/build/outputs/apk/release/app-release.apk`. Il file delle
credenziali e i keystore sono ignorati da Git. Gli artifact debug della CI
continuano a usare la firma debug e sono distinti dall'alpha firmata.

Per il caricamento in Play Console generare invece l'AAB firmato:

```powershell
.\gradlew.bat :app:bundleRelease -PsignRelease=true
```

Il bundle è `app/build/outputs/bundle/release/app-release.aab` e non si installa
direttamente su un dispositivo. La copia alpha03 per il caricamento è
`app/build/distributions/MediShelf-1.0.0-alpha03-vc4.aab`.

La procedura, la continuità verso Play Store e le verifiche manuali sono in
[`ALPHA_RELEASE_CHECKLIST.md`](ALPHA_RELEASE_CHECKLIST.md).
