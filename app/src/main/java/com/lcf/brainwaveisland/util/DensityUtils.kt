package com.lcf.brainwaveisland.util

import android.content.Context

fun Number.dp(context: Context): Float {
    val scale: Float = context.resources.displayMetrics.density
    return (this.toFloat() * scale + 0.5f)
}

fun Number.px(context: Context): Float {
    val scale: Float = context.resources.displayMetrics.density
    return this.toFloat() / scale
}