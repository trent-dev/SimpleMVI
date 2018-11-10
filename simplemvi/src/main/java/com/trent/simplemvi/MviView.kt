package com.trent.simplemvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.trent.simplemvi.mvi.components.MviIntent
import com.trent.simplemvi.mvi.components.MviResult
import com.trent.simplemvi.mvi.components.MviViewState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class MviView<I : MviIntent, R : MviResult, S : MviViewState>(private val viewModel: MviViewModel<I, R, S>) :
    LifecycleObserver {

    protected val intentsSubject = PublishSubject.create<I>()

    private fun intents(): Observable<I> = intentsSubject

    abstract fun render(state: S)

    /**
     * Livedata-like
     */
    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        disposables.add(viewModel.processIntents(intents()))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        disposables.clear()
    }
}