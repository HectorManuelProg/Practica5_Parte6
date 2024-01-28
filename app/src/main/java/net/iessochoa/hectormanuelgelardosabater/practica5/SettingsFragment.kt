package net.iessochoa.hectormanuelgelardosabater.practica5

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //buscamos la preferencia
        val buildVersion: Preference? = findPreference("buildVersion")
        //definimos la accion para la preferencia
        buildVersion?.setOnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://portal.edu.gva.es/03013224/"))
            )
         //hay que devolver booleano para indicar si se acepta el cambio o no
            false
        }
    }

}
