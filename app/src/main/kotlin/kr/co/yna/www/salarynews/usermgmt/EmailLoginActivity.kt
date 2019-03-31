package kr.co.yna.www.salarynews.usermgmt

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_email_login.*
import kr.co.yna.www.salarynews.BaseActivity
import kr.co.yna.www.salarynews.HomeActivity
import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.utils.network.PostLoadAsyncTask
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.lang.ref.WeakReference

class EmailLoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)

        getSaveSharedPreference().let {
            when(it.getEmailLoginOption()) {
                "auto_login" -> {
                    autoLogin_checkBox.isChecked = true
                    saveId_checkBox.isChecked = true
                    email_login_id.setText(it.getUserEmail())
                    email_login_password.setText(it.getUserPassword())
                    nodeLoginUser(this@EmailLoginActivity)
                }
                "save_id" -> {
                    saveId_checkBox.isChecked = true
                    email_login_id.setText(it.getUserEmail())
                }
                else -> {
                    Log.e(TAG, "unexpected conditional branch")
                }
            }
        }

        saveId.onClick {
            saveId_checkBox.isChecked = saveId_checkBox.isChecked.not()
        }
        autoLogin.onClick {
            autoLogin_checkBox.isChecked = autoLogin_checkBox.isChecked.not()
        }

        login_button.onClick {
            Log.v(TAG, "login_button clicked")

            nodeLoginUser(this@EmailLoginActivity)
        }

        signup_text.onClick {
            val intent = Intent(this@EmailLoginActivity, SignupEmailActivity::class.java)
            startActivity(intent)
        }
    }
    private fun nodeLoginUser(context: EmailLoginActivity) {
        val url = getString(R.string.app_server)

        val reqBody = JSONObject()
        reqBody.put("email", email_login_id.text.toString())
        reqBody.put("password", email_login_password.text.toString())
        Log.v(TAG, "reqBody: $reqBody")

        LoadAsyncTaskNodeLoginUser(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/user/login", "node", reqBody.toString())
    }

    companion object {
        private val TAG = EmailLoginActivity::class.java.simpleName
        class LoadAsyncTaskNodeLoginUser internal constructor(context: EmailLoginActivity): PostLoadAsyncTask(context) {
            private val activityReference: WeakReference<EmailLoginActivity> = WeakReference(context)

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                activityReference.get()?.let {activity ->
                    result?.let {
                        Log.d(TAG, "response: $it")
                        try {
                            JSONObject(it).run {
                                Log.w(TAG, "${JSONObject(it)}")
                                when (getString("ok")) {
                                    "1" -> {
                                        activity.getSaveSharedPreference().run {
                                            clearSharedPreference()
                                            if (activity.autoLogin_checkBox.isChecked) {
                                                setSkipLogin(true)
                                                setLoginType("email")
                                                setEmailLoginOption("auto_login")
                                                setUserEmail(activity.email_login_id.text.toString())
                                                setUserPassword(activity.email_login_password.text.toString())
                                            } else {
                                                if (activity.saveId_checkBox.isChecked) {
                                                    setSkipLogin(false)
                                                    setEmailLoginOption("save_id")
                                                    setUserEmail(activity.email_login_id.text.toString())
                                                } else {
                                                    setSkipLogin(false)
                                                    setEmailLoginOption("nothing")
                                                }
                                            }
                                            setUserId(getString("_id"))
                                            setUserName(getString("name"))
                                            setUserCategory(getString("category"))
                                        }
                                        val intent = Intent(activity, HomeActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        activity.startActivity(intent)
                                    }
                                    "0" -> {
                                        Toast.makeText(activity, "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
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