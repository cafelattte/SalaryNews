package kr.co.yna.www.salarynews.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import kr.co.yna.www.salarynews.R
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.net.URL
import javax.net.ssl.*
import java.io.*
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

open class PostLoadAsyncTask(context: Context) : AsyncTask<String, Void, String?>() {
    private val activityReference: WeakReference<Context> = WeakReference(context)

    override fun onPreExecute() {
        super.onPreExecute()
        Log.d("PostLoadAsyncTask", "POST onPreExecute")

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

                val hostnameVerifier = object : HostnameVerifier {
                    override fun verify(hostname: String?, session: SSLSession?): Boolean {
                        return true
                        /* when using verified HostName CA
                        val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                        return hv.verify(activity?.getString(R.string.user_server_ip_https), session)
                        */
                    }
                }
                urlConnection.hostnameVerifier = hostnameVerifier
                activity.let {
                    urlConnection.sslSocketFactory =
                        getPinnedCertSslSocketFactory(
                            it
                        )
                }

                when(params[1]) {
                    "node" -> {
                        urlConnection.setRequestProperty("Content-Type", "application/json")
                    }
                    "dialogflow" -> {
                        urlConnection.setRequestProperty("Content-Type", "application/json")
                        urlConnection.setRequestProperty("charset", "utf-8")
                        urlConnection.setRequestProperty("Authorization", "token")
                    }
                    else -> {
                        throw IllegalArgumentException("invalid argument, params[1] should be \"node\" or \"dialogflow\"")
                    }
                }

                // add request Body in POST REST API
                val writer = OutputStreamWriter(urlConnection.outputStream, "UTF-8")

                writer.write(params[2])
                writer.flush()
                writer.close()

                // get response from server
                val responseCode = urlConnection.responseCode
                Log.v(TAG, "responseCode: $responseCode")
                val br: BufferedReader
                if (responseCode == 200) {
                    val iss: InputStream = urlConnection.inputStream
                    val issReader = InputStreamReader(iss, "UTF-8")

                    br = BufferedReader(issReader)
                    val sb = StringBuilder()

                    var line: String? = null
                    while ({line = br.readLine(); line}() != null) {
                        sb.append(line)
                    }
                    br.close()

                    Log.d(TAG, "HTTPS_OK, result: $sb")
                    result = sb.toString()
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

                    Log.e(TAG, "HTTPS Error! error_msg: $sb")
                    result = sb.toString()
                }
            } catch (e: Exception) {
                Log.e(TAG,"doInBackground\nPOST $url\nerror: $e")
            }
        }
        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        Log.d(TAG, "POST onPostExecute\nresult: ${result?.let { it }}")
    }

    companion object {
        private val TAG = PostLoadAsyncTask::class.java.simpleName
        fun getPinnedCertSslSocketFactory(context: Context): SSLSocketFactory? {
            try {
                val cf = CertificateFactory.getInstance("X.509")
                val caInput = context.resources.openRawResource(R.raw.appserver)
                var ca: Certificate? = null
                try {
                    ca = cf.generateCertificate(caInput)
                    Log.v(TAG, "ca=${(ca as X509Certificate).subjectDN}")
                } catch (e: CertificateException) {
                    Log.e(TAG, "CertificateException\nerror: $e")
                } finally {
                    caInput.close()
                }

                val keyStoreType = KeyStore.getDefaultType()
                val keyStore = KeyStore.getInstance(keyStoreType)
                keyStore.load(null, null)
                ca ?.let {
                    keyStore.setCertificateEntry("ca", it)

                    val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                    val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
                    tmf.init(keyStore)

                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, tmf.trustManagers, null)

                    return sslContext.socketFactory
                } ?: return null
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "e: NoSuchAlgorithmException\nerror: $e")
            } catch (e: IOException) {
                Log.e(TAG, "e: IOException\nerror: $e")
            } catch (e: KeyStoreException) {
                Log.e(TAG, "e: KeyStoreException\nerror: $e")
            } catch (e: KeyManagementException) {
                Log.e(TAG, "e: KeyMangementException\nerror: $e")
            } catch (e: Exception) {
                Log.e(TAG, "e: Exception\nerror: $e")
            }
            return null
        }
    }
}