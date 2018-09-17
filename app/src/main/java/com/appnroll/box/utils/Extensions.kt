package com.appnroll.box.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream


fun Context.getFromAssets(assetName: String): File {
    val file = File(cacheDir, assetName)
    if (!file.exists()) {
        val buffer = ByteArray(1024)
        val inputStream = assets.open(assetName)
        val outputStream = FileOutputStream(file)
        var length = inputStream.read(buffer)
        while (length > 0) {
            outputStream.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }
    }
    return file
}