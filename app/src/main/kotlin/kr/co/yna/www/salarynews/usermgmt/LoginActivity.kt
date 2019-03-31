package kr.co.yna.www.salarynews.usermgmt

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import kotlinx.android.synthetic.main.activity_login.*
import kr.co.yna.www.salarynews.BaseActivity
import kr.co.yna.www.salarynews.HomeActivity
import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.usermgmt.kakao.KakaoSessionCallback
import kr.co.yna.www.salarynews.utils.network.PostLoadAsyncTask
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class LoginActivity : BaseActivity() {
    private var callback: KakaoSessionCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d(TAG, "onCreate")
        checkSkipLoginProcess()

        login_kakao.onClick {
            callback = KakaoSessionCallback(this@LoginActivity)
            val session = Session.getCurrentSession()
            session.addCallback(callback)
//        session.checkAndImplicitOpen()

            session.open(AuthType.KAKAO_ACCOUNT, this@LoginActivity)
        }

        login_facebook.onClick {
            Toast.makeText(this@LoginActivity, "facebook login은 준비중입니다", Toast.LENGTH_LONG).show()
        }

        login_email.onClick {
            Log.d(TAG, "call EmailLoginActivity")

            val intent = Intent(this@LoginActivity, EmailLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkSkipLoginProcess() {
        getSaveSharedPreference().run {
            if (getSkipLogin()) {
                Log.d(TAG, "getLoginType: ${getLoginType()}")
                Log.d(TAG, "getEmailLoginOption: ${getEmailLoginOption()}")
                Log.d(TAG, "getUserEamil: ${getUserEmail()}")
                Log.d(TAG, "getUserPassword: ${getUserPassword()}")
                Log.d(TAG, "getUserId: ${getUserId()}")
                Log.d(TAG, "getUserName: ${getUserName()}")
                Log.d(TAG, "getUserCategory: ${getUserCategory()}")
                when (getLoginType()) {
                    "kakaotalk" -> {
                        Log.e(TAG, "Unexpected: kakaotalk")
                    }
                    "facebook" -> {
                        Log.e(TAG, "Unexpected: facebook")
                    }
                    "email" -> {
                        when (getEmailLoginOption()) {
                            "auto_login" -> {
                                nodeLoginUser(this@LoginActivity)
                            }
                            else -> {
                                Log.e(TAG, "Unexpected: email but not auto_login")
                            }
                        }
                    }
                    else -> {
                        Log.e(TAG, "Unexpected conditional branch")
                    }
                }
            }
        }
    }

    private fun nodeLoginUser(context: LoginActivity) {
        val url = getString(R.string.app_server)

        val reqBody = JSONObject()
        getSaveSharedPreference().run {
            reqBody.put("email", getUserEmail())
            reqBody.put("password", getUserPassword())
            Log.v(TAG, "reqBody: $reqBody")
        }
        LoadAsyncTaskNodeLoginUser(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/user/login", "node", reqBody.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        super.onDestroy()
        callback?.let {
            Session.getCurrentSession().removeCallback(it)
        }
    }
    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        class LoadAsyncTaskNodeLoginUser internal constructor(context: LoginActivity): PostLoadAsyncTask(context) {
            private val activityReference: WeakReference<LoginActivity> = WeakReference(context)

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                activityReference.get()?.let { activity ->
                    result?.let {
                        Log.d(TAG, "response: $it")
                        try {
                            JSONObject(it).run {
                                Log.w(TAG, "${JSONObject(it)}")
                                when (getString("ok")) {
                                    "1" -> {
                                        activity.getSaveSharedPreference().run {
                                            setUserId(getString("_id"))
                                            setUserName(getString("name"))
                                            setUserCategory(activity.slashCategories(getString("category")))
                                        }
                                        val intent = Intent(activity, HomeActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        activity.startActivity(intent)
                                    }
                                    "0" -> {
                                        Log.e("Fail SkipLogin", "email and password not correct")
                                    }
                                    else -> {
                                        Log.e(TAG, "unexpected conditional branch")
                                    }
                                }
                            }
                        } catch (e: JSONException) {
                            Log.e(TAG, "JSONException: $e")
                        }
                    } ?: Toast.makeText(activity, "서버가 응답하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
