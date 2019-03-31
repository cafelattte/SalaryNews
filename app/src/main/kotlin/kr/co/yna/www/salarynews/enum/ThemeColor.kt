package kr.co.yna.www.salarynews.enum

import android.content.Context
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home_end.*
import kotlinx.android.synthetic.main.activity_home_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_theme_setting.*
import kr.co.yna.www.salarynews.*
import org.jetbrains.anko.textColor
import java.lang.ref.WeakReference

enum class ThemeColor {
    PURPLE, ORANGE, GREEN
}
private var themeColor: ThemeColor = ThemeColor.PURPLE

fun getThemeColor(): ThemeColor {
    return themeColor
}
fun setThemeColor(tColor: ThemeColor) {
    themeColor = tColor
}
fun setHomeActivityByThemeColor(context: Context, tColor: ThemeColor) {
    try {
        (context as HomeActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
//				home_setting.setImageDrawable(getDrawable(R.drawable.home_setting_purple))
                        nav_view.getHeaderView(0).background = getDrawable(R.drawable.color_purple)
                        home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_default_purple))
                        home_above_msg.textColor = resources.getColor(R.color.logo_color_purple)
                        home_goodmorning.textColor = resources.getColor(R.color.logo_color_purple)
                        home_start_person.setImageDrawable(getDrawable(R.drawable.start_person_purple))
                    }
                    ThemeColor.ORANGE -> {
//				home_setting.setImageDrawable(getDrawable(R.drawable.home_setting_orange))
                        nav_view.getHeaderView(0).background = getDrawable(R.drawable.color_orange)
                        home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_default_orange))
                        home_above_msg.textColor = resources.getColor(R.color.logo_color_orange)
                        home_goodmorning.textColor = resources.getColor(R.color.logo_color_orange)
                        home_start_person.setImageDrawable(getDrawable(R.drawable.start_person_orange))
                    }
                    ThemeColor.GREEN -> {
//				home_setting.setImageDrawable(getDrawable(R.drawable.home_setting_green))
                        nav_view.getHeaderView(0).background = getDrawable(R.drawable.color_green)
                        home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_default_green))
                        home_above_msg.textColor = resources.getColor(R.color.logo_color_green)
                        home_goodmorning.textColor = resources.getColor(R.color.logo_color_green)
                        home_start_person.setImageDrawable(getDrawable(R.drawable.start_person_green))
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setHomeByThemeColor", "$e")
    }
}
fun setHomeMicOnMode(context: Context) {
    try {
        (context as HomeActivity).let { activity ->
            WeakReference(activity).get()?.run {
                home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_rec))
//                home_mic_listening.visibility = View.VISIBLE
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setHomeMicOnMode", "$e")
    }
}
fun setHomeMicOffMode(context: Context, tColor: ThemeColor) {
    try {
        (context as HomeActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_default_purple))
                    }
                    ThemeColor.ORANGE -> {
                        home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_default_orange))
                    }
                    ThemeColor.GREEN -> {
                        home_mic.setImageDrawable(getDrawable(R.drawable.home_mic_default_green))
                    }
                }
//                home_mic_listening.visibility = View.INVISIBLE
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setHomeMicOffMode", "$e")
    }
}

fun setNavThemeSetting(context: Context, tColor: ThemeColor) {
    try {
        (context as NavThemeSetting).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        nav_theme_setting_icon.setImageDrawable(getDrawable(R.drawable.start_person_purple))
                        nav_theme_purple_checkBox.isChecked = true
                    }
                    ThemeColor.ORANGE -> {
                        nav_theme_setting_icon.setImageDrawable(getDrawable(R.drawable.start_person_orange))
                        nav_theme_orange_checkBox.isChecked = true
                    }
                    ThemeColor.GREEN -> {
                        nav_theme_setting_icon.setImageDrawable(getDrawable(R.drawable.start_person_green))
                        nav_theme_green_checkBox.isChecked = true
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setNavThemeSetting", "$e")
    }
}

