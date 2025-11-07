package com.example.myimageswitcher

import android.os.Bundle
import android.widget.Button
import android.widget.ImageSwitcher
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {

    private lateinit var imageSwitcher: ImageSwitcher
    private lateinit var buttonSwitch: Button

    // Array of image resource IDs
    private val images = arrayOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageSwitcher = findViewById(R.id.imageSwitcher)
        buttonSwitch = findViewById(R.id.buttonSwitch)

        // Set factory to create ImageView inside ImageSwitcher
        imageSwitcher.setFactory {
            val imageView = ImageView(applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER

            // Use FrameLayout.LayoutParams to match ImageSwitcher internal structure
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )

            imageView
        }

        // Set initial image
        imageSwitcher.setImageResource(images[currentIndex])

        // Button click to switch image
        buttonSwitch.setOnClickListener {
            // move to next image index
            currentIndex = (currentIndex + 1) % images.size
            imageSwitcher.setImageResource(images[currentIndex])
        }
    }
}
