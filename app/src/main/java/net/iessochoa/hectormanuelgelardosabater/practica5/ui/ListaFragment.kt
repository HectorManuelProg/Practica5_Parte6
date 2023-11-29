package net.iessochoa.hectormanuelgelardosabater.practica5.ui

import ViewModel.AppViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import model.Tarea
import net.iessochoa.hectormanuelgelardosabater.practica5.R
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.FragmentListaBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val viewModel: AppViewModel by activityViewModels()
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

        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
            actualizaLista(lista)
        })

       /* binding.fabNuevo.setOnClickListener{
            findNavController().navigate(R.id.action_editar)
        }*/
        binding.fabNuevo.setOnClickListener {
        //creamos acción enviamos argumento nulo porque queremos crear NuevaTarea
            val action=ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)
        }
        //para prueba, editamos una tarea aleatoria
        binding.btPruebaEdicion.setOnClickListener{
         //cogemos la lista actual de Tareas que tenemos en el ViewModel. No es lo más correcto
            val lista= viewModel.tareasLiveData.value
         //buscamos una tarea aleatoriamente
            val tarea=lista?.get((0..lista.lastIndex).random())
          //se la enviamos a TareaFragment para su edición
            val action=ListaFragmentDirections.actionEditar(tarea)
            findNavController().navigate(action)
        }

        //actualizaLista()
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
        binding.tvListaTareas.setText(listaString)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
