package com.ui.simplestories.components

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState


@ExperimentalComposeUiApi
@OptIn(ExperimentalPagerApi::class)
@Composable
fun StoryImage(
    pagerState: PagerState, onTap: (Boolean) -> Unit, onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit, content: @Composable (Int) -> Unit
) {

    val context = LocalContext.current
    val activityContext = context.applicationContext
    val displayMetrics = activityContext?.resources?.displayMetrics
    val width = displayMetrics?.widthPixels
    HorizontalPager(
        state = pagerState,
        dragEnabled = false,
        modifier = Modifier.pointerInteropFilter { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onTap(true)
                }
                MotionEvent.ACTION_UP -> {
                    onTap(false)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.x < width!! / 2) {
                        onSwipeLeft()
                    } else
                        onSwipeRight()
                }
            }
            true
        }) {
        content(it)
    }
}
