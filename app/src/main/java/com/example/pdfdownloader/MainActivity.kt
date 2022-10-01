package com.example.pdfdownloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.pdfdownloader.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var permisson = 0
    private val requestPermisson = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        permisson = if(it){
            1
        } else{
            0
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        binding.btnDefaultDownload.setOnClickListener {
            requestPermisson.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if(permisson == 1)
            {
                downloadPdf(getString(R.string.pdf_url),"DemoPDF")
            }
            else{
                Toast.makeText(this,"Need Permission for further processing",Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnDownload.setOnClickListener{
            requestPermisson.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if(permisson == 1)
            {
                if(!binding.edtTxtUrl.text.isNullOrEmpty() && binding.edtTxtUrl.text.toString().isValidURL())
                {
                    downloadPdf(binding.edtTxtUrl.text.toString(),"urlPDF")
                }
                else{
                    binding.edtTxtUrl.setText("")
                    binding.edtTxtUrl.requestFocus()
                    Toast.makeText(this,"Please enter valid URL",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun downloadPdf(url: String,fileName: String) {
        try {
            val donwloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val pdfLink = Uri.parse(url)
            val request = DownloadManager.Request(pdfLink)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,File.separator+fileName+".pdf")
            donwloadManager.enqueue(request)
            Toast.makeText(this,"File Downloading Started",Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Toast.makeText(this,"Download Failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun String.isValidURL():Boolean = Patterns.WEB_URL.matcher(this).matches()
}