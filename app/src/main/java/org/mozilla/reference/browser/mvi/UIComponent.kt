package org.mozilla.reference.browser.mvi

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class UIComponent<S : ViewState, A, C>(open val bus: ActionBusFactory) {
    abstract var initialState: S
    abstract val reducer: Reducer<S, C>
    abstract val uiView: UIView<S>

    abstract fun initView(): UIView<S>
    abstract fun getContainerId(): Int
    abstract fun getUserInteractionEvents(): Observable<A>
    abstract fun getModelChangeEvents(): Observable<C>

    /**
     * Render the ViewState to the View through the Reducer
     */
    inline fun <reified C : Change> render(noinline reducer: Reducer<S, C>): Disposable =
            bus.getSafeManagedObservable(C::class.java)
                    .scan(initialState, reducer)
                    .distinctUntilChanged()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uiView.updateView())
}