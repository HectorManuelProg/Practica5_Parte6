package net.iessochoa.hectormanuelgelardosabater.practica5.ui.adapters;
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.iessochoa.hectormanuelgelardosabater.practica5.databinding.ItemTareaBinding
import model.Tarea
import net.iessochoa.hectormanuelgelardosabater.practica5.R

class TareasAdapter():
    RecyclerView.Adapter<TareasAdapter.TareaViewHolder>() {
    var listaTareas: List<Tarea>? = null
    var onTareaClickListener:OnTareaClickListener?=null
    private var ABIERTA = 0
    private var EN_CURSO = 1
    private var CERRADA = 2
    fun setLista(lista: List<Tarea>) {
        listaTareas = lista
        //notifica al adaptador que hay cambios y tiene que redibujar el ReciclerView
        notifyDataSetChanged()
    }

    inner class TareaViewHolder(val binding: ItemTareaBinding) :
        RecyclerView.ViewHolder(binding.root){
        init {
            //inicio del click de icono borrar
            binding.ivBorrar.setOnClickListener(){
            //recuperamos la tarea de la lista
                val tarea=listaTareas?.get(this.adapterPosition)
            //llamamos al evento borrar que estará definido en el fragment
                onTareaClickListener?.onTareaBorrarClick(tarea)
            }
             //inicio del click sobre el Layout(constraintlayout)
            binding.root.setOnClickListener(){
                val tarea=listaTareas?.get(this.adapterPosition)
                onTareaClickListener?.onTareaClick(tarea)
            }
            binding.ivEstado.setOnClickListener(){
                val tarea=listaTareas?.get(this.adapterPosition)
                if (tarea != null) {
                    val nuevoEstado = cambiarEstado(tarea.estado)
                    tarea.estado = nuevoEstado
                    notifyDataSetChanged()
                    onTareaClickListener?.onEstadoIconClick(tarea)
                }
            }
        }
    }
    private fun cambiarEstado(estadoActual: Int): Int {
        return when (estadoActual) {
            ABIERTA -> EN_CURSO
            EN_CURSO -> CERRADA
            CERRADA -> ABIERTA
            else -> ABIERTA // Valor por defecto, si no es ninguno de los anteriores
        }
    }
//tamaño de la lista
override fun getItemCount(): Int = listaTareas?.size?:0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        //utilizamos binding, en otro caso hay que indicar el item.xml. Para más detalles puedes verlo en la documentación
        val binding = ItemTareaBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(tareaViewHolder: TareaViewHolder, pos: Int) {
        //Nos pasan la posición del item a mostrar en el viewHolder
        with(tareaViewHolder) {

             //cogemos la tarea a mostrar y rellenamos los campos del ViewHolder
            with(listaTareas!!.get(pos)) {
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

    interface OnTareaClickListener{
        //editar tarea que contiene el ViewHolder
        fun onTareaClick(tarea:Tarea?)
        //borrar tarea que contiene el ViewHolder
        fun onTareaBorrarClick(tarea:Tarea?)
        fun onEstadoIconClick(tarea: Tarea?)
    }
}