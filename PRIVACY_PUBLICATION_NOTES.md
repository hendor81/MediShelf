# Pubblicazione dell'informativa privacy di MediShelf

Preparata il 27 ottobre 2026 per la versione Free `1.0.0-alpha02` (codice 3).

## Contenuto da pubblicare

Il file [privacy-policy.html](privacy-policy.html) contiene italiano e inglese,
con la stessa impostazione bilingue del
[riferimento Tabletop Toolkit](https://sites.google.com/view/tabletoptoolkit-privacypolicy).
Usa HendorSoftware e GitHub Issues, come richiesto dal titolare del progetto;
il collegamento punta al repository MediShelf. Le issue sono pubbliche: il testo
invita a non includere dati personali o sanitari e a chiedere un canale privato
prima di comunicare informazioni riservate. Nessun recapito privato è stato
inventato; un eventuale canale privato dovrà essere effettivamente disponibile
quando richiesto.

In Google Sites si può copiare il contenuto visualizzato in una normale pagina
di testo oppure usare **Inserisci → Incorpora → Incorpora codice** incollando
l'HTML completo, come nel riferimento. Verificare che entrambe le lingue siano
leggibili da smartphone e che l'altezza dell'incorporamento non tagli il testo.
Il file non richiede script, font remoti, immagini o strumenti di tracciamento.

La pagina pubblicata deve essere accessibile senza autenticazione. Inserire il
suo URL in Play Console e renderla accessibile nelle Impostazioni dell'app:
attualmente «Privacy e Disclaimer» apre solo un breve testo locale, non questa
informativa completa. Questa attività aggiunge i documenti, senza cambiare l'app
o pubblicare pagine esterne.

## Riscontri sul codice

- `app/src/main/AndroidManifest.xml`: permessi `POST_NOTIFICATIONS` e
  `RECEIVE_BOOT_COMPLETED`; nessun permesso `INTERNET` nel manifest sorgente.
- `app/build.gradle.kts`: dipendenze locali/UI, senza SDK pubblicitari,
  analytics o crash reporting esterno dichiarati.
- `MedicineEntity.kt` e documentazione tecnica: inventario con note, posizione,
  quantità, scadenze, soglie, identificativi e date locali; preferenze in
  SharedPreferences, incluse introduzione e storico richiesta notifiche.
- `ExpiryNotificationScheduler.kt`: elaborazione locale; notifica con soli
  conteggi, senza nomi dei medicinali né note.
- `android:allowBackup="true"`, `backup_rules.xml` e
  `data_extraction_rules.xml`: backup di sistema consentito, senza esclusioni
  esplicite per database e preferenze. Non sarebbe corretto promettere che
  nessuna copia possa mai lasciare il dispositivo.
- Archivio e cancellazione sono operazioni distinte; sono disponibili
  eliminazione delle voci e svuotamento dell'Archivio con conferma.
- Nessuna cifratura aggiuntiva del database o blocco interno dell'app individuati.

Questi riscontri si riferiscono ai sorgenti correnti: prima della pubblicazione
verificare anche il manifest unificato e le dipendenze dell'artifact finale.
Se cambiano SDK, backup, permessi o flussi dei dati, aggiornare l'informativa e
le dichiarazioni Play Console. La verifica del comportamento effettivo di
backup/ripristino va effettuata sui dispositivi supportati.

## Fonti consultate

- [Norme Google Play sui dati utente e sulla privacy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=it):
  contenuti richiesti, contatti, conservazione/cancellazione e accessibilità
  della pagina e dell'informativa nell'app.
- [Android Auto Backup](https://developer.android.com/identity/data/autobackup):
  backup di database e preferenze e regole di inclusione/esclusione.
- [Norme sulla privacy di Google](https://policies.google.com/privacy?hl=it):
  trattamenti dei servizi di piattaforma e del sito ospitato su Google Sites.

L'informativa distingue il trattamento locale dall'eventuale contatto volontario
con lo sviluppatore e dai servizi della piattaforma. La pubblicazione su Google
Sites comporta i trattamenti propri del sito ospitante: non va presentata come
una pagina web che non usa in assoluto cookie o servizi di terzi.
