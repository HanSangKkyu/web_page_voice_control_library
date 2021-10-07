package com.example.test

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class MyWebViewClient : WebViewClient() {

    var zoomable: Boolean = true
    var maxScale: Float = 5.0f
    var minScale: Float = 1.0f
    var presentScale: Float = 1.0f

    // 페이지 로드 후 name 속성이 viewport인 meta 태그의 내용을 분석하여
    // 확대 가능 여부, 최대/최소 확대 가능 수치를 받아오며
    // 강제 줌을 세팅한 경우 그에 맞게 meta 태그를 변형한다.
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        try {
            var json: JSONObject = JSONObject()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view?.evaluateJavascript(
                    "javascript:(function(){" +

                            "   const viewportQuery = document.querySelector('meta[name=\"viewport\"]');" +
                            "       if(viewportQuery && viewportQuery.content) {" +
                            "           const str = viewportQuery.content.split(/[,=]+/);" +
                            "           let obj = new Object();" +
                            "           for(let i = 0; i < str.length-1; i += 2) {" +
                            "               str[i] = str[i].trim();" +
                            "               str[i+1] = str[i+1].trim();" +
                            "               obj[str[i]] = str[i+1];" +
                            "           }" +
                            "           return JSON.stringify(obj);" +
                            "      }" +
                            "   return '{}';" +
                            "})()"
                ) {
                    Log.v("테스트", it)
                    json = JSONObject(it.replace("\"", "").replace("\\", "\""))
                    var doesUserScalableExist = false
                    var doesMaxScaleExist = false
                    var doesMinScaleExist = false
                    var doesInitialScaleExist = false
                    for (key in json.keys()) {
                        if (key == "user-scalable" && view is MyWebView && view.forcedZoom == true) {
                            doesUserScalableExist = true
                            json.put("user-scalable", "yes")
                        }
                        if (key == "maximum-scale") {
                            doesMaxScaleExist = true
                            if (json.getString(key).toFloat() < 5.0f) {
                                json.put("maximum-scale", "5.0")
                            }
                        }
                        if (key == "minimum-scale") {
                            doesMinScaleExist = true
                        }
                        if (key == "initial-scale") {
                            doesInitialScaleExist = true
                        }
                    }
                    if (!doesUserScalableExist) {
                        json.put("user-scalable", "yes")
                }
                    if (!doesMaxScaleExist) {
                        json.put("maximum-scale", "5.0")
                    }
                    if (!doesMinScaleExist) {
                        json.put("minimum-scale", "1.0")
                    }
                    if (!doesInitialScaleExist) {
                        json.put("initial-scale", "1.0")
                    }
                    var newStr: String = ""
                    for (key in json.keys()) {
                        newStr = newStr.plus(key).plus('=').plus(json.getString(key)).plus(',')
                    }
                    newStr = newStr.substring(0, newStr.length-1)
                    view?.evaluateJavascript(
                        "javascript:(function() {" +
                                "   document.querySelector('meta[name=\"viewport\"]').content = '$newStr';" +
                                "   return document.querySelector('meta[name=\"viewport\"]').content" +
                                "})()"
                    ) {}
                    zoomable = json.getString("user-scalable") == "yes"
                    maxScale = json.getString("maximum-scale").toFloat()
                    minScale = json.getString("minimum-scale").toFloat()
//                    presentScale = json.getString("initial-scale").toFloat()
                }
            }

        } catch(e: JSONException) {
            Log.e("테스트", e.localizedMessage)
        }
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        super.onScaleChanged(view, oldScale, newScale)
        presentScale = newScale
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)


        try{
            Thread(Runnable {
                val doc: Document = Jsoup.connect(url).get()
                Log.e("asdf",url.toString())
                Log.e("asdf",doc.title())
                var titleText = doc.title()
                if(titleText.length > 5){
                    titleText = titleText.substring(0,5)+".."
                }
                MainActivity.changeBtnTitle(titleText)
            }).start()
        }
        catch(e : Exception){
            e.printStackTrace()
        }



    }


}