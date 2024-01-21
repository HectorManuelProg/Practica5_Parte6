package ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Tarea
import repository.Repository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    //creamos el LiveData de tipo Booleano. Representa nuestro filtro
    //private val soloSinPagarLiveData= MutableLiveData<Boolean>(false)
    //repositorio
    private val repositorio:Repository
    //liveData de lista de tareas
    var tareasLiveData :LiveData<List<Tarea>>
    //private val estadoLiveData = MutableLiveData<Int>(3)
    //LiveData que cuando se modifique un filtro cambia el tareasLiveData
    val SOLO_SIN_PAGAR="SOLO_SIN_PAGAR"
    val ESTADO="ESTADO"
    val PRIORIDAD="PRIORIDAD"
    private val filtrosLiveData by lazy {//inicio tardío
        val mutableMap = mutableMapOf<String, Any?>(
            SOLO_SIN_PAGAR to false,
            ESTADO to 3,
            PRIORIDAD to 3
        )
        MutableLiveData(mutableMap)
    }
    //inicio ViewModel
    init {
    //inicia repositorio
        Repository(getApplication<Application>().applicationContext)
        repositorio=Repository
        //tareasLiveData=soloSinPagarLiveData.switchMap {soloSinPagar->
            //Repository.getTareasFiltroSinPagar(soloSinPagar)}
        tareasLiveData=filtrosLiveData.switchMap{ mapFiltro ->
            val aplicarSinPagar = mapFiltro!![SOLO_SIN_PAGAR] as Boolean
            var estado = mapFiltro!![ESTADO] as Int
            val prioridad = mapFiltro!![PRIORIDAD] as Int
            //Devuelve el resultado del when
            when {//trae toda la lista de tareas
                (!aplicarSinPagar && (estado == 3) && (prioridad ==3)) ->
                    repositorio.getAllTareas()
                //Sólo filtra por ESTADO
                (!aplicarSinPagar && (estado != 3) && (prioridad ==3)) ->
                    repositorio.getTareasFiltroEstado(estado)
                //Sólo filtra SINPAGAR
                (aplicarSinPagar && (estado == 3) && (prioridad ==3)) ->
                    repositorio.getTareasFiltroSinPagar(aplicarSinPagar)
                //Filtra por ambos
                (!aplicarSinPagar && (estado == 3) && (prioridad !==3)) ->
                    repositorio.getTareasFiltroPrioridad(prioridad)
                (aplicarSinPagar && (estado != 3) && (prioridad == 3)) ->
                    repositorio.getTareasFiltroSinPagarEstado(aplicarSinPagar, estado)
                (!aplicarSinPagar && (estado != 3) && (prioridad !== 3)) ->
                    repositorio.getTareasFiltroEstadoPrioridad(estado, prioridad)
                (aplicarSinPagar && (estado == 3) && (prioridad !== 3)) ->
                    repositorio.getTareasFiltroSinPagarPrioridad(aplicarSinPagar, prioridad, estado)
                else ->
                    repositorio.getTareasFiltroSinPagarPrioridadEstado(aplicarSinPagar, estado, prioridad)
            }
        }
    }
    /**
     * activa el LiveData del filtro
     */
    //fun setSoloSinPagar(soloSinPagar:Boolean){soloSinPagarLiveData.value=soloSinPagar}
    fun addTarea(tarea: Tarea) = viewModelScope.launch(Dispatchers.IO) {
        Repository.addTarea(tarea)}
    //lanzamos el borrado por corrutina
    fun delTarea(tarea: Tarea) = viewModelScope.launch(Dispatchers.IO){
        Repository.delTarea(tarea)}
    //fun setEstado(estado: Int){estadoLiveData.value=estado}

    /**
     * Modifica el Map filtrosLiveData el elemento "SOLO_SIN_PAGAR"
     * que activará el Transformations de TareasLiveData
     */
    fun setSoloSinPagar(soloSinPagar: Boolean) {
//recuperamos el map
        val mapa = filtrosLiveData.value
//modificamos el filtro
        mapa!![SOLO_SIN_PAGAR] = soloSinPagar
//activamos el LiveData
        filtrosLiveData.value = mapa
    }
    /**
     * Modifica el Map filtrosLiveData el elemento "ESTADO"
     * que activará el Transformations de TareasLiveData lo
     *llamamos cuando cambia el RadioButton
     */
    fun setEstado(estado: Int) {
//recuperamos el map
        val mapa = filtrosLiveData.value
//modificamos el filtro
        mapa!![ESTADO] = estado
//activamos el LiveData
        filtrosLiveData.value = mapa
    }
    fun setPrioridad(prioridad: Int){
        val mapa = filtrosLiveData.value
        mapa!![PRIORIDAD] = prioridad
        filtrosLiveData.value = mapa
    }
    }


