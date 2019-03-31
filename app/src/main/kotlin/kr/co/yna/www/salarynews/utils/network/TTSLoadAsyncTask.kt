package kr.co.yna.www.salarynews.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kr.co.yna.www.salarynews.R
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.*

open class TTSLoadAsyncTask(context: Context) : AsyncTask<String, Void, String?>() {
    private val activityReference: WeakReference<Context> = WeakReference(context)

    override fun onPreExecute() {
        super.onPreExecute()
        Log.d("TTSLoadAsyncTask", "TTS onPreExecute")

        activityReference.get()?.let { activity ->
            val networkInfo = (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(activity, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                cancel(true)
            }
            networkInfo?.let {
                if (!it.isConnected) {
                    Toast.makeText(activity, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                    cancel(true)
                }
                if (it.type != ConnectivityManager.TYPE_WIFI && it.type != ConnectivityManager.TYPE_MOBILE) {
                    Toast.makeText(activity, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                    cancel(true)
                }
            }
        }
    }

    override fun doInBackground(vararg params: String?): String? {
        var result: String? = null

        activityReference.get()?.let {activity ->
            val url = URL(params[0])
            try {
                val urlConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                urlConnection.requestMethod = "POST"

                val clientId = activity.resources?.getString(R.string.naver_ClientId)
                val clientSecret = activity.resources?.getString(R.string.naver_ClientSecret)

                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                urlConnection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId)
                urlConnection.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret)

                val encodedText = URLEncoder.encode(params[1], "UTF-8")
                val bodyParams = "speaker=mijin&speed=-1&text=$encodedText"

                val wr = DataOutputStream(urlConnection.outputStream)
                wr.writeBytes(bodyParams)
                wr.flush()
                wr.close()

                val responseCode = urlConnection.responseCode
                Log.v(TAG, "responseCode: $responseCode")
                val br: BufferedReader
                if (responseCode == 200) {
                    val iss: InputStream = urlConnection.inputStream
                    var read = 0
                    val bytes = ByteArray(1024)

                    val dir = File(Environment.getExternalStorageDirectory(), "SalaryNews")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }

                    result = "${Environment.getExternalStorageDirectory()}/SalaryNews/tts.mp3"
                    val file = File(result)

                    file.createNewFile()

                    val os: OutputStream = FileOutputStream(file)
                    while ({read = iss.read(bytes); read}() != -1) {
                        os.write(bytes, 0, read)
                    }
                    iss.close()
                } else {
                    val ess: InputStream = urlConnection.errorStream
                    val essReader = InputStreamReader(ess, "UTF-8")

                    br = BufferedReader(essReader)
                    val sb = StringBuilder()

                    var line: String? = null
                    while ({line = br.readLine(); line}() != null) {
                        sb.append(line)
                    }
                    br.close()

                    Log.e(TAG, "HTTP Error! error_msg: $sb")
                    result = sb.toString()
                }
            } catch (e: Exception) {
                Log.e(TAG, "doInBackground\nTTS $url")
            }
        }
        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        Log.d(TAG, "TTS onPostExecute\nresult:${result?.let { it }}")
    }
    companion object {
        private val TAG = TTSLoadAsyncTask::class.java.simpleName
    }
}