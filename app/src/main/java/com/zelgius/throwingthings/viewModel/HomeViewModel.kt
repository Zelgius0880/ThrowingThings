package com.zelgius.throwingthings.viewModel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zelgius.throwingthings.R
import com.zelgius.throwingthings.entity.AbstractSound
import com.zelgius.throwingthings.entity.FireFlower
import com.zelgius.throwingthings.entity.PokeBall



class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val _selected = MutableLiveData<AbstractSound>(FireFlower())
    val selected: LiveData<AbstractSound>
        get() = _selected

    private val _ambient = MutableLiveData<Boolean>(false)
    val ambient: LiveData<Boolean>
        get() = _ambient

    private  val manager = app.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var mediaPlayer: MediaPlayer? = null

    var active = true
    set(value) {
        field = value

        if(value)
            manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL)
        else
            manager.unregisterListener(listener)
    }


    private val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            when(event.sensor.type) {
                //Sensor.TYPE_GRAVITY -> {event.values.copyInto(gravity)}
                //Sensor.TYPE_MAGNETIC_FIELD -> {event.values.copyInto(geoMagnetic)}
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    /*val trueacceleration = FloatArray(4)
                    val R = FloatArray(16)
                    val RINV = FloatArray(16)

                    SensorManager.getRotationMatrix(
                        R,
                        javax.swing.text.html.HTML.Tag.I,
                        gravity,
                        geomagnetic
                    )
                    Matrix.invertM(RINV, 0, R, 0)
                    Matrix.multiplyMV(trueAcceleration, 0, RINV, 0, linearAcceleration, 0)*/

                    val (x,y,z) = event.values

                    if(active && y >= 6 && mediaPlayer?.isPlaying != true) {
                        mediaPlayer = selected.value?.play(app, mediaPlayer)
                        mediaPlayer?.setOnCompletionListener {
                            Log.d(this::class.simpleName, "Media finished")
                        }

                        mediaPlayer?.setOnPreparedListener {
                            it.start()
                        }
                    }
                }
            }
        }

    }

    init {
        //manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL)
        //manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL)

    }

/*    override fun onCleared() {
        super.onCleared()
        manager.unregisterListener(listener)
    }*/

    fun change(index: Int) {
        _selected.value =
            when(index) {
                0 -> FireFlower()
                1 -> PokeBall()
                else -> error("index unavailable")
            }
    }

    fun setAmbientMode(ambient: Boolean) {
        _ambient.value = ambient
    }


}