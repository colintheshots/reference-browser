package org.mozilla.reference.browser.tabselector

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import org.mozilla.reference.browser.mvi.Action
import org.mozilla.reference.browser.mvi.ActionBusFactory
import org.mozilla.reference.browser.mvi.Change
import org.mozilla.reference.browser.mvi.UIComponent

open class SessionsComponent(private val sessionManager: SessionManager,
                             private val container: ViewGroup,
                             final override val bus: ActionBusFactory,
                             private val callback: (String) -> Unit)
    : UIComponent<SessionViewState, SessionsActions, SessionsChanges>(bus) {

    override var initialState: SessionViewState = SessionViewState(sessionManager.sessions)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override val uiView = initView()

    override fun initView() = SessionsUIView(container, bus)

    override val reducer : (SessionViewState, SessionsChanges) -> SessionViewState = { state, change ->
        when (change) {
            is SessionsChanges.SessionsChanged -> state.copy(sessions = sessionManager.sessions)
        }
    }

    @SuppressLint("CheckResult")
    fun setup(): SessionsComponent {
        render(reducer)
        sessionManager.register(object : SessionManager.Observer {
            override fun onSessionAdded(session: Session) {
                super.onSessionAdded(session)
                sessionChanged()
            }

            override fun onSessionRemoved(session: Session) {
                super.onSessionRemoved(session)
                sessionChanged()
            }

            override fun onAllSessionsRemoved() {
                super.onAllSessionsRemoved()
                sessionChanged()
            }

            override fun onSessionsRestored() {
                super.onSessionsRestored()
                sessionChanged()
            }
        }) // TODO Unregister at some point

        getUserInteractionEvents()
                .subscribe {
                    when (it) {
                        is SessionsActions.SelectSession -> {
                            callback.invoke(it.session.id)
                        }
                        is SessionsActions.RemoveSession -> {
                            sessionManager.remove(it.session)
                        }
                    }
                }
        return this
    }

    override fun getContainerId(): Int {
        return uiView.containerId
    }

    override fun getUserInteractionEvents(): Observable<SessionsActions> {
        return bus.getSafeManagedObservable(SessionsActions::class.java)
    }

    override fun getModelChangeEvents(): Observable<SessionsChanges> {
        return bus.getSafeManagedObservable(SessionsChanges::class.java)
    }

    fun sessionChanged() {
        bus.emit(SessionsChanges::class.java, SessionsChanges.SessionsChanged)
    }
}

sealed class SessionsActions : Action {
    data class SelectSession(val session: Session) : SessionsActions()
    data class RemoveSession(val session: Session) : SessionsActions()
}

sealed class SessionsChanges : Change {
    object SessionsChanged : SessionsChanges()
}