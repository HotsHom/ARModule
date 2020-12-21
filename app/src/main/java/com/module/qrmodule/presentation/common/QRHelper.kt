package com.module.qrmodule.presentation.common

import android.graphics.Bitmap
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class QRHelper {

    fun getResult(mBitmap: Bitmap?): String? {
        mBitmap?.let {
            return scanBitmap(it)
        }
        return null
    }

    private fun scanBitmap(mBitmap: Bitmap): String? {
        scan(mBitmap)?.let {
            return recode(it.toString())
        }
        return null
    }

    private fun recode(str: String): String? {
        try {
            val iso = charset("ISO-8859-1").newEncoder().canEncode(str)
            val format = if (iso) {
                String(str.toByteArray(charset("ISO-8859-1")), charset("GB2312"))
            } else str
            return if (format == "") null else format
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }



    private fun scan(mBitmap: Bitmap): Result? {
        val hints: Hashtable<DecodeHintType, String> =
            Hashtable<DecodeHintType, String>()
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        val scanBitmap = Bitmap.createBitmap(mBitmap)
        val px = IntArray(scanBitmap.width * scanBitmap.height)
        scanBitmap.getPixels(
            px, 0, scanBitmap.width, 0, 0,
            scanBitmap.width, scanBitmap.height
        )
        val source = RGBLuminanceSource(
            scanBitmap.width, scanBitmap.height, px
        )
        val tempBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        try {
            return reader.decode(tempBitmap, hints)
        } catch (e: NotFoundException) {
            e.printStackTrace()
        } catch (e: ChecksumException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
        return null
    }

}