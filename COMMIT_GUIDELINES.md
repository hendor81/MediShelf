# COMMIT_GUIDELINES.md

## Scopo del documento
Definire come scrivere commit chiari, utili in review e coerenti con il backlog GitHub del progetto.

## Principi generali
- Ogni commit deve avere un messaggio dettagliato: la prima riga deve sintetizzare il cambiamento, mentre il corpo deve spiegare cosa è stato modificato, perché, e quali limiti o trade-off sono rilevanti.
- Ogni commit deve avere uno scopo chiaro.
- Evitare commit troppo grandi e confusi.
- Evitare commit che mescolano refactoring, feature e fix non collegati.
- Preferire una storia Git leggibile e facilmente ricostruibile.
- Il messaggio di commit deve essere scritto in italiano.
- I messaggi di commit devono essere sempre dettagliati: la prima riga sintetizza, il corpo spiega cosa è stato fatto, perché, con quali limiti e a quale issue si riferisce.

## Struttura consigliata del commit
Formato consigliato della prima riga:

`<tipo>: <descrizione sintetica>`

Esempi:
- `feat: aggiunge la schermata inventario`
- `fix: corregge il calcolo dello stato scaduto`
- `test: aggiunge test per il filtro dell'inventario`
- `refactor: semplifica il mapping del medicinale`
- `docs: aggiorna le linee guida UI`

## Tipi consigliati
- `feat` – nuova funzionalità
- `fix` – correzione bug
- `refactor` – miglioramento interno senza cambiare comportamento atteso
- `test` – aggiunta o modifica test
- `docs` – documentazione
- `chore` – attività tecniche o manutentive
- `perf` – miglioramento prestazionale

## Regole sul contenuto del messaggio
### Prima riga
- Breve ma informativa.
- Scritta al presente indicativo o forma verbale semplice.
- Non superare idealmente 72 caratteri.

### Corpo del commit
Il corpo è obbligatorio per tutti i commit non puramente documentali o meccanici. Anche nei commit piccoli va preferito un corpo descrittivo quando aiuta la review.

Deve includere, quando applicabile:
- cosa è stato fatto;
- perché è stato fatto;
- eventuali note su trade-off o limiti;
- riferimento alle issue coinvolte;
- eventuali test eseguiti o non eseguiti;
- eventuali impatti su documentazione tecnica, UI o dominio.

## Riferimento alle issue GitHub
Per collegare un commit a una issue usare sempre il riferimento esplicito.

Esempi:
- `Refs #12`
- `Closes #18`
- `Fixes #21`
- `Resolves #34`

## Chiusura automatica delle issue
Per fare in modo che GitHub chiuda automaticamente una issue quando il commit entra nel branch target, usare nel messaggio:
- `Closes #numero`
- `Fixes #numero`
- `Resolves #numero`

Esempio completo:

```text
feat: aggiunge la schermata inventario

Implementa la schermata con lista dei medicinali attivi,
ricerca per nome e accesso al dettaglio.

Closes #27
```

## Regole pratiche
- Un commit dovrebbe chiudere una issue solo se completa davvero quel lavoro.
- Se il commit contribuisce ma non conclude, usare `Refs #numero` invece di `Closes #numero`.
- Se una modifica tocca più issue, chiuderne automaticamente solo una se davvero completa, e usare `Refs` per le altre.
- Non usare riferimenti automatici di chiusura in commit esplorativi o parziali.

## Granularità consigliata
### Va bene fare un commit quando
- una schermata è davvero completa in un suo blocco coerente;
- una regola di business è implementata e testata;
- un refactor è autonomo e verificabile;
- una correzione bug è isolabile.

### Evitare commit che
- mescolano UI, dominio, test e fix non collegati;
- includono file toccati accidentalmente;
- hanno messaggi generici tipo `update`, `fix stuff`, `varie`.

## Convenzioni utili per questo progetto
### Commit su feature
```text
feat: aggiunge il form di inserimento del medicinale

Implementa i campi minimi richiesti, la validazione del nome e dei campi effettivamente obbligatori
ed il salvataggio locale della nuova voce.

Closes #31
```

### Commit su fix
```text
fix: corregge il filtro dei medicinali scaduti

La lista inventario non escludeva correttamente i medicinali validi
quando il filtro era impostato su "scaduti".

Fixes #44
```

### Commit su test
```text
test: aggiunge test per la classificazione dello stato del medicinale

Copre i casi valido, in scadenza, scaduto, esaurito e senza data.

Refs #52
```

### Commit su documentazione
```text
docs: aggiorna le linee guida di navigazione della versione free

Allinea la documentazione alla navigation map corrente.

Refs #16
```

## Regola finale
Ogni commit deve essere sufficientemente dettagliato da permettere a chi legge di capire rapidamente:
- cosa è stato fatto;
- perché è stato fatto;
- a quale issue è collegato;
- se quella issue deve essere chiusa automaticamente oppure no.
