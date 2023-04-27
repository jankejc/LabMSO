package pg.eti.mso.lab1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var firstButton: Button
    private lateinit var secondButton: Button
    private lateinit var textView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var editText: EditText
    private lateinit var myThread: MyThread
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialization
        seekBar = findViewById(R.id.seekBar)
        editText = findViewById(R.id.editTextSeekBar)
        firstButton = findViewById(R.id.button1)
        secondButton = findViewById(R.id.button2)
        textView = findViewById(R.id.textView)

        // EditText configuration
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val string: String = s.toString()
                seekBar.progress = string.toInt()
            }
        })

        // Seekbar configuration
        seekBar.max = 150
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                editText.setText(progressChangedValue.toString())
            }
        })

        // First button preparation
        firstButton.setOnClickListener {
            myThread = MyThread(seekBar.progress, handler)
            myThread.start()
        }

        // Second button preparation
        secondButton.setOnClickListener {
            myThread.interrupt()
        }

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                textView.text = msg.data.getString("status")
            }
        }
    }
}