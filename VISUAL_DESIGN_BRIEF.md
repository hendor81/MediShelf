# VISUAL_DESIGN_BRIEF.md

## Scopo
Questo documento definisce la direzione visuale di **MediShelf**.

Non sostituisce `UI_GUIDELINES.md`, `SCREEN_CATALOG.md` o `NAVIGATION_MAP.md`: li completa con indicazioni più concrete su identità visiva, atmosfera, palette, componenti e layout.

Il documento è pensato per guidare:
- designer umani;
- strumenti di design come Figma;
- agenti IA che generano wireframe, mockup o componenti Compose;
- sviluppatori che implementano il design system.

## Obiettivo visuale
MediShelf deve apparire come un'app:
- ordinata;
- pulita;
- rassicurante;
- domestica;
- moderna;
- affidabile;
- semplice da usare.

L'app deve comunicare controllo e chiarezza, non ansia.

## Cosa l'app non deve sembrare
MediShelf non deve sembrare:
- un software clinico ospedaliero;
- un'app di diagnosi medica;
- una cartella sanitaria;
- un'app farmaceutica commerciale;
- un'app giocosa o infantile;
- un'app di emergenza.

Il tono visuale deve restare vicino all'organizzazione domestica, non alla medicina clinica.

## Personalità visiva
La personalità visiva può essere sintetizzata così:

> Un piccolo assistente domestico, ordinato e affidabile, che aiuta l'utente a sapere cosa ha in casa e cosa richiede attenzione.

Parole chiave:
- calm;
- clear;
- organized;
- practical;
- warm;
- trustworthy;
- minimal.

## Direzione cromatica
La palette deve essere chiara e rassicurante.

### Colore primario
Il colore primario dovrebbe appartenere a una famiglia calma e non aggressiva.

Opzioni consigliate:
- verde salvia;
- teal morbido;
- blu petrolio chiaro;
- azzurro-grigio;
- verde acqua sobrio.

Evitare:
- rosso come colore primario;
- colori troppo saturi;
- colori neon;
- palette troppo medicali bianco/blu ospedale.

### Background
Preferire:
- background chiari;
- superfici leggermente differenziate;
- grigi caldi o neutri;
- bianco non eccessivamente freddo.

### Colori di stato
Gli stati devono essere distinguibili a colpo d'occhio, ma senza creare allarme eccessivo.

Stati minimi:
- `VALID`: colore positivo ma sobrio, ad esempio verde morbido;
- `EXPIRING`: colore di attenzione, ad esempio ambra/giallo caldo;
- `EXPIRED`: colore critico, ad esempio rosso controllato o terracotta;
- `OUT_OF_STOCK`: colore neutro/secondario, ad esempio grigio o blu-grigio;
- `NO_EXPIRATION_DATE`: colore informativo/neutro, ad esempio grigio chiaro o azzurro tenue.

Il colore non deve mai essere l'unico mezzo per comunicare lo stato: usare sempre anche testo, icona o badge.

## Tipografia
La tipografia deve privilegiare leggibilità e scansione rapida.

Indicazioni:
- titoli schermata chiari e non troppo grandi;
- nomi dei medicinali ben leggibili;
- metadati più piccoli ma non sacrificati;
- evitare troppe varianti tipografiche;
- usare la scala Material 3 come base.

Gerarchia consigliata:
- titolo schermata;
- heading di sezione;
- nome medicinale;
- stato/scadenza;
- metadati secondari;
- note o descrizioni.

## Stile generale dei componenti
### Card
Le card devono essere:
- morbide;
- pulite;
- leggibili;
- con angoli arrotondati;
- con ombre leggere o differenza di superficie Material 3;
- mai troppo dense.

Le card non devono sembrare blocchi pesanti o pannelli tecnici.

### Badge di stato
I badge devono essere:
- coerenti in tutta l'app;
- leggibili;
- abbastanza compatti;
- associati a testo esplicito;
- eventualmente accompagnati da icona.

Esempi di testo:
- `Valid`;
- `Expires soon`;
- `Expired`;
- `Low stock`;
- `No expiry date`.

Nella versione italiana dell'app:
- `Valido`;
- `In scadenza`;
- `Scaduto`;
- `Scorta bassa`;
- `Senza scadenza`.

### Pulsanti
Usare un solo pulsante primario per schermata quando possibile.

Esempi:
- `Add medicine` nella Home o Inventario;
- `Save` nel form;
- `Archive` o `Restore` dove appropriato.

Le azioni distruttive devono essere visibili ma non dominanti.

### Icone
Iconografia consigliata:
- matita per modifica;
- cestino per cancellazione;
- calendario per scadenza;
- lente per ricerca;
- campanella per notifiche;
- scatola, scaffale o armadietto per inventario;
- archivio per elementi archiviati;
- più/aggiungi per inserimento;
- meno/più per aggiornamento quantità.

Le icone devono essere accompagnate da content description e non devono essere l'unico segnale dell'azione.

## Home / Dashboard
La Home è la schermata più identitaria dell'app.

Deve rispondere subito alla domanda:

> Com'è messo il mio armadietto oggi?

