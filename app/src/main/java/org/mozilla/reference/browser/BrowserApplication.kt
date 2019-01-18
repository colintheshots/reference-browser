/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser

import android.app.Application
import android.content.Context
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mozilla.components.service.fretboard.Fretboard
import mozilla.components.service.fretboard.ValuesProvider
import mozilla.components.service.fretboard.source.kinto.KintoExperimentSource
import mozilla.components.service.fretboard.storage.flatfile.FlatFileExperimentStorage
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.logger.Logger
import mozilla.components.support.base.log.sink.AndroidLogSink
import org.mozilla.reference.browser.ext.isCrashReportActive
import java.io.File

class BrowserApplication : Application() {
    val components by lazy { Components(this) }
    lateinit var fretboard: Fretboard

    override fun onCreate() {
        super.onCreate()

        // We want the log messages of all builds to go to Android logcat
        Log.addSink(AndroidLogSink())

        if (isCrashReportActive) {
            components.analytics.crashReporter.install(this)
        }

        // mozilla.appservices.ReferenceBrowserMegazord will be missing if we're doing an application-services
        // dependency substitution locally. That class is supplied dynamically by the org.mozilla.appservices
        // gradle plugin, and that won't happen if we're not megazording. We won't megazord if we're
        // locally substituting every module that's part of the megazord's definition, which is what
        // happens during a local substitution of application-services.
        // As a workaround, use reflections to conditionally initialize the megazord in case it's present.
        // See https://github.com/mozilla-mobile/reference-browser/pull/356.
        try {
            val megazordClass = Class.forName("mozilla.appservices.ReferenceBrowserMegazord")
            val megazordInitMethod = megazordClass.getDeclaredMethod("init")
            megazordInitMethod.invoke(megazordClass)
        } catch (e: ClassNotFoundException) {
            Logger.info("mozilla.appservices.ReferenceBrowserMegazord not found; skipping megazord init.")
        }

        loadExperiments()
    }

    private fun loadExperiments() {
        val experimentsFile = File(filesDir, EXPERIMENTS_JSON_FILENAME)
        val experimentSource = KintoExperimentSource(
                EXPERIMENTS_BASE_URL, EXPERIMENTS_BUCKET_NAME, EXPERIMENTS_COLLECTION_NAME
        )
        fretboard = Fretboard(experimentSource, FlatFileExperimentStorage(experimentsFile),
                object : ValuesProvider() {
                    override fun getClientId(context: Context): String {
                        return "10" // hardcode clientId to determine in or out of experiment
                    }
                })
        fretboard.loadExperiments()
        Logger.debug("Bucket is ${fretboard.getUserBucket(this)}")
        Logger.debug("Experiments active: ${fretboard.getExperimentsMap(this)}")
        GlobalScope.launch(IO) {
            fretboard.updateExperiments()
        }
    }

    companion object {
        const val NON_FATAL_CRASH_BROADCAST = "org.mozilla.reference.browser"
    }
}