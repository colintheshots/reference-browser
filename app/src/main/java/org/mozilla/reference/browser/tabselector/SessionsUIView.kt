package org.mozilla.reference.browser.tabselector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.component_sessions.*
import mozilla.components.browser.session.Session
import org.mozilla.reference.browser.R
import org.mozilla.reference.browser.mvi.ActionBusFactory
import org.mozilla.reference.browser.mvi.UIView
import org.mozilla.reference.browser.mvi.ViewState

class SessionsUIView(container: ViewGroup, bus: ActionBusFactory,
                     initialState: SessionViewState = SessionViewState())
    : UIView<SessionViewState>(container, bus) {

    val view: ConstraintLayout = LayoutInflater.from(container.context)
            .inflate(R.layout.component_sessions, container, true) as ConstraintLayout

    private var sessionAdapter = SessionsAdapter(bus)

    init {
        recycler_sessions.apply {
            layoutManager = LinearLayoutManager(view.context)
            sessionAdapter.sessions = initialState.sessions
            adapter = sessionAdapter
        }
    }

    override fun show() {
        view.visibility = View.VISIBLE
    }

    override fun hide() {
        view.visibility = View.GONE
    }

    override fun updateView() = Consumer<SessionViewState> {
        sessionAdapter.updateSessions(it.sessions)
    }
}

data class SessionViewState(val sessions: List<Session> = listOf()) : ViewState