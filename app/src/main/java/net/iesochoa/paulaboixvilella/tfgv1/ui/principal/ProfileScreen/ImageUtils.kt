package net.iesochoa.paulaboixvilella.tfgv1.ui.principal.ProfileScreen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String {
    val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file.absolutePath
}

fun copyUriToInternalStorage(context: Context, uri: Uri): String {
    val input = context.contentResolver.openInputStream(uri) ?: return ""
    val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
    val output = FileOutputStream(file)

    input.copyTo(output)
    input.close()
    output.close()

    return file.absolutePath
}
