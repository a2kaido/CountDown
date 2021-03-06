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
package com.example.androiddevchallenge.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.vm.CountState
import com.example.androiddevchallenge.ui.vm.CountViewModel

private const val fontSize: Int = 60
private fun <T> getTween() = tween<T>(durationMillis = 800)

@ExperimentalAnimationApi
@Composable
fun CountDownNumber(viewModel: CountViewModel) {
    val countState: CountState by viewModel.countState.observeAsState(CountState())

    AnimationNumber(countState.first.visible, countState.first.count.toString())
    AnimationNumber(countState.second.visible, countState.second.count.toString())
}

@ExperimentalAnimationApi
@Composable
fun AnimationNumber(visibility: Boolean, text: String) {
    val alpha: Float by animateFloatAsState(
        targetValue = if (visibility) 1f else 0f,
        animationSpec = getTween()
    )
    val size: Int by animateIntAsState(
        targetValue = if (visibility) fontSize else 30,
        animationSpec = getTween()
    )
    val offset = with(LocalDensity.current) { 60.dp.toPx() }.toInt()

    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically(
            initialOffsetY = { -offset },
            animationSpec = getTween()
        ),
        exit = slideOutVertically(
            targetOffsetY = { offset },
            animationSpec = getTween()
        ),
    ) {
        Text(
            text,
            fontSize = size.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .graphicsLayer(alpha = alpha)
        )
    }
}
