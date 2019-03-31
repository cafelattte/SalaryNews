package kr.co.yna.www.salarynews.utils.recognition

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import com.naver.speech.clientapi.SpeechRecognitionResult
import kr.co.yna.www.salarynews.HomeActivity
import kr.co.yna.www.salarynews.HomeEndActivity
import kr.co.yna.www.salarynews.MainActivity
import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.enum.micOffMode
import kr.co.yna.www.salarynews.enum.micOnMode
import kr.co.yna.www.salarynews.utils.SoundBeep
import java.lang.ref.WeakReference

class NaverRecognitionHandler(val context: Context): Handler() {
    private val activityReference: WeakReference<Context> = WeakReference(context)
    var soundBeep: SoundBeep?
    init {
        soundBeep = activityReference.get()?.let {
            SoundBeep(it)
        }
        soundBeep?.initSoundBeep(R.raw.sound_effect)
    }
    private var writer: NaverAudioWriterPCM? = null

    override fun handleMessage(msg: Message?) {
        activityReference.get()?.let {mContext ->
            msg?.let { msg ->
                when(msg.what) {
                    R.id.clientReady -> {
                        micOnMode(mContext)
                        Log.d("handler", "clientReady")
                        // user can speak
                        soundBeep?.playSoundBeep()
                        writer = NaverAudioWriterPCM("${Environment.getExternalStorageDirectory().absolutePath}/NaverSpeechTest")
                        writer?.open(mContext.javaClass.simpleName)
                    }
                    R.id.audioRecording -> {
                        Log.d("handler", "audioRecording")
                        writer?.write(msg.obj as ShortArray)
                    }
                    R.id.partialResult -> {
                        micOnMode(mContext)
                        Log.d("handler", "partialResult")
                    }
                    R.id.finalResult -> {
                        micOffMode(mContext)
                        val results: List<String> = (msg.obj as SpeechRecognitionResult).results
                        Log.d("handler", "finalResult: ${results[0]}")
                        (mContext as Activity).let { activity ->
                            when (activity.localClassName) {
                                "HomeActivity" -> {
                                    (mContext as HomeActivity).let {
                                        it.dialogflowQueryDetectIntent(it, results[0])
                                    }
                                }
                                "MainActivity" -> {
                                    (mContext as MainActivity).let {
                                        it.dialogflowQueryDetectIntent(it, results[0])
                                    }
                                }
                                "HomeEndActivity" -> {
                                    (mContext as HomeEndActivity).let {
                                        it.dialogflowQueryDetectIntent(it, results[0])
                                    }
                                }
                                else -> {
                                    Log.e("NaverRecognitionHandler", "unexpected condition branch")
                                }
                            }
                        }
                    }
                    R.id.recognitionError -> {
                        micOffMode(mContext)
                        Log.d("handler", "recognitionError")
                        writer?.close()
                    }
                    R.id.clientInactive -> {
                        micOffMode(mContext)
                        Log.d("handler", "clientInactive")
                        writer?.close()
                    }
                    else -> {
                        Log.e("NaverRecognitionHandler", "unexpected condition branch")
                    }
                }
            }
        }
    }
}
