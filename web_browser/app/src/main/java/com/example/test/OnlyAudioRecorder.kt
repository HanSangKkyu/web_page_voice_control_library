package com.example.test
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.charset.Charset

/**
 *@author :
 *@date : 2020/1/17
 *@description :Audio only class
 */
class OnlyAudioRecorder private constructor(){
    //1. Set recording related parameters, audio acquisition source, sampling rate, channel and data format
    //2. Calculate the minimum recording buffer size
    //3. Create audioRecord object
    //4. Start recording
    //5. Create files to save PCM files
    //6. Close recording and release related resources after recording
    //7. Convert pcm file to WAV file

    companion object{
        private const val TAG:String = "OnlyAudioRecorder"
        private const val AudioSource = MediaRecorder.AudioSource.MIC//Student source
        private const val SampleRate = 44100//sampling rate
        private const val Channel = AudioFormat.CHANNEL_IN_MONO//Mono channel
        private const val EncodingType = AudioFormat.ENCODING_PCM_16BIT//data format

        //Single example of double check
        val instance:OnlyAudioRecorder by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED){
            OnlyAudioRecorder()
        }
    }
    var PCMPath = Environment.getExternalStorageDirectory().path.toString()+"/RawAudio.pcm"
    var WAVPath = Environment.getExternalStorageDirectory().path.toString()+"/FinalAudio.wav"
    var Amplitude = 0
    var isWavComplete = false // wav 파일을 완성했는지

    private var bufferSizeInByte:Int = 0//Minimum recording buffer
    private var audioRecorder:AudioRecord? = null//Recording object
    private var isRecord = false

    private fun initRecorder() {//Initializing the audioRecord object

        bufferSizeInByte = AudioRecord.getMinBufferSize(SampleRate, Channel, EncodingType)
        audioRecorder = AudioRecord(AudioSource, SampleRate, Channel,
            EncodingType, bufferSizeInByte)
    }

    fun startRecord():Int {

        if (isRecord) {
            return -1
        } else{

            audioRecorder?: initRecorder()
            audioRecorder?.startRecording()
            isRecord = true

            AudioRecordToFile().start()
            isWavComplete = false

            return 0
        }
    }

    fun stopRecord() {

        audioRecorder?.stop()
        audioRecorder?.release()
        isRecord = false
        audioRecorder = null
    }

    private fun writeDateTOFile() {

        var audioData = ByteArray(bufferSizeInByte)
        val file = File(PCMPath)
        if (!file.parentFile.exists()) {

            file.parentFile.mkdirs()
        }
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val out = BufferedOutputStream(FileOutputStream(file))
        var length = 0
        while (isRecord && audioRecorder!=null) {
            var sum = 0
            for (i in 0 until bufferSizeInByte) {
                sum += Math.abs(audioData.get(i).toInt())
            }

            if (bufferSizeInByte > 0) {
//                println("here is ${sum}")
                Amplitude = sum
            }

            try{
                length = audioRecorder!!.read(audioData, 0, bufferSizeInByte)//Get audio data
            }catch (e:Exception){
                Log.e("asdf",e.toString())
            }


            if (AudioRecord.ERROR_INVALID_OPERATION != length) {
                try{
                    out.write(audioData, 0, length)//write file
                }catch (e:Exception){
                    Log.e("asdf",e.toString())
                }
                out.flush()
            }
        }
        out.close()
    }

    //Converting pcm files to WAV files
    private fun copyWaveFile(pcmPath: String, wavPath: String) {

        var fileIn = FileInputStream(pcmPath)
        var fileOut = FileOutputStream(wavPath)
        val data = ByteArray(bufferSizeInByte)
        val totalAudioLen = fileIn.channel.size()
        val totalDataLen = totalAudioLen + 36
        writeWaveFileHeader(fileOut, totalAudioLen, totalDataLen)
        var count = fileIn.read(data, 0, bufferSizeInByte)
        while (count != -1) {
            fileOut.write(data, 0, count)
            fileOut.flush()
            count = fileIn.read(data, 0, bufferSizeInByte)
        }
        fileIn.close()
        fileOut.close()

        isWavComplete = true
    }

    //Add file header in WAV format
    private fun writeWaveFileHeader(out:FileOutputStream , totalAudioLen:Long,
                                    totalDataLen:Long){

        val channels = 1
        val byteRate = 16 * SampleRate * channels / 8
        val header = ByteArray(44)
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte() // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (SampleRate and 0xff).toByte()
        header[25] = (SampleRate shr 8 and 0xff).toByte()
        header[26] = (SampleRate shr 16 and 0xff).toByte()
        header[27] = (SampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

    private inner class AudioRecordToFile : Thread() {

        override fun run() {
            super.run()

            writeDateTOFile()
            wavFormatter().rawToWave(File(PCMPath), File(WAVPath))
            isWavComplete = true
//            copyWaveFile(PCMPath, WAVPath)
        }
    }
}