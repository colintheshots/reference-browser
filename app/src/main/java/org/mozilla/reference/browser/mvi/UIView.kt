/*
 * Copyright (C) 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by Juliano Moraes, Rohan Dhruva, Emmanuel Boudrant.
 */
package org.mozilla.reference.browser.mvi

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import io.reactivex.functions.Consumer
import kotlinx.android.extensions.LayoutContainer

abstract class UIView<S : ViewState>(
        val container: ViewGroup, val bus: ActionBusFactory) : LayoutContainer {
    /**
     * Get the XML id for the UIView
     */
    @get:IdRes
    val containerId: Int
        get() = container.id

    /**
     * Provides container to empower Kotlin Android Extensions
     */
    override val containerView: View?
        get() = container

    /**
     * Show the UIView
     */
    abstract fun show()

    /**
     * Hide the UIView
     */
    abstract fun hide()

    /**
     * Update the view from the ViewState
     */
    abstract fun updateView(): Consumer<S>
}