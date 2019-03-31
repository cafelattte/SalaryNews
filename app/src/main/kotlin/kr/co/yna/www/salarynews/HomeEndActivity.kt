package kr.co.yna.www.salarynews

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import kotlinx.android.synthetic.main.activity_home_end.*
import kr.co.yna.www.salarynews.enum.*
import kr.co.yna.www.salarynews.utils.network.PostLoadAsyncTask
import kr.co.yna.www.salarynews.utils.network.TTSLoadAsyncTask
import kr.co.yna.www.salarynews.utils.recognition.GetPermission
import kr.co.yna.www.salarynews.utils.recognition.NaverRecognitionHandler
import kr.co.yna.www.salarynews.utils.recognition.NaverRecognizer
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONObject
import java.lang.ref.WeakReference

class HomeEndActivity : BaseActivity() {
    private var context: Context? = null

    private var recognizer: NaverRecognizer? = null
    private var authToken: String? = null
    private var audioPath: String? = null

    private var audioPlay: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_end)

        context = str2context(intent.getStringExtra("context"))
        setHomeEndActivityByThemeColor(this@HomeEndActivity, getThemeColor())

        volumeControlStream = AudioManager.STREAM_MUSIC
        val handler = NaverRecognitionHandler(this@HomeEndActivity)
        recognizer = NaverRecognizer(this@HomeEndActivity, handler, getString(R.string.naver_ClientId))

        recognizer?.initialize()

        authToken = getSaveSharedPreference().getAuthToken()
        audioPlay = MediaPlayer()
        startAudioPlay(audioPlay, intent.getStringExtra("audio_path"))

        initMicState(this@HomeEndActivity)

        audioPlay?.setOnCompletionListener {
            if (context == Context.CLOSING) {
                recognizer?.startRecognize(this@HomeEndActivity)
            }
        }

        home_end_exitButton.onClick {
            recognizer?.release()

            stopAudioPlay(audioPlay)
            val intent = Intent(this@HomeEndActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
//        Log.e(TAG, "Recognizer INIT")
//        recognizer?.initialize()
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
//        Log.e(TAG, "Recognizer STOPPED")
        // release() must called on stop time
//        recognizer?.release()
    }
    fun dialogflowQueryDetectIntent(context: HomeEndActivity, query: String) {
        val url = getString(R.string.app_server)

        val reqBody = JSONObject()
        reqBody.put("token", authToken)
        reqBody.put("query", query)
        Log.v(TAG, "reqBody: $reqBody")

        LoadAsyncTaskDialogflowQueryDetectIntent(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/dialogflow/query", "dialogflow", reqBody.toString())
    }
    fun ttsClova(context: HomeEndActivity, input_text: String) {
        val url = getString(R.string.naver_tts_url)
        LoadAsyncTaskTTS(context)
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url", input_text)
    }

    companion object {
        private val TAG = HomeEndActivity::class.java.simpleName

        class LoadAsyncTaskDialogflowQueryDetectIntent internal constructor(context: HomeEndActivity) : PostLoadAsyncTask(context) {
            private val activityReference: WeakReference<HomeEndActivity> = WeakReference(context)

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                activityReference.get()?.let { activity ->
                    result?.let {
                        try {
                            JSONObject(it).run {
                                getInt("statusCode").let { statusCode ->
                                    when (statusCode) {
                                        200 -> {
                                            getJSONObject("body").run {
                                                activity.context?.let { context ->
                                                    when (getString("action")) {
                                                        "Closing" -> {
                                                            Log.d(TAG, "context: $context, expect: Context.CLOSING")
                                                            activity.context = str2context(getString("output_context"))
                                                            activity.ttsClova(activity, getString("tts_text"))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        400 -> {
                                            if (JSONObject(getString("error")).getString("status").equals("INVALID_ARGUMENT")) {
                                                activity.startAudioPlay(activity.audioPlay, activity.audioPath as String)
                                            } else {
                                                Log.e(TAG, "statusCode: $statusCode\nerror: ${getString("error")}")
                                            }
                                        }
                                        else -> {
                                            Log.e(TAG, "statusCode: $statusCode\nerror: ${getString("error")}")
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception\nerror: $e")
                        }
                    }
                }
            }
        }
        class LoadAsyncTaskTTS internal constructor(context: HomeEndActivity): TTSLoadAsyncTask(context) {
            private val activityReference: WeakReference<HomeEndActivity> = WeakReference(context)

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                activityReference.get()?.let {activity ->
                    result?.let {
                        activity.startAudioPlay(activity.audioPlay, it)
                    }
                }
            }
        }
    }
}