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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CountViewModel : ViewModel() {
    private val _countState = MutableLiveData(CountState())
    val countState: LiveData<CountState> = _countState

    private val _uiState = MutableLiveData(UiState.Preview)
    val uiState: LiveData<UiState> = _uiState

    var countDownJob: Job? = null

    fun startCountDown() {
        _uiState.value = UiState.Play
        setUpCountState()
        countDownJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                val previous = countState.value ?: continue
                val next = previous.copy(
                    first = previous.first.inverseVisible(),
                    second = previous.second.inverseVisible()
                )
                _countState.value = next

                launch loop@{
                    delay(900)
                    _countState.value = next.copy(
                        first = next.first.decrementCount(),
                        second = next.second.decrementCount()
                    )
                }
                if (previous.first.count <= 0 || previous.second.count <= 0) {
                    break
                }
                delay(1000)
            }
        }
    }

    private fun setUpCountState() {
        val previous = countState.value ?: return
        val first = previous.first
        val second = previous.second

        if (first.visible) {
            _countState.value = previous.copy(
                first = first,
                second = second.setCount(first.count - 1)
            )
        } else {
            _countState.value = previous.copy(
                first = first.setCount(second.count - 1),
                second = second
            )
        }
    }

    fun pause() {
        countDownJob?.cancel()
        _uiState.value = UiState.Preview
    }

    fun plusCount(count: Int) {
        val previous = countState.value ?: return
        if ((previous.first.count <= 0 || previous.second.count <= 0) && count < 0) return

        _countState.value = previous.copy(
            first = previous.first.plusCount(count),
            second = previous.second.plusCount(count)
        )
    }
}

data class CountItem(
    val visible: Boolean = true,
    val count: Int = 0
) {
    fun setCount(count: Int) = copy(count = count)
    fun plusCount(count: Int) = copy(count = this.count + count)
    fun inverseVisible() = copy(visible = visible.not())
    fun decrementCount() = copy(count = if (visible) count else count - 2)
}

data class CountState(
    val first: CountItem = CountItem(),
    val second: CountItem = CountItem(visible = false, count = first.count - 1)
)

enum class UiState {
    Preview, Play
}
