package pg.eti.mso.lab1

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast


class MyThread(parameter: Int, handler: Handler) : Thread() {
    private val p = parameter
    private val handler = handler
    private val bundle = Bundle()

    override fun run() {
        try {
            if (!Thread.interrupted()) {
                for (i in 0 .. p) {
                    Thread.sleep(1000)
                    // Send status to main activity to update TextView.
                    bundle.putString("status", i.toString())

                    val msg = Message.obtain()
                    msg.what = 1
                    msg.target = handler
                    msg.data = bundle
                    msg.sendToTarget()
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return
        }
    }
}