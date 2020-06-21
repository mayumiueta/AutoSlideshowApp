package com.example.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.provider.MediaStore
import android.database.Cursor
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.content.ContentUris as ContentUris1


class MainActivity : AppCompatActivity() {
    var cursor: Cursor? = null

    private val PERMISSIONS_REQUEST_CODE = 100
    private val mHandler = Handler()
    private var mTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }

        go_button.setOnClickListener {
            val hasNextImage = cursor!!.moveToNext()

            if (!hasNextImage!!) {
                cursor!!.moveToFirst()
            }
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris1.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            ImageView.setImageURI(imageUri)
        }

        back_button.setOnClickListener {
            val hasPreviousImage = cursor!!.moveToPrevious()
            if (!hasPreviousImage!!) {
                cursor!!.moveToLast()
            }
            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor!!.getLong(fieldIndex)
            val imageUri =
                ContentUris1.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            ImageView.setImageURI(imageUri)
        }

        start_button.setOnClickListener{
            if (mTimer == null){
                mTimer = Timer()
                start_button.text = "停止"
                mTimer!!.schedule(object: TimerTask(){
                    override fun run() {
                        mHandler.post {
                            val hasNextImage = cursor!!.moveToNext()

                            if (!hasNextImage!!) {
                                cursor!!.moveToFirst()
                            }
                            val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor!!.getLong(fieldIndex)
                            val imageUri =
                                ContentUris1.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            ImageView.setImageURI(imageUri)
                        }
                    }
                }, 2000, 2000)
                go_button.isClickable = false
                back_button.isClickable = false

            }else{
                start_button.text = "再生"
                mTimer!!.cancel()
                mTimer = null
                go_button.isClickable = true
                back_button.isClickable = true

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor!!.getLong(fieldIndex)
                val imageUri =
                    ContentUris1.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                ImageView.setImageURI(imageUri)
            } while (cursor!!.moveToNext())
        }
    }
}
