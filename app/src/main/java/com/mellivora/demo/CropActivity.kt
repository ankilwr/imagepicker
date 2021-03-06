package com.mellivora.demo

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import com.base.library.utils.FileUtils
import com.base.library.utils.LogUtil
import com.glide.GlideApp
import com.glide.loadDefault
import com.imagepicket.ImagePickerLoader
import com.mellivora.imagepicker.ImagePicker
import com.mellivora.imagepicker.data.MediaFile
import com.soundcloud.android.crop.Crop
import kotlinx.android.synthetic.main.activity_crop.*
import java.io.File
import java.io.FileOutputStream


class CropActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CAMERA_CODE: Int = 101
    private var cropUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

//        if(!hasPhoneStatePermission()){
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CAMERA_CODE)
//        }

        btnChoose.setOnClickListener {
//            if(!hasPhoneStatePermission()){
//                Toast.makeText(this, "请开启存储权限", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
            ImagePicker.getInstance()
                .setTitle("图库")
                .showCamera(true)
                .setPickerType(ImagePicker.ALL)
                .setImageSingleEnable(false)
                .setVideoSingleEnable(true)
                .setSingleType(true)//图片、视频只能选一种
                .setMaxCount(1)
                .setImagePaths(null)
                .setImageLoader(ImagePickerLoader())
                .start(this, 100)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                100 -> {
                    val pathList: ArrayList<MediaFile> = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES)!!
                    val media = pathList[0]
                    GlideApp.with(this).loadDefault(media.uri, iv1)

                    val copyFile = uriToFileQ(this, media.uri)
                    val temp = getExternalFilesDir("temp")
                    val cropFile = File("$temp/${System.currentTimeMillis()}.jpg")
                    cropUri = Uri.fromFile(cropFile)
                    Crop.of(Uri.fromFile(copyFile), cropUri).asSquare().start(this@CropActivity)
                }

                Crop.REQUEST_CROP -> {
                    GlideApp.with(this).load(cropUri).placeholder(R.drawable.default_image).into(iv2)
                }
            }
        }
    }


    private fun uriToFileQ(context: Context, uri: Uri): File?{
        return when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> uri.toFile()
            ContentResolver.SCHEME_CONTENT -> {
                //把文件保存到沙盒
                val documentFile = DocumentFile.fromSingleUri(context, uri)
                val ois = context.contentResolver.openInputStream(uri)
                val copyFile = FileUtils.getSdCacheFile(FileUtils.Temp, "${System.currentTimeMillis()}-${documentFile!!.name}")
                ois?.let {
                    val fos = FileOutputStream(copyFile)
                    android.os.FileUtils.copy(ois, fos)
                    fos.close()
                    it.close()
                    return copyFile
                }
                null
            }
            else -> null
        }
    }


    private fun hasPhoneStatePermission(): Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

}
