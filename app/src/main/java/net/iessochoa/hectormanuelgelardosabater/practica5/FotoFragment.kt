package net.iessochoa.hectormanuelgelardosabater.practica5

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Insets.add
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.FragmentFotoBinding
import android.Manifest

class FotoFragment : Fragment() {
    private var _binding: FragmentFotoBinding? = null
    private val binding get() = _binding!!
    //Array con los permisos necesarios
    private val PERMISOS_REQUERIDOS =
        mutableListOf (Manifest.permission.CAMERA).apply {
           //si la versión de Android es menor o igual a la 9 pedimos el permiso de escritura
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFotoBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
        }

    }
    private fun allPermissionsGranted() = PERMISOS_REQUERIDOS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_GRANTED
    }
    private fun startCamera() {
        Toast.makeText(requireContext(),
            "Camara iniciada…",
            Toast.LENGTH_SHORT).show()
    }
    //Permite lanzar la solicitud de permisos al sistema operativo y actuar según el usuario
    //los acepte o no
    val solicitudPermisosLauncher = registerForActivityResult(
        //realizamos una solicitud de multiples permisos
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted: Map<String, Boolean> ->
        if (allPermissionsGranted()) {
        //Si tenemos los permisos, iniciamos la cámara
            startCamera()
        } else {
        // Si no tenemos los permisos. Explicamos al usuario
            explicarPermisos()
        }
    }
    fun explicarPermisos() {
        AlertDialog.Builder(requireContext())
            .setTitle(android.R.string.dialog_alert_title)
             //TODO:recuerda: el texto en string.xml
            .setMessage("Son necesarios los permisos para hacer una foto.\nDesea aceptar los permisos?")
            //acción si pulsa si
            .setPositiveButton(android.R.string.ok) { v, _ ->
            //Solicitamos los permisos de nuevo
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            //cerramos el dialogo
            v.dismiss()
            }
            //accion si pulsa no
            .setNegativeButton(android.R.string.cancel) { v, _ ->
                v.dismiss()
            //cerramos el fragment
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}