package com.umniyali.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    data class Msg(val me: Boolean, val text: String)

    private val msgs = mutableListOf(
        Msg(false, "Salom! Men Umniy Aliman. Mikrofonni bosing yoki yozing. / Privet! Nazhmi na mikrofon ili napishi soobshenie.")
    )
    private lateinit var adapter: A
    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val prefs by lazy { getSharedPreferences("umniy_ali", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rv = findViewById<RecyclerView>(R.id.chat)
        adapter = A()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        findViewById<Button>(R.id.send).setOnClickListener { send() }
        findViewById<Button>(R.id.mic).setOnClickListener { mic() }
        findViewById<Button>(R.id.settings).setOnClickListener { settingsDialog() }
        initTts()
    }

    private fun server() = prefs.getString("server", "http://192.168.1.100:8000")!!.trimEnd('/')
    private fun lang() = prefs.getString("lang", "ru-RU") ?: "ru-RU"

    private fun initTts() {
        tts?.stop()
        tts?.shutdown()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val parts = lang().split("-")
                tts?.language = Locale(parts[0], if (parts.size > 1) parts[1] else "")
            }
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "reply")
    }

    private fun send() {
        val input = findViewById<EditText>(R.id.input)
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        input.setText("")
        add(Msg(true, text))
        askServer(text)
    }

    private fun askServer(text: String) {
        Thread {
            try {
                val root = JSONObject()
                root.put("message", text)
                root.put("user_name", prefs.getString("user", "") ?: "")
                root.put("assistant_name", prefs.getString("name", "Umniy Ali") ?: "Umniy Ali")
                root.put("character", prefs.getString("character", "business") ?: "business")
                root.put("language", lang())
                val req = Request.Builder()
                    .url(server() + "/chat")
                    .post(root.toString().toRequestBody(JSON))
                    .build()
                http.newCall(req).execute().use { resp ->
                    val body = resp.body?.string()
                    if (!resp.isSuccessful || body == null) {
                        runOnUiThread {
                            add(Msg(false, "Server error: " + resp.code + ". Check server is running and IP in Settings."))
                        }
                        return@Thread
                    }
                    val reply = JSONObject(body).optString("reply", "...")
                    runOnUiThread {
                        add(Msg(false, reply))
                        speak(reply)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    add(Msg(false, "Cannot connect to server: " + (e.message ?: "error")))
                }
            }
        }.start()
    }

    private fun add(m: Msg) {
        msgs.add(m)
        adapter.notifyItemInserted(msgs.size - 1)
        findViewById<RecyclerView>(R.id.chat).scrollToPosition(msgs.size - 1)
    }

    private fun mic() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                toast("Listening...")
            }

            override fun onResults(results: Bundle?) {
                val text = results?.getStringArrayList(RecognizerIntent.EXTRA_RESULTS)?.getOrNull(0)
                if (!text.isNullOrEmpty()) {
                    findViewById<EditText>(R.id.input).setText(text)
                    send()
                }
            }

            override fun onError(error: Int) {
                toast("Speech error (" + error + "). Try again.")
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang())
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang())
            .putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang())
            .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        recognizer?.startListening(intent)
    }

    private fun settingsDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_settings, null)
        val srv = view.findViewById<EditText>(R.id.srv)
        val aname = view.findViewById<EditText>(R.id.aname)
        val uname = view.findViewById<EditText>(R.id.uname)
        val charac = view.findViewById<EditText>(R.id.charac)
        srv.setText(prefs.getString("server", "http://192.168.1.100:8000"))
        aname.setText(prefs.getString("name", "Umniy Ali"))
        uname.setText(prefs.getString("user", ""))
        charac.setText(prefs.getString("character", "business"))
        AlertDialog.Builder(this)
            .setTitle("Settings")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                prefs.edit()
                    .putString("server", srv.text.toString())
                    .putString("name", aname.text.toString())
                    .putString("user", uname.text.toString())
                    .putString("character", charac.text.toString())
                    .apply()
                initTts()
                toast("Saved")
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Language") { _, _ -> langDialog() }
            .show()
    }

    private fun langDialog() {
        val items = arrayOf("Russian (ru-RU)", "Ozbekcha (uz-UZ)")
        AlertDialog.Builder(this)
            .setTitle("Voice language")
            .setItems(items) { _, which ->
                prefs.edit().putString("lang", if (which == 0) "ru-RU" else "uz-UZ").apply()
                initTts()
                toast("Language: " + items[which])
            }
            .show()
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        recognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    inner class A : RecyclerView.Adapter<A.H>() {
        inner class H(v: View) : RecyclerView.ViewHolder(v) {
            val t: TextView = v.findViewById(R.id.txt)
        }

        override fun onCreateViewHolder(p: ViewGroup, vt: Int): H =
            H(LayoutInflater.from(p.context).inflate(R.layout.item_msg, p, false))

        override fun getItemCount() = msgs.size

        override fun onBindViewHolder(h: H, pos: Int) {
            val m = msgs[pos]
            h.t.text = m.text
            val lp = h.t.layoutParams as LinearLayout.LayoutParams
            if (m.me) {
                h.t.setBackgroundColor(0xFF2E7D32.toInt())
                lp.gravity = Gravity.END
            } else {
                h.t.setBackgroundColor(0xFF37474F.toInt())
                lp.gravity = Gravity.START
            }
            h.t.layoutParams = lp
        }
    }
}
