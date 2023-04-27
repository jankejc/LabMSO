package pg.eti.mso.lab3_mso

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*


class MainActivity : AppCompatActivity() {
    private lateinit var btnChooseFile: Button
    private lateinit var etvOpenedFile: EditText
    private lateinit var tvLastFileName: TextView
    private lateinit var startForResultChooseFile: ActivityResultLauncher<Intent>
    private lateinit var startForResultSaveToFile: ActivityResultLauncher<Intent>
    private lateinit var btnSaveToFile: Button
    private lateinit var btnOpenLast: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private val lastOpenedFileKey = "pg.eti.mso.lab3_mso.last_opened_file"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Link views.
        btnChooseFile = findViewById(R.id.btnChooseFile)
        etvOpenedFile = findViewById(R.id.etvOpenedFile)
        btnSaveToFile = findViewById(R.id.btnSaveToFile)
        btnOpenLast = findViewById(R.id.btnOpenLast)
        tvLastFileName = findViewById(R.id.tvLastFileName)

        // Check permission for reading to open last opened file.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            val strPermWriteExtStorage = Array(1) {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}
            ActivityCompat.requestPermissions(this,
                strPermWriteExtStorage, 1111)
        }

        // Set scrolling for text view.
        etvOpenedFile.movementMethod = ScrollingMovementMethod()

        // Initialize SharedPreferences.
        sharedPreferences = getSharedPreferences("pg.eti.mso.lab3_mso", MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        // Show last file filename in GUI.
        try {
            tvLastFileName.text = getFileName(
                Uri.parse(
                    sharedPreferences.getString(
                        lastOpenedFileKey,
                        null
                    )
                )
            )
        } catch (e: NullPointerException) {
            Log.e("CRASH", "Details: $e")
            tvLastFileName.text = ""
        }


        // Read file.
        val intChooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        val intChooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
        //intChooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        intChooseFile.type = "*/*"
        intChooseFile.putExtra(
            DocumentsContract.EXTRA_INITIAL_URI,
            Environment.DIRECTORY_DOCUMENTS
        )

        startForResultChooseFile = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                sharedPreferencesEditor.putString(lastOpenedFileKey, uri.toString())
                sharedPreferencesEditor.commit()
                tvLastFileName.text = getFileName(
                    Uri.parse(
                        sharedPreferences.getString(
                            lastOpenedFileKey,
                            null
                        )
                    )
                )

                if(result != null){
                    try {
                        val inputStream: InputStream? = contentResolver.openInputStream(uri)
                        val r = BufferedReader(InputStreamReader(inputStream))
                        var total: StringBuilder? = StringBuilder()

                        r.forEachLine { line ->
                            total!!.append(line)
                        }
                        etvOpenedFile.text.clear()
                        etvOpenedFile.text.append(total)
                    } catch (e: IOException) {
                        Log.e("CRASH", "Details: $e")
                    }
                }
            }
        }

        btnChooseFile.setOnClickListener {
            startForResultChooseFile.launch(intChooseFile)
        }

        // Save to file.
        val intSaveToFile = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intSaveToFile.addCategory(Intent.CATEGORY_OPENABLE)
        intSaveToFile.type = "application/txt"
        intSaveToFile.putExtra(
            DocumentsContract.EXTRA_INITIAL_URI,
            Environment.DIRECTORY_DOCUMENTS
        )

        startForResultSaveToFile = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if(result != null){
                    val uri = result.data?.data!!
                    try {
                        val charset = Charsets.UTF_8
                        val pfd = contentResolver.openFileDescriptor(uri, "w")
                        val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)
                        fileOutputStream.write((etvOpenedFile.text).toString().toByteArray(charset))
                        pfd.close()
                    } catch (e: IOException) {
                        Log.e("CRASH", "Details: $e")
                    }
                }
            }
        }

        btnSaveToFile.setOnClickListener {
            startForResultSaveToFile.launch(intSaveToFile)
        }

        // Open last file.
        btnOpenLast.setOnClickListener {
            try {
                val uri = Uri.parse(sharedPreferences.getString(lastOpenedFileKey, null))
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val r = BufferedReader(InputStreamReader(inputStream))
                var total: StringBuilder? = StringBuilder()

                r.forEachLine { line ->
                    total!!.append(line)
                }
                etvOpenedFile.text.clear()
                etvOpenedFile.text.append(total)
            }
            catch(e: Exception) {
                Log.e("CRASH", "Details: $e")
            }
        }
    }

    // Convert uri to filename to show it in GUI.
    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        try {
            if (uri.scheme == "content") {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor.use {
                    if (cursor!!.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
            return uri.path!!.substring(uri.path!!.lastIndexOf('/') + 1)
        } catch (e: SecurityException) {
            Log.e("CRASH", "Details: $e")
        }
        return ""
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1111 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return
                } else {
                    btnOpenLast.visibility = View.GONE
                    tvLastFileName.visibility = View.GONE
                }
                return
            }
        }
    }

}