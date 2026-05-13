# TECHNICAL_DOCUMENTATION.md

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


## Collegamento con gli altri documenti
La documentazione tecnica deve restare allineata con:

- `DOMAIN_MODEL.md`, per regole di dominio e stati;
- `DATA_MODEL.md`, per schema Room, entity, DAO, indici e migrazioni;
- `ARCHITECTURES_RULES.md`, per struttura e responsabilità;
- `TESTING_STRATEGY.md`, per test implementati e lacune note;
- `SCREEN_CATALOG.md` e `NAVIGATION_MAP.md`, per schermate e flussi effettivamente realizzati.
