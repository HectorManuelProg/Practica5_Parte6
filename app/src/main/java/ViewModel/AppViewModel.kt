package ViewModel

import android.app.Application
import android.service.autofill.Transformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import model.Tarea
import repository.Repository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    //creamos el LiveData de tipo Booleano. Representa nuestro filtro
    private val soloSinPagarLiveData= MutableLiveData<Boolean>(false)
    //repositorio
    private val repositorio:Repository
    //liveData de lista de tareas
    var tareasLiveData :LiveData<List<Tarea>>
    private val estadoSeleccionadoLiveData = MutableLiveData<Int>(3)

    //inicio ViewModel
    init {
    //inicia repositorio
        Repository(getApplication<Application>().applicationContext)
        repositorio=Repository
        tareasLiveData=soloSinPagarLiveData.switchMap {soloSinPagar->
            Repository.getTareasFiltroSinPagar(soloSinPagar)}
        //tareasLiveData=estadoSeleccionadoLiveData.switchMap {estadoSeleccionado->
          //  Repository.getTareasFiltroEstado(estadoSeleccionado)}

    }
    /**
     * activa el LiveData del filtro
     */
    fun setSoloSinPagar(soloSinPagar:Boolean){soloSinPagarLiveData.value=soloSinPagar}
    fun addTarea(tarea: Tarea) = repositorio.addTarea(tarea)
    fun delTarea(tarea: Tarea) = repositorio.delTarea(tarea)
    fun setEstado(estado: Int){estadoSeleccionadoLiveData.value=estado}
    }


