package com.example.test

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val REQUEST_CODE = 1

        // 마이크를 허용해달라고 요청한다.
        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), REQUEST_CODE)

        // 인터넷 연결 되어 있을 때 (셀룰러/와이파이)
        webview.settings.javaScriptEnabled = true // 자바 스크립트 허용

        // 웹뷰안에 새 창이 뜨지 않도록 방지
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()

        // 원하는 주소를 WebView에 연결
        webview.loadUrl("http://www.naver.com")

        micBtn.setOnClickListener(View.OnClickListener() {
            startSTT()

        })

        urlEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                menuBtn.setVisibility(View.GONE);
                micBtn.setVisibility(View.GONE);
            }else{
                menuBtn.setVisibility(View.VISIBLE);
                micBtn.setVisibility(View.VISIBLE);
            }

        }

        urlEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                v.clearFocus()
                webview.loadUrl("http://"+urlEditText.text)
                return@OnKeyListener true
            }
            false
        })



        refreshBtn.setOnClickListener{ v ->
            // 앱에서 자바스크립트 코드 실행시키기
            webview.loadUrl("javascript:location.reload()");
        }
    }
    // RecognitionListener 사용한 예제
    private fun startSTT() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }

    }

    private fun recognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@MainActivity, "퍼미션 없음", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResults(results: Bundle) {
            Log.e("result",SpeechRecognizer.RESULTS_RECOGNITION)
//            tv.text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
        }
    }

    override fun onDestroy() {

        if (speechRecognizer != null) {
            speechRecognizer!!.stopListening()
        }

        super.onDestroy()
    }

}
