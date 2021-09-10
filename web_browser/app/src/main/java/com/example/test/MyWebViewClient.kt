package com.example.test

import android.os.Build
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject

class MyWebViewClient : WebViewClient() {

    var zoomable: Boolean = true
    var maxScale: Float = 5.0f
    var minScale: Float = 1.0f

    // 페이지 로드 후 name 속성이 viewport인 meta 태그의 내용을 분석하여
    // 확대 가능 여부, 최대/최소 확대 가능 수치를 받아오며
    // 강제 줌을 세팅한 경우 그에 맞게 meta 태그를 변형한다.
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        var json : JSONObject = JSONObject()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (view is MyWebView && view.forcedZoom == true) {
                view?.evaluateJavascript(
                    "javascript:(function() {" +
                            "   const viewportQuery = document.querySelector('meta[name\"viewport\"]');" +
                            "   if(viewportQuery && viewportQuery.content) {" +
                            "       const str = viewportQuery.content.split(/[,=]+/);" +
                            "       let newStr = String();" +
                            "       let obj = new Object();" +
                            "       for(let i = 0; i < str.length; i += 2) {" +
                            "           str[i] = str[i].trim();" +
                            "           str[i+1] = str[i+1].trim();" +
                            "           if(str[i] == 'user-scalable' && str[i+1] == 'no) {" +
                            "               str[i+1] = 'yes';" +
                            "           }" +
                            "           if(str[i] == 'maximum-scale' && parseFloat(str[i+1]) < 5.0) {" +
                            "               str[i+1] = '5.0';" +
                            "           }" +
                            "           newStr = newStr.concat(str[i], '=', str[i+1], ', ');" +
                            "           obj[str[i]] = str[i+1];" +
                            "       }" +
                            "       viewportQuery.content = newStr;" +
                            "       return JSON.stringify(obj);" +
                            "   }" +
                            "   return '{}';" +
                            "})()"
                ) {
                    json = JSONObject(it.replace("\"", "").replace("\\", "\""))
                }
            } else {
                view?.evaluateJavascript("javascript:(function(){" +
                        "   const viewportQuery = document.querySelector('meta[name\"viewport\"]');" +
                        "   if(viewportQuery && viewportQuery.content) {" +
                        "       const str = viewportQuery.content.split(/[,=]+/);" +
                        "       let obj = new Object();" +
                        "       for(let i = 0; i < str.length; i += 2) {" +
                        "           str[i] = str[i].trim();" +
                        "           str[i+1] = str[i+1].trim();" +
                        "           newStr = newStr.concat(str[i], '=', str[i+1], ', ');" +
                        "           obj[str[i]] = str[i+1];" +
                        "       }" +
                        "       return JSON.stringify(obj);" +
                        "   }" +
                        "   return '{}';" +
                        "})()") {
                    json = JSONObject(it.replace("\"", "").replace("\\", "\""))
                }
            }
            zoomable = json.getString("user-scalable") == "yes"
            maxScale = json.getString("maximum-scale").toFloat()
            minScale = json.getString("minimum-scale").toFloat()
        }
    }
}