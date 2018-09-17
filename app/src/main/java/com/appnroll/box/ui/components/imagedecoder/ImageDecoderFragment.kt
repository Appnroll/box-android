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
import com.appnroll.box.utils.getFromAssets
import kotlinx.android.synthetic.main.fragment_image_decoder.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch


class ImageDecoderFragment : Fragment() {

    private val emptyListener = ImageDecoder.OnHeaderDecodedListener { _, _, _ ->
        // do nothing
    }

    private val resizeListener = ImageDecoder.OnHeaderDecodedListener { decoder, info, _ ->
        decoder.setOnPartialImageListener { true }
        decoder.setTargetSize(info.size.width / 2, info.size.height / 2)
    }

    private val cropListener = ImageDecoder.OnHeaderDecodedListener { decoder, info, _ ->
        decoder.setOnPartialImageListener { true }
        decoder.crop = getCropRect(info.size.width / 2, info.size.height / 2)
    }

    private val postProcessListener = ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
        decoder.setOnPartialImageListener { true }

        // This will add round corners to the image/gif
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

    private var assetFile: AssetFile = AssetFile.CatJpg
    private var decodeListener: ImageDecoder.OnHeaderDecodedListener = emptyListener

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

        updateImage()
    }

    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked) {
            when (buttonView) {
                catJpgButton -> assetFile = AssetFile.CatJpg
                catWebpButton -> assetFile = AssetFile.CatWebp
                catGifButton -> assetFile = AssetFile.CatGif
                originalButton -> decodeListener = emptyListener
                resizedButton -> decodeListener = resizeListener
                croppedButton -> decodeListener = cropListener
                postProcessedButton -> decodeListener = postProcessListener
            }
            updateImage()
        }
    }

    private fun updateImage() {
        GlobalScope.launch(Dispatchers.Default) {
            requireContext().getFromAssets(assetFile.name)
                    .let { file -> ImageDecoder.createSource(file) }
                    .let { source ->ImageDecoder.decodeDrawable(source, decodeListener) }
                    .let { drawable ->
                        GlobalScope.launch(Dispatchers.Main) {
                            if (drawable is AnimatedImageDrawable) {
                                drawable.start()
                            }
                            imageView.setImageDrawable(drawable)
                        }
                    }
        }
    }

    private fun getCropRect(centerX: Int, centerY: Int): Rect {
        val cropSize = 100
        return Rect(
                centerX - cropSize,
                centerY - cropSize,
                centerX + cropSize,
                centerY + cropSize)
    }

    companion object {

        fun getInstance(): Fragment {
            return ImageDecoderFragment()
        }
    }

    sealed class AssetFile(val name: String) {
        object CatJpg: AssetFile("cat.jpg")
        object CatGif: AssetFile("cat.gif")
        object CatWebp: AssetFile("cat.webp")
    }
}
