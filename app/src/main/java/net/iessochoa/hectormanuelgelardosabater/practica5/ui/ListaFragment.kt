package net.iessochoa.hectormanuelgelardosabater.practica5.ui

import ViewModel.AppViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
                //actualizaLista(lista)
                tareasAdapter.setLista(lista)
            })
        iniciaFiltros()
        iniciaFiltrosEstado()
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
            actualizaLista(lista)
        })
        binding.fabNuevo.setOnClickListener {
        //creamos acción enviamos argumento nulo porque queremos crear NuevaTarea
            val action=ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)
        }

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

