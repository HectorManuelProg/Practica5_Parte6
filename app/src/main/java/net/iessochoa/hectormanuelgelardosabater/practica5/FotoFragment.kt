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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.FragmentFotoBinding
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.Locale

class FotoFragment : Fragment() {
    private var _binding: FragmentFotoBinding? = null
    private var uriFoto: Uri?=null
    private val binding get() = _binding!!
    val args: FotoFragmentArgs by navArgs()

    //Array con los permisos necesarios
    private val PERMISOS_REQUERIDOS =
        mutableListOf (
            Manifest.permission.CAMERA).apply {
//si la versión de Android es menor o igual a la 9 pedimos el permiso de escritura
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    private var imageCapture: ImageCapture? = null

    companion object {
        private const val TAG = "Practica5_CameraX"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurar el OnClickListener para el botón redondo
        binding.btCapturaFoto.setOnClickListener {
            // Llamar al método para capturar la foto
            takePhoto()
        }

        binding.ivMuestra.setOnClickListener(){

            var tarea=args.tarea?.copy(fotoUri =uriFoto.toString())
            val action =
                FotoFragmentDirections.actionFotoFragmentToTareaFragment(tarea)
            findNavController().navigate(action)
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun takePhoto() {
// Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
// Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
// val name = "practica5_1"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//funiona en versiones superiores a Android 9
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" +
                         getString(R.string.app_name))
            }
        }
// Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
// Set up image capture listener, which is triggered after photo has
// been taken
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(requireContext()),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}",
                                exc)
                        }
                        override fun onImageSaved(output:
                                                  ImageCapture.OutputFileResults) {
                            val msg = "Photo capture succeeded:${output.savedUri}"
                            Toast.makeText(requireContext(), msg,
                                Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                            binding.ivMuestra.setImageURI(output.savedUri)
                            uriFoto=output.savedUri
                        }
                    }
                )
    }

    private fun allPermissionsGranted() = PERMISOS_REQUERIDOS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        //Se usa para vincular el ciclo de vida de las cámaras al propietario del ciclo de vida.
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        //Agrega un elemento Runnable como primer argumento
        cameraProviderFuture.addListener({
            // Esto se usa para vincular el ciclo de vida de nuestra cámara al LifecycleOwner dentro del proceso de la aplicación
            val cameraProvider: ProcessCameraProvider =
                cameraProviderFuture.get()
            //Inicializa nuestro objeto Preview,
            // llama a su compilación, obtén un proveedor de plataforma desde el visor y,
            // luego, configúralo en la vista previa.
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
            // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
            //segundo argumento
        }, ContextCompat.getMainExecutor(requireContext()))
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



