package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import it.hendorsoftware.medishelf.R
import java.util.Locale

/** Associa i formati selezionabili alle risorse grafiche, mantenendo compatibili i dati liberi esistenti. */
object MedicinePackageForms {
    /** Traduce i formati noti solo in presentazione; conserva il testo libero e i valori persistiti. */
    @Composable
    fun labelFor(value: String?): String? {
        val values = stringArrayResource(R.array.medicine_package_forms)
        val labels = stringArrayResource(R.array.medicine_package_form_labels)
        // I valori italiani preesistenti restano le chiavi stabili, indipendenti dalla lingua.
        val index = values.indexOfFirst { it.equals(value?.trim(), ignoreCase = true) }
        val label = labels.getOrNull(index)
        return if (label != null && label != values.getOrNull(index)) label else value
    }

    /** Icona del formato salvato; per valori assenti o sconosciuti usa la compressa. */
    @DrawableRes
    fun iconFor(value: String?): Int = when (value?.trim()?.lowercase(Locale.ROOT)) {
        "compresse" -> R.drawable.compressa
        "vitamine" -> R.drawable.vitamine
        "supposte" -> R.drawable.supposta
        "pomata" -> R.drawable.pomata
        "sciroppo" -> R.drawable.sciroppo
        "collirio" -> R.drawable.collirio
        "bustina" -> R.drawable.bustina
        "spray" -> R.drawable.spray
        "spray orale" -> R.drawable.spray_orale
        "gocce auricolari" -> R.drawable.gocce_auricolari
        "flacone contagocce" -> R.drawable.flacone_contagocce
        "cerotti" -> R.drawable.cerotti
        "test" -> R.drawable.test
        "bottiglia" -> R.drawable.bottiglia
        "siringhe" -> R.drawable.siringhe
        "bendaggi" -> R.drawable.bendaggi
        "fiale" -> R.drawable.fiala
        "flacone con dosatore" -> R.drawable.flacone_dosatore
        else -> R.drawable.compressa
    }
}
