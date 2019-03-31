package kr.co.yna.www.salarynews

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_main.*
import kotlinx.android.synthetic.main.app_bar_home.*
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

class HomeActivity : BaseActivity() {
	private var isReady = false
	private var context: Context? = null

	private var recognizer: NaverRecognizer? = null
	private var audioPlay: MediaPlayer? = null

	private var isThumbnailReady = false
	private var strNewsList: String? = null
	private var lastBuildDate: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home_main)

		isReady = false
		isThumbnailReady = false
		context = Context.INACTIVE

		volumeControlStream = AudioManager.STREAM_MUSIC
		val handler = NaverRecognitionHandler(this@HomeActivity)
		recognizer = NaverRecognizer(this@HomeActivity, handler, getString(R.string.naver_ClientId))
		recognizer?.initialize()

		audioPlay = MediaPlayer()
		initMicState(this@HomeActivity)

		getAuthToken(this@HomeActivity)

		getSaveSharedPreference().run {
			nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_bar_userName).text = getUserName()
			nav_view.getHeaderView(0).findViewById<TextView>(R.id.nav_bar_userEmail).text = getUserEmail()
		}

		setSupportActionBar(toolbar)

		home_mic.onClick {
			if (isReady && isThumbnailReady) {
				stopAudioPlay(audioPlay)
				recognizer?.startRecognize(this@HomeActivity)
			}
		}
		home_start_person.onClick {
			if (isReady && isThumbnailReady) {
				stopAudioPlay(audioPlay)
				dialogflowQueryDetectIntent(this@HomeActivity, "샐러리뉴스")
			}
		}

		val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener {
			when (it.itemId) {
				R.id.nav_myinfo -> {
					Toast.makeText(this@HomeActivity, "아직 지원하지 않는 기능입니다.", Toast.LENGTH_SHORT).show()
				}
				R.id.nav_alarm -> {
					Toast.makeText(this@HomeActivity, "아직 지원하지 않는 기능입니다.", Toast.LENGTH_SHORT).show()
				}
				R.id.nav_category -> {
					Toast.makeText(this@HomeActivity, "아직 지원하지 않는 기능입니다.", Toast.LENGTH_SHORT).show()
				}
				R.id.nav_theme -> {
					val intent = Intent(this@HomeActivity, NavThemeSetting::class.java)
					startActivity(intent)
				}
				else -> { }
			}
			drawer_layout.closeDrawer(GravityCompat.START)
			true
		}
	}

	override fun onBackPressed() {
		if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
			drawer_layout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.activity_navigate_home_drawer, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		Toast.makeText(this@HomeActivity, "${item?.itemId}", Toast.LENGTH_SHORT).show()

		return super.onOptionsItemSelected(item)
	}


	override fun onResume() {
		setHomeActivityByThemeColor(this@HomeActivity, getThemeColor())
		super.onResume()
	}
	override fun onStop() {
		Log.e(TAG, "onStop")
		super.onStop()
	}

	private fun newsGetThumbnail(context: HomeActivity) {
		val url = getString(R.string.newsDB_server)
		LoadAsyncTaskNewsGetThumbnail(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/thumbnail${getSaveSharedPreference().getUserCategory()}", "newsDB")
	}
	private fun getAuthToken(context: HomeActivity) {
		val url = getString(R.string.app_server)
		LoadAsyncTaskDialogflowGetAuthToken(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/dialogflow/auth_token", "app")
	}
	fun dialogflowQueryDetectIntent(context: HomeActivity, input_query: String) {
		val url = getString(R.string.app_server)

		val reqBody = JSONObject()
		reqBody.put("token", getSaveSharedPreference().getAuthToken())
		reqBody.put("query", input_query)
		Log.v(TAG, "reqBody: $reqBody")

		LoadAsyncTaskDialogflowQueryDetectIntent(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/dialogflow/query", "dialogflow", reqBody.toString())
	}
	private fun ttsClova(context: HomeActivity, input_text: String, nextActivity: Boolean) {
		val url = getString(R.string.naver_tts_url)
		LoadAsyncTaskTTS(context, nextActivity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url", input_text)
	}

	companion object {
		private val TAG = HomeActivity::class.java.simpleName

		class LoadAsyncTaskNewsGetThumbnail internal constructor(context: HomeActivity) : GetLoadAsyncTask(context) {
			private val activityReference: WeakReference<HomeActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						try {
							activity.isThumbnailReady = true
							activity.strNewsList = it
							activity.lastBuildDate = JSONArray(it).getJSONObject(0).getString("lastBuildDate")
						} catch (e: Exception) {
							Log.e(TAG, "JSONException\n e: $e")
						}
					} ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}

		class LoadAsyncTaskDialogflowGetAuthToken internal constructor(context: HomeActivity): GetLoadAsyncTask(context) {
			private val activityReference: WeakReference<HomeActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)

				activityReference.get()?.let { activity ->
					result?.let {
						try {
							val authJSON = JSONObject(it)
							val authToken = authJSON.getString("auth_token")
							Log.d(TAG, "auth_token: $authToken")
							activity.getSaveSharedPreference().setAuthToken(authToken)

							activity.dialogflowQueryDetectIntent(activity, activity.getString(R.string.dialogflow_reserved_start))
						} catch (e: JSONException) {
							Log.e(TAG, "JSONException: ", e)
						}
					} ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}

		class LoadAsyncTaskDialogflowQueryDetectIntent internal constructor(context: HomeActivity): PostLoadAsyncTask(context) {
			private val activityReference: WeakReference<HomeActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						try {
							JSONObject(it).run {
								getInt("statusCode").let {statusCode ->
									when(statusCode) {
										200 -> {
											getJSONObject("body").run {
												activity.context?.let { context ->
													when(getString("action")) {
														"Start" -> {
															Log.d(TAG, "context: $context, expect: Context.INACTIVE")
															activity.isReady = true
															activity.context = str2context(getString("output_context"))
															activity.newsGetThumbnail(activity)
														}
														"DefaultFallback" -> {
															Log.d(TAG, "context: $context, expect: Context.INACTIVE")
															activity.context = str2context(getString("output_context"))
															Log.e(TAG, "unexpected queryText, expect: 스타트")
														}
														"SalaryNews" -> {
															Log.d(TAG, "context: $context, expect: Context.START")
															activity.context = str2context(getString("output_context"))
															activity.ttsClova(activity, getString("tts_text"), true)
														}
														"SpeakSalaryNewsAgain" -> {
															Log.d(TAG, "context: $context, expect: Context.START")
															activity.context = str2context(getString("output_context"))
															activity.ttsClova(activity, getString("tts_text"), false)
														}
														else -> {}
													}
												}
											}
										}
										400 -> {
											if (JSONObject(getString("error")).getString("status").equals("INVALID_ARGUMENT")) {
												activity.context?.let { context ->
													if (context != Context.INACTIVE) {
														activity.ttsClova(activity, activity.getString(R.string.no_input_msg), false)
													}
												}
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
		class LoadAsyncTaskTTS internal constructor(context: HomeActivity, private val nextActivity: Boolean): TTSLoadAsyncTask(context) {
			private val activityReference: WeakReference<HomeActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						if (nextActivity) {
							val intent = Intent(activity, MainActivity::class.java)
							intent.putExtra("audio_path", it)
							activity.context?.let { context ->
								intent.putExtra("context", context2str(context))
							}
							intent.putExtra("strNewsList", activity.strNewsList)
							intent.putExtra("lastBuildDate", activity.lastBuildDate)

							activity.recognizer?.release()

							activity.startActivity(intent)
						} else {
							activity.startAudioPlay(activity.audioPlay, it)
						}
					} ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}
}