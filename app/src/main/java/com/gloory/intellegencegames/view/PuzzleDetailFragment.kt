package com.gloory.intellegencegames.view

import android.app.AlertDialog
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentPuzzleDetailBinding
import com.gloory.intellegencegames.game.PuzzlePiece
import com.gloory.intellegencegames.game.TouchListener
import com.gloory.intellegencegames.loadImage
import java.io.IOException
import java.util.*
import kotlin.random.Random

//setPicFromPhotoPath görüntünün cihazdan yüklenmesini sağlar.
//setPicFromAsset, görüntüyü assets den yükler.Gerekirse EXIF verilerine göre görüntüyü döndürür.
// splitImage'ı kullanarak görüntüyü bulmaca için daha küçük parçalara böler.
// Her görüntü parçası için PuzzlePiece nesneleri oluşturur ve bunları RelativeLayout'a ekler.
// Yapboz parçaları için touchListener uygulanır.


class PuzzleDetailFragment : Fragment() {
    private val navArgs by navArgs<PuzzleDetailFragmentArgs>()

    private lateinit var imageView: ImageView
    var pieces: ArrayList<PuzzlePiece>? = null

    var mCurrentPhotoPath: String? = null
    var mCurrentPhotoUri: String? = null

    private var _binding: FragmentPuzzleDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPuzzleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = binding.layout
        imageView = binding.imageView

        val intent = requireActivity().intent
        val assetName = navArgs.puzzlePath
        mCurrentPhotoPath = intent.getStringExtra("mCurrentPhotoPath")
        mCurrentPhotoUri = intent.getStringExtra("mCurrentPhotoUri")


