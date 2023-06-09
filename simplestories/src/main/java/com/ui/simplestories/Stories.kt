package com.ui.simplestories

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ui.simplestories.components.LinearIndicator
import com.ui.simplestories.components.StoryImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Stories(
    numberOfPages: Int,
    touchToPause: Boolean = true,
    hideIndicators: Boolean = false,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
    content: @Composable (Int) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = numberOfPages)
    val coroutineScope = rememberCoroutineScope()

    var pauseTimer by remember {
        mutableStateOf(false)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Full screen content behind the indicator
        StoryImage(
            pagerState = pagerState, onTap = {
                if (touchToPause)
                    pauseTimer = it
            },
            onSwipeLeft = {




                if (pagerState.currentPage > 0) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(pagerState.currentPage - 1, 0f)
                    }


                }
            },
            onSwipeRight = {
                if (pagerState.currentPage < pagerState.pageCount - 1) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(page = pagerState.currentPage + 1, pageOffset = 1f)
                    }
                }
            },
            content
        )

        //Indicator based on the number of items
        val modifier =
            if (hideIndicators) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Black,
                                Color.Transparent
                            )
                        )
                    )
            }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.padding(4.dp))

            ListOfIndicators(
                numberOfPages,
                pauseTimer,
                hideIndicators,
                pagerState,
                onEveryStoryChange = onEveryStoryChange,
                onComplete = onComplete,
                coroutineScope
            )
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RowScope.ListOfIndicators(
    numberOfPages: Int,
    pauseTimer: Boolean,
    hideIndicators: Boolean,
    pagerState: PagerState,
    onEveryStoryChange: ((Int) -> Unit)? = null,
    onComplete: () -> Unit,
    coroutineScope: CoroutineScope

) {
    var currentPage by remember {
        mutableStateOf(0)
    }

    for (index in 0 until numberOfPages) {
        LinearIndicator(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .weight(1f),
            index == currentPage,
            pauseTimer,
            hideIndicators
        ) {
            coroutineScope.launch {

                currentPage++

                if (currentPage < numberOfPages) {
                    onEveryStoryChange?.invoke(currentPage)
                    pagerState.animateScrollToPage(currentPage)
                }

                if (currentPage == numberOfPages) {
                    onComplete()
                }
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))
    }
}