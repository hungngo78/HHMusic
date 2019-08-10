package com.hhmusic.ui.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/*
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}*/

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    if (url == null) {
        this.setImageDrawable(null)
    } else {
        Glide.with(context)
            .load(url)
            .into(this)
    }
}

