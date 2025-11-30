package com.example.real_time_object_detector

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.real_time_object_detector.ml.SsdMobilenetV11Metadata1
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import android.graphics.RectF
import org.tensorflow.lite.support.common.FileUtil

class MainActivity : AppCompatActivity() {

    // Define a companion object for constants like TAG
    companion object {
        private const val TAG = "MainActivity"
    }

    var colors = listOf(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.LTGRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.WHITE
    )
    val paint = Paint()
    private lateinit var labels: List<String>
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var bitmap: Bitmap
    private lateinit var imageView: ImageView
    private lateinit var cameraDevice: CameraDevice
    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var cameraManager: CameraManager
    private lateinit var textureView: TextureView
    private lateinit var model: SsdMobilenetV11Metadata1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() is often unnecessary with modern themes
        setContentView(R.layout.activity_main)
        get_permission()

        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = SsdMobilenetV11Metadata1.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        labels = FileUtil.loadLabels(this, "labels.txt")
        imageView = findViewById(R.id.imageView)
        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                open_camera()
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                // Return true to indicate the surface was released
                return true
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                // Can be left empty for this use case
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                bitmap = textureView.bitmap!!

                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)


                val outputs = model.process(image)
                val locations = outputs.locationsAsTensorBuffer.floatArray
                val classes = outputs.classesAsTensorBuffer.floatArray
                val scores = outputs.scoresAsTensorBuffer.floatArray
                val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                val h = mutable.height
                val w = mutable.width

                paint.textSize = h/15f
                paint.strokeWidth = h/85f
                var x = 0
                scores.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if(fl > 0.5) {
                        paint.setColor(colors.get(index))
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(RectF(locations.get(x+1) * w, locations.get(x) * h, locations.get(x+3) * w, locations.get(x+2) * h), paint)
                        paint.style = Paint.Style.FILL
                        canvas.drawText(labels.get(classes.get(index).toInt())+" "+fl.toString(), locations.get(x+1) * w, locations.get(x) * h, paint)
                    }
                }
                imageView.setImageBitmap(mutable)
            }
        }

            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    @SuppressLint("MissingPermission")
    private fun open_camera() {
        cameraManager.openCamera(cameraManager.cameraIdList[0], object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)

                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        session.setRepeatingRequest(captureRequest.build(), null, handler)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Camera configuration failed")
                    }
                }, handler)
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.e(TAG, "CameraDevice Error: $error")
                camera.close()
            }
        }, handler)
    }

    private fun get_permission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, // Corrected to non-nullable String
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // The user denied the permission. You might want to show a rationale.
                Log.w(TAG, "Camera permission was denied.")
                get_permission() // Be careful with re-requesting immediately.
            }
        }
    }
}
