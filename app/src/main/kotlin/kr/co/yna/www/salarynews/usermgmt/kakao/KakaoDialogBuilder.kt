package kr.co.yna.www.salarynews.usermgmt.kakao

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import kr.co.yna.www.salarynews.R

/**
 * @author leo.shin
 * Created by leoshin on 15. 6. 19..
 */
class KakaoDialogBuilder(context: Context) {

	private var context: Context
	private var title: String? = null
	private var message: String? = null
	private var positiveBtnText: String? = null
	private var negativeBtnText: String? = null
	private var contentView: View? = null
	private var titleBgResId = 0
	private var titleTextColor = 0
	private var showTitleDivider = true
	private var positiveListener: DialogInterface.OnClickListener? = null
	private var negativeListner: DialogInterface.OnClickListener? = null

	private class CustomDialog(context: Context, private val builderKakao: KakaoDialogBuilder)// Dialog 배경을 투명 처리 해준다.
		: Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {

		override fun onCreate(savedInstanceState: Bundle) {
			super.onCreate(savedInstanceState)

			val lpWindow = WindowManager.LayoutParams()
			lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
			lpWindow.dimAmount = 0.8f
			window!!.attributes = lpWindow

			setContentView(R.layout.view_popup)
			initView()
		}

		private fun initView() {
			val title = builderKakao.title
			val message = builderKakao.message
			val negativeBtnText = builderKakao.negativeBtnText
			val positiveBtnText = builderKakao.positiveBtnText
			val positiveListener = builderKakao.positiveListener
			val negativeListner = builderKakao.negativeListner
			val contentView = builderKakao.contentView
			val showTitleDivider = builderKakao.showTitleDivider
			val titleBgResId = builderKakao.titleBgResId
			val titleTextColor = builderKakao.titleTextColor

			val titleView = findViewById<View>(R.id.title) as TextView
			if (title != null && title.length > 0) {
				titleView.text = title
			} else {
				findViewById<View>(R.id.popup_header).visibility = View.GONE
			}

			if (titleBgResId > 0) {
				titleView.setBackgroundResource(titleBgResId)
			}

			if (titleTextColor > 0) {
				titleView.setTextColor(titleView.context.resources.getColor(titleTextColor))
			}

			val titleDivider = findViewById<View>(R.id.divide) as ImageView
			if (showTitleDivider) {
				titleDivider.visibility = View.VISIBLE
			} else {
				titleDivider.visibility = View.GONE
			}

			val messageView = findViewById<View>(R.id.content) as TextView
			if (message != null && message.length > 0) {
				messageView.text = message
				messageView.movementMethod = ScrollingMovementMethod()
			} else {
				messageView.visibility = View.GONE
			}

			if (contentView != null) {
				val container = findViewById<View>(R.id.content_group) as FrameLayout
				container.visibility = View.VISIBLE
				container.addView(contentView)
			}

			val positiveBtn = findViewById<View>(R.id.bt_right) as Button
			if (positiveBtnText != null && positiveBtnText.length > 0) {
				positiveBtn.text = positiveBtnText
			} else {
				positiveBtn.visibility = View.GONE
			}

			val negativeBtn = findViewById<View>(R.id.bt_left) as Button
			if (negativeBtnText != null && negativeBtnText.length > 0) {
				negativeBtn.text = negativeBtnText
			} else {
				negativeBtn.visibility = View.GONE
			}

			if (positiveBtn.visibility == View.VISIBLE && negativeBtn.visibility == View.GONE) {
				positiveBtn.setBackgroundResource(R.drawable.popup_btn_c)
			}

			negativeBtn.setOnClickListener {
				negativeListner?.onClick(this@CustomDialog, 0)
				dismiss()
			}

			positiveBtn.setOnClickListener {
				positiveListener?.onClick(this@CustomDialog, 0)
				dismiss()
			}

			findViewById<View>(R.id.root).setOnClickListener { dismiss() }
			findViewById<View>(R.id.popup).setOnClickListener {
				// skip
			}
		}
	}

	init {
		this.context = context
	}

	fun setTitle(title: String): KakaoDialogBuilder {
		this.title = title
		return this
	}

	fun setTitle(titleResId: Int): KakaoDialogBuilder {
		this.title = context.getString(titleResId)
		return this
	}

	fun setMessage(message: String): KakaoDialogBuilder {
		this.message = message
		return this
	}

	fun setMessage(messageResId: Int): KakaoDialogBuilder {
		this.message = context.getString(messageResId)
		return this
	}

	fun setPositiveButton(positiveResId: Int, positiveListener: DialogInterface.OnClickListener): KakaoDialogBuilder {
		this.positiveBtnText = context.getString(positiveResId)
		this.positiveListener = positiveListener
		return this
	}

	fun setNegativeButton(negativeResId: Int, negativeListner: DialogInterface.OnClickListener): KakaoDialogBuilder {
		this.negativeBtnText = context.getString(negativeResId)
		this.negativeListner = negativeListner
		return this
	}

	fun setView(view: View): KakaoDialogBuilder {
		this.contentView = view
		return this
	}

	fun setTitleBgResId(titleBgResId: Int): KakaoDialogBuilder {
		this.titleBgResId = titleBgResId
		return this
	}

	fun setShowTitleDivider(showTitleDivider: Boolean): KakaoDialogBuilder {
		this.showTitleDivider = showTitleDivider
		return this
	}

	fun setTitleTextColor(titleTextColor: Int): KakaoDialogBuilder {
		this.titleTextColor = titleTextColor
		return this
	}

	fun create(): Dialog {
		return CustomDialog(context, this)
	}
}
