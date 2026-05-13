# UI_GUIDELINES.md

## Direzione visiva di base
L'app deve trasmettere ordine, chiarezza e controllo.

La direzione visiva deve essere:
- pulita;
- sobria;
- moderna ma non fredda;
- pratica;
- rassicurante.

L'interfaccia non deve sembrare né un tool clinico ospedaliero né un'app giocosa.

## Principi generali di layout
- Gerarchia visiva forte e leggibile.
- Poche azioni primarie per schermata.
- Spaziature generose.
- Raggruppamento chiaro delle informazioni.
- Scansione verticale semplice.
- Nessun sovraccarico informativo nella Home.

## Indicazioni per ogni schermata
### Home / Dashboard
- Mostrare pochi indicatori chiave.
- Dare evidenza a ciò che richiede attenzione.
- CTA principale ben visibile: aggiungi medicinale.
- Accessi rapidi a Inventario e Scadenzario.

### Inventario
- Lista molto leggibile.
- Ricerca immediata.
- Stato visibile a colpo d'occhio.
- Ogni item deve essere compatto ma informativo.

### Dettaglio medicinale
- Evidenziare nome, stato, scadenza e quantità quando disponibile.
- Le azioni frequenti devono essere immediatamente accessibili.
- Modifica e archiviazione devono essere facili da trovare ma non intrusive.

### Inserimento / Modifica
- Form corto e semplice.
- La quantità non deve essere obbligatoria: l'utente deve poter salvare rapidamente una confezione senza aprirla o stimarne il contenuto.
- Campi obbligatori chiari.
- Campi opzionali non invasivi.
- Errori di validazione vicini ai campi.

### Scadenzario
- Separazione chiarissima tra in scadenza e scaduti.
- Ordinamento cronologico intuitivo.
- Uso attento di colore e badge per evidenziare priorità.

### Impostazioni
- Poche preferenze ma leggibili.
- Nessuna complessità inutile.
- Linguaggio semplice.

### Archivio
- Deve apparire come area secondaria ma utile.
- Azioni di ripristino chiare.

## Principi tipografici
- Usare una scala tipografica semplice e coerente.
- Differenziare bene titolo schermata, heading di sezione, testo corpo, metadati.
- Non usare troppi stili diversi.
- Priorità alla leggibilità rispetto all'impatto estetico.

## Principi sui componenti
- Card semplici e coerenti.
- Badge di stato chiari e ripetibili.
- Pulsante primario unico per schermata quando possibile.
- FAB solo se davvero utile e coerente con il flusso.
- Item lista riusabili per inventario e scadenzario, con varianti minime.
- Feedback di salvataggio e conferma discreti ma chiari.


### Regole su modifica ed eliminazione delle entità
Per ogni entità modificabile, l'azione di modifica deve essere rappresentata con la classica icona a matita.

Per ogni entità eliminabile, l'azione di cancellazione deve essere rappresentata con la classica icona a cestino.

La cancellazione definitiva richiede sempre una conferma esplicita da parte dell'utente.

Quando esiste un'alternativa meno distruttiva, ad esempio archiviazione, l'interfaccia deve privilegiare l'azione reversibile o meno rischiosa rispetto alla cancellazione definitiva.

La cancellazione non deve mai essere eseguita con un singolo tap accidentale.

## Principi di motion
- Animazioni leggere e funzionali.
- Nessuna motion decorativa inutile.
- Transizioni coerenti con la gerarchia di navigazione.
- L'animazione deve aiutare a capire il passaggio di stato o di contesto.

## Principi di feedback
- Ogni azione importante deve produrre un feedback chiaro.
- Validazioni immediate quando opportuno.
- Conferma per azioni potenzialmente distruttive.
- Errori espressi in linguaggio semplice.
- Empty state sempre utili, mai vuoti passivi.

## Principi di accessibilità
- Contrasto sufficiente.
- Testi leggibili e dimensioni coerenti.
- Touch target adeguati.
- Colore mai unica fonte di informazione.
- Stati e priorità devono essere comunicati anche con testo, icone o struttura.

## Regole visive sullo stato del medicinale
Lo stato deve essere sempre comprensibile a colpo d'occhio.

Stati minimi da distinguere:
- valido;
- in scadenza;
- scaduto;
- esaurito;
- senza data.

La rappresentazione dello stato deve restare coerente in Home, Inventario, Dettaglio e Scadenzario.

## Regole per azioni di modifica e cancellazione
Per ogni entità modificabile, l'azione di modifica deve essere rappresentata con la classica icona a matita.

Regole:
- l'icona a matita apre la modalità di edit o la schermata di modifica dell'entità;
- l'icona deve essere visibile dove l'utente si aspetta di poter modificare il contenuto, ad esempio nel dettaglio;
- il significato dell'azione deve essere accessibile anche tramite content description.

Per ogni entità cancellabile, l'azione di cancellazione deve essere rappresentata con la classica icona a cestino.

Regole:
- l'icona a cestino non deve mai eliminare immediatamente senza conferma;
- la cancellazione richiede sempre una conferma esplicita dell'utente;
- il dialog di conferma deve spiegare in modo semplice cosa verrà eliminato;
- quando l'archiviazione è disponibile, proporla come alternativa più sicura alla cancellazione definitiva;
- l'azione distruttiva deve essere riconoscibile ma non predominante rispetto alle azioni ordinarie.

## Relazione con il visual design brief
`UI_GUIDELINES.md` definisce principi UX/UI generali e regole di comportamento.

Per direzione visuale concreta, palette indicativa, stile delle card, badge, microcopy, iconografia e output visuali da produrre, consultare anche `VISUAL_DESIGN_BRIEF.md`.

## Regole specifiche per Figma
In Figma conviene costruire almeno:
- una navigation map;
- wireframe low fidelity;
- componenti base riusabili;
- varianti di item medicinale;
- badge di stato;
- empty state principali.

## Criterio guida finale
Quando ci sono più alternative grafiche, scegliere quella che:
- riduce il carico cognitivo;
- rende più rapida l'azione frequente;
- chiarisce meglio lo stato dei medicinali;
- preserva un tono sobrio e affidabile.


## Regole su risorse grafiche e Preview Compose
- Ogni Composable di schermata deve avere almeno una `@Preview` significativa.
- Ogni componente riusabile del design system deve avere almeno una `@Preview`.
- Le Preview devono usare dati fake locali e non devono dipendere da ViewModel, Hilt, Room o servizi reali.
- Stringhe visibili all'utente, descrizioni di accessibilità, messaggi di errore e testi dei dialog devono stare in `strings.xml`.
- Dimensioni, spaziature, radius e misure ricorrenti devono essere centralizzate in `Dimens.kt` o in token equivalenti del design system.
- Colori e palette devono essere centralizzati in `colors.xml` e nel tema Compose, non hardcoded nelle schermate.
- Shape e tipografia devono essere centralizzate nei file del tema.
