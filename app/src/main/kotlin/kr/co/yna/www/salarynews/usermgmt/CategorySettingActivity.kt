package kr.co.yna.www.salarynews.usermgmt

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.hsalf.smilerating.BaseRating
import com.hsalf.smilerating.SmileRating
import kotlinx.android.synthetic.main.activity_category_setting.*
import kr.co.yna.www.salarynews.BaseActivity
import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.utils.network.PostLoadAsyncTask
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

class CategorySettingActivity : BaseActivity() {
    lateinit var userName: String
    lateinit var userEmail: String
    lateinit var userPassword: String
    private var userCategory = "33333333"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_setting)

        val intent = intent
        userName = if (intent.hasExtra("user_name")) intent.getStringExtra("user_name") else ""
        userEmail = if (intent.hasExtra("user_email")) intent.getStringExtra("user_email") else ""
        userPassword = if (intent.hasExtra("user_password")) intent.getStringExtra("user_password") else ""

        buttonDisable()
        initRating()

        rating_bar_1.setOnRatingSelectedListener { level, _ ->
            userCategory = "$level${userCategory.substring(1)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_2.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 1)}$level${userCategory.substring(2)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_3.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 2)}$level${userCategory.substring(3)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_4.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 3)}$level${userCategory.substring(4)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_5.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 4)}$level${userCategory.substring(5)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_6.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 5)}$level${userCategory.substring(6)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_7.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 6)}$level${userCategory.substring(7)}"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        rating_bar_8.setOnRatingSelectedListener { level, _ ->
            userCategory = "${userCategory.substring(0, 7)}$level"
            if (isAllSelected()) buttonEnable() else buttonDisable()
        }
        later_button.onClick {
            Toast.makeText(this@CategorySettingActivity, "미설정 항목은 기본값\n \"보통\"으로 설정됩니다.", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "save_user complete(with incomplete category)")

            val requestBody = makeRequestBody(userName, userEmail, userPassword, userCategory)
            Log.v(TAG, "requestBody: $requestBody")
            nodeCreateUser(this@CategorySettingActivity, requestBody)
        }
        ok_button.onClick {
            val requestBody = makeRequestBody(userName, userEmail, userPassword, userCategory)
            Log.d(TAG, "$requestBody")
            nodeCreateUser(this@CategorySettingActivity, requestBody)
        }
    }
    private fun isAllSelected(): Boolean {
        val select1 = rating_bar_1.rating != 0
        val select2 = rating_bar_2.rating != 0
        val select3 = rating_bar_3.rating != 0
        val select4 = rating_bar_4.rating != 0
        val select5 = rating_bar_5.rating != 0
        val select6 = rating_bar_6.rating != 0
        val select7 = rating_bar_7.rating != 0
        val select8 = rating_bar_8.rating != 0
        
        return select1 and select2 and select3 and select4 and select5 and select6 and select7 and select8
    }
    private fun initRating() {
        removeText(rating_bar_1)
        removeText(rating_bar_2)
        removeText(rating_bar_3)
        removeText(rating_bar_4)
        removeText(rating_bar_5)
        removeText(rating_bar_6)
        removeText(rating_bar_7)
        removeText(rating_bar_8)
    }
    private fun buttonEnable() {
        ok_button.isEnabled = true
        ok_button.setImageDrawable(getDrawable(R.drawable.category_ok_enable))
    }
    private fun buttonDisable() {
        ok_button.isEnabled = false
        ok_button.setImageDrawable(getDrawable(R.drawable.category_ok_disable))
    }
    private fun removeText(rating_bar: SmileRating) {
        rating_bar.setNameForSmile(BaseRating.GREAT, "")
        rating_bar.setNameForSmile(BaseRating.GOOD, "")
        rating_bar.setNameForSmile(BaseRating.OKAY, "")
        rating_bar.setNameForSmile(BaseRating.BAD, "")
        rating_bar.setNameForSmile(BaseRating.TERRIBLE, "")
    }

    private fun makeRequestBody(name: String, email: String, password: String, category: String): JSONObject {
        val reqBody = JSONObject()
        reqBody.put("name", name)
        reqBody.put("email", email)
        reqBody.put("password", password)
        reqBody.put("login_type", "email")
        reqBody.put("category", category)

        return reqBody
    }

    private fun nodeCreateUser(context: CategorySettingActivity, reqBody: JSONObject) {
        val url = getString(R.string.app_server)
        LoadAsyncTaskNodeCreateUser(context, reqBody).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "$url/user/create", "node", reqBody.toString())
    }

    companion object {
        private val TAG = CategorySettingActivity::class.java.simpleName
        class LoadAsyncTaskNodeCreateUser internal constructor(context: CategorySettingActivity, private val reqBody: JSONObject) : PostLoadAsyncTask(context) {
            private val activityReference: WeakReference<CategorySettingActivity> = WeakReference(context)

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                activityReference.get()?.let { activity ->
                    result?.let {
                        Log.d(TAG, "response: $it")
                        try {
                            JSONObject(it).run {
                                if (getString("ok").equals("1")) {
                                    activity.getSaveSharedPreference().run {
                                        clearSharedPreference()
                                        Log.d("setSaveSharedPreference", "$reqBody")
                                        setSkipLogin(true)
                                        setLoginType("email")
                                        setEmailLoginOption("auto_login")
                                        setUserName(reqBody.getString("name"))
                                        setUserEmail(reqBody.getString("email"))
                                        setUserPassword(reqBody.getString("password"))
                                        setUserCategory(activity.slashCategories(reqBody.getString("category")))
                                        setUserId(getString("_id"))
                                    }

                                    Toast.makeText(activity, "회원가입이 완료되었습니다.\n 자동로그인을 진행합니다", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(activity, EmailLoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    activity.startActivity(intent)
                                }
                            }
                        } catch (e: JSONException) {
                            Log.e(TAG, "JSONException: $e")
                        }
                    }
                }
            }
        }
    }
}