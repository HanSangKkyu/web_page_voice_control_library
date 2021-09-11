package com.example.test

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView
import androidx.annotation.RequiresApi

class MyWebView : WebView {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, int: Int): super(context, attributeSet, int)
    constructor(context: Context, attributeSet: AttributeSet, int: Int, boolean: Boolean): super(context, attributeSet, int, boolean)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, int1: Int, int2: Int): super(context, attributeSet, int1, int2)

    // 강제로 줌을 가능하게 만들 것인지
    var forcedZoom : Boolean = true

    val verticalScrollableRange: Int
        get() {
            return computeVerticalScrollRange() - height
        }

    val horizontalScrollableRange : Int
        get() {
            return computeHorizontalScrollRange() - width
        }
}