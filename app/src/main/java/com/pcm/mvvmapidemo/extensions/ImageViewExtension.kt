package com.pcm.mvvmapidemo.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.pcm.mvvmapidemo.R
import java.io.File

fun ImageView.loadImage(
    path: String?,
    placeholderResId: Int = R.drawable.ic_placeholder,
    errorResId: Int = R.drawable.ic_placeholder
) {
    if (path.isNullOrEmpty()) {
        setImageResource(placeholderResId)
        return
    }

    val option = RequestOptions().apply {
        placeholder(placeholderResId)
        error(errorResId)
        circleCrop()
        diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    if (path.startsWith("http")) {
        Glide.with(this)
            .load(path)
            .thumbnail(0.5f)
            .apply(option)
            .into(this)
    } else {
        Glide.with(this)
            .load(Uri.fromFile(File(path)))
            .apply(option)
            .into(this)
    }
}