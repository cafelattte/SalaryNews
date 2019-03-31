package kr.co.yna.www.salarynews

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.yna.www.salarynews.enum.*
import kr.co.yna.www.salarynews.utils.network.GetLoadAsyncTask
import kr.co.yna.www.salarynews.utils.network.PostLoadAsyncTask
import kr.co.yna.www.salarynews.utils.network.TTSLoadAsyncTask
import kr.co.yna.www.salarynews.utils.recognition.NaverRecognitionHandler
import kr.co.yna.www.salarynews.utils.recognition.NaverRecognizer
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.ArrayList


class MainActivity : BaseActivity() {
	enum class NewsCallType {BUTTON, AUDIO}

	private var context: Context? = null
	private var recognizer: NaverRecognizer? = null
	private var authToken: String? = null

	private var audioPlay: MediaPlayer? = null

	private var phase = 0
	private val final_phase = 3
	private var newsList: JSONArray? = null
	private var arr: ArrayList<Int> = ArrayList()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		intent.getStringExtra("lastBuildDate").let { lastBuildDate ->
			main_last_build.text = lastBuildDateMsg(lastBuildDate)
		}
		intent.getStringExtra("strNewsList").let {
			try {
				newsList = JSONArray(it)
			} catch (e: JSONException) {
				Log.e(TAG, "JSONException: $e")
			}
		}

		phase = 0
		context = str2context(intent.getStringExtra("context"))
		setMainActivityByThemeColor(this@MainActivity, getThemeColor())
		fillNewsBoard(newsList, 1)

		volumeControlStream = AudioManager.STREAM_MUSIC
		val handler = NaverRecognitionHandler(this@MainActivity)
		recognizer = NaverRecognizer(this@MainActivity, handler, getString(R.string.naver_ClientId))

		recognizer?.initialize()

		authToken = getSaveSharedPreference().getAuthToken()
		audioPlay = MediaPlayer()
		startAudioPlay(audioPlay, intent.getStringExtra("audio_path"))

		initMicState(this@MainActivity)

		audioPlay?.setOnCompletionListener {
			if (phase == 0) {
				phase += 1
				ttsClova(this@MainActivity, newsSelectText(phase, newsList))
			} else {
				recognizer?.startRecognize(this@MainActivity)
			}
		}

