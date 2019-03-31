package kr.co.yna.www.salarynews.usermgmt.kakao

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_usermgmt_extra_user_property.view.*

import kr.co.yna.www.salarynews.R

class KakaoExtraUserPropertyLayout (
	@get:JvmName("getContext_") val context: Context,
	val attrs: AttributeSet? = null,
	val defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
	constructor(context: Context) : this(context, null, 0)

	private val NAME_KEY: String = "name"
	private val AGE_KEY: String = "age"
	private val GENDER_KEY: String = "gender"

	init {
		val view: View = View.inflate(context, R.layout.layout_usermgmt_extra_user_property, this)
		// binding 되어있는지 확인
	}

	fun getProperties(): HashMap<String, String> {
		val properties: HashMap<String, String> = HashMap<String, String>()

		properties.put(NAME_KEY, name.text.toString())
		properties.put(AGE_KEY, age.text.toString())
		properties.put(GENDER_KEY, gender.getSelectedItem() as String)

		return properties
	}

	fun showProperties(properties: Map<String, String>): Unit {
		name.setText(properties[NAME_KEY])
		age.setText(properties[AGE_KEY])
		if (properties[GENDER_KEY] == getContext().getString(R.string.female)) {
			gender.setSelection(0)
		} else {
			gender.setSelection(1)
		}
	}
}
