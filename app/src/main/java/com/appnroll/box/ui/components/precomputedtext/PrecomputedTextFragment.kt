package com.appnroll.box.ui.components.precomputedtext

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.text.PrecomputedText
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appnroll.box.R
import com.appnroll.box.utils.RandomStringGenerator
import com.appnroll.box.utils.isAtLeastPie
import kotlinx.android.synthetic.main.fragment_precomputed_text.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference


class PrecomputedTextFragment : Fragment() {

    private val randomStringGenerator = RandomStringGenerator()
    private val randomText = randomStringGenerator.randomText(TEXT_LENGTH)
    private val randomTextToPreCompute = randomStringGenerator.randomText(TEXT_LENGTH)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_precomputed_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        longText.movementMethod = ScrollingMovementMethod()
        longPrecomputedText.movementMethod = ScrollingMovementMethod()

        clearTextButton.setOnClickListener {
            longText.text = ""
        }

        clearPrecomputedTextButton.setOnClickListener {
            longPrecomputedText.text = ""
        }

        showTextButton.setOnClickListener {
            longText.text = randomText
        }

        preComputeText()
    }

    private fun preComputeText() {
        if (isAtLeastPie()) {
            preComputeLongText()
        } else {
            preComputeLongTextCompat()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun preComputeLongText() {
        val params = longPrecomputedText.textMetricsParams
        showPrecomputedTextButton.isEnabled = false
        val ref = WeakReference(showPrecomputedTextButton)
        GlobalScope.launch(Dispatchers.Default) {
            val precomputedText = PrecomputedText.create(randomTextToPreCompute, params)
            GlobalScope.launch(Dispatchers.Main) {
                ref.get()?.let { button ->
                    button.isEnabled = true
                    button.setOnClickListener {
                        longPrecomputedText.text = precomputedText
                    }
                }
            }
        }
    }

    private fun preComputeLongTextCompat() {
        val params = TextViewCompat.getTextMetricsParams(longPrecomputedText)
        showPrecomputedTextButton.isEnabled = false
        val ref = WeakReference(showPrecomputedTextButton)
        GlobalScope.launch(Dispatchers.Default) {
            val precomputedTextCompat = PrecomputedTextCompat.create(randomTextToPreCompute, params)
            GlobalScope.launch(Dispatchers.Main) {
                ref.get()?.let { button ->
                    button.isEnabled = true
                    button.setOnClickListener {
                        TextViewCompat.setPrecomputedText(longPrecomputedText, precomputedTextCompat)
                    }
                }
            }
        }
    }

    companion object {

        private const val TEXT_LENGTH = 30000

        fun getInstance() = PrecomputedTextFragment()
    }
}
