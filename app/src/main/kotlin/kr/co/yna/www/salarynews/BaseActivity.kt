package kr.co.yna.www.salarynews

import android.content.Context
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kr.co.yna.www.salarynews.utils.SaveSharedPreference

abstract class BaseActivity : AppCompatActivity() {
	fun slashCategories(userCategory: String): String {
		var result = ""
		userCategory.let {
			result += "/${it[0]}"
			result += "/${it[1]}"
			result += "/${it[2]}"
			result += "/${it[3]}"
			result += "/${it[4]}"
			result += "/${it[5]}"
			result += "/${it[6]}"
			result += "/${it[7]}"
		}
		return result
	}
	fun lastBuildDateMsg(lastBuild: String): String {
		try {
			val month = lastBuild.substring(5, 7)
			val date = lastBuild.substring(8, 10)
			val hour = lastBuild.substring(11, 13)
			val minute = lastBuild.substring(14, 16)

			return "최근 업데이트: ${month}월 ${date}일 $hour:$minute"
		} catch (e: Exception) {
			Log.e("lastBuildDate", "error: $e")
		}
		return ""
	}

	fun startAudioPlay(audioPlay: MediaPlayer?, dataSource: String) {
		try {
			audioPlay?.run {
				reset()
				setDataSource(dataSource)
				prepare()
				start()
			}
		} catch (e: Exception) {
			Log.e("startAudioPlay", "TTS audioPlay error")
		}
	}
	fun stopAudioPlay(audioPlay: MediaPlayer?) {
		audioPlay?.let {
			if (it.isPlaying) {
				it.stop()
			}
		}
	}

	fun getSaveSharedPreference(): SaveSharedPreference {
		val appData = getSharedPreferences("appData", Context.MODE_PRIVATE)
		return SaveSharedPreference(appData)
	}
}