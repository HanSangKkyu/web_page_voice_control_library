package com.example.test

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_speaker_recnition.*
import java.io.File
import java.nio.charset.Charset
import org.json.JSONObject




class SpeakerRecnitionActivity : AppCompatActivity() {
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.pcm"
    }
    private val wavFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.wav"
    }
    val audioRecord : OnlyAudioRecorder = OnlyAudioRecorder.instance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speaker_recnition)

        requestMic()
        initWidget()

        if(getProfileID() == "not Created"){
            // 아직 음성 프로필을 만들지 않은 것임
            infoText.text = "음성 프로필이 없습니다. 음성 프로필을 만들어 주세요."
            createProfile()
        }else{
            infoText.text = "profileID: "+getProfileID()
        }
    }

    private fun initWidget() {
        registBtn.setOnClickListener{ v ->
            startREC()
        }


        if(getOnSRChk() == "on"){
            onSRChk.isChecked = true
        }else{
            // off
            onSRChk.isChecked = false
        }

        onSRChk.setOnCheckedChangeListener  { _, isChecked ->
            val sharedPref = getSharedPreferences("onSRChk", Context.MODE_PRIVATE)
            if (isChecked) {
                with(sharedPref.edit()) {
                    putString("onSRChk", "on")
                    commit()
                }
            }else {
                with(sharedPref.edit()) {
                    putString("onSRChk", "off")
                    commit()
                }
            }
        }



    }

    fun getProfileID(): String? {
        val sharedPref = getSharedPreferences("profileId", Context.MODE_PRIVATE)
        return sharedPref.getString("profileId", "not Created")
    }

    fun getOnSRChk(): String? {
        val sharedPref = getSharedPreferences("onSRChk", Context.MODE_PRIVATE)
        return sharedPref.getString("onSRChk", "off")
    }


    private fun requestMic() {
        // 마이크를 허용해달라고 요청한다.
        val REQUEST_CODE = 1

        if (Build.VERSION.SDK_INT >= 23)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE
            )
    }

    private fun startREC(){
        // 파일 경로 설정
        audioRecord.PCMPath = recordingFilePath
        audioRecord.WAVPath = wavFilePath

        audioRecord.startRecord() //Start recording

        Thread(Runnable {
            var startFlag = false
            var cnt = 10 // 1초 동안 말이 없으면 녹음을 멈춘다.
            while(true) {
                Thread.sleep(100L) // 0.1초 마다 발화를 하고 있는 상태인지 확인한다.
                var Amplitude =  audioRecord.Amplitude
                Log.e("asdf",Amplitude.toString())
                if(Amplitude.toString().toInt() > 100000){
                    if(!startFlag) {
                        startFlag = true
                    }
                    cnt = 10
                }

                if(Amplitude.toString().toInt() < 100000 && startFlag){
                    cnt --
                    if(cnt == 0){
                        stopREC()
                        break
                    }
                }

            }
        }).start()

    }

    private fun stopREC() {

        audioRecord.stopRecord()

        Thread(Runnable {
            while(true) {
                Thread.sleep(100L) // 0.1초 마다 wav파일이 완성되었는지 확인한다.
                if(audioRecord.isWavComplete){
                    Log.e("asdf","wav file completed!")
                    enroll() // 프로필을 등록합니다.
                    break
                }
            }
        }).start()


//        음성인식 결과 듣고 싶을 때 사용
//        var mediaPlayer = MediaPlayer()
//        mediaPlayer.setDataSource(wavFilePath)
//        mediaPlayer.prepare()
//        mediaPlayer.start()
    }

    private fun createProfile() {
        // 프로필을 생성합니다.
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            "https://westus.api.cognitive.microsoft.com/speaker/verification/v2.0/text-independent/profiles",
            Response.Listener {
                val response = it
                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))


                val jObject = JSONObject(json)
                val profileId: String = jObject.getString("profileId")
                val sharedPref = getSharedPreferences("profileId", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("profileId", profileId)
                    commit()
                }
                infoText.text = "profileID: "+getProfileID()

                println("error is: $it $json")
            },
            Response.ErrorListener {
                val response = it.networkResponse
                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

                println("error is: $it $json")
            }
        ) {
            // wav 파일 보내기
            override fun getBody(): ByteArray {
                val json = "{\"locale\":\"en-us\"}"
                return json.toByteArray()
            }

            // Providing Request Headers
            override fun getHeaders(): MutableMap<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()
                headers["Ocp-Apim-Subscription-Key"] = "11dee688d18444d9837321f89ce98c38"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun enroll() {
        // 프로필을 등록합니다.
        val request = object : VolleyFileUploadRequest(
            Request.Method.POST,
            "https://westus.api.cognitive.microsoft.com/speaker/verification/v2.0/text-independent/profiles/"+getProfileID()+"/enrollments",
            Response.Listener {
                val response = it
                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

                val jObject = JSONObject(json)
                val enrollmentStatus: String = jObject.getString("enrollmentStatus")

                val enrollmentsSpeechLength = jObject.getInt("enrollmentsSpeechLength")
                if(enrollmentStatus != "Enrolled"){
                    infoText.text = "더 입력해서 등록을 완료해주세요. 총 20초의 음성을 입력해야 합니다. 현재 총 $enrollmentsSpeechLength 초 입력되었습니다."
                }else{
                    infoText.text = "등록이 완료되었습니다."
                }

                println("error is: $it $json")
            },
            Response.ErrorListener {
                val response = it.networkResponse
                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

                println("error is: $it $json")
            }
        ) {
            // wav 파일 보내기
            override fun getBody(): ByteArray {
                return File(wavFilePath).readBytes()
            }

            // Providing Request Headers
            override fun getHeaders(): MutableMap<String, String> {
                // Create HashMap of your Headers as the example provided below

                val headers = HashMap<String, String>()
                headers["Ocp-Apim-Subscription-Key"] = "11dee688d18444d9837321f89ce98c38"
                headers["Content-Type"] = "audio/wav"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}