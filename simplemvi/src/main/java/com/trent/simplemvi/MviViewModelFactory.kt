/*
 *    Copyright 2018 Trent Oh
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.trent.simplemvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trent.simplemvi.mvi.MviProcessorHolder
import com.trent.simplemvi.mvi.MviReducerHolder
import java.lang.reflect.InvocationTargetException

class MviViewModelFactory(
    private val processorHolder: MviProcessorHolder<*, *>,
    private val reducerHolder: MviReducerHolder<*, *>
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(
                MviProcessorHolder::class.java,
                MviReducerHolder::class.java
            ).newInstance(processorHolder, reducerHolder)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }
}