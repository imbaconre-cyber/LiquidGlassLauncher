package com.liquidglass.launcher.gestures

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Gesture { SWIPE_UP, SWIPE_DOWN, DOUBLE_TAP, PINCH_IN, PINCH_OUT, TWO_FINGER_SWIPE_UP, TWO_FINGER_SWIPE_DOWN, HOME_PRESS }

enum class GestureAction {
    OPEN_DRAWER, OPEN_SEARCH, LOCK_SCREEN, OPEN_NOTIFICATIONS, OPEN_QUICK_SETTINGS,
    LAUNCH_APP, TOGGLE_GLASS_INTENSITY, NONE
}

class GestureManager(private val context: Context) {
    private val _bindings = MutableStateFlow<Map<Gesture, GestureAction>>(emptyMap())
    val bindings: StateFlow<Map<Gesture, GestureAction>> = _bindings

    fun bind(gesture: Gesture, action: GestureAction) {
        _bindings.value = _bindings.value + (gesture to action)
    }

    fun actionFor(gesture: Gesture): GestureAction =
        _bindings.value[gesture] ?: GestureAction.NONE
}
