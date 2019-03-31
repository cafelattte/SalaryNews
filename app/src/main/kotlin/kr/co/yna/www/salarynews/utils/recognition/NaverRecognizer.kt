package kr.co.yna.www.salarynews.utils.recognition

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.annotation.WorkerThread
import android.util.Log
import com.naver.speech.clientapi.*
import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.enum.micOffMode

class NaverRecognizer (
    val context: Context,
    private val handler: Handler,
    private val clientId: String) : SpeechRecognitionListener {

    companion object {
        private val TAG: String = NaverRecognizer::class.java.simpleName
    }
    private var mRecognizer: SpeechRecognizer? = null

    init {
        try {
            mRecognizer = SpeechRecognizer(context, clientId)
        } catch (e: SpeechRecognitionException) {
            // 예외 발생 예시
            // 1. activity 파라미터가 올바른 MainActivity의 인스턴스가 아닙니다.
            // 2. AndroidManifest.xml에서 package를 올바르게 등록하지 않았습니다.
            // 3. package를 올바르게 등록했지만 과도하게 긴 경우, 256바이트 이하면 좋습니다.
            // 4. clientId가 null인 경우
            Log.e(TAG, "SpeechRecognitionException")
            e.printStackTrace()
        }
        mRecognizer?.setSpeechRecognitionListener(this)
    }

    fun getSpeechRecognizer(): SpeechRecognizer? {
        return mRecognizer
    }

    private fun recognize() {
        try {
            mRecognizer?.recognize(SpeechConfig(SpeechConfig.LanguageType.KOREAN, SpeechConfig.EndPointDetectType.AUTO))
        } catch (e: SpeechRecognitionException) {
            Log.e(TAG, "SpeechRecognitionException")
            e.printStackTrace()
        }
    }
    fun startRecognize(context: Context) {
        GetPermission().getAudioRecordPermission(context)
        mRecognizer?.isRunning?.let {
            if (!it) {
                // Run SpeechRecognizer by calling recognize().
                Log.d(TAG, "Connecting...")
                micOffMode(context)
                recognize()
            } else {
                Log.d(TAG, "stop and wait Final Result")
                stop()
            }
        }
    }
    private fun stop() {
        mRecognizer?.stop()
    }
    fun initialize() {
        mRecognizer?.initialize()
    }
    fun release() {
        mRecognizer?.release()
    }

    @WorkerThread
    override fun onInactive() {
        Log.d(TAG, "Event occurred : Inactive")
        val msg = Message.obtain(handler, R.id.clientInactive)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onReady() {
        Log.d(TAG, "Event occurred : Ready")
        val msg = Message.obtain(handler, R.id.clientReady)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onRecord(speech: ShortArray?) {
        Log.d(TAG, "Event occurred : Record")
        val msg = Message.obtain(handler, R.id.audioRecording, speech)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onPartialResult(partialResult: String?) {
        Log.d(TAG, "Partial Result!! ($partialResult)")
        val msg = Message.obtain(handler, R.id.partialResult, partialResult)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected")
    }

    @WorkerThread
    override fun onResult(finalResult: SpeechRecognitionResult) {
        Log.d(TAG, "Final Result!! (${finalResult.results[0]})")
        val msg = Message.obtain(handler, R.id.finalResult, finalResult)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onError(errorCode: Int) {
        Log.d(TAG, "Error!! (${Integer.toString(errorCode)})")
        val msg = Message.obtain(handler, R.id.recognitionError, errorCode)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetectTypeSelected(epdType: SpeechConfig.EndPointDetectType) {
        Log.d(TAG, "EndPointDetectType is selected!! (${Integer.toString(epdType.toInteger())})")
        val msg = Message.obtain(handler, R.id.endPointDetectTypeSelected, epdType)
        msg.sendToTarget()
    }

}