package com.example.myofflinebot.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface WallpaperInteractor {
    suspend fun setWallpaper(uri: Uri): Result<Unit>
}

class WallpaperInteractorImpl(private val context: Context): WallpaperInteractor {
    override suspend fun setWallpaper(uri: Uri): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    WallpaperManager.getInstance(context).setBitmap(bitmap)
                } ?: error("")
            }
        }
    }

}