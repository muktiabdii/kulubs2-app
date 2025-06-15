package com.example.kulubs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kulubs.model.MenuModel
import com.example.kulubs.util.CloudinaryConfig
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditMenuActivity : AppCompatActivity() {
    private lateinit var ivPreviewImage: ImageView
    private lateinit var etNamaMenu: TextInputEditText
    private lateinit var etDeskripsiMenu: TextInputEditText
    private lateinit var etHargaMenu: TextInputEditText
    private lateinit var etKategoriMenu: TextInputEditText
    private lateinit var switchKetersediaan: SwitchMaterial
    private var gambarPath: String? = null
    private var menu: MenuModel? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_menu)

        ivPreviewImage = findViewById(R.id.ivPreviewImage)
        etNamaMenu = findViewById(R.id.etNamaMenu)
        etDeskripsiMenu = findViewById(R.id.etDeskripsiMenu)
        etHargaMenu = findViewById(R.id.etHargaMenu)
        etKategoriMenu = findViewById(R.id.etKategoriMenu)
        switchKetersediaan = findViewById(R.id.switchKetersediaan)

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnBatal).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnUploadImage).setOnClickListener { pickImageFromGallery() }
        findViewById<Button>(R.id.btnSimpan).setOnClickListener { saveMenu() }

        menu = intent.getParcelableExtra("MENU")
        if (menu != null) {
            findViewById<TextView>(R.id.tvTitle).text = "Edit Menu"
            etNamaMenu.setText(menu?.nama)
            etDeskripsiMenu.setText(menu?.deskripsi)
            etHargaMenu.setText(menu?.harga?.toInt()?.toString())
            etKategoriMenu.setText(menu?.kategori)
            switchKetersediaan.isChecked = menu?.tersedia ?: true
            gambarPath = menu?.gambarPath
            if (!menu?.gambarPath.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this)
                    .load(menu?.gambarPath)
                    .placeholder(R.drawable.placeholder_food)
                    .into(ivPreviewImage)
            }
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                ivPreviewImage.setImageBitmap(bitmap)
                gambarPath = saveImageToInternalStorage(bitmap)
            } catch (e: IOException) {
                Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        pickImage.launch("image/*")
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "MENU_$timeStamp.jpg"
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, fileName)

        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            file.absolutePath
        } catch (e: IOException) {
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun saveMenu() {
        val nama = etNamaMenu.text.toString().trim()
        val deskripsi = etDeskripsiMenu.text.toString().trim()
        val hargaStr = etHargaMenu.text.toString().trim()
        val kategori = etKategoriMenu.text.toString().trim()
        val tersedia = switchKetersediaan.isChecked

        if (nama.isEmpty() || deskripsi.isEmpty() || hargaStr.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        val harga = hargaStr.toDoubleOrNull() ?: run {
            Toast.makeText(this, "Harga tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (gambarPath != null && File(gambarPath!!).exists()) {
            // Upload image to Cloudinary
            CloudinaryConfig.uploadImage(gambarPath!!) { url, error ->
                if (url != null) {
                    saveMenuToFirestore(nama, deskripsi, harga, kategori, tersedia, url)
                } else {
                    Toast.makeText(this, "Gagal mengunggah gambar: $error", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Save without new image (use existing URL if editing)
            saveMenuToFirestore(nama, deskripsi, harga, kategori, tersedia, menu?.gambarPath)
        }
    }

    private fun saveMenuToFirestore(nama: String, deskripsi: String, harga: Double, kategori: String, tersedia: Boolean, gambarPath: String?) {
        // Generate a new ID for new menus, use existing ID for edits
        val menuId = menu?.id ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        val newMenu = MenuModel(menuId, nama, deskripsi, harga, kategori, tersedia, gambarPath)

        // Store using menuId as document ID
        db.collection("warungs").document("WARUNG_ID").collection("menus").document(menuId.toString())
            .set(newMenu)
            .addOnSuccessListener {
                Toast.makeText(this, "Menu berhasil disimpan", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("MENU", newMenu)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}