package pg.eti.mso.lab_4

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

class CustomService: Service() {
    private var mServiceLooper: Looper? = null
    private var mServiceHandler: ServiceHandler? = null

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // Obsługa przesłanej wiadomości
            Log.d("SRV_MSG", "MSG: ${msg.arg1}")

            // zatrzymanie serwisu o konkretnym ID
//            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        // Utworzenie dedykowanego wątku o niskim priorytecie
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        mServiceLooper = thread.looper
        mServiceHandler = ServiceHandler(mServiceLooper!!)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Przekazanie ID żądania w wiadomości
        val msg = mServiceHandler!!.obtainMessage()
        msg.arg1 = startId
        mServiceHandler!!.sendMessage(msg)

        // zrestartuj serwis w przypadku jego zatrzymania
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {}

}