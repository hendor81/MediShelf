# NAVIGATION_MAP.md

## Scopo
Rappresentare in modo sintetico la navigazione principale della versione Free dell'app.

L'elenco ufficiale delle schermate e delle loro responsabilità è definito in `SCREEN_CATALOG.md`.

## Mappa generale
La mappa è espressa con una struttura ad albero per leggibilità, anche se tecnicamente alcune schermate sono raggiungibili da più percorsi e quindi la navigazione reale è un grafo.

```text
Avvio app
├── Onboarding essenziale (solo primo avvio o se non completato)
│   └── Home / Dashboard
└── Home / Dashboard
    ├── Inserimento / Modifica medicinale
    │   └── Salvataggio
    │       └── Inventario
    │           └── Dettaglio medicinale
    ├── Inventario
    │   ├── Ricerca e filtri
    │   ├── Dettaglio medicinale
    │   │   ├── Inserimento / Modifica medicinale
    │   │   ├── Aggiorna quantità
    │   │   ├── Archivia medicinale
    │   │   │   └── Archivio
    │   │   └── Elimina medicinale
    │   │       └── Conferma eliminazione
    │   └── Archivio
    │       └── Dettaglio medicinale archiviato
    │           └── Ripristina medicinale
    ├── Scadenzario
    │   ├── In scadenza
    │   │   └── Dettaglio medicinale
    │   ├── Scaduti
    │   │   └── Dettaglio medicinale
    │   └── Senza data
    │       └── Dettaglio medicinale
    └── Impostazioni
        ├── Soglia giorni in scadenza
        ├── Notifiche
        └── Preferenze archiviazione/cancellazione
```

### Note sulla natura a grafo
- `Dettaglio medicinale` è raggiungibile da Inventario e Scadenzario.
- `Inserimento / Modifica medicinale` può essere aperta sia per creare una nuova voce sia per modificarne una esistente.
- `Archivio` è raggiungibile da Inventario o come esito di un'archiviazione.
- `Impostazioni` resta accessibile principalmente da Home.

## Flussi principali
### Aggiunta medicinale
`Home -> Inserimento / Modifica -> Salvataggio -> Inventario`

### Consultazione inventario
`Home -> Inventario -> Dettaglio medicinale`

### Aggiornamento quantità
`Inventario -> Dettaglio medicinale -> Aggiorna quantità`

### Consultazione scadenze
`Home -> Scadenzario -> Dettaglio medicinale`

### Archiviazione
`Dettaglio medicinale -> Archivia -> Archivio`

### Impostazioni
`Home -> Impostazioni -> Aggiorna preferenze -> ritorno`

## Regole di navigazione
- Punto di ingresso standard: `Home`.
- `Inventario` e `Scadenzario` sono i due accessi primari ai contenuti.
- `Dettaglio medicinale` è raggiungibile da più punti ma deve restare unica come esperienza.
- `Inserimento / Modifica` usa la stessa base schermata con due modalità.
- `Archivio` è area secondaria, non primaria.
