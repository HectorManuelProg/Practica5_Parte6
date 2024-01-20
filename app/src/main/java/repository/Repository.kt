package repository

import android.app.Application
import android.content.Context
import model.Tarea
import model.db.TareasDao
import model.db.TareasDataBase

object Repository {
    //instancia al modelo
    //private lateinit var modelTareas: ModelTempTareas
    private lateinit var modelTareas: TareasDao
    //private val listaTareas = mutableListOf<Tarea>()
    //el context suele ser necesario para recuperar datos
    private lateinit var application: Application

    //inicio del objeto singleton
    operator fun invoke(context: Context) {
        this.application = context.applicationContext as Application
        //iniciamos el modelo
        //ModelTempTareas(application)
        //modelTareas = ModelTempTareas
        // iniciamos BD
       modelTareas = TareasDataBase.getDatabase(application).tareasDao()
    }

    fun getTareasFiltroSinPagar (soloSinPagar:Boolean) = modelTareas.getTareasFiltroSinPagar(soloSinPagar)
    suspend fun addTarea(tarea: Tarea) = modelTareas.addTarea(tarea)
    suspend fun delTarea(tarea: Tarea) = modelTareas.delTarea(tarea)
    fun getTareasFiltroEstado(estado: Int) = modelTareas.getTareasFiltroEstado(estado)
    fun getTareasFiltroSinPagarEstado(soloSinPagar:Boolean, estado:Int)= modelTareas.getTareasFiltroSinPagarEstado(soloSinPagar,estado)
    fun getAllTareas() = modelTareas.getAllTareas()
    fun getTareasFiltroPrioridad(prioridad: Int) = modelTareas.getTareasFiltroPrioridad(prioridad)
    fun getTareasFiltroEstadoPrioridad(estado: Int, prioridad: Int) = modelTareas.getTareasFiltroEstadoPrioridad(estado, prioridad)
    fun getTareasFiltroSinPagarPrioridadEstado(soloSinPagar:Boolean, estado:Int, prioridad:Int)= modelTareas.getTareasFiltroSinPagarPrioridadEstado(soloSinPagar,estado, prioridad)
    fun getTareasFiltroSinPagarPrioridad(soloSinPagar: Boolean, prioridad: Int, estado: Int)= modelTareas.getTareasFiltroSinPagarPrioridad(soloSinPagar, prioridad)
}