package pg.eti.mso.lab2_mso.my_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*


class DrawView : View {
    constructor(
        ctx: Context,
        attrs: AttributeSet
    ) : super(ctx, attrs)

    private var paints = mutableListOf<Paint>()
    private var paths = mutableListOf<Path>()
    private var paint = Paint()
    private var path = Path()


    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                paint = Paint()
                path = Path()
                setPaintAndPath(paint)
                path.moveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                paints.add(paint)
                paths.add(path)
            }
            else -> return false
        }

        invalidate()
        return false
    }

    private fun setPaintAndPath(paint: Paint) {
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        val rnd = Random()
        paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for(i in 0 until paths.size){
            canvas.drawPath(paths[i], paints[i])
        }
    }
}
