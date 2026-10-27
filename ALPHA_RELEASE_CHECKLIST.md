# Alpha per installazione diretta — issue #34

## Identità e versione

- Nome applicazione: MediShelf.
- Application ID e namespace: `it.hendorsoftware.medishelf`.
- Prima alpha firmata: `versionName = 1.0.0-alpha01`, `versionCode = 2`.
- Alpha corrente: `versionName = 1.0.0-alpha03`, `versionCode = 4`, con
  interfaccia italiano/inglese. Il precedente aggiornamento alpha01 → alpha02
  è stato verificato senza disinstallazione; tale verifica non certifica
  automaticamente l'aggiornamento alla nuova alpha03.
- Min SDK 24, target SDK 36, compile SDK 36.1.
- Aumentare `versionCode` a ogni nuova alpha distribuita e a ogni successiva
  release Play; il nome versione da solo non determina l'ordine di aggiornamento.
- La variante release non è debuggable. La minificazione resta disattivata.

## Firma locale

1. Creare e conservare una chiave di firma stabile fuori dal repository.
2. Copiare `keystore.properties.example` in `keystore.properties` nella radice.
3. Compilare percorso del keystore, alias e password solo nel file locale.
   Nei percorsi Windows usare `/`. Nei valori Properties un carattere `\`
   letterale richiede `\\`; non aggiungere virgolette intorno alle password.
4. Conservare un backup protetto del keystore e le password nel proprio password
   manager. Non pubblicare il file credenziali o la chiave come artifact.
5. Generare l'APK firmato dalla radice del progetto:

   ```powershell
   .\gradlew.bat :app:assembleRelease -PsignRelease=true
   ```

   Il file è `app/build/outputs/apk/release/app-release.apk`.
   Il parametro `signRelease=true` verifica la presenza dei campi di firma;
   se mancano, la build fallisce invece di usare una chiave debug di ripiego.

6. Per preparare un AAB firmato, quando sarà necessario per Play Console:

   ```powershell
   .\gradlew.bat :app:bundleRelease -PsignRelease=true
   ```

   Il file è `app/build/outputs/bundle/release/app-release.aab`.
   L'AAB non si installa direttamente sul telefono.

Le build ordinarie senza `-PsignRelease=true` non leggono credenziali di firma.
L'APK debug continua a usare la chiave debug; un APK release non firmato non è
distribuibile per installazione diretta. Non confondere questi artifact con
l'alpha firmata.

## Continuità verso Play Store

Per aggiornare le installazioni alpha dirette senza disinstallazione, mantenere
lo stesso application ID e una firma compatibile. Alla prima configurazione di
Play App Signing fornire la chiave di firma dell'app già usata per gli APK
diretti, seguendo la procedura protetta di Play Console. Una upload key separata
potrà firmare gli AAB caricati; non sostituisce la firma degli APK installati.

Riferimento: [Firma dell'app — Android Developers](https://developer.android.com/studio/publish/app-signing).

Gli APK debug precedenti hanno una firma diversa dalla nuova alpha: non
disinstallare un'installazione con inventario personale per aggirare un errore
di firma. Verificare la prima installazione su un emulatore o dispositivo di
test. La continuità senza perdita dati va verificata tra due APK firmati con
la nuova chiave stabile e codici versione crescenti.

## Permessi dichiarati

| Permesso | Motivazione | Verifica |
| --- | --- | --- |
| `POST_NOTIFICATIONS` | Notifiche locali di scadenza su Android 13+. | Consenso e rifiuto non impediscono la gestione dell'inventario. |
| `RECEIVE_BOOT_COMPLETED` | Job di scadenza persistente dopo il riavvio. | Verificare la pianificazione dopo il riavvio con notifiche abilitate. |

Controllare anche il manifest finale dell'APK, inclusi i permessi aggiunti
dalle dipendenze. Il servizio JobScheduler è non esportato e protetto da
`BIND_JOB_SERVICE`; MainActivity è esportata per l'avvio dal launcher.
Il manifest release include il permesso AndroidX
`it.hendorsoftware.medishelf.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, di livello
`signature`: protegge ricevitori dinamici interni e non richiede consenso runtime.

## Checklist manuale sull'APK firmato

Annotare versione, commit, dispositivo, versione Android e risultato effettivo.
Le caselle restano vuote finché la verifica non è stata eseguita.