		news_1.onClick {
			stopAudioPlay(audioPlay)
			try {
				val intent = Intent(this@MainActivity, ContentActivity::class.java)
				intent.putExtra("user_category", getSaveSharedPreference().getUserCategory())

				val newsObjectList = arrayListOf<NewsObject>()
				if (phase == 0) {
					phase += 1
				}
				newsList?.getJSONObject(3*phase-1) ?.run {
					newsObjectList.add(NewsObject(3*phase, getString("titles"), getString("imagelinks")))
				}
				intent.putExtra("newsObjectList", newsObjectList)

				startActivityForResult(intent, NewsCallType.BUTTON.ordinal)
			} catch (e: Exception) {
				Log.e(TAG, "news_1 click\nerror: $e")
			}
		}
		news_2.onClick {
			stopAudioPlay(audioPlay)
			try {
				val intent = Intent(this@MainActivity, ContentActivity::class.java)
				intent.putExtra("user_category", getSaveSharedPreference().getUserCategory())

				val newsObjectList = arrayListOf<NewsObject>()
				if (phase == 0) {
					phase += 1
				}
				newsList?.getJSONObject(3*phase-2) ?.run {
					newsObjectList.add(NewsObject(3*phase-1, getString("titles"), getString("imagelinks")))
				}
				intent.putExtra("newsObjectList", newsObjectList)

				startActivityForResult(intent, NewsCallType.BUTTON.ordinal)
			} catch (e: Exception) {
				Log.e(TAG, "news_2 click\nerror: $e")
			}
		}
		news_3.onClick {
			stopAudioPlay(audioPlay)
			try {
				val intent = Intent(this@MainActivity, ContentActivity::class.java)
				intent.putExtra("user_category", getSaveSharedPreference().getUserCategory())

				val newsObjectList = arrayListOf<NewsObject>()
				if (phase == 0) {
					phase += 1
				}
				newsList?.getJSONObject(3*phase-1) ?.run {
					newsObjectList.add(NewsObject(3*phase, getString("titles"), getString("imagelinks")))
				}
				intent.putExtra("newsObjectList", newsObjectList)

				startActivityForResult(intent, NewsCallType.BUTTON.ordinal)
			} catch (e: Exception) {
				Log.e(TAG, "news_3 click\nerror: $e")
			}
		}
	}

	override fun onStart() {
		super.onStart()
//		Log.e(TAG, "onStart")
		// initialize() must called on start time.
//		GetPermission().getAudioRecordPermission(this@MainActivity)
//		Log.e(TAG, "Recognizer INIT")
//		recognizer?.initialize()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (resultCode == RESULT_OK) {
			when (requestCode) {
				NewsCallType.AUDIO.ordinal -> {
					fillNewsBoard(newsList, phase)
					dialogflowQueryDetectIntent(this@MainActivity, "뉴스종료")
				}
				NewsCallType.BUTTON.ordinal -> {
					ttsClova(this@MainActivity, newsSelectText(phase, newsList))
				}
			}
		}
	}

	private fun selectedIndex(arr: ArrayList<Int>): String {
		return when (arr.size) {
			1-> "${arr[0]}번째"
			2 -> "${arr[0]}번째, ${arr[1]}번째"
			3 -> "${arr[0]}번째, ${arr[1]}번째, ${arr[2]}번째"
			else -> {
				Log.e(TAG, "unexpected conditional branch, arr.size: ${arr.size}")
				""
			}
		}
	}
	private fun newsSelectText(phase: Int, newsList: JSONArray?): String {
		var first: String? = null
		newsList?.getJSONObject(3*phase-3)?.run {
			first = "${3*phase-2}번 뉴스: ${getString("titles")}.\n"
		}
		var second: String? = null
		newsList?.getJSONObject(3*phase-2)?.run {
			second = "${3*phase-1}번 뉴스: ${getString("titles")}.\n"
		}
		var third: String? = null
		newsList?.getJSONObject(3*phase-1)?.run {
			third = "${3*phase}번 뉴스: ${getString("titles")}.\n"
		}
		return "$first$second$third"
	}
	private fun checkNewsIndex(phase: Int, arr: ArrayList<Int>): Boolean {
		val phaseArray: ArrayList<Int> = arrayListOf(3*phase-2, 3*phase-1, 3*phase)
		for (item in arr) {
			if (!(phaseArray.contains(item))) {
				return false
			}
		}
		return true
	}
	private fun fillNewsBoard(newsList: JSONArray?, phase: Int) {
		try {
		    newsList?.getJSONObject(3*phase-3) ?.run {
				news_1_index.text = (3*phase-2).toString()
				news_1_title.text = getString("titles")
			}
			newsList?.getJSONObject(3*phase-2) ?.run {
				news_2_index.text = (3*phase-1).toString()
				news_2_title.text = getString("titles")
			}
			newsList?.getJSONObject(3*phase-1) ?.run {
				news_3_index.text = (3*phase).toString()
				news_3_title.text = getString("titles")
			}
		} catch (e: JSONException) {
			Log.e(TAG, "JSONException: $e")
		}
	}
	private fun newsGetThumbnail(context: MainActivity) {
		val url = getString(R.string.newsDB_server)
        LoadAsyncTaskNewsGetThumbnail(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/thumbnail${getSaveSharedPreference().getUserCategory()}", "newsDB")
	}
	fun dialogflowQueryDetectIntent(context: MainActivity, query: String) {
		val url = getString(R.string.app_server)

		val reqBody = JSONObject()
		reqBody.put("token", authToken)
		reqBody.put("query", query)
		Log.v(TAG, "reqBody: $reqBody")

		LoadAsyncTaskDialogflowQueryDetectIntent(context)
			.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/dialogflow/query", "dialogflow", reqBody.toString())
	}
	fun ttsClova(context: MainActivity, input_text: String) {
		val url = getString(R.string.naver_tts_url)
		LoadAsyncTaskTTS(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url", input_text)
	}

	companion object {
		private val TAG = MainActivity::class.java.simpleName

		class LoadAsyncTaskNewsGetThumbnail internal constructor(context: MainActivity) : GetLoadAsyncTask(context) {
			private val activityReference: WeakReference<MainActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						try {
							activity.newsList = JSONArray(it)
							val lastBuildDate = JSONArray(it).getJSONObject(0).getString("lastBuildDate")

							activity.main_last_build.text = activity.lastBuildDateMsg(lastBuildDate)
						} catch (e: Exception) {
							Log.e(TAG, "JSONException\n e: $e")
						}
					} ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}

		class LoadAsyncTaskDialogflowQueryDetectIntent internal constructor(context: MainActivity): PostLoadAsyncTask(context) {
			private val activityReference: WeakReference<MainActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let {activity ->
					result?.let {
						try {
							JSONObject(it).run {
								getInt("statusCode").let { statusCode ->
									when (statusCode) {
										200 -> {
											getJSONObject("body").run {
												activity.context?.let { context ->
													when (getString("action")) {
														"SelectNumber_number" -> {
															Log.d(TAG, "context: $context, expect: Context.SALARYNEWS")
															activity.context = str2context(getString("output_context"))
															getJSONObject("parameters").run {
																activity.arr = ArrayList()
																getJSONArray("number").let { numList ->
																	if (numList.length() != 0) {
																		for (i in 0..(numList.length()-1)) {
																			(numList[i] as String).toInt()
																			activity.arr.add(Integer.parseInt(numList[i] as String))
//																			activity.arr.add(numList[i] as Int)
																		}
																	}
																}
																getJSONArray("number_ko").let { numkoList ->
																	if (numkoList.length() != 0) {
																		for (i in 0..(numkoList.length()-1)) {
																			activity.arr.add(Integer.parseInt(numkoList[i] as String))
//																			activity.arr.add(numkoList[i] as Int)
																		}
																	}
																}
															}
															if (activity.checkNewsIndex(activity.phase, activity.arr)) {
																activity.dialogflowQueryDetectIntent(activity, "승인")
															} else {
																activity.dialogflowQueryDetectIntent(activity, "거절")
															}
														}
														"SelectNumber_all" -> {
															Log.d(TAG, "context: $context, expect: Context.SALARYNEWS")
															activity.context = str2context(getString("output_context"))

															val phase = activity.phase
															activity.arr = arrayListOf(3*phase-2, 3*phase-1, 3*phase)

															val intent = Intent(activity, ContentActivity::class.java)
															intent.putExtra("user_category", activity.getSaveSharedPreference().getUserCategory())

															val newsObjectList = arrayListOf<NewsObject>()
															for (item in activity.arr) {
																activity.newsList?.getJSONObject(item-1)?.run {
																	newsObjectList.add(NewsObject(item, getString("titles"), getString("imagelinks")))
																}
															}
															intent.putExtra("newsObjectList", newsObjectList)

															intent.putExtra("tts_text", "모두 고르셨군요! 지금 바로 들려드릴게요.")
															activity.startActivityForResult(intent, NewsCallType.AUDIO.ordinal)
														}
														"SelectNumber_next" -> {
															Log.d(TAG, "context: $context, expect: Context.SALARYNEWS")
															activity.context = str2context(getString("output_context"))
															if (activity.final_phase == activity.phase) {
																activity.dialogflowQueryDetectIntent(activity, "엔드")
															} else {
																activity.phase += 1
																activity.fillNewsBoard(activity.newsList, activity.phase)
																activity.ttsClova(activity, "${activity.getString(R.string.select_next_msg)}\n${activity.newsSelectText(activity.phase, activity.newsList)}")
															}
														}
														"SelectNumber_closing" -> {
															Log.d(TAG, "context: $context, expect: Context.SALARYNEWS")
															activity.context = str2context(getString("output_context"))
															activity.ttsClova(activity, getString("tts_text"))
														}
														"SelectNumber_fallback" -> {
															Log.d(TAG, "context: $context, expect: Context.SALARYNEWS")
															activity.context = str2context(getString("output_context"))
															activity.ttsClova(activity, activity.getString(R.string.mic_again_msg))
														}
														"SelectNumberConfirm" -> {
															Log.d(TAG, "context: $context, expect: Context.SELECT_NUMBER")
															activity.context = str2context(getString("output_context"))

															val intent = Intent(activity, ContentActivity::class.java)
															intent.putExtra("user_category", activity.getSaveSharedPreference().getUserCategory())

															val newsObjectList = arrayListOf<NewsObject>()
															for (item in activity.arr) {
																activity.newsList?.getJSONObject(item-1)?.run {
																	newsObjectList.add(NewsObject(item, getString("titles"), getString("imagelinks")))
																}
															}
															intent.putExtra("newsObjectList", newsObjectList)

															val selected = activity.selectedIndex(activity.arr)
															intent.putExtra("tts_text", "네. 선택하신 $selected 뉴스를 들려드릴게요.")
															activity.startActivityForResult(intent, NewsCallType.AUDIO.ordinal)
														}
														"SelectNumberRefuse" -> {
															Log.d(TAG, "context: $context, expect: Context.SELECT_NUMBER")
															activity.context = str2context(getString("output_context"))
															activity.ttsClova(activity, "${activity.getString(R.string.wrong_news_index_msg)}\n${activity.newsSelectText(activity.phase, activity.newsList)}")
														}
														"SpeakConfirmOrRefuse" -> {
															Log.d(TAG, "context: $context, expect: Context.SELECT_NUMBER")
															activity.context = str2context(getString("output_context"))
															Log.e(TAG, "unexpected conditional branch: $context")
														}
														"BacktoMainActivity" -> {
															Log.d(TAG, "context: $context, expect: Context.SELECT_NUMBER_PLAYING")
															activity.context = str2context(getString("output_context"))

															if (activity.final_phase == activity.phase) {
																activity.dialogflowQueryDetectIntent(activity, "엔드")
															} else {
																activity.phase += 1
																activity.fillNewsBoard(activity.newsList, activity.phase)
																activity.ttsClova(activity, activity.newsSelectText(activity.phase, activity.newsList))
															}
														}
														else -> {
															Log.d(TAG, "context: $context")
															Log.e(TAG, "unexpected conditional branch, intent: ${getString("intent")}")
														}
													}
												}
											}
										}
										400 -> {
											if (JSONObject(getString("error")).getString("status").equals("INVALID_ARGUMENT")) {
												activity.ttsClova(activity, activity.getString(R.string.no_input_msg))
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
					} ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}
		class LoadAsyncTaskTTS internal constructor(context: MainActivity): TTSLoadAsyncTask(context) {
			private val activityReference: WeakReference<MainActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						activity.context?.let { context ->
							Log.d(TAG, "context: $context")
							when (context) {
								Context.SALARYNEWS -> {
									activity.startAudioPlay(activity.audioPlay, it)
								}
								Context.CLOSING -> {
									val intent = Intent(activity, HomeEndActivity::class.java)
									intent.putExtra("audio_path", it)
									intent.putExtra("context", context2str(context))

									Log.e(TAG, "Recognizer STOPPED")
									// release() must called on stop time
									activity.recognizer?.release()
									activity.startActivity(intent)
								}
								else -> {

								}
							}
						}
					} ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}
}