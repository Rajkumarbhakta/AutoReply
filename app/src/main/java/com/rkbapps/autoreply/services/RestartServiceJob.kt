package com.rkbapps.autoreply.services

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent

@SuppressLint("SpecifyJobSchedulerIdRange")
class RestartServiceJob : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        val serviceIntent = Intent(this, MyNotificationListenerService::class.java)
        startService(serviceIntent)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}
