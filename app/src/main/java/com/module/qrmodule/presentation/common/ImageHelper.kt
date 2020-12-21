package com.module.qrmodule.presentation.common

import android.graphics.*
import android.media.Image
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ImageHelper {

    private fun NV21ToJPEG(nv21: ByteArray, width: Int, height: Int, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        yuv.compressToJpeg(Rect(0, 0, width, height), quality, out)
        return out.toByteArray()
    }

    private fun NV21ToJPEG(nv21: ByteArray, width: Int, height: Int, out: OutputStream) {
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        yuv.compressToJpeg(Rect(0, 0, width, height), 30, out)
    }

    fun Image2Bitmap(image: Image): Bitmap {
        val bytes = NV21ToJPEG(YUV_420_888toNV21(image), image.width, image.height, 100)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun YUV_420_888toNV21(image: Image): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        return nv21
    }

    fun save(image: Image, out: OutputStream) {
        NV21ToJPEG(YUV_420_888toNV21(image), image.width, image.height, out)
    }

}