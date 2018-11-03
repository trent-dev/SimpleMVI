package com.trent.simplemvi

import android.arch.lifecycle.ViewModel
import com.trent.simplemvi.mvi.MviProcessorHolder
import com.trent.simplemvi.mvi.MviReducerHolder
import com.trent.simplemvi.mvi.components.MviIntent
import com.trent.simplemvi.mvi.components.MviResult
import com.trent.simplemvi.mvi.components.MviViewState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

abstract class MviViewModel<I : MviIntent, R : MviResult, S : MviViewState>(
    private val processorHolder: MviProcessorHolder<I, R>,
    private val reducerHolder: MviReducerHolder<R, S>
) : ViewModel() {

    private val intentsSubject: PublishSubject<I> = PublishSubject.create()

    abstract val initialState: S

    fun processIntents(intents: Observable<I>): Disposable {
        Timber.d("processIntents")
        return intents.subscribe { intentsSubject.onNext(it) }
    }

    fun states(): Observable<S> {
        Timber.d("states")
        return intentsSubject
            .compose(processorHolder.intentProcessor)
            .scan(initialState, reducerHolder.resultReducer)
    }
}