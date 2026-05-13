# ISSUE_TEMPLATE.md

## Scopo
Definire un formato standard per le issue GitHub di **MediShelf**, in modo che siano chiare per sviluppatori umani e agenti IA come GPT-Codex.

Ogni issue deve essere piccola, implementabile, verificabile e collegata a milestone, documenti e criteri di accettazione.

## Template consigliato

```markdown
# Titolo

## Contesto
Breve descrizione del perché questa issue esiste e a quale parte del prodotto appartiene.

## Obiettivo
Descrizione sintetica del risultato atteso.

## Milestone
Indicare la milestone di riferimento.

Esempio:
- Milestone 1 – Fondazioni

## Documenti da consultare
Elencare i documenti rilevanti.

Esempio:
- `AGENTS.md`
- `PRODUCT_OVERVIEW.md`
- `DOMAIN_MODEL.md`
- `DATA_MODEL.md`
- `ARCHITECTURES_RULES.md`
- `CODING_CONVENTIONS.md`
- `TESTING_STRATEGY.md`

## Scope incluso
Elenco puntuale di cosa deve essere fatto.

- ...
- ...

## Scope escluso
Elenco puntuale di cosa non deve essere fatto in questa issue.

- ...
- ...

## Requisiti funzionali
Descrivere il comportamento utente o di sistema atteso.

## Requisiti tecnici
Descrivere vincoli tecnici, package, classi, framework o pattern da rispettare.

## Criteri di accettazione
La issue può considerarsi completa quando:

- [ ] criterio 1
- [ ] criterio 2
- [ ] criterio 3

## Test richiesti
Indicare test automatici o verifiche manuali richieste.

- [ ] test unitario
- [ ] test ViewModel
- [ ] test DAO
- [ ] test UI Compose
- [ ] verifica manuale

## Note UX/UI
Da compilare se la issue tocca schermate, componenti o flussi.

## Note di documentazione
Indicare se aggiornare documentazione tecnica o file `.md`.

## Definizione di completamento
La issue è completata solo se:

- il codice compila;
- i test rilevanti passano;
- i criteri di accettazione sono soddisfatti;
- la documentazione impattata è aggiornata;
- il commit segue `COMMIT_GUIDELINES.md`.
```

## Regole per scrivere buone issue
- Una issue deve avere un obiettivo chiaro.
- Evitare issue troppo grandi o vaghe.
- Separare setup, feature, refactor e bugfix.
- Non mischiare UI, data layer e dominio se il lavoro diventa troppo ampio.
- Esplicitare sempre cosa è escluso.
- Inserire criteri di accettazione verificabili.
- Collegare l'issue alla milestone corretta.

## Esempio issue – Setup Room
```markdown
# Configurare Room per la persistenza locale

## Contesto
MediShelf è un'app offline-first. La versione Free deve salvare localmente l'inventario dei medicinali.

## Obiettivo
Configurare Room e predisporre la prima struttura database locale.

## Milestone
- Milestone 1 – Fondazioni

## Documenti da consultare
- `DOMAIN_MODEL.md`
- `DATA_MODEL.md`
- `ARCHITECTURES_RULES.md`
- `CODING_CONVENTIONS.md`
- `TESTING_STRATEGY.md`

## Scope incluso
- Aggiungere dipendenze Room.
- Creare database Room iniziale.
- Creare `MedicineEntity` coerente con `DATA_MODEL.md`.
- Creare DAO minimo.
- Configurare type converter per date.

## Scope escluso
- UI inventario.
- Repository completo.
- Notifiche.
- Funzioni Pro o Subscription.

## Criteri di accettazione
- [ ] Il progetto compila.
- [ ] Esiste una entity Room per i medicinali.
- [ ] Esiste un DAO con operazioni minime.
- [ ] La quantità è opzionale.
- [ ] La scadenza è opzionale.
- [ ] L'archiviazione logica è supportata.

## Test richiesti
- [ ] Test DAO per inserimento e lettura.
- [ ] Test DAO per campo quantità nullo.
- [ ] Test DAO per campo scadenza nullo.

## Note di documentazione
Aggiornare `TECHNICAL_DOCUMENTATION.md` con schema Room iniziale.
```

## Esempio issue – Form inserimento medicinale
```markdown
# Implementare il form di inserimento medicinale

## Contesto
L'utente deve poter aggiungere rapidamente un medicinale anche con informazioni minime.

## Obiettivo
Creare la schermata di inserimento con nome obbligatorio e campi opzionali.

## Milestone
- Milestone 2 – Inventario base

## Documenti da consultare
- `DOMAIN_MODEL.md`
- `DATA_MODEL.md`
- `UI_GUIDELINES.md`
- `SCREEN_CATALOG.md`
- `NAVIGATION_MAP.md`
- `TESTING_STRATEGY.md`

## Scope incluso
- Campo nome obbligatorio.
- Campi opzionali: formato, quantità, unità, scadenza, luogo, note.
- Validazione nome.
- Salvataggio locale.
- Feedback di salvataggio.

## Scope escluso
- Barcode.
- OCR.
- Gestione confezioni multiple.
- Profili persona.

## Criteri di accettazione
- [ ] L'utente può salvare un medicinale inserendo solo il nome.
- [ ] La quantità non è obbligatoria.
- [ ] La scadenza non è obbligatoria.
- [ ] Se il nome manca, viene mostrato un errore vicino al campo.
- [ ] Dopo il salvataggio il medicinale compare nell'inventario.

## Test richiesti
- [ ] Test ViewModel su validazione nome.
- [ ] Test ViewModel su salvataggio con solo nome.
- [ ] Test UI Compose sul flusso minimo.
```

## Label consigliate
Label utili su GitHub:

- `type: feature`
- `type: bug`
- `type: refactor`
- `type: test`
- `type: docs`
- `area: domain`
- `area: data`
- `area: ui`
- `area: navigation`
- `area: notifications`
- `priority: high`
- `priority: medium`
- `priority: low`

## Regola finale
Una buona issue deve permettere a un agente IA di lavorare senza inventare il contesto. Tutto ciò che è importante deve essere scritto nella issue o rimandare chiaramente ai documenti corretti.
