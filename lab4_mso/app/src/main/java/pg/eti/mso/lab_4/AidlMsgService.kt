package pg.eti.mso.lab_4

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

class AidlMsgService : Service() {
    private val mBinder = object : MsgAidlInterface.Stub() {
        override fun getPid(): Int = Process.myPid()
        override fun getMessenger(): Messenger = mMessenger
    }

    // Klasa obsługująca wiadomości otrzymane od klienta.
    @Suppress("DEPRECATION")
    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.d("AIDL_MSG_SRV", "MSG: ${msg.data.getString("msg")}")
        }
    }

    // Obiekt Messenger służący do odbierania wiadomości od klienta.
    private val mMessenger = Messenger(IncomingHandler())

    // Zwrócenie interfejsu obiektu Messengera podczas zestawienia połączenia
    // pomiędzy klientem a serwisem.
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}