Struttura consigliata:
- saluto o titolo semplice;
- riepilogo sintetico dell'armadietto;
- card principali per totale medicinali, in scadenza, scaduti, scorta bassa;
- CTA evidente per aggiungere un medicinale;
- sezione `Needs attention` o equivalente per ciò che richiede azione.

La Home non deve mostrare troppi dettagli. Deve invitare ad agire solo dove serve.

## Inventario
La lista inventario deve essere molto leggibile.

Ogni item dovrebbe mostrare:
- nome medicinale;
- stato;
- scadenza se presente;
- luogo di conservazione se presente;
- quantità se presente;
- eventuale indicatore di scorta bassa.

La ricerca deve essere immediata e visibile.

Filtri e ordinamenti devono essere utili ma non invadenti.

## Dettaglio medicinale
Il dettaglio deve dare priorità a:
- nome;
- stato;
- scadenza;
- quantità;
- luogo;
- note.

Azioni consigliate:
- modifica con icona matita;
- archivia;
- elimina con icona cestino e conferma;
- incrementa/decrementa quantità se la quantità è presente.

Se la quantità non è stata indicata, la UI deve evitare messaggi colpevolizzanti. Può mostrare un suggerimento discreto come:

> Quantity not set

oppure:

> You can add it later

## Inserimento / Modifica medicinale
Il form deve restare corto.

Campo obbligatorio minimo:
- nome medicinale.

Campi opzionali:
- formato/confezione;
- quantità;
- unità;
- soglia scorta bassa;
- scadenza;
- luogo;
- note.

La UI deve comunicare chiaramente che quantità e scadenza possono essere aggiunte anche in seguito.

Evitare un form troppo clinico o burocratico.

## Scadenzario
Lo Scadenzario deve separare chiaramente:
- in scadenza;
- scaduti;
- senza data.

L'ordinamento deve essere cronologico.

Gli elementi scaduti devono essere evidenti, ma il tono deve restare pratico, non allarmistico.

Esempi di microcopy:
- `These medicines are expired`;
- `Check and remove them from your cabinet`;
- `No medicines expiring soon`.

## Archivio
L'Archivio è una sezione secondaria.

Deve comunicare che gli elementi non sono più nell'inventario attivo, ma possono essere consultati o ripristinati.

L'azione principale è il ripristino, non la cancellazione definitiva.

## Impostazioni
Le impostazioni devono essere semplici e leggibili.

Nella versione Free evitare molte sezioni.

Se presente una sezione Pro/Subscription, deve essere discreta e non aggressiva.

## Empty state
Gli empty state devono essere utili e orientati all'azione.

Esempi:
- Inventario vuoto: spiegare che si può iniziare aggiungendo il primo medicinale;
- Scadenzario vuoto: rassicurare che non ci sono scadenze da gestire;
- Archivio vuoto: spiegare che i medicinali archiviati appariranno lì.

Gli empty state non devono essere pagine vuote passive.

## Motion
Le animazioni devono essere leggere e funzionali.

Usare motion per:
- transizioni tra schermate;
- apparizione di dialog;
- feedback di salvataggio;
- cambiamenti di stato.

Evitare motion decorativa o giocosa.

## Tono testuale e microcopy
Il linguaggio deve essere:
- semplice;
- pratico;
- rassicurante;
- non clinico;
- non ansiogeno.

Evitare frasi come:
- `Danger`;
- `Critical medicine alert`;
- `Medical warning`.

Preferire frasi come:
- `Expires soon`;
- `Expired`;
- `Check this item`;
- `No expiry date set`;
- `You can complete this later`.

## Risorse centralizzate
Il design visuale deve essere implementabile tramite risorse centralizzate.

Regole:
- nessuna stringa visibile hardcoded nelle schermate;
- nessuna dimensione hardcoded ricorrente nei Composable;
- nessun colore hardcoded nei Composable;
- usare `strings.xml` per testi e content description;
- usare `Dimens.kt` o token equivalenti per spaziature, radius e dimensioni;
- usare `colors.xml` e tema Compose per palette e colori;
- usare file del tema per typography e shapes.

## Preview Compose
Ogni schermata o componente visuale deve essere accompagnato da Preview Compose.

Regole:
- ogni Composable di schermata deve avere almeno una Preview;
- ogni componente riusabile deve avere almeno una Preview;
- le Preview devono usare dati fake locali;
- le Preview non devono dipendere da ViewModel, Hilt, Room o servizi reali;
- dove utile, prevedere Preview per stati diversi: vuoto, pieno, errore, caricamento, stato critico.

## Output visuali consigliati
Prima dell'implementazione completa, produrre almeno:
- moodboard o direzione visuale sintetica;
- palette preliminare;
- componenti base;
- badge di stato;
- item lista medicinale;
- wireframe Home;
- wireframe Inventario;
- wireframe Dettaglio;
- wireframe Inserimento/Modifica;
- wireframe Scadenzario.

## Criterio decisionale
Quando esistono più alternative visuali, scegliere quella che:
- rende più chiara la situazione dell'armadietto;
- riduce il carico cognitivo;
- rende più facile aggiungere o aggiornare un medicinale;
- evita toni clinici o ansiogeni;
- rispetta la semplicità della versione Free;
- può essere implementata con componenti Compose riusabili.
