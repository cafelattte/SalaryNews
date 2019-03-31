package kr.co.yna.www.salarynews.usermgmt.kakao

import android.content.Context
import com.kakao.auth.*

class KakaoSDKAdapter : KakaoAdapter() {
	override fun getSessionConfig(): ISessionConfig {
		return object : ISessionConfig {
			override fun getAuthTypes(): Array<AuthType> {
				return Array<AuthType>(1, {AuthType.KAKAO_ACCOUNT})
			}

			override fun isUsingWebviewTimer(): Boolean {
				return false
			}

			override fun isSecureMode(): Boolean {
				return false
			}

			override fun getApprovalType(): ApprovalType? {
				return ApprovalType.INDIVIDUAL
			}

			override fun isSaveFormData(): Boolean {
				return true
			}
		}
	}

	override fun getApplicationConfig(): IApplicationConfig {
		return object : IApplicationConfig {
			override fun getApplicationContext(): Context {
				return KakaoGlobalApplication.getGlobalApplicationContext()
			}
		}
	}

}