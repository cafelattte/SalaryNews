package kr.co.yna.www.salarynews

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_content.*
import kr.co.yna.www.salarynews.enum.getThemeColor
import kr.co.yna.www.salarynews.enum.setContent2PauseButton
import kr.co.yna.www.salarynews.enum.setContent2PlayButton
import kr.co.yna.www.salarynews.enum.setContentActivityByThemeColor
import kr.co.yna.www.salarynews.utils.network.GetLoadAsyncTask
import kr.co.yna.www.salarynews.utils.network.TTSLoadAsyncTask
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onSeekBarChangeListener
import org.json.JSONArray
import java.lang.ref.WeakReference

class ContentActivity : BaseActivity() {
	enum class State {PLAY, PAUSE}

	private var audioPlay: MediaPlayer? = null
	private var audioState: State = State.PAUSE
	private var audioPos: Int? = 0

	var user_category: String? = null
	var size = 0
	var round = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_content)
		Log.d(TAG, "onCreate")

		setContentActivityByThemeColor(this@ContentActivity, getThemeColor())

		audioPlay = MediaPlayer()
		audioState = State.PAUSE

		user_category = intent.getStringExtra("user_category")
		round = 1

		val newsObjectList = intent.getParcelableArrayListExtra<NewsObject>("newsObjectList")
		size = newsObjectList.size

		newsObjectList[0].run {
			news_index.text = index.toString()
			news_title.text = title
			image.let {
				if (it.isNotEmpty()) {
					Glide.with(this@ContentActivity)
						.load(it)
						.apply(RequestOptions.fitCenterTransform())
						.into(news_image)
					news_image.visibility = View.VISIBLE
				} else {
					news_image.visibility = View.INVISIBLE
				}
			}
		}
		if (intent.hasExtra("tts_text")) {
			val ttsText = intent.getStringExtra("tts_text")
			ttsClova(this@ContentActivity, ttsText, false)
		} else {
			val index = newsObjectList[0].index
			round += 1
			newsGetPreference(this@ContentActivity, index-1)
		}

		content_seekBar.progress = 0
		content_seekBar.onSeekBarChangeListener {
			onProgressChanged { _, progress, fromUser ->
				if (fromUser) {
					audioPlay?.seekTo(progress)
				}
			}
		}

		audioPlay?.setOnCompletionListener {
			if (size < round) {
				val intent = Intent()
				setResult(RESULT_OK, intent)
				finish()
			} else {
				newsObjectList[round-1].run {
					news_index.text = index.toString()
					news_title.text = title
					image.let {
						if (it.isNotEmpty()) {
							Glide.with(this@ContentActivity)
								.load(it)
								.apply(RequestOptions.fitCenterTransform())
								.into(news_image)
							news_image.visibility = View.VISIBLE
						} else {
							news_image.visibility = View.INVISIBLE
						}
					}
					round += 1
					newsGetPreference(this@ContentActivity, index-1)
				}
			}
		}

		content_play_pause_button.onClick {
			if (round != 1) {
				when (audioState) {
					State.PLAY -> {
						setAudioPuaseMode()
						audioPos = audioPlay?.currentPosition
						audioPlay?.pause()
					}
					State.PAUSE -> {
						setAudioPlayMode()
						audioPlay?.run {
							seekTo(audioPos as Int)
							start()
							Thread(Runnable {
								while (isPlaying) {
									try {
										Thread.sleep(500)
									} catch (e: Exception) {
										Log.e(TAG, "SeekBar error: $e")
									}
									content_seekBar.progress = currentPosition
								}
							}).start()
						}
					}
				}
			}
		}
		content_forward_button.onClick {
			Toast.makeText(this@ContentActivity, "아직 지원하지 않는 기능입니다", Toast.LENGTH_SHORT).show()
		}
		content_backward_button.onClick {
			Toast.makeText(this@ContentActivity, "아직 지원하지 않는 기능입니다", Toast.LENGTH_SHORT).show()
		}
	}

	override fun onBackPressed() {
		val intent = Intent()
		setResult(RESULT_OK, intent)
		finish()
	}

	private fun setAudioPlayMode() {
		if (audioState == State.PAUSE) {
			setContent2PauseButton(
				this@ContentActivity,
				getThemeColor()
			)
//			content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_pause))
			audioState = State.PLAY
		}
	}
	private fun setAudioPuaseMode() {
		if (audioState == State.PLAY) {
			setContent2PlayButton(
				this@ContentActivity,
				getThemeColor()
			)
//			content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play))
			audioState = State.PAUSE
		}
	}
	private fun newsGetPreference(context: ContentActivity, idx: Int) {
		val url = getString(R.string.newsDB_server)
		LoadAsyncTaskNewsGetPreference(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/preference$user_category/$idx", "newsDB")
	}
	private fun ttsClova(context: ContentActivity, input_text: String, useSeekBar: Boolean) {
		val url = getString(R.string.naver_tts_url)
		LoadAsyncTaskTTS(context, useSeekBar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url", input_text)
	}

	companion object {
		private val TAG = ContentActivity::class.java.simpleName
		class LoadAsyncTaskNewsGetPreference internal constructor(context: ContentActivity) : GetLoadAsyncTask(context) {
			private val activityReference: WeakReference<ContentActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						JSONArray(it).run {
							getJSONObject(0).run {
								val scripts = getString("scripts")
								val index = activity.news_index.text
								activity.ttsClova(activity, "${index}번째 뉴스입니다!!\n\n$scripts", true)
							}
						}
					}
 				}
			}
		}
		class LoadAsyncTaskTTS internal constructor(context: ContentActivity, private val useSeekBar: Boolean): TTSLoadAsyncTask(context) {
			private val activityReference: WeakReference<ContentActivity> = WeakReference(context)

			override fun onPostExecute(result: String?) {
				super.onPostExecute(result)
				activityReference.get()?.let { activity ->
					result?.let {
						try {
							activity.audioPlay?.run {
								reset()
								setDataSource(activity, Uri.parse(it))
								prepare()
								isLooping = false
								start()
								activity.setAudioPlayMode()

								if (useSeekBar) {
									activity.content_seekBar.max = duration
									activity.content_seekBar.progress = 0
									Thread(Runnable {
										while (isPlaying) {
											try {
												Thread.sleep(500)
											} catch (e: Exception) {
												Log.e(TAG, "SeekBar error: $e")
											}
											activity.content_seekBar.progress = currentPosition
										}
									}).start()
								}
							}
						} catch (e: Exception) {
							Log.e(TAG, "TTS audioPlay error")
							e.printStackTrace()
						}
					}
				}
			}
		}
	}
}