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
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.component.CountDownNumber
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.vm.CountViewModel
import com.example.androiddevchallenge.ui.vm.UiState

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<CountViewModel>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(viewModel = viewModel)
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp(viewModel: CountViewModel) {
    val uiState: UiState by viewModel.uiState.observeAsState(UiState.Preview)

    Surface(color = MaterialTheme.colors.background) {
        TopAppBar(
            title = { Text("CountDown") }
        )

        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CountDownNumber(viewModel = viewModel)
        }

        Box(
            modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            PlayButton(viewModel)
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(end = 24.dp)
                .graphicsLayer(alpha = if (uiState == UiState.Preview) 1f else 0f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Column {
                TextButton(
                    onClick = { viewModel.plusCount(1) }
                ) {
                    Text(
                        text = "+1",
                        fontSize = 24.sp
                    )
                }
                TextButton(
                    onClick = { viewModel.plusCount(-1) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "-1",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PlayButton(viewModel: CountViewModel) {
    val uiState: UiState by viewModel.uiState.observeAsState(UiState.Preview)

    Crossfade(targetState = uiState) {
        when (it) {
            UiState.Preview -> Button(onClick = { viewModel.startCountDown() }) {
                Image(painterResource(id = R.drawable.ic_play_arrow), null)
            }
            UiState.Play -> Button(onClick = { viewModel.pause() }) {
                Image(painterResource(id = R.drawable.ic_pause), null)
            }
        }
    }
}
