package kr.co.yna.www.salarynews.usermgmt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_signup_email.*
import kr.co.yna.www.salarynews.BaseActivity

import kr.co.yna.www.salarynews.R
import org.jetbrains.anko.sdk27.coroutines.onClick

class SignupEmailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_email)

        signup_button.onClick {
            val userName = signup_name.text.toString()
            val userEmail = signup_email.text.toString()
            val userPassword = signup_password.text.toString()
            val userPasswordConfirm = signup_passwordConfirm.text.toString()

            val validateName: Boolean = checkValidateName(userName)
            val validateEmail: Boolean = checkValidateEmail(userEmail)
            val validatePassword: Boolean = checkValidatePassword(userPassword)
            val validatePasswordConfirm: Boolean = checkValidatePasswordConfirm(userPassword, userPasswordConfirm)

            if (validateName and validateEmail and validatePassword and validatePasswordConfirm) {
                Log.d("SignupEmailActivity::", "goto CategorySettingActivity")

                val intent = Intent(this@SignupEmailActivity, CategorySettingActivity::class.java)
                intent.putExtra("user_name", userName)
                intent.putExtra("user_email", userEmail)
                intent.putExtra("user_password", userPassword)
                startActivity(intent)
            } else {
                if (validateName) {
                    signup_name_warning.visibility = View.INVISIBLE
                    signup_name_warn_image.visibility = View.INVISIBLE
                } else {
                    signup_name_warning.visibility = View.VISIBLE
                    signup_name_warn_image.visibility = View.VISIBLE
                    Log.w(TAG, "Invalid name")
                }
                if (validateEmail) {
                    signup_email_warning.visibility = View.INVISIBLE
                    signup_email_warn_image.visibility = View.INVISIBLE
                } else {
                    signup_email_warning.visibility = View.VISIBLE
                    signup_email_warn_image.visibility = View.VISIBLE
                    Log.w(TAG, "Invalid email")
                }
                if (validatePassword) {
                    signup_password_warning.visibility = View.INVISIBLE
                    signup_password_warn_image.visibility = View.INVISIBLE
                } else {
                    signup_password_warning.visibility = View.VISIBLE
                    signup_password_warn_image.visibility = View.VISIBLE
                    Log.w(TAG, "Invalid password")
                }
                if (validatePasswordConfirm) {
                    signup_passwordConfirm_warning.visibility = View.INVISIBLE
                    signup_passwordConfirm_warn_image.visibility = View.INVISIBLE
                } else {
                    signup_passwordConfirm_warning.visibility = View.VISIBLE
                    signup_passwordConfirm_warn_image.visibility = View.VISIBLE
                    Log.w(TAG, "Invalid passwordConfirm")
                }
            }
        }

        signup_cancel_button.onClick {
            finish()
        }
    }

    private fun checkValidateName(name_text: String): Boolean {
        return name_text.isNotEmpty()
    }
    private fun checkValidateEmail(email_text: String): Boolean {
        return email_text.contains("@")
    }
    private fun checkValidatePassword(password_text: String): Boolean {
        return password_text.length.let {
            when (it >= 6) {
                true -> (it <= 15)
                false -> false
            }
        }
    }
    private fun checkValidatePasswordConfirm
                (password_text: String, confirm_text: String): Boolean {
        return password_text == confirm_text
    }
    companion object {
        private val TAG = SignupEmailActivity::class.java.simpleName
    }
}