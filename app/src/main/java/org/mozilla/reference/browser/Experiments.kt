package org.mozilla.reference.browser

import android.content.Context
import mozilla.components.service.fretboard.ExperimentDescriptor

const val EXPERIMENTS_JSON_FILENAME = "experiments.json"
const val EXPERIMENTS_BASE_URL = "https://settings.prod.mozaws.net/v1"
const val EXPERIMENTS_BUCKET_NAME = "main"
const val EXPERIMENTS_COLLECTION_NAME = "focus-experiments"

val AATestDescriptor = ExperimentDescriptor("AAtest")

val Context.app: BrowserApplication
    get() = applicationContext as BrowserApplication

fun Context.isInExperiment(descriptor: ExperimentDescriptor): Boolean =
        app.fretboard.isInExperiment(this, descriptor)
