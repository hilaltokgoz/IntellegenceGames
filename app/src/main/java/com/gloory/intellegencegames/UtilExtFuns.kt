package com.gloory.intellegencegames

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import java.io.IOException
import java.io.InputStream

fun ImageView.loadImage(imageName:String){
    try {
        val ims: InputStream = this.context.assets.open("img/$imageName")
        val d = Drawable.createFromStream(ims, null)
        this.setImageDrawable(d)
        ims.close()
    } catch (ex: IOException) {
        return
    }
}

fun Fragment.navigate(direction: NavDirections){
    this.findNavController().navigate(direction)
}
