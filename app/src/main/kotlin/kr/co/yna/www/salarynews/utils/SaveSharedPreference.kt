package kr.co.yna.www.salarynews.utils

import android.content.SharedPreferences

class SaveSharedPreference(appData: SharedPreferences) {
    private val appData = appData

    fun clearSharedPreference() {
        appData.run {
            val editor = edit()
            editor?.clear()
            editor?.apply()
        }
    }
    fun getAuthToken(): String? {
        return appData.getString(authToken, null)
    }
    fun getUserId(): String? {
        return appData.getString(userId, null)
    }
    fun getUserName(): String? {
        return appData.getString(userName, null)
    }
    fun getUserEmail(): String? {
        return appData.getString(userEmail, null)
    }
    fun getUserPassword(): String? {
        return appData.getString(userPassword, null)
    }
    fun getUserCategory(): String? {
        return appData.getString(userCategory, null)
    }
    fun getEmailLoginOption(): String? {
        return appData.getString(emailLoginOption, null)
    }
    fun getLoginType(): String? {
        return appData.getString(loginType, null)
    }
    fun getSkipLogin(): Boolean {
        return appData.getBoolean(skipLogin, false)
    }

    fun setAuthToken(_authToken: String) {
        appData.edit()?.run {
            putString(authToken, _authToken)
            apply()
        }
    }
    fun setUserId(_userId: String) {
        appData.edit()?.run {
            putString(userId, _userId)
            apply()
        }
    }
    fun setUserName(_userName: String) {
        appData.edit()?.run {
            putString(userName, _userName)
            apply()
        }
    }
    fun setUserEmail(_userEmail: String) {
        appData.edit()?.run {
            putString(userEmail, _userEmail)
            apply()
        }
    }
    fun setUserPassword(_userPassword: String) {
        appData.edit()?.run {
            putString(userPassword, _userPassword)
            apply()
        }
    }
    fun setUserCategory(_userCategory: String) {
        appData.edit()?.run {
            putString(userCategory, _userCategory)
            apply()
        }
    }
    fun setEmailLoginOption(_emailLoginOption: String) {
        appData.edit()?.run {
            putString(emailLoginOption, _emailLoginOption)
            apply()
        }
    }
    fun setLoginType(_loginType: String) {
        appData.edit()?.run {
            putString(loginType, _loginType)
            apply()
        }
    }
    fun setSkipLogin(_skipLogin: Boolean) {
        appData.edit()?.run {
            putBoolean(skipLogin, _skipLogin)
            apply()
        }
    }

    companion object {
        const val authToken = "auth_token"
        const val userId = "user_id"
        const val userName = "user_name"
        const val userEmail = "user_email"
        const val userPassword = "user_password"
        const val userCategory = "user_category"
        const val emailLoginOption = "email_login_option"
        const val loginType = "login_type"
        const val skipLogin = "skip_login"
    }
}
