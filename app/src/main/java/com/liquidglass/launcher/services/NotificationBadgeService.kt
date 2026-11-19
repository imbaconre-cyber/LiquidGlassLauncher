package com.liquidglass.launcher.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationBadgeService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        // Broadcast to UI via a shared flow (simplified)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // update counts
    }

    companion object {
        fun getUnreadCount(packageName: String): Int = 0
    }
}
