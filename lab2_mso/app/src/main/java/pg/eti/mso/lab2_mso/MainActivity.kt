package pg.eti.mso.lab2_mso

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var drawButton: Button
    var pickedBitmap: Uri? = null
    var bitmap: Bitmap? = null
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                bitmap = result.data?.getParcelableExtra("bitmap")
                val imageSelect = findViewById<ImageView>(R.id.imageView)
                imageSelect.setImageBitmap(bitmap)
            }
        }

        drawButton = findViewById(R.id.drawButton)
        drawButton.setOnClickListener {
            startForResult.launch(Intent(this, DrawOnScreenActivity::class.java))
        }
    }

    // Exercise 1
    override fun onBackPressed() {
        val adb: AlertDialog.Builder = AlertDialog.Builder(this)
        adb.setTitle("Czy mam wyjść?")
        adb.setPositiveButton("TAK") { _, _ ->
            super.getOnBackPressedDispatcher().onBackPressed()
        }

        adb.setNegativeButton("NIE"){ _,_ ->  }
        adb.create().show()
    }

}