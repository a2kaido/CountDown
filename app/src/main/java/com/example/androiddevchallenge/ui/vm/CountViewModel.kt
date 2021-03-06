/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CountViewModel: ViewModel() {
    private val _countState = MutableLiveData(CountState())
    val countState: LiveData<CountState> = _countState

    init {
        viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                val previous = countState.value ?: continue
                _countState.value = previous.copy(
                    first = previous.first.inverseVisible(),
                    second = previous.second.inverseVisible()
                )

                launch loop@ {
                    delay(500)
                    val previous2 = countState.value ?: return@loop

                    _countState.value = previous2.copy(
                        first = previous2.first.decrementCount(),
                        second = previous2.second.decrementCount()
                    )
                }
                if (previous.first.count <= 0 || previous.second.count <= 0) {
                    break
                }
                delay(1000)
            }
        }
    }
}

data class CountItem(
    val visible: Boolean = true,
    val count: Int = 100
) {
    fun inverseVisible() = copy(visible = visible.not())
    fun decrementCount() = copy(count = if (visible) count else count - 2)
}

data class CountState(
    val first: CountItem = CountItem(),
    val second: CountItem = CountItem(visible = false, count = first.count - 1)
)
