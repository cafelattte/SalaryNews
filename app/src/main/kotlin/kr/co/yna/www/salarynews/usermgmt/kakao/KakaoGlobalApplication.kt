package kr.co.yna.www.salarynews.usermgmt.kakao

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.util.LruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.kakao.auth.KakaoSDK

class KakaoGlobalApplication : Application(){
	companion object {
		private val TAG = KakaoGlobalApplication::class.java.simpleName

		private var instance: KakaoGlobalApplication? = null
		private var imageLoader: ImageLoader? = null

		fun getGlobalApplicationContext() : KakaoGlobalApplication {
			Log.e(TAG, "getGlobalApplicationContext")

			return instance
					?: throw IllegalStateException("This Application does not inherit com.kakao.KakaoGlobalApplication")
		}
	}

	override fun onCreate() {
		Log.e(TAG, "onCreate")
		super.onCreate()
		instance = this

		KakaoSDK.init(KakaoSDKAdapter())

		val requestQueue: RequestQueue = Volley.newRequestQueue(this)

		val imageCache: ImageLoader.ImageCache = object : ImageLoader.ImageCache {
			val imageCache: LruCache<String, Bitmap> = LruCache(30)

			override fun putBitmap(url: String?, bitmap: Bitmap?) {
				imageCache.put(url, bitmap)
			}

			override fun getBitmap(url: String?): Bitmap {
				return imageCache.get(url)
			}
		}

		imageLoader = ImageLoader(requestQueue, imageCache)

		createNotificationChannel()

	}

	fun getImageLoader(): ImageLoader? {
		return imageLoader
	}

	override fun onTerminate() {
		super.onTerminate()
		instance = null
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val nm: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

			val channelId = "kakao_push_channel"
			val channelName = "Kakao SDK Push"
			val channel: NotificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
				enableLights(true)
				lightColor = Color.RED
				enableVibration(true)
			}
			nm.createNotificationChannel(channel)
			Log.d("KakaoGlobalApplication", "createNotificationChannel success")
 		}
	}
}