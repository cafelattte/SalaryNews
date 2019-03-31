package kr.co.yna.www.salarynews

import android.os.Bundle
import kotlinx.android.synthetic.main.nav_theme_setting.*
import kr.co.yna.www.salarynews.enum.ThemeColor
import kr.co.yna.www.salarynews.enum.getThemeColor
import kr.co.yna.www.salarynews.enum.setNavThemeSetting
import kr.co.yna.www.salarynews.enum.setThemeColor
import org.jetbrains.anko.sdk27.coroutines.onClick

class NavThemeSetting: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_theme_setting)

        setNavThemeSetting(this@NavThemeSetting, getThemeColor())

        nav_theme_purple_checkBox.onClick {
            nav_theme_purple_checkBox.isChecked = true
            nav_theme_orange_checkBox.isChecked = false
            nav_theme_green_checkBox.isChecked = false
            setThemeColor(ThemeColor.PURPLE)
            setNavThemeSetting(this@NavThemeSetting, getThemeColor())
        }
        nav_theme_orange_checkBox.onClick {
            nav_theme_purple_checkBox.isChecked = false
            nav_theme_orange_checkBox.isChecked = true
            nav_theme_green_checkBox.isChecked = false
            setThemeColor(ThemeColor.ORANGE)
            setNavThemeSetting(this@NavThemeSetting, getThemeColor())
        }
        nav_theme_green_checkBox.onClick {
            nav_theme_purple_checkBox.isChecked = false
            nav_theme_orange_checkBox.isChecked = false
            nav_theme_green_checkBox.isChecked = true
            setThemeColor(ThemeColor.GREEN)
            setNavThemeSetting(this@NavThemeSetting, getThemeColor())
        }
        nav_theme_setting_backButton.onClick {
            finish()
        }
    }
}