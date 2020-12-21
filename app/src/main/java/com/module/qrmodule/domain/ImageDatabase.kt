package com.module.qrmodule.domain

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Session
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageDatabase (session: Session, context: Context) {

    private val DEFAULT_IMAGE_NAME = "images/QRC.png"

    private var augmentedImageDatabase: AugmentedImageDatabase? = null

    init {
        augmentedImageDatabase = AugmentedImageDatabase(session)
//        val assetManager: AssetManager = context.assets
//        val augmentedImageBitmap: Bitmap = loadAugmentedImageBitmap(assetManager, DEFAULT_IMAGE_NAME)
//        augmentedImageDatabase?.addImage("QRC.jpg", augmentedImageBitmap)


        val bitmap: Bitmap = context.assets.open("images/QRC.png").use { BitmapFactory.decodeStream(it) }
        augmentedImageDatabase?.addImage("QRC.png", bitmap, 0.05f)
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            null
        }
    }

    private fun loadAugmentedImageBitmap(
        assetManager: AssetManager,
        imageName: String
    ): Bitmap {
            assetManager.open(imageName).use { `is` -> return BitmapFactory.decodeStream(`is`) }
    }

    fun getImageDatabase() = augmentedImageDatabase
}