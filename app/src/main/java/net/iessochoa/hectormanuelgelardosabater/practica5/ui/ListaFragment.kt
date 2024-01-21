package net.iessochoa.hectormanuelgelardosabater.practica5.ui

import ViewModel.AppViewModel
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import model.Tarea
import net.iessochoa.hectormanuelgelardosabater.practica5.R
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.FragmentListaBinding
import net.iessochoa.hectormanuelgelardosabater.practica5.ui.adapters.TareasAdapter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val viewModel: AppViewModel by activityViewModels()
    lateinit var tareasAdapter: TareasAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniciaRecyclerView()
        iniciaCRUD()
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
                //actualizaLista(lista)
                tareasAdapter.setLista(lista)
            })
        iniciaFiltros()
        iniciaFiltrosEstado()
        iniciaSpPrioridad()
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
            actualizaLista(lista)
        })
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { listaFiltrada ->
            // Actualizar el RecyclerView con la nueva lista filtrada
            actualizaLista(listaFiltrada)
        })
    }
    private fun iniciaCRUD(){
        binding.fabNuevo.setOnClickListener {
            //creamos acción enviamos argumento nulo porque queremos crear NuevaTarea
            val action=ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)
        }
        tareasAdapter.onTareaClickListener = object :
            TareasAdapter.OnTareaClickListener {
            //**************Editar Tarea*************
            override fun onTareaClick(tarea: Tarea?) {
                //creamos la acción y enviamos como argumento la tarea para editarla
                val action = ListaFragmentDirections.actionEditar(tarea)
                findNavController().navigate(action)
            }
            //***********Borrar Tarea************
            override fun onTareaBorrarClick(tarea: Tarea?) {
                //borramos directamente la tarea
                //viewModel.delTarea(tarea!!)
                borrarTarea(tarea!!)
            }
            override fun onEstadoIconClick(tarea: Tarea?) {
                // Lógica cuando se hace clic en el icono de estado de la tarea
            }
        }
    }
    private fun iniciaSpPrioridad() {
        ArrayAdapter.createFromResource(
            //contexto suele ser la Activity
            requireContext(),
            //array de strings
            R.array.prioridad,
            //layout para mostrar el elemento seleccionado
            R.layout.spinner_items).also { adapter ->
            // Layout para mostrar la apariencia de la lista
            adapter.setDropDownViewResource(R.layout.spinner_items)
            // asignamos el adaptador al spinner
            binding.spPrioridad.adapter = adapter
            binding.spPrioridad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, posicion: Int, id: Long) {
                    viewModel.setPrioridad(posicion)
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }
    fun borrarTarea(tarea:Tarea){
        AlertDialog.Builder(activity as Context)
            .setTitle(getString(R.string.atencion))
            //recuerda: todo el texto en string.xml
            .setMessage("Desea borrar la Tarea ${tarea.id}?")
            //acción si pulsa si
            .setPositiveButton(getString(R.string.aceptar)){_,_->
                //borramos la tarea
                viewModel.delTarea(tarea)
                //cerramos el dialogo
                //v.dismiss()
            }
            //accion si pulsa no
            .setNegativeButton(getString(R.string.cancelar)){v,_->v.dismiss()}
            .setCancelable(false)
            .create()
            .show()
    }
    private fun iniciaRecyclerView() {
        //creamos el adaptador
        tareasAdapter = TareasAdapter()
        with(binding.rvTareas) {
        //Creamos el layoutManager
            layoutManager = LinearLayoutManager(activity)
            //le asignamos el adaptador
            adapter = tareasAdapter
        }
    }

    private fun iniciaFiltros(){
        binding.swSinPagar.setOnCheckedChangeListener( ) { _,isChecked->
        //actualiza el LiveData SoloSinPagarLiveData que a su vez modifica tareasLiveData
        //mediante el Transformation
            viewModel.setSoloSinPagar(isChecked)}

    }
    private fun iniciaFiltrosEstado() {
        //listener de radioGroup
        binding.rgFiltrarEstado.setOnCheckedChangeListener { _, checkedId ->
            val estado = when (checkedId) {
                // IDs de cada RadioButton
                R.id.rgbAbiertas -> 0 // Abierta
                R.id.rgbEnCurso -> 1 // En curso
                R.id.rgbCerrada -> 2 // Cerrada
                else -> 3 // Recuperar toda la lista de tareas
            }
            viewModel.setEstado(estado)
        }
        //iniciamos a abierto
        binding.rgFiltrarEstado.check(R.id.rbAbierta)
    }

    private fun actualizaLista(lista: List<Tarea>?) {
        //creamos un string modificable
        val listaString = buildString {
            lista?.forEach() {
            //añadimos al final del string
                append(
                    "${it.id}-${it.tecnico}-${
                    //mostramos un trozo de la descripción
                        if (it.descripcion.length < 21) it.descripcion
                        else
                            it.descripcion.subSequence(0, 20)
                    }-${
                        if (it.pagado) "SI-PAGADO" else
                            "NO-PAGADO"
                    }-" + when (it.estado) {
                        0 -> "ABIERTA"
                        1 -> "EN_CURSO"
                        else -> "CERRADA"
                    } + "\n"
                )
            }
        }
       // binding.tvListaTareas.setText(listaString)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun Any.setBackgroundColor(color: Int) {

}



