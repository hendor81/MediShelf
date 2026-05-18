# MediShelf – Home Screen Visual Spec

## Scopo

Definire una specifica operativa della Home / Dashboard di MediShelf, basata sulla direzione visuale selezionata: Proposta 2.

La Home è la schermata di ingresso dell'app e deve rispondere rapidamente alla domanda:

```text
Com'è messo il mio armadietto dei medicinali?
```

## Obiettivo UX

La Home deve permettere all'utente di capire in pochi secondi:
- quanti medicinali sono presenti;
- se ci sono medicinali in scadenza;
- se ci sono medicinali scaduti;
- se ci sono medicinali senza data;
- quali elementi richiedono attenzione;
- come aggiungere rapidamente un nuovo medicinale.

## Layout consigliato

```text
┌─────────────────────────────┐
│ MediShelf              🔔   │
│ Ciao! Ecco il riepilogo...  │
│                             │
│ ┌─────────┐ ┌─────────┐     │
│ │ Validi  │ │ In scad.│     │
│ │ 12      │ │ 3       │     │
│ └─────────┘ └─────────┘     │
│ ┌─────────┐ ┌─────────┐     │
│ │ Scaduti │ │ Senza d.│     │
│ │ 1       │ │ 2       │     │
│ └─────────┘ └─────────┘     │
│                             │
│ Da tenere d'occhio  Vedi    │
│ ┌─────────────────────────┐ │
│ │ Ibuprofene       badge  │ │
│ │ Scade il ...            │ │
│ └─────────────────────────┘ │
│                             │
│ [ + Aggiungi medicinale ]   │
│                             │
│ Home Inventario Scadenze... │
└─────────────────────────────┘
```

## Componenti

### Header

Contenuti:
- nome app `MediShelf`;
- eventuale icona notifiche;
- breve frase introduttiva.

Testo suggerito:

```text
Ciao! Ecco il riepilogo del tuo armadietto.
```

Alternative:

```text
Il tuo armadietto, sotto controllo.
```

```text
Ecco cosa richiede attenzione oggi.
```

### Summary cards

Card previste nella prima release:
1. Validi
2. In scadenza
3. Scaduti
4. Senza data

`Esauriti` può comparire nella sezione attenzione o in una fase successiva, perché dipende dalla quantità opzionale.

### Sezione "Da tenere d'occhio"

Mostra massimo 2 o 3 elementi prioritari.

Priorità suggerita:
1. scaduti;
2. in scadenza;
3. senza data;
4. scorta bassa, se quantità presente.

### CTA principale

Bottone:

```text
+ Aggiungi medicinale
```

Deve essere ben visibile e usare il colore primario.

### Bottom navigation

Voci:
- Home
- Inventario
- Scadenze
- Impostazioni

## HomeUiState suggerito

```kotlin
data class HomeUiState(
    val isLoading: Boolean = false,
    val validCount: Int = 0,
    val expiringCount: Int = 0,
    val expiredCount: Int = 0,
    val noExpirationDateCount: Int = 0,
    val attentionItems: List<HomeAttentionItemUiState> = emptyList()
)

data class HomeAttentionItemUiState(
    val medicineId: String,
    val name: String,
    val subtitle: String,
    val status: MedicineStatus,
    val daysInfo: String?
)
```

## Eventi UI suggeriti

```kotlin
fun onAddMedicineClick()
fun onInventoryClick()
fun onExpiryClick()
fun onSettingsClick()
fun onAttentionItemClick(medicineId: String)
fun onNotificationsClick()
```

## Preview richieste

Ogni Composable deve avere Preview.

Preview minime consigliate:
- Home con dati normali;
- Home vuota;
- Home con molti elementi critici;
- Home loading;
- Home con font scale elevato, se possibile.

## Risorse

### Stringhe

Tutte le stringhe devono essere in `strings.xml`.

Esempi:

```xml
<string name="app_name">MediShelf</string>
<string name="home_greeting">Ciao! Ecco il riepilogo del tuo armadietto.</string>
<string name="home_valid_medicines">Validi</string>
<string name="home_expiring_medicines">In scadenza</string>
<string name="home_expired_medicines">Scaduti</string>
<string name="home_no_expiration_date">Senza data</string>
<string name="home_attention_title">Da tenere d'occhio</string>
<string name="home_see_all">Vedi tutto</string>
<string name="home_add_medicine">Aggiungi medicinale</string>
```

### Dimensioni

Dimensioni ricorrenti in `Dimens.kt`.

Esempi:

```kotlin
object Dimens {
    val screenPadding = 16.dp
    val cardCornerRadius = 20.dp
    val cardPadding = 16.dp
    val itemSpacing = 12.dp
    val summaryCardMinHeight = 104.dp
}
```

### Colori

Colori centralizzati nel tema Compose.
Nessun colore hardcoded dentro le Composable.

## Criteri di accettazione

La Home è accettabile quando:
- comunica chiaramente lo stato dell'armadietto;
- non appare clinica o ospedaliera;
- mostra massimo quattro indicatori principali;
- evidenzia gli elementi da controllare;
- ha CTA evidente per aggiungere medicinale;
- rispetta il tono rassicurante;
- funziona con quantità opzionale;
- funziona con scadenza opzionale;
- usa risorse centralizzate;
- ogni Composable ha Preview;
- non contiene logica di business.
