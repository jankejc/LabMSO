// TODO: Check if services are up before onDestroy. I got function for it in comment.

package pg.eti.mso.lab_4

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Message
import android.os.Messenger

const val SOME_TYPE_OF_MSG = 5

class MainActivity : AppCompatActivity() {
    private lateinit var btnStartMsgService: Button
    var isBoundMsg = false
    var mMessenger: Messenger? = null
    private lateinit var serviceConnection: ServiceConnection
    private lateinit var msgIntent: Intent

    private lateinit var btnStartAidlMsgService: Button
    var isBoundAidlMsg = false
    private var mAidlService: MsgAidlInterface? = null
    private lateinit var aidlServiceConnection: ServiceConnection
    private var aidlMessenger: Messenger? = null
    private lateinit var aidlMsgIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // MESSENGER SERVICE ---------------------------------------------------------
        // Create connection scheme.
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                isBoundMsg = true

                // Utworzenie obiektu Messengera umożliwiającego nadawanie wiadomości.
                mMessenger = Messenger(service)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mMessenger = null
                isBoundMsg = false
            }
        }

        // Initialize messenger with connection scheme.
        val msgIntent = Intent()
        msgIntent.setClassName("pg.eti.mso.lab_4", "pg.eti.mso.lab_4.MessengerService")
        bindService(msgIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Use messenger.
        btnStartMsgService = findViewById(R.id.btn_start_msg_service)
        btnStartMsgService.setOnClickListener {
            sendMsg("siemanko")
        }

/*
        btnStartCustomService = findViewById(R.id.btn_start_custom_service)
        btnStartCustomService.setOnClickListener {
            intentService = Intent(this, CustomService::class.java)
            startService(intentService)
        }

        btnCheckIfServiceIsRunning = findViewById(R.id.btn_sheck_srv_is_running)
        btnCheckIfServiceIsRunning.setOnClickListener {
            val isMyServiceRunning = isServiceRunning(this, CustomService::class.java)
            if (isMyServiceRunning) {
                Log.d("SRV_MSG_MAIN", "RUNNING")
            } else {
                Log.d("SRV_MSG_MAIN", "NOT_RUNNING")
            }
        }

        btnStopCustomSrv = findViewById(R.id.btn_stop_srv)
        btnStopCustomSrv.setOnClickListener {
            stopService(intentService)
        }
 */

        // AIDL MESSENGER SERVICE ---------------------------------------------------------
        // Create AIDL connection scheme.
        aidlServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                isBoundAidlMsg = true
                mAidlService = MsgAidlInterface.Stub.asInterface(service)
                aidlMessenger = mAidlService!!.messenger
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                isBoundAidlMsg = false
                mAidlService = null
                aidlMessenger = null
            }
        }

        // Initialize AIDL messenger with connection scheme.
        aidlMsgIntent = Intent()
        aidlMsgIntent.setClassName("pg.eti.mso.lab_4", "pg.eti.mso.lab_4.AidlMsgService")
        bindService(aidlMsgIntent, aidlServiceConnection, Context.BIND_AUTO_CREATE)

        // Use AIDL messenger.
        btnStartAidlMsgService = findViewById(R.id.btn_start_aidl_msg_service)
        btnStartAidlMsgService.setOnClickListener {
            sendAidlMsg("siemanko AIDL")
        }
    }

    /*
    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)
        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

     */

    private fun sendMsg(text: String) {
        if (isBoundMsg) {
            val msg: Message = Message.obtain(null, SOME_TYPE_OF_MSG, 0, 0)

            val myBundle = Bundle()
            myBundle.putString("msg", text)

            msg.data = myBundle

            try {
                mMessenger!!.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendAidlMsg(text: String) {
        if (isBoundAidlMsg) {
            val msg: Message = Message.obtain(null, SOME_TYPE_OF_MSG, 0, 0)

            val myBundle = Bundle()
            myBundle.putString("msg", text)

            msg.data = myBundle

            try {
                aidlMessenger!!.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        // Close messenger connection.
        unbindService(serviceConnection)
        stopService(msgIntent)

        // Close aidl messenger connection.
        unbindService(aidlServiceConnection)
        stopService(aidlMsgIntent)

        super.onDestroy()
    }
}