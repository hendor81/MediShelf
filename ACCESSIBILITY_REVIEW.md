# Accessibilità di base — issue #32

Verifica statica dei flussi Free e dei token dei temi chiaro/scuro.
Non sostituisce una prova con TalkBack su dispositivo o un audit completo.

## Controlli e informazioni

- Modifica, elimina, archivio, indietro, incrementa/decrementa quantità e cancella
  ricerca hanno descrizioni accessibili in `strings.xml`. Le icone decorative
  accanto a testo equivalente restano escluse dagli annunci.
- I badge comunicano ogni stato con testo, oltre a colore e icona.
- Le azioni delle barre usano `IconButton` Material con target minimo di 48 dp.
  “Vedi tutti” e la ricerca Home hanno dimensioni minime esplicite; le righe radio
  hanno altezza minima di 48 dp.
- Notifiche: l'intera riga è un singolo switch con etichetta e stato. Tema e azione
  predefinita usano selezione radio; il controllo visuale non crea un secondo focus.
- I riepiloghi Home raggruppano conteggio ed etichetta in un singolo annuncio.

## Leggibilità

- Home: due colonne sotto 360 dp di spazio disponibile o da font scale 1,3;
  le card crescono in altezza, senza ridurre automaticamente il testo.
- I nomi dei medicinali possono andare a capo; a font grandi e su schermi compatti
  i badge della Home passano sotto il nome.
- Titoli, descrizioni e valori delle righe Impostazioni non sono troncati; il
  valore segue la descrizione. Il selettore tema cresce e può scorrere.
- Preview a font scale 2 e test Compose verificano i principali adattamenti.

## Contrasto

`PaletteContrastTest` verifica un rapporto minimo di 4,5:1 per testo normale su
sfondo/superficie, testo secondario, pulsanti primari/secondari, errori e tutte
le coppie testo/sfondo dei badge, in entrambi i temi. La verifica riguarda colori
opachi; non misura le illustrazioni, i controlli disabilitati o ogni composizione
con trasparenze.

Il secondario chiaro con testo bianco è stato corretto da `#6D7F55` (4,36:1) a
`#697B51` (oltre 4,5:1), mantenendo la direzione cromatica originale.

## Verifiche da completare su dispositivo

- Percorrere aggiunta, ricerca, dettaglio, quantità, archivio e impostazioni con
  TalkBack: ordine del focus, annunci, selezione e feedback.
- Ripetere a font scale 1,3 / 1,5 / 2 e larghezza 320 dp, verificando barre,
  dialog, calendario, tastiera e raggiungibilità delle azioni con scorrimento.
- Eseguire i test strumentali `AccessibilityTest`: nell'ambiente di sviluppo
  non sono disponibili un emulatore o un dispositivo Android.
