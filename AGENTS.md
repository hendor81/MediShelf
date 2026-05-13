# AGENTS.md

## Scopo
Questo file guida gli agenti IA nella consultazione della documentazione del progetto. Va letto per primo.

## Ordine di consultazione consigliato
Gli agenti devono consultare i file in questo ordine, salvo task molto specifici.

1. `AGENTS.md`
   - Indice operativo della documentazione.
   - Definisce l'ordine di lettura e il ruolo dei documenti.

2. `README.md`
   - Panoramica rapida del progetto.
   - Serve a capire in pochi minuti che prodotto stiamo costruendo.

3. `PRODUCT_OVERVIEW.md`
   - Fonte principale per intento di prodotto, target, scope, principi e traiettoria evolutiva.
   - Va usato come riferimento quando una decisione può alterare il posizionamento del prodotto.

4. `DOMAIN_MODEL.md`
   - Fonte principale per entità, stati, regole di dominio, quantità, scadenze e archiviazione.
   - Va consultato prima di implementare logica di business o persistenza collegata ai medicinali.

5. `DATA_MODEL.md`
   - Fonte principale per schema persistente, entity Room, campi nullable, indici, mapping e migrazioni.
   - Va consultato prima di implementare persistenza, DAO, repository o migration.

6. `ARCHITECTURES_RULES.md`
   - Fonte principale per regole architetturali, stack, separazione delle responsabilità e convenzioni di implementazione.
   - Va seguito in modo rigoroso per tutte le modifiche al codice.

7. `CODING_CONVENTIONS.md`
   - Regole di scrittura codice, commenti KDoc/Javadoc, commenti inline e documentazione tecnica.
   - Va consultato prima di generare o modificare codice.

8. `UI_GUIDELINES.md`
   - Fonte principale per decisioni di UX/UI, flussi, layout, componenti e accessibilità.
   - Va consultato prima di creare o modificare schermate, componenti o prototipi.

9. `VISUAL_DESIGN_BRIEF.md`
   - Direzione visuale concreta: personalità, palette, card, badge, iconografia, microcopy e regole visuali operative.
   - Va consultato prima di generare mockup, wireframe visuali, componenti grafici o design system.

10. `SCREEN_CATALOG.md`
   - Elenco ufficiale delle schermate della versione Free e loro responsabilità.
   - Va consultato prima di creare, rinominare o accorpare schermate.

11. `NAVIGATION_MAP.md`
   - Mappa sintetica dei flussi e delle relazioni tra schermate.
   - Va usata per evitare percorsi di navigazione incoerenti.

12. `BACKLOG_FREE.md`
   - Roadmap operativa iniziale della versione Free.
   - Va usata per allineare task, milestone, issue e ordine di implementazione.

13. `TESTING_STRATEGY.md`
   - Strategia di test per dominio, data layer, ViewModel, UI, navigazione e notifiche.
   - Va consultato quando una issue introduce comportamento verificabile.

14. `ISSUE_TEMPLATE.md`
   - Template standard per scrivere issue GitHub complete e utili agli agenti IA.
   - Va usato quando si generano nuove issue o si raffinano milestone.

15. `TECHNICAL_DOCUMENTATION.md`
   - Documentazione tecnica viva del progetto.
   - Va aggiornata al termine di ogni milestone e quando cambiano decisioni architetturali rilevanti.

16. `COMMIT_GUIDELINES.md`
   - Regole di commit e collegamento con le issue GitHub.
   - Va consultato prima di proporre messaggi di commit o PR.

## Regole di priorità
Quando due documenti sembrano entrare in conflitto, usare questa priorità:

1. `PRODUCT_OVERVIEW.md`
2. `DOMAIN_MODEL.md`
3. `DATA_MODEL.md`
4. `ARCHITECTURES_RULES.md`
5. `CODING_CONVENTIONS.md`
6. `UI_GUIDELINES.md`
7. `VISUAL_DESIGN_BRIEF.md`
8. `SCREEN_CATALOG.md`
9. `NAVIGATION_MAP.md`
10. `BACKLOG_FREE.md`
11. `TESTING_STRATEGY.md`
12. `ISSUE_TEMPLATE.md`
13. `TECHNICAL_DOCUMENTATION.md`
14. `COMMIT_GUIDELINES.md`
15. `README.md`

## Regole operative per gli agenti IA
- Non introdurre funzionalità fuori scope senza segnalarlo esplicitamente.
- Non trasformare l'app in uno strumento medico-clinico.
- Privilegiare semplicità, chiarezza, offline-first e basso attrito.
- Prima di proporre codice, verificare sempre coerenza con architettura, naming e navigazione.
- Prima di proporre UI, verificare coerenza con `UI_GUIDELINES.md`, `VISUAL_DESIGN_BRIEF.md`, `SCREEN_CATALOG.md` e `NAVIGATION_MAP.md`.
- Prima di proporre task o commit, verificare coerenza con `BACKLOG_FREE.md` e `COMMIT_GUIDELINES.md`.

## Tipi di task e documenti da consultare
### Se il task riguarda il prodotto
Leggere: `README.md` → `PRODUCT_OVERVIEW.md`

### Se il task riguarda il codice
Leggere: `README.md` → `PRODUCT_OVERVIEW.md` → `DOMAIN_MODEL.md` → `DATA_MODEL.md` → `ARCHITECTURES_RULES.md` → `CODING_CONVENTIONS.md`

### Se il task riguarda UI, design visuale, mockup o Figma
Leggere: `README.md` → `PRODUCT_OVERVIEW.md` → `UI_GUIDELINES.md` → `VISUAL_DESIGN_BRIEF.md` → `SCREEN_CATALOG.md` → `NAVIGATION_MAP.md`

### Se il task riguarda backlog, issue o roadmap
Leggere: `README.md` → `PRODUCT_OVERVIEW.md` → `BACKLOG_FREE.md` → `ISSUE_TEMPLATE.md` → `TESTING_STRATEGY.md` → `TECHNICAL_DOCUMENTATION.md`

### Se il task riguarda commit o branch
Leggere: `COMMIT_GUIDELINES.md`

### Se il task riguarda regole di dominio
Leggere: `PRODUCT_OVERVIEW.md` → `DOMAIN_MODEL.md`

### Se il task riguarda dati, Room o repository
Leggere: `DOMAIN_MODEL.md` → `DATA_MODEL.md` → `ARCHITECTURES_RULES.md` → `TESTING_STRATEGY.md`

### Se il task riguarda test
Leggere: `TESTING_STRATEGY.md` → `DOMAIN_MODEL.md` → `DATA_MODEL.md` → `ARCHITECTURES_RULES.md`

### Se il task riguarda documentazione tecnica o commenti nel codice
Leggere: `CODING_CONVENTIONS.md` → `TECHNICAL_DOCUMENTATION.md`

## Output atteso dagli agenti
Gli agenti dovrebbero produrre output:
- coerenti con il livello di maturità del progetto;
- piccoli, incrementali e verificabili;
- tracciabili rispetto a schermate, use case, milestone o issue;
- compatibili con un flusso di lavoro GitHub + GPT-Codex + Figma.