        imageView.post {
            if (assetName != null) {
                imageView.loadImage(assetName)
            } else if (mCurrentPhotoPath != null) {
                imageView.loadImage(mCurrentPhotoPath!!)
            } else if (mCurrentPhotoUri != null) {
                imageView.setImageURI(Uri.parse(mCurrentPhotoUri))
            }
            pieces = splitImage()
            pieces?.let { pieceList ->
                val touchListener = TouchListener(this@PuzzleDetailFragment)
                // puzzle parçalarını karıştır
                Collections.shuffle(pieceList)
                for (piece in pieceList) {
                    piece.setOnTouchListener(touchListener)
                    layout.addView(piece)

                    //pozisyonları karıştır
                    val lParams = piece.layoutParams as RelativeLayout.LayoutParams
                    lParams.leftMargin = Random.nextInt(
                        layout.width - piece.pieceWidth
                    )
                    lParams.topMargin = layout.height - piece.pieceHeight
                    lParams.bottomMargin = dpToPx(24)
                    piece.layoutParams = lParams
                }
            }
        }
    }

    private fun splitImage2(): ArrayList<PuzzlePiece> {
        val piecesNumber = 12
        val rows = 4
        val cols = 3
        val imageView = binding.imageView
        val pieces = ArrayList<PuzzlePiece>(piecesNumber)

        // bitmap in kaynak resmi
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        val dimensions = getBitmapPositionInsideImageView(imageView)

        val scaledBitmapLeft = dimensions[0]
        val scaledBitmapTop = dimensions[1]
        val scaledBitmapWidth = dimensions[2]
        val scaledBitmapHeight = dimensions[3]

        val croppedImageWidth = scaledBitmapWidth - 2 * Math.abs(scaledBitmapLeft)
        val croppedImageHeight = scaledBitmapHeight - 2 * Math.abs(scaledBitmapTop)

        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap, scaledBitmapWidth, scaledBitmapHeight, true
        )

        val croppedBitmap = Bitmap.createBitmap(
            scaledBitmap,
            Math.abs(scaledBitmapLeft),
            Math.abs(scaledBitmapTop),
            croppedImageWidth,
            croppedImageHeight
        )
        // parçaların yükseklik vve genişliğini hesaplama

        val pieceWidth = croppedImageWidth / cols
        val pieceHeight = croppedImageHeight / rows

        var yCoord = 0
        for (row in 0 until rows) {
            var xCoord = 0
            for (col in 0 until cols) {
                var offsetX = 0
                var offsetY = 0
                if (col > 0) {
                    offsetX = pieceWidth / 3
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3
                }

                val pieceBitmap = Bitmap.createBitmap(
                    croppedBitmap, xCoord - offsetX, yCoord - offsetY,
                    pieceWidth + offsetX, pieceHeight + offsetY
                )
                val piece = PuzzlePiece(requireContext().applicationContext)
                piece.setImageBitmap(pieceBitmap)
                piece.xCoord = xCoord - offsetX + imageView.left
                piece.yCoord = yCoord - offsetY + imageView.top

                piece.pieceWidth = pieceWidth + offsetX
                piece.pieceHeight = pieceHeight + offsetY

                // puzzle parçaları sonunda naıl tutulacak
                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth + offsetX,
                    pieceHeight + offsetY,
                    Bitmap.Config.ARGB_8888
                )
                //path leri çizdir
                val bumpSize = pieceHeight / 4
                val canvas = Canvas(puzzlePiece)
                val path = Path()
                path.moveTo(offsetX.toFloat(), offsetY.toFloat())

                if (row == 0) {
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        offsetY.toFloat()
                    )
                } else {
                    path.lineTo(
                        (offsetX + (pieceBitmap.width - offsetX) / 3).toFloat(),
                        offsetY.toFloat()
                    )
                    path.cubicTo(
                        ((offsetX + (pieceBitmap.width - offsetX)).toFloat()),
                        (offsetY - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 6 * 5).toFloat(),
                        (offsetY - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 3 * 2).toFloat(),
                        offsetY.toFloat()
                    )
                    path.lineTo(pieceBitmap.width.toFloat(), offsetY.toFloat())
                }
                if (col == cols - 1) {
                    //sağ kenr parçası
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                } else {
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3).toFloat()
                    )
                    path.cubicTo(
                        (pieceBitmap.width - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 6).toFloat(),
                        (pieceBitmap.width - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 6 * 5).toFloat(),
                        pieceBitmap.width.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3 * 2).toFloat()
                    )
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                }
                if (row == -1) {
                    //bottom kenar
                    path.lineTo(
                        offsetX.toFloat(), pieceBitmap.height.toFloat()
                    )
                } else {
                    path.lineTo(
                        (offsetX + (pieceBitmap.width) / 3 * 2).toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                    path.cubicTo(
                        (offsetX + (pieceBitmap.width - offsetX) / 6 * 5).toFloat(),
                        (pieceBitmap.height - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 6).toFloat(),
                        (pieceBitmap.height - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 3).toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                    path.lineTo(
                        offsetX.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                }
                if (col == 0) {
                    //sol kenar parçaları
                    path.close()
                } else {
                    path.lineTo(
                        offsetX.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3 * 2).toFloat()
                    )
                    path.cubicTo(
                        (offsetX - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height) / 6 * 5).toFloat(),
                        (offsetX - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 6).toFloat(),
                        offsetX.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3).toFloat()
                    )
                    path.close()
                }

                // parçaları maskele
                val paint = Paint()
                paint.color = 0x10000000
                paint.style = Paint.Style.FILL
                canvas.drawPath(path, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)

                // border beyaz yap.
                var border = Paint()
                border.color = -0x7f000001
                border.style = Paint.Style.STROKE
                border.strokeWidth = 8.0f
                canvas.drawPath(path, border)

                // siyah border
                border = Paint()
                border.color = -0x80000000
                border.style = Paint.Style.STROKE
                border.strokeWidth = 3.0f
                canvas.drawPath(path, border)

                //parça bitmapleri çözümle
                //piece.setImageBitmap(puzzlePiece)
                pieces.add(piece)
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }
        return pieces
    }

    private fun splitImage(): ArrayList<PuzzlePiece> {
        val piecesNumber = 12
        val rows = 4
        val cols = 3
        val imageView = binding.imageView
        val pieces = ArrayList<PuzzlePiece>(piecesNumber)

        // bitmap in kaynak resmi
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        val dimensions = getBitmapPositionInsideImageView(imageView)

        val scaledBitmapLeft = dimensions[0]
        val scaledBitmapTop = dimensions[1]
        val scaledBitmapWidth = dimensions[2]
        val scaledBitmapHeight = dimensions[3]

        val croppedImageWidth = scaledBitmapWidth - 2 * Math.abs(scaledBitmapLeft)
        val croppedImageHeight = scaledBitmapHeight - 2 * Math.abs(scaledBitmapTop)

        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap, scaledBitmapWidth, scaledBitmapHeight, true
        )

        val croppedBitmap = Bitmap.createBitmap(
            scaledBitmap,
            Math.abs(scaledBitmapLeft),
            Math.abs(scaledBitmapTop),
            croppedImageWidth,
            croppedImageHeight
        )
        // parçaların yükseklik ve genişliğini hesaplama

        val pieceWidth = croppedImageWidth / cols
        val pieceHeight = croppedImageHeight / rows

        var yCoord = 0
        for (row in 0 until rows) {
            var xCoord = 0
            for (col in 0 until cols) {
                var offsetX = 0
                var offsetY = 0
                if (col > 0) {
                    offsetX = pieceWidth / 3
                }
                if (row > 0) {
                    offsetY = pieceHeight / 3
                }

                val pieceBitmap = Bitmap.createBitmap(
                    croppedBitmap, xCoord, yCoord,
                    pieceWidth, pieceHeight
                )
                val piece = PuzzlePiece(requireContext().applicationContext)
                piece.setImageBitmap(pieceBitmap)
                piece.xCoord = xCoord + imageView.left
                piece.yCoord = yCoord + imageView.top

                piece.pieceWidth = pieceWidth
                piece.pieceHeight = pieceHeight

                // puzzle parçaları sonunda naıl tutulacak
                val puzzlePiece = Bitmap.createBitmap(
                    pieceWidth,
                    pieceHeight,
                    Bitmap.Config.ARGB_8888
                )
                //path leri çizdir
                val bumpSize = pieceHeight / 4
                val canvas = Canvas(puzzlePiece)
                val path = Path()
                path.moveTo(offsetX.toFloat(), offsetY.toFloat())

                if (row == 0) {
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        offsetY.toFloat()
                    )
                } else {
                    path.lineTo(
                        (offsetX + (pieceBitmap.width - offsetX) / 3).toFloat(),
                        offsetY.toFloat()
                    )
                    path.cubicTo(
                        ((offsetX + (pieceBitmap.width - offsetX)).toFloat()),
                        (offsetY - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 6 * 5).toFloat(),
                        (offsetY - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 3 * 2).toFloat(),
                        offsetY.toFloat()
                    )
                    path.lineTo(pieceBitmap.width.toFloat(), offsetY.toFloat())
                }
                if (col == cols - 1) {
                    //sağ kenr parçası
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                } else {
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3).toFloat()
                    )
                    path.cubicTo(
                        (pieceBitmap.width - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 6).toFloat(),
                        (pieceBitmap.width - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 6 * 5).toFloat(),
                        pieceBitmap.width.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3 * 2).toFloat()
                    )
                    path.lineTo(
                        pieceBitmap.width.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                }
                if (row == rows - 1) {
                    //bottom kenar
                    path.lineTo(
                        offsetX.toFloat(), pieceBitmap.height.toFloat()
                    )
                } else {
                    path.lineTo(
                        (offsetX + (pieceBitmap.width) / 3 * 2).toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                    path.cubicTo(
                        (offsetX + (pieceBitmap.width - offsetX) / 6 * 5).toFloat(),
                        (pieceBitmap.height - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 6).toFloat(),
                        (pieceBitmap.height - bumpSize).toFloat(),
                        (offsetX + (pieceBitmap.width - offsetX) / 3).toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                    path.lineTo(
                        offsetX.toFloat(),
                        pieceBitmap.height.toFloat()
                    )
                }
                if (col == 0) {
                    //sol kenar parçaları
                    path.close()
                } else {
                    path.lineTo(
                        offsetX.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3 * 2).toFloat()
                    )
                    path.cubicTo(
                        (offsetX - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height) / 6 * 5).toFloat(),
                        (offsetX - bumpSize).toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 6).toFloat(),
                        offsetX.toFloat(),
                        (offsetY + (pieceBitmap.height - offsetY) / 3).toFloat()
                    )
                    path.close()
                }

                // parçaları maskele
                val paint = Paint()
                paint.color = 0x10000000
                paint.style = Paint.Style.FILL
                canvas.drawPath(path, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(pieceBitmap, 0f, 0f, paint)

                // border beyaz yap.
                var border = Paint()
                border.color = -0x7f000001
                border.style = Paint.Style.STROKE
                border.strokeWidth = 8.0f
                canvas.drawPath(path, border)

                // siyah border
                border = Paint()
                border.color = -0x80000000
                border.style = Paint.Style.STROKE
                border.strokeWidth = 3.0f
                canvas.drawPath(path, border)

                //parça bitmapleri çözümle
                //piece.setImageBitmap(puzzlePiece)
                pieces.add(piece)
                xCoord += pieceWidth
            }
            yCoord += pieceHeight
        }

        return pieces
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
    fun checkGameOver() {
        if (isGameOver) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_game_completed, null)
            val dialogBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogBuilder.show()

            val playAgainButton = dialogView.findViewById<Button>(R.id.btn_play_again)
            val exitButton = dialogView.findViewById<Button>(R.id.btn_exit)

            playAgainButton.setOnClickListener {
                dialogBuilder.dismiss()
                findNavController().navigateUp()
            }
            exitButton.setOnClickListener {
                dialogBuilder.dismiss()
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }
    private val isGameOver: Boolean
        private get() {
            for (piece in pieces!!) {
                if (piece.canMove) {
                    return false
                }
            }
            return true
        }

    private fun getBitmapPositionInsideImageView(imageView: ImageView?): IntArray {
        val ret = IntArray(4)
        if (imageView == null || imageView.drawable == null) {
            return ret
        }
        // resin dimens lerini al ve matrix lere değer ata, listewnin içine yerleştir
        val f = FloatArray(9)
        imageView.imageMatrix.getValues(f)

        val scaleX = f[Matrix.MSCALE_X]
        val scaleY = f[Matrix.MSCALE_Y]

        val d = imageView.drawable
        val origW = d.intrinsicWidth
        val origH = d.intrinsicHeight

        val actW = Math.round(origW * scaleX)
        val actH = Math.round(origH * scaleY)

        ret[2] = actW
        ret[3] = actH

        // image pozisyonları
        val imageViewW = imageView.width
        val imageViewH = imageView.height

        val top = (imageViewH - actH) / 2
        val left = (imageViewW - actW) / 2
        ret[0] = left
        ret[1] = top

        return ret
    }

    companion object {
        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height, matrix, true
            )
        }
    }

}