package com.gloory.intellegencegames.game

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.loadImage
import java.io.IOException
import java.io.InputStream


// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 21.05.2024               │
//└──────────────────────────┘
//img deki görüntüleri yükler
//görüntüyü asenkron yüklemek ve imageview ayarlamak için AsyncTask kullanır.
class PuzzleImageAdapter(private val mContext: Context) : BaseAdapter() {
    val am: AssetManager
    private var files: Array<String>? = null

    init {
        am = mContext.assets
        try {
            files = am.list("img")
        } catch (e:IOException){
            e.printStackTrace()
        }
    }

    override fun getCount(): Int = files!!.size

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long = 0



    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val v =  LayoutInflater.from(mContext).inflate(R.layout.grid_element, null)
        val imageView = v.findViewById<ImageView>(R.id.gridImageView)
        files?.get(position)?.let{ assetName->
            imageView.loadImage(assetName)
        }
        return v
    }


    private fun getPicFromAsset(imageView: ImageView?, assetName: String): Bitmap? {
        val targetW = imageView!!.width
        val targetH = imageView!!.height

        return if (targetW == 0 || targetH == 0) {
            null
        } else
            try {
            val  `is` = am.open("img/$assetName")
            val bmOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(
                `is`,
                Rect(-1, -1, -1, -1), bmOptions //rect= null
            )
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            //peternine imageView'in ölçeği ne kadar küçültülecek
            val scaleFactor = Math.min(photoW/targetW/photoH,targetH)

                bmOptions.apply {
                    inJustDecodeBounds = false
                    inSampleSize = scaleFactor
                    inPurgeable = true
                }
            BitmapFactory.decodeStream(
                `is`,
                Rect(-1, -1, -1, -1), bmOptions //rect = null
            )
        } catch (e: IOException){
            e.printStackTrace()
            null
        }
    }

}