package pg.eti.mso.lab2_mso

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pg.eti.mso.lab2_mso.my_views.DrawView

private const val MAX_SIZE_FOR_MEM_OF_ACTIVITY_RESULT_API = 498

class DrawOnScreenActivity : AppCompatActivity() {
    private lateinit var drawView: DrawView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_on_screen)

        drawView = findViewById(R.id.drawView)
    }

    private fun getScaledBitmapFromView(view: View, maxSize: Int): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)

        // draw the view on the canvas
        view.draw(canvas)

        var width = returnedBitmap.width
        var height = returnedBitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        //return the bitmap
        return Bitmap.createScaledBitmap(returnedBitmap, width, height, true)
    }

    override fun onBackPressed() {
        val returnedBitmap = getScaledBitmapFromView(drawView, MAX_SIZE_FOR_MEM_OF_ACTIVITY_RESULT_API)
        val resultIntent = Intent()
        resultIntent.putExtra("bitmap", returnedBitmap)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}