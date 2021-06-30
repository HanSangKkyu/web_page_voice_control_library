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
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_blank.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var speechRecognizer: SpeechRecognizer? = null
    private var frList: HashMap<String,BlankFragment> = HashMap()
    private var selectedBtnTag: String = ""

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val REQUEST_CODE = 1

        // 마이크를 허용해달라고 요청한다.
        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), REQUEST_CODE)

        micBtn.setOnClickListener(View.OnClickListener() {
            startSTT()
        })

        urlEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                menuBtn.setVisibility(View.GONE)
                micBtn.setVisibility(View.GONE)
            }else{
                menuBtn.setVisibility(View.VISIBLE)
                micBtn.setVisibility(View.VISIBLE)
            }

        }

        urlEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                v.clearFocus()
                frList.get(selectedBtnTag)!!.changeUrl(urlEditText.text.toString())
                return@OnKeyListener true
            }
            false
        })



        refreshBtn.setOnClickListener{ v ->
            // 앱에서 자바스크립트 코드 실행시키기
            webview.loadUrl("javascript:location.reload()");

            webview.evaluateJavascript("(function(){return('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"){
                Log.e("it",it)
            }
        }


        tabBtn.setOnClickListener{ v ->
            val newTabBtn = Button(this)
            val charPool = arrayOf('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','z')

            val randomString = (1..10)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");

            newTabBtn.setLayoutParams(TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            newTabBtn.tag = randomString
            newTabBtn.text = randomString
            selectedBtnTag = randomString

            frList.put(newTabBtn.tag.toString(),BlankFragment())
            supportFragmentManager.beginTransaction().add(R.id.frame, frList.get(newTabBtn.tag.toString())!!).commit()


            newTabBtn.setOnClickListener{v ->
                supportFragmentManager.beginTransaction().hide(frList.get(selectedBtnTag)!!).commit()
                supportFragmentManager.beginTransaction().show(frList.get(newTabBtn.tag.toString())!!).commit()
                selectedBtnTag = newTabBtn.tag.toString()


            }

            newTabBtn.setOnLongClickListener { v ->
                supportFragmentManager.beginTransaction().remove(frList.get(selectedBtnTag)!!).commit()
                newTabBtn.visibility = View.GONE
                return@setOnLongClickListener true
            }

            tabList.addView(newTabBtn)
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
            Log.e("음성 인식 결과:", results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0])
        }
    }

    override fun onDestroy() {

        if (speechRecognizer != null) {
            speechRecognizer!!.stopListening()
        }

        super.onDestroy()
    }

}
