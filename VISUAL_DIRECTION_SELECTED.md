# MediShelf – Visual Direction Selected

## Scelta visuale

La direzione visuale scelta per MediShelf è la **Proposta 2**.

Questa proposta viene assunta come riferimento principale per:
- schermate dell'app;
- icona di lancio;
- logo;
- componenti UI;
- palette;
- tono visuale generale.

## Sintesi

La Proposta 2 comunica:
- cura domestica;
- ordine;
- semplicità;
- serenità;
- protezione;
- gestione familiare e quotidiana;
- distanza da un'estetica ospedaliera o eccessivamente clinica.

Il concetto principale è:

```text
Casa + armadietto + medicinali = cura domestica organizzata
```

## Identità visiva

MediShelf deve sembrare:
- domestica;
- ordinata;
- affidabile;
- calda ma non infantile;
- moderna ma non fredda;
- semplice ma non banale.

MediShelf non deve sembrare:
- un'app ospedaliera;
- un'app di emergenza sanitaria;
- un'app farmaceutica commerciale;
- un'app giocosa;
- un'app ansiogena;
- un gestionale freddo.

## Logo

La direzione scelta combina:
- casa stilizzata;
- piccolo armadietto/scaffale;
- elementi minimi che richiamano medicinali o confezioni;
- foglia o elemento naturale come dettaglio secondario.

Logo orizzontale consigliato:

```text
[icona casa/armadietto] MediShelf
```

La casa comunica il contesto domestico.
L'armadietto comunica l'organizzazione.
La foglia comunica cura, benessere e tono rassicurante.

## Icona di lancio

L'icona di lancio deve essere una versione molto semplificata del logo:
- casa stilizzata;
- scaffale/armadietto interno;
- uno o due elementi medicinali;
- eventuale foglia laterale;
- palette verde/teal morbida.

Requisiti:
- leggibile a dimensioni piccole;
- senza testo;
- pochi dettagli;
- croce medica solo come dettaglio minimo;
- niente rosso dominante;
- compatibile con adaptive icon Android.

## Palette indicativa

```text
Primary Teal:        #0F6B68
Deep Teal:           #0B4F4D
Sage Green:          #A8D5C7
Warm Background:     #F7F4EE
Surface:             #FFFFFF
Surface Soft:        #F2F4F0
Warning Amber:       #F5C84C
Warning Soft:        #FFF3D2
Expired Red:         #E85757
Expired Soft:        #FFE1E1
Out Of Stock Purple: #8E7CC3
No Date Grey:        #9AA3AE
Text Primary:        #102A2A
Text Secondary:      #5F6F6F
```

I valori definitivi vanno centralizzati nelle risorse del progetto, senza colori hardcoded nelle Composable.

## Stile UI

Le card devono essere:
- morbide;
- arrotondate;
- con bordo leggero o ombra discreta;
- chiare;
- ordinate;
- facilmente leggibili.

I badge devono essere:
- piccoli;
- leggibili;
- coerenti;
- accompagnati da testo;
- non aggressivi.

Stati minimi:
- `Valido`
- `In scadenza`
- `Scaduto`
- `Esaurito`
- `Senza data`

## Iconografia

Icone standard:
- Home: casa;
- Inventario: scaffale/lista/box;
- Aggiungi: plus;
- Scadenzario: calendario;
- Notifiche: campanella;
- Cerca: lente;
- Modifica: matita;
- Elimina: cestino;
- Archivia: scatola/archivio;
- Impostazioni: ingranaggio.

## Regole implementative

- Nessuna stringa UI hardcoded.
- Stringhe in `strings.xml`.
- Dimensioni ricorrenti in `Dimens.kt`.
- Colori nel tema Compose o in risorse centralizzate.
- Ogni Composable deve avere almeno una Preview.
- La UI riceve `UiState` e callback.
- Nessuna logica business complessa nelle Composable.

## Prompt per design tool

```text
Crea un design mobile Android per l'app MediShelf.

Direzione visuale:
- app domestica, rassicurante, ordinata;
- stile casa + armadietto dei medicinali;
- palette verde teal, salvia, sfondi caldi;
- non deve sembrare ospedaliera, clinica o ansiogena;
- UI moderna, pulita, con card morbide e badge stato leggibili.

Schermate da creare:
1. Home / Dashboard
2. Inventario
3. Dettaglio medicinale
4. Inserimento / Modifica medicinale
5. Scadenzario
6. Impostazioni

Vincoli:
- quantità e scadenza sono opzionali;
- modifica tramite icona matita;
- eliminazione tramite icona cestino con conferma;
- tono semplice e non medico-clinico.
```

## Decisione finale

La Proposta 2 è scelta come riferimento visuale principale per MediShelf.

Le proposte alternative possono restare come ispirazione secondaria, ma non devono guidare il design della prima versione.
