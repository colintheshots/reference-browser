/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.tabselector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.mozilla.reference.browser.R
import org.mozilla.reference.browser.browser.BrowserFragment
import org.mozilla.reference.browser.ext.requireComponents
import org.mozilla.reference.browser.mvi.ActionBusFactory

class SessionsFragment : Fragment() {

    private val bus = ActionBusFactory.get(this)
    private lateinit var sessionsComponent: SessionsComponent

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<ConstraintLayout>(R.id.componentRoot)
        initializeUIComponents(container)
        layoutUIComponents(sessionsComponent.uiView.view)
    }

    @SuppressLint("CheckResult")
    private fun initializeUIComponents(container: ViewGroup) {
        sessionsComponent = SessionsComponent(requireComponents.core.sessionManager, container, bus
        ) { id ->
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.container, BrowserFragment.create(id))
                        .commit()
            }
        }
        sessionsComponent.setup()
        bus.logMergedObservables()
    }

    companion object {
        private const val SESSION_ID = "session_id"

        fun create(sessionId: String? = null): SessionsFragment = SessionsFragment().apply {
            arguments = Bundle().apply {
                putString(SESSION_ID, sessionId)
            }
        }
    }
}