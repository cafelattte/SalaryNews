package kr.co.yna.www.salarynews.usermgmt.kakao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope

import kr.co.yna.www.salarynews.R
import org.jetbrains.anko.sdk27.coroutines.onClick

class KakaoSpinnerAdapter (
        private val items: KakaoSpinnerItems? = null,
        private val listener: ISpinnerListener? = null
) : BaseAdapter() {
	var selectedItemPosition: Int = 0

	constructor() : this(null, null)

	interface ISpinnerListener {
		fun onItemSelected(adapter: CoroutineScope, position: Int): Unit
	}

	companion object {
		class KakaoSpinnerItems(val iconResId: Int, val titleList: List<String>) {
			fun getTitle(position: Int): String {
				return titleList.get(position)
			}
			fun getSize(): Int {
				return titleList.size
			}
		}
	}

	override fun getCount(): Int {
		return items?.getSize() ?: 0
	}

	override fun getItem(position: Int): String {
		return items?.getTitle(position) ?: ""
	}

	override fun getItemId(position: Int): Long {
		return 0
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		var item: View? = convertView
		item?.let {
			var inflater: LayoutInflater = LayoutInflater.from(parent?.context)

			var view_item: View = inflater.inflate(R.layout.view_spinner_item, parent, false).apply {
				var title: TextView = findViewById(R.id.menu_title)
				title.text = items?.getTitle(position)

				val checked: CheckBox = findViewById(R.id.menu_checkbox)
				checked.isChecked = (selectedItemPosition == position)
				onClick {
					selectedItemPosition = position
					listener?.onItemSelected(KakaoSpinnerAdapter@this, position)
				}
			}

			return view_item
		}
	}
}