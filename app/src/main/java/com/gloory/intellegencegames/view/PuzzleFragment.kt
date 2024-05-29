package com.gloory.intellegencegames.view

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.gloory.intellegencegames.R
import com.gloory.intellegencegames.databinding.FragmentPuzzleBinding
import com.gloory.intellegencegames.game.PuzzleImageAdapter
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
//Resim seçmek için kullanıcı etkileşimini yönetiyor.
//Galeriden görüntü seçmek  / kamera kullanarak görüntü yakalamak gibi..
//gridview i img dosyasındaki resimlerle doldurmk için adapter i kullanır
class PuzzleFragment : Fragment() {
    private var _binding: FragmentPuzzleBinding? = null
    private val binding get() = _binding!!

    var mCurrentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPuzzleBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val am = requireContext().assets
        try {
            val files = am.list("img")
            val grid = binding.grid

            grid.adapter = PuzzleImageAdapter(requireContext())
            grid.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, i, l ->

                    val bundle = Bundle()
                    bundle.putString("assetName", files!![i % files.size])

                    val fragment = PuzzleDetailFragment()
                    fragment.arguments = bundle

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                   /* val intent = Intent(requireContext().applicationContext, PuzzleDetailFragment::class.java)
                    intent.putExtra("assetName",files!![i % files.size])
                    startActivity(intent)*/

                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
        }

        binding.cameraButton.setOnClickListener { onImageCameraClicked(it) }
        binding.gallerButton.setOnClickListener { onImageGallerClicked(it) }

    }

    fun onImageCameraClicked(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

   @Throws(IOException::class)
    private fun createImageFile(): File? {
        if (ContextCompat.checkSelfPermission(
                requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                   android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        } else {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timestamp + "_"
            val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            val image = File.createTempFile(imageFileName, ".jpg", storageDir)
            mCurrentPhotoPath = image.absolutePath
            return image
        }
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onImageCameraClicked(View(requireContext()))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val intent = Intent(
                requireContext(), PuzzleDetailFragment::class.java
            )
            intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath)
            startActivity(intent)
        }
        if (resultCode == REQUEST_IMAGE_GALLERY && resultCode ==  Activity.RESULT_OK) {
            val uri = data?.data ?: return
            val intent = Intent(requireContext(), PuzzleDetailFragment::class.java)
            intent.putExtra("mCurrentPhotoUri", uri.toString())
            startActivity(intent)
        }
    }

    fun onImageGallerClicked(view: View) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
            )
        }else{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2
        private const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3
        const val REQUEST_IMAGE_GALLERY = 4
    }

}