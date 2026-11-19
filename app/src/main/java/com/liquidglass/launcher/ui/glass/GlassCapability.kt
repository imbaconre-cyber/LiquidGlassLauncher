package com.liquidglass.launcher.ui.glass

import android.content.Context
import android.opengl.GLES20
import android.os.Build
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

object GlassCapability {
    enum class Mode { FULL, FROSTED, SOLID }

    fun detect(context: Context): Mode {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val info = android.app.ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val lowRam = info.totalMem < 3L * 1024 * 1024 * 1024
        val oldAndroid = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
        val gpuOk = supportsGles32()
        return when {
            !gpuOk || oldAndroid || lowRam -> Mode.FROSTED
            else -> Mode.FULL
        }
    }

    private fun supportsGles32(): Boolean {
        return try {
            val egl = EGLContext.getEGL() as EGL10
            val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            egl.eglInitialize(display, null)
            val configAttribs = intArrayOf(
                EGL10.EGL_RENDERABLE_TYPE, 0x0040, // EGL_OPENGL_ES3_BIT_KHR
                EGL10.EGL_NONE
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            egl.eglChooseConfig(display, configAttribs, configs, 1, numConfigs)
            egl.eglTerminate(display)
            numConfigs[0] > 0
        } catch (t: Throwable) {
            false
        }
    }
}
