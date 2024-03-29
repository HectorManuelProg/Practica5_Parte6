package net.iessochoa.hectormanuelgelardosabater.practica5.ui

import ViewModel.AppViewModel
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import model.Tarea
import net.iessochoa.hectormanuelgelardosabater.practica5.R
import net.iessochoa.hectormanuelgelardosabater.practica5.VerFoto
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.FragmentTareaBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TareaFragment : Fragment() {

    private var _binding: FragmentTareaBinding? = null
    val args: TareaFragmentArgs by navArgs()
    private val viewModel: AppViewModel by activityViewModels()
    var uriFoto=""

    //petición de foto de la galería
    private val solicitudFotoGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
//uri de la foto elegida
            val uri = result.data?.data
//mostramos la foto
            binding.ivFoto.setImageURI(uri)
//guardamos la uri
            uriFoto = uri.toString()
        }
    }

    //será una tarea nueva si no hay argumento
    val esNuevo by lazy { args.tarea == null }
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTareaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivHacerFoto.setOnClickListener {
            abrirFotoFragment()
        }

        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
       /* binding.ivFoto.setOnClickListener {
            // Configurar la transición de ampliación
            val action = TareaFragmentDirections.actionTareaFragmentToVerFotoFragment(uriFoto)
            findNavController().navigate(action)

            // Aplicar la transición
           /* exitTransition = transition
            reenterTransition = transition

            // Ampliar la imagen
            val detalleFragmento = VerFotoFragment.newInstance(uriFoto)  // Pasa la URI de la foto
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.fragment_ver_foto, detalleFragmento)
                ?.addToBackStack(null)
                ?.commit()*/
        }*/

        //Iniciamos las funciones
        iniciaSpCategoria()
        iniciaSpPrioridad()
        iniciaSwPagado()
        iniciaRgEstado()
        iniciaSbHoras()
        iniciaIvBuscarFoto()

        iniciaFabGuardar()

        //si es nueva tarea o es una edicion
        if (esNuevo) {//nueva tarea
            //cambiamos el título de la ventana
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva tarea"
            iniciaTecnico()
        } else
            iniciaTarea(args.tarea!!)
    }

    /**
     * Carga los valores de la tarea a editar
     */
    private fun iniciaTarea(tarea: Tarea) {
        if (tarea == null) {
            // Si la tarea es nula, mostramos "Nueva Tarea"
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva Tarea"
        } else {
            // Si hay una tarea, mostramos su número de tarea
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tarea ${tarea.id}"
        }
        binding.spCategoria.setSelection(tarea.categoria)
        binding.spPrioridad.setSelection(tarea.prioridad)
        binding.swPagado.isChecked = tarea.pagado
        binding.rgEstado.check(
            when (tarea.estado) {
                0 -> R.id.rbAbierta
                1 -> R.id.rgbEnCurso
                else -> R.id.rgbCerrada
            }
        )
        binding.sbHoras.progress = tarea.horasTrabajo
        binding.rtbValoracion.rating = tarea.valoracionCliente
        binding.etTecnico.setText(tarea.tecnico)
        binding.etDescripcion.setText(tarea.descripcion)
        if (!tarea.fotoUri.isNullOrEmpty())
            binding.ivFoto.setImageURI(tarea.fotoUri.toUri())
        uriFoto=tarea.fotoUri
        //cambiamos el título
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tarea ${tarea.id}"
    }
    private fun abrirFotoFragment() {
        // Navega al FotoFragment
        findNavController().navigate(TareaFragmentDirections.actionTareaFragmentToFotoFragment())
    }

    /*private fun guardaTarea() {
        //recuperamos los datos
        val categoria = binding.spCategoria.selectedItemPosition
        val prioridad = binding.spPrioridad.selectedItemPosition
        val pagado = binding.swPagado.isChecked

        val estado = when (binding.rgEstado.checkedRadioButtonId) {
            R.id.rbAbierta -> 0
            R.id.rgbEnCurso -> 1
            else -> 2
        }
        val horas = binding.sbHoras.progress
        val valoracion = binding.rtbValoracion.rating
        val tecnico = binding.etTecnico.text.toString()
        val descripcion = binding.etDescripcion.text.toString()
        //creamos la tarea: si es nueva, generamos un id, en otro caso le asignamos su id

        val tarea = if (esNuevo)
            Tarea(
                categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion,uriFoto)
        else
            Tarea(
                args.tarea!!.id,categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion,uriFoto)
        //guardamos la tarea desde el viewmodel
        viewModel.addTarea(tarea)
        //salimos de editarFragment
        findNavController().popBackStack()
    }*/
    private fun guardaTarea() {
//recuperamos los datos
//guardamos la tarea desde el viewmodel
        viewModel.addTarea(creaTarea())
//salimos de editarFragment
        findNavController().popBackStack()
    }
    private fun creaTarea():Tarea{
        val categoria=binding.spCategoria.selectedItemPosition
        val prioridad=binding.spPrioridad.selectedItemPosition
        val pagado=binding.swPagado.isChecked
        val estado=when (binding.rgEstado.checkedRadioButtonId) {
            R.id.rbAbierta -> 0
            R.id.rgbEnCurso -> 1
            else -> 2
        }
        val horas=binding.sbHoras.progress
        val valoracion=binding.rtbValoracion.rating
        val tecnico=binding.etTecnico.text.toString()
        val descripcion=binding.etDescripcion.text.toString()
//creamos la tarea: si es nueva, generamos un id, en otro caso le asignamos su id
        val tarea = if(esNuevo)
            Tarea(categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion, uriFoto)
        else//venimos de hacer foto
            Tarea(args.tarea!!.id,categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion, uriFoto)
        return tarea
    }

    private fun iniciaFabGuardar() {
        binding.fabGuardar.setOnClickListener {
            if (binding.etTecnico.text.toString().isEmpty() || binding.etDescripcion.text.toString()
                    .isEmpty()
            ) {
                // muestraMensajeError()
                val mensaje = "Los campos no pueden estar vacios"
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG).setAction("Action", null)
                    .show()
            } else
                guardaTarea()
        }
    }

    private fun iniciaSpCategoria() {
        ArrayAdapter.createFromResource(
            //contexto suele ser la Activity
            requireContext(),
            //array de strings
            R.array.categoria,
            //layout para mostrar el elemento seleccionado
            R.layout.spinner_items
        ).also { adapter ->
            // Layout para mostrar la apariencia de la lista
            adapter.setDropDownViewResource(R.layout.spinner_items)
            // asignamos el adaptador al spinner
            binding.spCategoria.adapter = adapter
        }
        binding.spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                posicion: Int,
                id: Long
            ) {
                //recuperamos el valor
                val valor = binding.spCategoria.getItemAtPosition(posicion)
                //creamos el mensaje desde el recurso string parametrizado
                val mensaje = getString(R.string.mensaje_categoria, valor)
                //mostramos el mensaje donde "binding.root" es el ContrainLayout principal
                Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar evento cuando no se selecciona nada en el Spinner
            }
        }
    }

    private fun iniciaRgEstado() {
        //listener de radioGroup
        binding.rgEstado.setOnCheckedChangeListener { _, checkedId ->
            val imagen = when (checkedId) {//el id del RadioButton seleccionado
                //id del cada RadioButon
                R.id.rbAbierta -> R.drawable.ic_abierto
                R.id.rgbEnCurso -> R.drawable.ic_encurso
                else -> R.drawable.ic_cerrado
            }
            binding.ivEstado.setImageResource(imagen)
        }
        //iniciamos a abierto
        binding.rgEstado.check(R.id.rbAbierta)
    }

    private fun iniciaSwPagado() {
        binding.swPagado.setOnCheckedChangeListener { _, isChecked ->
            //cambiamos el icono si está marcado o no el switch
            val imagen = if (isChecked) R.drawable.ic_pagado
            else R.drawable.ic_no_pagado
            //asignamos la imagen desde recursos
            binding.ivPagado.setImageResource(imagen)
        }
        //iniciamos a valor false
        binding.swPagado.isChecked = false
        binding.ivPagado.setImageResource(R.drawable.ic_no_pagado)
    }

    private fun iniciaSpPrioridad() {
        ArrayAdapter.createFromResource(
            //contexto suele ser la Activity
            requireContext(),
            //array de strings
            R.array.prioridad,
            //layout para mostrar el elemento seleccionado
            R.layout.spinner_items
        ).also { adapter ->
            // Layout para mostrar la apariencia de la lista
            adapter.setDropDownViewResource(R.layout.spinner_items)
            // asignamos el adaptador al spinner
            binding.spPrioridad.adapter = adapter
        }
        binding.spPrioridad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, posicion: Int, id: Long) {
                //el array son 3 elementos y "alta" ocupa la tercera posición
                if (posicion == 2) {
                    binding.clytTarea.setBackgroundColor(requireContext().getColor(R.color.prioridad_alta))
                } else {//si no es prioridad alta quitamos el color
                    binding.clytTarea.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.clytTarea.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun iniciaSbHoras() {
        //asignamos el evento
        binding.sbHoras.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progreso: Int, p2: Boolean) {
                //Mostramos el progreso en el textview
                binding.tvHoras.text = getString(R.string.horas_trabajadas, progreso)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        //inicio del progreso
        binding.sbHoras.progress = 0
        binding.tvHoras.text = getString(R.string.horas_trabajadas, 0)
    }

    private fun buscarFoto() {
//Toast.makeText(requireContext(), "Buscando la foto...", Toast.LENGTH_SHORT).show()
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        solicitudFotoGallery.launch(intent)
    }
        fun iniciaIvBuscarFoto() {
        binding.ivBuscarFoto.setOnClickListener() {
            when {
                //si tenemos los permisos
                permisosAceptados() -> buscarFoto()
                //no tenemos los permisos y los solicitamos
                else ->
                    solicitudPermisosLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val solicitudPermisosLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
// Permission has been granted.
            buscarFoto()
        } else {
// Permission request was denied.
            explicarPermisos()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun iniciaTecnico() {
        //recuperamos las preferencias
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        //recuperamos el nombre del usuario
        val tecnico = sharedPreferences.getString(MainActivity.PREF_NOMBRE, "")
        //lo asignamos
        binding.etTecnico.setText(tecnico)
    }

    fun permisosAceptados() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    fun explicarPermisos() {
        AlertDialog.Builder(activity as Context)
            .setTitle(android.R.string.dialog_alert_title)
//TODO:recuerda: el texto en string.xml
            .setMessage("Es necesario el permiso de \"Lectura de fichero\" para mostrar una foto.\nDesea aceptar los permisos?")
//acción si pulsa si
            .setPositiveButton(android.R.string.ok) { v, _ ->
//Solicitamos los permisos de nuevo
                solicitudPermisosLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//cerramos el dialogo
                v.dismiss()
            }
//accion si pulsa no
            .setNegativeButton(android.R.string.cancel) { v, _ -> v.dismiss() }
            .setCancelable(false)
            .create()
            .show()
    }
}