package com.example.programiranje_1

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class App : Application(), Application.ActivityLifecycleCallbacks {
    var queue: BlockingQueue<Bundle> = ArrayBlockingQueue<Bundle>(size)
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
        activityReferences++

    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        isActivityChangingConfigurations = activity?.isChangingConfigurations ?: false
        activityReferences--

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    companion object {
        private const val size = 10
    }
}
