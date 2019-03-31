package kr.co.yna.www.salarynews.usermgmt.kakao

import android.content.Context
import android.content.Intent
import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.util.exception.KakaoException
import kr.co.yna.www.salarynews.usermgmt.CategorySettingActivity

class KakaoSessionCallback(var context: Context): ISessionCallback {
    private val TAG = context.javaClass.name

    override fun onSessionOpened() {
        Log.d("LoginActivity::", "onSessionOpened")

        redirectSignupActivity()
    }
    override fun onSessionOpenFailed(exception: KakaoException?) {
        Log.d("LoginActivity::", "onSessionOpenFailed\nmsg=$exception")

        redirectCategorySettingActivity()
    }

    protected fun redirectSignupActivity(): Unit {
        Log.d("LoginActivity::", "redirectSignupActivity")

        val intent = Intent(context, KakaoSignupActivity::class.java)
        context.startActivity(intent)
    }

    protected fun redirectCategorySettingActivity(): Unit {
        Log.d("LoginActivity::", "redirectCategorySettingActivity")

        val intent = Intent(context, CategorySettingActivity::class.java)
        context.startActivity(intent)
    }

    protected fun redirectLoginActivity(): Unit {
        Log.d("LoginActivity::", "redirectLoginActivity")

        val intent = Intent(context, CategorySettingActivity::class.java)
        context.startActivity(intent)
    }
}