- [x] Firma APK valida; application ID, versione e nome corretti, variante non debuggable.
- [x] Aspetto dell'icona nel launcher verificato manualmente.
- [x] Installazione pulita e avvio senza crash su emulatore di test.
- [x] Onboarding: Avanti, cinque passi, Fine e assenza della guida al nuovo avvio.
- [ ] Onboarding: uscita anticipata tramite Salta (non ripetuta manualmente
      sull'APK firmato; coperta dai test automatici onboarding della issue #33).
- [x] Inserimento con solo nome, modifica, quantità assente e quantità zero.
- [x] Inventario: ricerca e accesso al dettaglio.
- [x] Inventario: filtri verificati manualmente.
- [x] Home e Scadenzario: conteggi e stati coerenti con i dati inseriti.
- [x] Archivio: archiviazione, ripristino ed eliminazione confermata/annullata.
- [x] Impostazioni: soglia e tema persistono al riavvio.
- [x] Impostazioni: preferenza di rimozione persiste al riavvio.
- [x] Notifiche: permesso concesso/negato e collegamento alle impostazioni Android.
- [x] Pubblicazione del riepilogo notifiche, tap verso Home e verifica della voce nello Scadenzario.
- [x] Pianificazione notifiche dopo riavvio del dispositivo.
- [x] Funzionamento con Wi-Fi e dati mobili disattivati, inclusi salvataggio e nuovo avvio.
- [x] Font grandi: contenuti e azioni raggiungibili con scroll sull'emulatore.
- [x] Aggiornamento con stessa firma e codice versione superiore: inventario e
      preferenze conservati senza disinstallazione.

## Stato delle verifiche

Verifica tecnica del 25 ottobre 2026, branch `codex/34-alpha-internal-testing`:

- APK e AAB release generati con la chiave stabile locale tramite
  `:app:assembleRelease :app:bundleRelease -PsignRelease=true --offline`.
- APK verificato con `apksigner`: firma v2 valida, compatibile con il minimo
  SDK 24; AAB verificato con `jarsigner`. L'AAB non è ancora validato da Play Console.
- Certificato di firma SHA-256 pubblico:
  `3becc5a3f67639ac1461ff67ee8764b4d23c0ac92dc3ee5c051c4194d69bf8ef`.
- Installazione pulita e avvio di MainActivity riusciti su emulatore Android
  16/API 36.1 isolato. Package installato con codice 2, nome versione alpha01
  e senza flag `DEBUGGABLE`. Nessuna installazione effettuata sul telefono.
- Test JVM e `lintRelease` completati con successo; le prove funzionali manuali
  e l'aggiornamento fra due versioni firmate restano da eseguire.
- Copie per distribuzione locale in `app/build/distributions/`:
  `MediShelf-1.0.0-alpha01-vc2.apk` e `MediShelf-1.0.0-alpha01-vc2.aab`.
  Non contengono la chiave privata o le password.

### Prove manuali confermate dall'utente il 26 ottobre 2026

Sull'emulatore dell'utente (Android 16/API 36.1 confermato via ADB dopo
l'aggiornamento ad alpha02), inizialmente alpha01:

- Passi 5–6: guida, salvataggio minimo, modifica, scorta bassa, quantità zero
  senza valori negativi e ricerca.
- Passo 7: soglia 30 giorni, scadenza domani/ieri/assente, sezioni e conteggi.
- Passi 8–9: archivio, ripristino, eliminazione annullata e confermata.
- Passi 10–11: persistenza dopo chiusura e nuovo avvio e funzionamento offline.

- Passo 12: aggiornamento alpha01/codice 2 → alpha02/codice 3 senza
  disinstallazione; conservati Test persistenza senza quantità né scadenza,
  Test offline con quantità 2 e senza scadenza, tema Scuro e soglia 60 giorni.
  L'onboarding non ricompare. Versione alpha02 verificata dall'utente nelle
  informazioni dell'app e tramite ADB su Android 16/API 36.1.
- Passo 13: blocco notifiche dalle impostazioni Android, recupero tramite il
  percorso dell'app e riabilitazione confermati. Test offline ha ora scadenza
  domani e quantità 2 per la verifica della pubblicazione della notifica.
  ADB conferma `POST_NOTIFICATIONS` concesso e job 27 presente in attesa.
- Passo 14: job 27 eseguito manualmente tramite ADB; Android conferma una
  notifica attiva sul canale `medicine_expiry`. L'utente conferma riepilogo,
  tap con apertura della Home e Test offline nella sezione In scadenza.
- Riavvio completo dell'emulatore tramite ADB: job periodico 27 presente con
  flag `PERSISTED` prima del riavvio e nuovamente in stato `waiting` dopo
  `sys.boot_completed=1`, senza avvio dell'app da parte dell'agente.
- Passo 15: filtri In scadenza/Senza data/Scaduti/Tutti coerenti, preferenza
  Elimina conservata al nuovo avvio, conferma annullata senza perdita della
  voce e ripristino della preferenza Archivia.
- Passo 16: nome e icona corretti; caratteri al massimo con Home, Inventario,
  Dettaglio e Salva del form leggibili e raggiungibili tramite scroll. Nessuna
  nuova voce salvata durante questa prova; caratteri ripristinati a misura normale.

Le verifiche richieste dalla issue #34 su configurazione, permessi, generazione,
installazione e aggiornamento dell'alpha sono completate. La pubblicazione su
Play Store e la configurazione di Play App Signing restano attività successive.
La verifica manuale Salta non è stata aggiunta agli esiti positivi: rimane
distinta dalla copertura automatica già presente.

Verifica finale alpha02 del 26 ottobre 2026:
`:app:testDebugUnitTest :app:lintRelease :app:assembleRelease :app:bundleRelease
-PsignRelease=true --offline` completato con successo. 116 test JVM senza
fallimenti, errori o esclusioni; lint release con zero errori e 121 avvisi.
Firma v2, package e versione dell'APK e checksum SHA-256 delle copie APK/AAB
verificati. Gli artifact correnti sono `MediShelf-1.0.0-alpha02-vc3.apk` e
`MediShelf-1.0.0-alpha02-vc3.aab` in `app/build/distributions/`, con relativi
file `.sha256`. Chiavi e credenziali non sono tracciate da Git.

### AAB alpha03 del 27 ottobre 2026

- Versione `1.0.0-alpha03`, codice 4, con supporto italiano/inglese provato
  dall'utente su emulatore.
- `:app:bundleRelease :app:lintRelease -PsignRelease=true --offline` riuscito;
  lint release con 0 errori e 125 warning.
- Firma AAB verificata; certificato SHA-256 identico a quello stabile sopra
  riportato. Identità/versione del manifest release controllate, app non debuggable.
- Copia disponibile: `app/build/distributions/MediShelf-1.0.0-alpha03-vc4.aab`
  con checksum nel corrispondente file `.sha256`.
- Non è stato generato un nuovo APK firmato, né ripetuta la prova di
  aggiornamento tra APK firmati. Nessun caricamento su Play Console effettuato.
