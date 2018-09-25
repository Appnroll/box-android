package com.appnroll.box.ui.components.imagedecoder

import android.graphics.*
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.appnroll.box.R
import kotlinx.android.synthetic.main.fragment_image_decoder.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.io.IOException


class ImageDecoderFragment : Fragment() {

    private val emptyListener = ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
        decoder.setOnPartialImageListener { true }
    }

    private var assetFileName: String = ASSET_FILE_CAT_JPG
    private var decodedListener: ImageDecoder.OnHeaderDecodedListener = emptyListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_decoder, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        catJpgButton.setOnCheckedChangeListener(onCheckedChangeListener)
        catWebpButton.setOnCheckedChangeListener(onCheckedChangeListener)
        catGifButton.setOnCheckedChangeListener(onCheckedChangeListener)

        originalButton.setOnCheckedChangeListener(onCheckedChangeListener)
        resizedButton.setOnCheckedChangeListener(onCheckedChangeListener)
        croppedButton.setOnCheckedChangeListener(onCheckedChangeListener)
        postProcessedButton.setOnCheckedChangeListener(onCheckedChangeListener)

        catJpgCheckbox.setOnCheckedChangeListener { _, isChecked ->
            originalButton.isEnabled = !isChecked
            resizedButton.isEnabled = !isChecked
            croppedButton.isEnabled = !isChecked
            postProcessedButton.isEnabled = !isChecked

            updateImage(assetFileName, decodedListener)
        }

        updateImage(assetFileName, decodedListener)
    }

    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) {
            when (buttonView) {
                catJpgButton -> assetFileName = ASSET_FILE_CAT_JPG
                catWebpButton -> assetFileName = ASSET_FILE_CAT_WEBP
                catGifButton -> assetFileName = ASSET_FILE_CAT_GIF
                originalButton -> decodedListener = emptyListener
                resizedButton -> decodedListener = resizeListener
                croppedButton -> decodedListener = cropListener
                postProcessedButton -> decodedListener = postProcessListener
            }
            updateImage(assetFileName, decodedListener)
        }
    }

    private fun updateImage(assetFileName: String, decodedListener: ImageDecoder.OnHeaderDecodedListener) {
        if (catJpgCheckbox.isChecked) {
            updateImageWithBitmapFactory(assetFileName)
        } else {
            updateImageWithImageDecoder(assetFileName, decodedListener)
        }
    }

    private fun updateImageWithBitmapFactory(assetFileName: String) {
        val context = requireContext()
        GlobalScope.launch(Dispatchers.Default) {
            val bitmap = try {
                BitmapFactory.decodeStream(context.assets.open(assetFileName))
            } catch (e: IOException) {
                null
            }
            GlobalScope.launch(Dispatchers.Main) {
                bitmap?.let {
                    imageView.setImageBitmap(it)
                }
            }
        }
    }

    private fun updateImageWithImageDecoder(assetFileName: String, decodedListener: ImageDecoder.OnHeaderDecodedListener) {
        val context = requireContext()
        GlobalScope.launch(Dispatchers.Default) {
            val source = ImageDecoder.createSource(context.assets, assetFileName)
            val drawable = ImageDecoder.decodeDrawable(source, decodedListener)
            GlobalScope.launch(Dispatchers.Main) {
                imageView.setImageDrawable(drawable)
                if (drawable is AnimatedImageDrawable) {
                    drawable.start()
                }
            }
        }
    }

    private val resizeListener = ImageDecoder.OnHeaderDecodedListener { decoder, info, _ ->
        decoder.setOnPartialImageListener { true }
        decoder.setTargetSize(info.size.width / 2, info.size.height / 2)
    }

    private val cropListener = ImageDecoder.OnHeaderDecodedListener { decoder, info, _ ->
        decoder.setOnPartialImageListener { true }
        val cropSize = 100
        val centerX = info.size.width / 2
        val centerY = info.size.height / 2
        decoder.crop = Rect(
                centerX - cropSize,
                centerY - cropSize,
                centerX + cropSize,
                centerY + cropSize)
    }

    private val postProcessListener = ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
        decoder.setOnPartialImageListener { true }

        val path = Path().apply {
            fillType = Path.FillType.INVERSE_EVEN_ODD
        }
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        }
        decoder.setPostProcessor { canvas ->
            val width = canvas.width.toFloat()
            val height = canvas.height.toFloat()
            path.addRoundRect(0f, 0f, width, height, 40f, 40f, Path.Direction.CW)
            canvas.drawPath(path, paint)
            PixelFormat.TRANSLUCENT
        }
    }

    companion object {

        const val ASSET_FILE_CAT_JPG = "cat.jpg"
        const val ASSET_FILE_CAT_GIF = "cat.gif"
        const val ASSET_FILE_CAT_WEBP = "cat.webp"

        fun getInstance(): Fragment {
            return ImageDecoderFragment()
        }
    }
}
