package kr.co.yna.www.salarynews.usermgmt.kakao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.auth.ApiResponseCallback
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.ApiErrorCode
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.OptionalBoolean
import kotlinx.android.synthetic.main.layout_usermgmt_signup.*
import kr.co.yna.www.salarynews.BaseActivity
import kr.co.yna.www.salarynews.HomeActivity
import kr.co.yna.www.salarynews.MainActivity
import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.usermgmt.LoginActivity
import org.jetbrains.anko.sdk27.coroutines.onClick

class KakaoSignupActivity : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestMe()
	}

	private fun requestSignup(properties: Map<String, String>): Unit {
		UserManagement.getInstance().requestSignup(object : ApiResponseCallback<Long>() {
			override fun onSuccess(result: Long?) {
				Log.d("KakaoSignupActivity::", "onSuccess\n")
				requestMe()
			}

			override fun onFailure(errorResult: ErrorResult?) {
				Log.e("KakaoSignupActivity::", "onFailure\nmsg=$errorResult")
				KakaoToast.makeToast(applicationContext, "UsermgmtResponseCallback : failure : $errorResult", Toast.LENGTH_LONG).show()
				finish()
			}

			override fun onSessionClosed(errorResult: ErrorResult?) {
				Log.e("KakaoSignupActivity::", "onSessionClosed\nmsg=$errorResult")
			}

			override fun onNotSignedUp() {
				Log.e("KakaoSignupActivity::", "onNotSignup\n")
			}
		}, properties)
	}

	protected fun requestMe(): Unit {
		UserManagement.getInstance().me(object : MeV2ResponseCallback() {
			override fun onSuccess(result: MeV2Response?) {
				if (result?.hasSignedUp() == OptionalBoolean.FALSE) {
					// showSignup
					setContentView(R.layout.layout_usermgmt_signup)
					buttonSignup.onClick {
						val kakaoExtraUserPropertyLayout: KakaoExtraUserPropertyLayout = findViewById(R.id.extra_user_property)
						requestSignup(kakaoExtraUserPropertyLayout.getProperties())
					}
				} else {
//					Log.d("requestMe", "id: ${result?.id ?: ""}")
//					Log.d("requestMe", "email: ${result?.kakaoAccount?.email ?: ""}")
//					Log.d("requestMe", "profile image: ${result?.profileImagePath ?: ""}")

					redirectHomeActivity()
				}
			}

			override fun onFailure(errorResult: ErrorResult?) {
				super.onFailure(errorResult)

				Log.d("KakaoSignupActivity::", "onFailure\nmsg=$errorResult")

				var result: Int = errorResult?.errorCode as Int
				if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
					KakaoToast.makeToast(applicationContext, getString(R.string.error_message_for_service_unavailable), Toast.LENGTH_SHORT).show()
					finish()
				} else {
					redirectLoginActivity()
				}
			}

			override fun onSessionClosed(errorResult: ErrorResult?) {
				Log.e("KakaoSignupActivity::", "onSessionClosed\nmsg=${errorResult?.toString() ?: "null"}")
				redirectLoginActivity()
			}

		})
	}

	private fun redirectLoginActivity(): Unit {
		val intent: Intent = Intent(this, LoginActivity::class.java)
		intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or
						Intent.FLAG_ACTIVITY_CLEAR_TASK or
						Intent.FLAG_ACTIVITY_CLEAR_TOP
		startActivity(intent)
	}

	private fun redirectHomeActivity(): Unit {
		val intent: Intent = Intent(this, HomeActivity::class.java)
		intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or
						Intent.FLAG_ACTIVITY_CLEAR_TASK or
						Intent.FLAG_ACTIVITY_CLEAR_TOP
		startActivity(intent)
	}


}