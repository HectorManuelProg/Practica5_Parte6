package repository

import android.app.Application
import android.content.Context
import model.Tarea
import model.temp.ModelTempTareas

object Repository {
    //instancia al modelo
    private lateinit var modelTareas: ModelTempTareas

    //el context suele ser necesario para recuperar datos
    private lateinit var application: Application

    //inicio del objeto singleton
    operator fun invoke(context: Context) {
        this.application = context.applicationContext as Application
//iniciamos el modelo
        ModelTempTareas(application)
        modelTareas = ModelTempTareas
    }
    fun getTareasFiltroSinPagar (soloSinPagar:Boolean)= modelTareas.getTareasFiltroSinPagar(soloSinPagar)
    fun addTarea(tarea: Tarea) = modelTareas.addTarea(tarea)
    fun delTarea(tarea: Tarea) = modelTareas.delTarea(tarea)
    fun getAllTareas() = modelTareas.getAllTareas()
}