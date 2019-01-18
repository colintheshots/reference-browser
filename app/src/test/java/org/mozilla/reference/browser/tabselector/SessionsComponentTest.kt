package org.mozilla.reference.browser.tabselector

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mozilla.reference.browser.mvi.ActionBusFactory


class SessionsComponentTest {

    @MockK private lateinit var sessionManager: SessionManager
    @MockK private lateinit var callback: (String) -> Unit

    private val owner = mockk<LifecycleOwner> {
        every { lifecycle } returns mockk()
        every { lifecycle.addObserver(any()) } just Runs
    }
    private val bus: ActionBusFactory = ActionBusFactory.get(owner)

    private lateinit var sessionsComponent : SessionsComponent
    private lateinit var actionsObserver : TestObserver<SessionsActions>
    private lateinit var changesObserver : TestObserver<SessionsChanges>

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        every { sessionManager.sessions } returns listOf()
        every { sessionManager.register(any()) } just Runs
        every { sessionManager.remove(any()) } just Runs
        every { callback.invoke(any()) } just Runs
        sessionsComponent = spyk (TestSessionsComponent(sessionManager, mockk(), bus, callback),
                recordPrivateCalls = true)
        sessionsComponent.setup()
    }

    @Test
    fun `select session`() {
        actionsObserver = sessionsComponent.getUserInteractionEvents().test()
        val session = Session("abc123", id = "123")
        bus.emit(SessionsActions::class.java, SessionsActions.SelectSession(session))
        verify { callback.invoke(session.id) }
        actionsObserver.assertSubscribed().awaitCount(1)
        actionsObserver.assertNoErrors()
                .assertValue(SessionsActions.SelectSession(session))
    }

    @Test
    fun `remove session`() {
        actionsObserver = sessionsComponent.getUserInteractionEvents().test()
        val session = Session("def456", id = "456")
        bus.emit(SessionsActions::class.java, SessionsActions.RemoveSession(session))
        verify { sessionManager.remove(session) }
        actionsObserver.assertSubscribed().awaitCount(1)
        actionsObserver.assertNoErrors()
                .assertValue(SessionsActions.RemoveSession(session))
    }

    @Test
    fun `sessions changed so change data shown`() {
        changesObserver = sessionsComponent.getModelChangeEvents().test()
        sessionsComponent.sessionChanged()
        changesObserver.assertSubscribed().awaitCount(1)
        changesObserver.assertNoErrors()
                .assertValue(SessionsChanges.SessionsChanged)
    }

    class TestSessionsComponent(sessionManager: SessionManager, container: ViewGroup,
                                bus: ActionBusFactory, callback: (String) -> Unit) :
            SessionsComponent(sessionManager, container, bus, callback) {
        override fun initView(): SessionsUIView = mockk(relaxed = true)
    }
}