fun setMainActivityByThemeColor(context: Context, tColor: ThemeColor) {
    try {
        (context as MainActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        main_background.background = getDrawable(R.color.color_background_purple)
                        mic_speaking_main.setImageDrawable(getDrawable(R.drawable.mic_speaking_purple))
                        news_1_title.textColor = resources.getColor(R.color.main_color_purple)
                        news_1_index.textColor = resources.getColor(R.color.main_color_purple)
                        news_2_title.textColor = resources.getColor(R.color.main_color_purple)
                        news_2_index.textColor = resources.getColor(R.color.main_color_purple)
                        news_3_title.textColor = resources.getColor(R.color.main_color_purple)
                        news_3_index.textColor = resources.getColor(R.color.main_color_purple)
                    }
                    ThemeColor.ORANGE -> {
                        main_background.background = getDrawable(R.color.color_background_orange)
                        mic_speaking_main.setImageDrawable(getDrawable(R.drawable.mic_speaking_orange))
                        news_1_title.textColor = resources.getColor(R.color.main_color_orange)
                        news_1_index.textColor = resources.getColor(R.color.main_color_orange)
                        news_2_title.textColor = resources.getColor(R.color.main_color_orange)
                        news_2_index.textColor = resources.getColor(R.color.main_color_orange)
                        news_3_title.textColor = resources.getColor(R.color.main_color_orange)
                        news_3_index.textColor = resources.getColor(R.color.main_color_orange)
                    }
                    ThemeColor.GREEN -> {
                        main_background.background = getDrawable(R.color.color_background_green)
                        mic_speaking_main.setImageDrawable(getDrawable(R.drawable.mic_speaking_green))
                        news_1_title.textColor = resources.getColor(R.color.main_color_green)
                        news_1_index.textColor = resources.getColor(R.color.main_color_green)
                        news_2_title.textColor = resources.getColor(R.color.main_color_green)
                        news_2_index.textColor = resources.getColor(R.color.main_color_green)
                        news_3_title.textColor = resources.getColor(R.color.main_color_green)
                        news_3_index.textColor = resources.getColor(R.color.main_color_green)
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setMainByThemeColor", "$e")
    }
}
fun setContentActivityByThemeColor(context: Context, tColor: ThemeColor) {
    try {
        (context as ContentActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        news_index.textColor = resources.getColor(R.color.main_color_purple)
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play_purple))
                        content_seekBar.thumb = getDrawable(R.drawable.content_seekbar_thumb_purple)
                        content_seekBar.progressDrawable =
                            getDrawable(R.drawable.content_seekbar_purple)
                        content_image_background.background =
                            getDrawable(R.drawable.content_gradient_purple)
                    }
                    ThemeColor.ORANGE -> {
                        news_index.textColor = resources.getColor(R.color.main_color_orange)
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play_orange))
                        content_seekBar.thumb = getDrawable(R.drawable.content_seekbar_thumb_orange)
                        content_seekBar.progressDrawable =
                            getDrawable(R.drawable.content_seekbar_orange)
                        content_image_background.background =
                            getDrawable(R.drawable.content_gradient_orange)
                    }
                    ThemeColor.GREEN -> {
                        news_index.textColor = resources.getColor(R.color.main_color_green)
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play_green))
                        content_seekBar.thumb = getDrawable(R.drawable.content_seekbar_thumb_green)
                        content_seekBar.progressDrawable =
                            getDrawable(R.drawable.content_seekbar_green)
                        content_image_background.background =
                            getDrawable(R.drawable.content_gradient_green)
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setContentByThemeColor", "$e")
    }
}
fun setContent2PlayButton(context: Context, tColor: ThemeColor) {
    try {
        (context as ContentActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play_purple))
                    }
                    ThemeColor.ORANGE -> {
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play_orange))
                    }
                    ThemeColor.GREEN -> {
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_play_green))
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setContent2PlayButton", "$e")
    }
}
fun setContent2PauseButton(context: Context, tColor: ThemeColor) {
    try {
        (context as ContentActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_pause_purple))
                    }
                    ThemeColor.ORANGE -> {
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_pause_orange))
                    }
                    ThemeColor.GREEN -> {
                        content_play_pause_button.setImageDrawable(getDrawable(R.drawable.content_pause_green))
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setContent2PauseButton", "$e")
    }
}
fun setHomeEndActivityByThemeColor(context: Context, tColor: ThemeColor) {
    try {
        (context as HomeEndActivity).let { activity ->
            WeakReference(activity).get()?.run {
                when (tColor) {
                    ThemeColor.PURPLE -> {
                        home_end_person.setImageDrawable(getDrawable(R.drawable.end_person_purple))
                        home_end_msg.textColor = resources.getColor(R.color.logo_color_purple2)
                        mic_speaking_home_end.setImageDrawable(getDrawable(R.drawable.mic_speaking_purple))
                    }
                    ThemeColor.ORANGE -> {
                        home_end_person.setImageDrawable(getDrawable(R.drawable.end_person_orange))
                        home_end_msg.textColor = resources.getColor(R.color.logo_color_orange2)
                        mic_speaking_home_end.setImageDrawable(getDrawable(R.drawable.mic_speaking_orange))
                    }
                    ThemeColor.GREEN -> {
                        home_end_person.setImageDrawable(getDrawable(R.drawable.end_person_green))
                        home_end_msg.textColor = resources.getColor(R.color.logo_color_green2)
                        mic_speaking_home_end.setImageDrawable(getDrawable(R.drawable.mic_speaking_green))
                    }
                }
            }
        }
    } catch (e: ClassCastException) {
        Log.e("setHomeEndByThemeColor", "$e")
    }
}
