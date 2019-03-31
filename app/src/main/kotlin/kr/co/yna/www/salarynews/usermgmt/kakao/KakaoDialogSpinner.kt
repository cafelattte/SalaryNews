package kr.co.yna.www.salarynews.usermgmt.kakao

import android.app.Dialog
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope

import kr.co.yna.www.salarynews.R
import kr.co.yna.www.salarynews.usermgmt.kakao.KakaoSpinnerAdapter.ISpinnerListener
import kr.co.yna.www.salarynews.usermgmt.kakao.KakaoSpinnerAdapter.Companion
import org.jetbrains.anko.backgroundResource
import java.util.*

class KakaoDialogSpinner (
	@get:JvmName("getContext_") val context: Context,
	val attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
	constructor(context: Context) : this(context, null)

	init {
		initView(context, attrs)
	}

	lateinit private var entryList: List<String>
	lateinit private var title: String
	lateinit private var dialog: Dialog
	lateinit private var listView: ListView
	lateinit private var adapter: KakaoSpinnerAdapter
	lateinit private var spinner: TextView
	private var iconResId: Int = 0
	private var showTitleDivider: Boolean = false
	private var titleBgResId: Int = 0
	private var titleTextColor: Int = 0
	lateinit private var listener: ISpinnerListener

	fun initView(context: Context, attrs: AttributeSet?): Unit {
		var inflater: LayoutInflater = LayoutInflater.from(context)
		val layout: View = inflater.inflate(R.layout.view_spinner, this, false)
		addView(layout)

		attrs?.let {
			var attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.KakaoDialogSpinner).apply {
				title = getString(R.styleable.KakaoDialogSpinner_kakao_prompt)
				iconResId = getResourceId(R.styleable.KakaoDialogSpinner_kakao_icon, 0)
				titleBgResId = getResourceId(R.styleable.KakaoDialogSpinner_kakao_dialogTitle, 0)
				titleTextColor = getResourceId(R.styleable.KakaoDialogSpinner_kakao_titleTextColor, 0)
				showTitleDivider = getBoolean(R.styleable.KakaoDialogSpinner_kakao_showTitleDivider, false)
			}
			val entriesResId: Int = attributes.getResourceId(R.styleable.KakaoDialogSpinner_kakao_entries, 0)
			if (entriesResId > 0) {
				entryList = Arrays.asList(resources.getStringArray(entriesResId) as String)
			}
			attributes.recycle()
		}

		val icon: ImageView = layout.findViewById(R.id.menu_icon)
		if (iconResId > 0) {
			icon.visibility = View.VISIBLE
			icon.backgroundResource = iconResId
		} else {
			icon.visibility = View.GONE
		}

		spinner = layout.findViewById(R.id.menu_title)
		if (entryList.size > 0) {
			spinner.text = entryList.get(0)
		}

		var builderKakao: KakaoDialogBuilder = KakaoDialogBuilder(context)
		builderKakao = builderKakao.setTitle(title)

		builderKakao.setTitleBgResId(titleBgResId)
		builderKakao.setTitleTextColor(titleTextColor)
		builderKakao.setShowTitleDivider(showTitleDivider)

		listView = inflater.inflate(R.layout.view_custom_list, null, false) as ListView
		builderKakao.setView(listView)
		dialog = builderKakao.create()

		adapter = KakaoSpinnerAdapter(Companion.KakaoSpinnerItems(iconResId, entryList), object : ISpinnerListener {
			override fun onItemSelected(adapter: CoroutineScope, position: Int) {
				spinner.setText(entryList.get(position))
				dialog.dismiss()
				listener.onItemSelected(adapter, position)
			}
		})
		listView.adapter = adapter

		setOnClickListener {
			object : OnClickListener {
				override fun onClick(view: View?) {
					showDialog()
				}
			}
		}
	}

	fun setTitle(title: String) {
		this.title = title
	}

	fun setOnListener(listener: ISpinnerListener) {
		this.listener = listener
	}

	private fun showDialog(): Unit {
		dialog.show()
	}

	fun getSelectedItem(): String? {
		return adapter.getItem(adapter.selectedItemPosition)
	}

	fun setSelection(position: Int): Unit {
		spinner.text = entryList.get(position)
	}

	fun getSelectedItemPosition(): Int {
		return adapter.selectedItemPosition
	}
}