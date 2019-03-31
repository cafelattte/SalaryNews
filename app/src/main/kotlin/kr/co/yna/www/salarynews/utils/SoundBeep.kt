package kr.co.yna.www.salarynews.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.lang.ref.WeakReference

class SoundBeep(val context: Context) {
    private var soundPool: SoundPool? = null
    private var streamId: Int? = null
    private var audioManager: AudioManager? = null
    private var soundIdBeep: Int? = null

    private var volume: Float? = null
    private var loaded = false

    fun initSoundBeep(rawId: Int) {
        WeakReference(context).get()?.run {
            loaded = false
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolumeIndex = audioManager?.getStreamMaxVolume(streamType)?.toFloat()
            val currentVolumeIndex = audioManager?.getStreamVolume(streamType)?.toFloat()
            volume = currentVolumeIndex?.div(maxVolumeIndex as Float)
//            volumeControlStream = streamType

            val audioAttribute = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val builder = SoundPool.Builder()
            builder.setAudioAttributes(audioAttribute).setMaxStreams(MAX_STREAMS)

            soundPool = builder.build()
            soundPool?.setOnLoadCompleteListener { _, _, _ ->
                loaded = true
            }
            soundIdBeep = soundPool?.load(context, rawId, 1)
        }
    }

    fun playSoundBeep() {
        if (loaded) {
            val leftVolume = volume as Float
            val rightVolume = volume as Float

            streamId = soundPool?.play(soundIdBeep as Int, leftVolume, rightVolume, 1, 0, 1f)
        }
    }
    fun stopSoundBeep() {
        try {
            soundPool?.stop(streamId as Int)
        } catch (e: TypeCastException) {
            Log.e("stopSoundBeep", "$e")
        }
    }
    fun releaseSoundBeep() {
        soundPool?.release()
    }

    companion object {
        const val streamType = AudioManager.STREAM_MUSIC
        const val MAX_STREAMS = 5
    }
}