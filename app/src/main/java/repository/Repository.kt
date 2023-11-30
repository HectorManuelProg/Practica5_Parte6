package repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import model.Tarea
import model.temp.ModelTempTareas

object Repository {
    //instancia al modelo
    private lateinit var modelTareas: ModelTempTareas
    private val listaTareas = mutableListOf<Tarea>()
    //el context suele ser necesario para recuperar datos
    private lateinit var application: Application

    // Método para filtrar las tareas por estado y devolver un LiveData
    /*fun getTareasFiltroEstado(estado: Int): LiveData<List<Tarea>> {
        val tareasFiltradasLiveData = MutableLiveData<List<Tarea>>()

        // Lógica para filtrar las tareas según el estado proporcionado
        val tareasFiltradas = listaTareas.filter { tarea ->
            tarea.estado == estado // Suponiendo que 'estado' es un campo en la clase Tarea
        }

        tareasFiltradasLiveData.value = tareasFiltradas // Establecer las tareas filtradas en el LiveData

        return tareasFiltradasLiveData // Devolver el LiveData con las tareas filtradas
    }*/

    //inicio del objeto singleton
    operator fun invoke(context: Context) {
        this.application = context.applicationContext as Application
        //iniciamos el modelo
        ModelTempTareas(application)
        modelTareas = ModelTempTareas
    }

    fun getTareasFiltroSinPagar (soloSinPagar:Boolean) = modelTareas.getTareasFiltroSinPagar(soloSinPagar)
    fun addTarea(tarea: Tarea) = modelTareas.addTarea(tarea)
    fun delTarea(tarea: Tarea) = modelTareas.delTarea(tarea)
    fun getTareasFiltroEstado(estado: Int) = modelTareas.getTareasFiltroEstado(estado)
    fun getTareasFiltroSinPagarEstado(soloSinPagar:Boolean, estado:Int)=
        modelTareas.getTareasFiltroSinPagarEstado(soloSinPagar,estado)
    fun getAllTareas() = modelTareas.getAllTareas()
}