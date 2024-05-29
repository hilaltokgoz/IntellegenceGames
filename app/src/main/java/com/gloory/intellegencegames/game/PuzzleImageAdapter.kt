package com.gloory.intellegencegames.game

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.view.PuzzleFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.min

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
        setPicFromAsset(files!![position],imageView,mContext)

        return v
    }
    private fun setPicFromAsset(assetName: String, imageView: ImageView?, context: Context) {
        val targetW = imageView!!.width
        val targetH = imageView.height

        val am = context.assets

        try {
            val `is` = am.open("img/$assetName")
            val bmOption = BitmapFactory.Options()
            BitmapFactory.decodeStream(`is`, Rect(-1, -1, -1, -1), bmOption)

            val photoW = bmOption.outWidth
            val photoH = bmOption.outHeight

            val scalFctor = Math.min(
                photoW / targetW, photoH / targetH
            )
            bmOption.inJustDecodeBounds = false
            bmOption.inSampleSize = scalFctor
            bmOption.inPurgeable = true

            val bitmap = BitmapFactory.decodeStream(
                `is`, Rect(-1, -1, -1, -1), bmOption
            )
            imageView.setImageBitmap(bitmap)

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
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