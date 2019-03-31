package kr.co.yna.www.salarynews.usermgmt.kakao

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kr.co.yna.www.salarynews.R

class KakaoToast {
	companion object {
		fun makeToast(context: Context, body: String, duration: Int): Toast {
			val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
			val view: View = inflater.inflate(R.layout.view_toast, null)
			val text: TextView = view.findViewById(R.id.kakao_toast_message)
			text.text = body

			val toast: Toast = Toast(context)
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
			toast.view = view
			toast.duration = duration
			return toast
		}
	}
}