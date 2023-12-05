package net.iessochoa.hectormanuelgelardosabater.practica5.ui.adapters;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.ItemTareaBinding
import model.Tarea

internal class TareasAdapter :
    RecyclerView.Adapter<TareasAdapter.TareaViewHolder>()

var listaTareas: List<Tarea>?=null
fun setLista(lista: List<Tarea>) {
    listaTareas = lista
    //notifica al adaptador que hay cambios y tiene que redibujar el
    ReciclerView
    notifyDataSetChanged()
}
inner class TareaViewHolder
    : RecyclerView.ViewHolder(binding.root)

fun onCreateViewHolder(
    parent: ViewGroup, viewType:
    Int
): TareaViewHolder {
    TODO("Not yet implemented")
}
fun onBindViewHolder(
    holder: TareaViewHolder,
    position: Int
) {
    TODO("Not yet implemented")
}
fun getItemCount(): Int {
    TODO("Not yet implemented")
}

fun getItemCount(): Int = listaTareas?.size ?: 0
fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        TareaViewHolder {
//utilizamos binding, en otro caso hay que indicar el item.xml.
    Para más detalles puedes verlo en la documentación
    val binding = ItemTareaBinding
        .inflate(LayoutInflater.from(parent.context), parent, false)
    return TareaViewHolder(binding)
}

fun onBindViewHolder(
    tareaViewHolder: TareaViewHolder, pos:
    Int
) {
//Nos pasan la posición del item a mostrar en el viewHolder
    with(tareaViewHolder) {
//cogemos la tarea a mostrar y rellenamos los campos del
        RecyclerView.ViewHolder
        with(listaTareas!![pos]) {
            binding.tvId.text = id.toString()
            binding.tvDescripcion.text = descripcion
            binding.tvTecnico.text = tecnico
            binding.rbValoracion.rating = valoracionCliente
//mostramos el icono en función del estado
            binding.ivEstado.setImageResource(
                when (estado) {
                    0 -> R.drawable.ic_abierto
                    1 -> R.drawable.ic_encurso
                    else -> R.drawable.ic_cerrado
                }
            )
//cambiamos el color de fondo si la prioridad es alta
            binding.cvItem.setBackgroundResource(
                if (prioridad == 2)//prioridad alta
                    R.color.prioridad_alta
                else
                    Color.TRANSPARENT
            )
        }
    }
}