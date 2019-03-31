package kr.co.yna.www.salarynews.enum

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_home_end.*
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.yna.www.salarynews.*
import org.jetbrains.anko.backgroundColor
import java.lang.ref.WeakReference

enum class MicState {
    ON, OFF
}
private var micState: MicState = MicState.OFF
fun getState(): MicState {
    return micState
}
fun setState(mState: MicState) {
    micState = mState
}

fun initMicState(context: Context) {
    (context as Activity).let { activity ->
        when (activity.localClassName) {
            "HomeActivity" -> {
                setHomeMicOffMode(activity, getThemeColor())
            }
            "MainActivity" -> {
                WeakReference(activity).get()?.run {
                    mic_speaking_background_main.backgroundColor = resources.getColor(R.color.transparent)
                    mic_speaking_main.visibility = View.INVISIBLE
                }
            }
            "HomeEndActivity" -> {
                WeakReference(activity).get()?.run {
                    mic_speaking_background_home_end.backgroundColor = resources.getColor(R.color.transparent)
                    mic_speaking_home_end.visibility = View.INVISIBLE
                }
            }
            else -> {
                Log.e("initMicState", "unexpected condition branch")
            }
        }
    }
    setState(MicState.OFF)
}
fun micOnMode(context: Context) {
    if (getState() == MicState.OFF) {
        (context as Activity).let { activity ->
            when (activity.localClassName) {
                "HomeActivity" -> {
                    setHomeMicOnMode(activity)
                }
                "MainActivity" -> {
                    WeakReference(activity).get()?.run {
                        mic_speaking_background_main.backgroundColor = resources.getColor(R.color.main_speaking_pad)
                        mic_speaking_main.visibility = View.VISIBLE
                    }
                }
                "HomeEndActivity" -> {
                    WeakReference(activity).get()?.run {
                        mic_speaking_background_home_end.backgroundColor = resources.getColor(R.color.main_speaking_pad)
                        mic_speaking_home_end.visibility = View.VISIBLE
                    }
                }
                else -> {
                    Log.e("micOnMode", "unexpected condition branch")
                }
            }
            setState(MicState.ON)
        }
    }
}
fun micOffMode(context: Context) {
    if (getState() == MicState.ON) {
        (context as Activity).let { activity ->
            when (activity.localClassName) {
                "HomeActivity" -> {
                    setHomeMicOffMode(activity, getThemeColor())
                }
                "MainActivity" -> {
                    WeakReference(activity).get()?.run {
                        mic_speaking_background_main.backgroundColor = resources.getColor(R.color.transparent)
                        mic_speaking_main.visibility = View.INVISIBLE
                    }
                }
                "HomeEndActivity" -> {
                    WeakReference(activity).get()?.run {
                        mic_speaking_background_home_end.backgroundColor = resources.getColor(R.color.transparent)
                        mic_speaking_home_end.visibility = View.INVISIBLE
                    }
                }
                else -> {
                    Log.e("micOffMode", "unexpected condition branch")
                }
            }
            setState(MicState.OFF)
        }
    }
}
