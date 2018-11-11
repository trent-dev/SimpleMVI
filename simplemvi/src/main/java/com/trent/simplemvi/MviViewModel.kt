package com.trent.simplemvi

import androidx.lifecycle.ViewModel
import com.trent.simplemvi.mvi.MviProcessorHolder
import com.trent.simplemvi.mvi.MviReducerHolder
import com.trent.simplemvi.mvi.components.MviIntent
import com.trent.simplemvi.mvi.components.MviResult
import com.trent.simplemvi.mvi.components.MviViewState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

abstract class MviViewModel<I : MviIntent, R : MviResult, S : MviViewState>(
    private val processorHolder: MviProcessorHolder<I, R>,
    private val reducerHolder: MviReducerHolder<R, S>
) : ViewModel() {

    private val intentsSubject: PublishSubject<I> = PublishSubject.create()

    abstract val initialState: S

    private var cachedState: S? = null

    fun processIntents(intents: Observable<I>): Disposable {
        return intents.subscribe { intentsSubject.onNext(it) }
    }

    fun states(): Observable<S> {
        return intentsSubject
            .compose(this::processToObservable)
            .scan(cachedState ?: initialState, reducerHolder::resultReducer)
            .doOnNext { cachedState = it }
    }

    private fun processToObservable(intents: Observable<I>) = intents.flatMap { intent: I ->
        val (sync, asyncObservable) = processorHolder.intentProcessor(intent)
        val syncObservable = Observable.just(sync).concatMapIterable { it -> it }

        if (asyncObservable != null) Observable.merge(syncObservable, asyncObservable)
        else syncObservable
    }
}