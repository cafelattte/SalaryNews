package kr.co.yna.www.salarynews.utils.recognition

import android.Manifest
import android.content.Context
import android.util.Log
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class GetPermission {
    private var permissionListener : PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Log.i(TAG, "Permission Granted")
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            Log.d(TAG, "Permission Denied")
        }
    }
    fun getAudioRecordPermission(context: Context) {
        TedPermission.with(context)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("음성 인식 기능을 사용하기 위해 권한이 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
    }

    companion object {
        val TAG: String = this::class.java.simpleName
    }
}