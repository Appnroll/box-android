package com.appnroll.box.ui.components.precomputedtext

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.text.PrecomputedText
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appnroll.box.R
import kotlinx.android.synthetic.main.fragment_precomputed_text.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference


class PrecomputedTextFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_precomputed_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        longText.movementMethod = ScrollingMovementMethod()

        clearTextButton.setOnClickListener {
            longText.text = ""
        }

        showTextButton.setOnClickListener {
            longText.setText(R.string.precomputed_text_lorem_ipsum)
        }

        preComputeLongText()
    }

    private fun preComputeLongText() {
        val params = longText.textMetricsParams
        showPrecomputedTextButton.isEnabled = false
        val ref = WeakReference(showPrecomputedTextButton)
        GlobalScope.launch(Dispatchers.Default) {
            val precomputedText = PrecomputedText.create(getText(R.string.precomputed_text_lorem_ipsum), params)
            GlobalScope.launch(Dispatchers.Main) {
                ref.get()?.let { button ->
                    button.isEnabled = true
                    button.setOnClickListener {
                        longText.text = precomputedText
                    }
                }
            }
        }

    }

    companion object {

        fun getInstance(): Fragment {
            return PrecomputedTextFragment()
        }
    }
}
