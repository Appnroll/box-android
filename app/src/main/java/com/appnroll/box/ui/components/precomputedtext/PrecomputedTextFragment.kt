package com.appnroll.box.ui.components.precomputedtext

import android.os.Bundle
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
import kotlinx.coroutines.experimental.launch


class PrecomputedTextFragment : Fragment() {

    private var precomputedText: PrecomputedText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_precomputed_text, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        preComputeLongText()
        longText.movementMethod = ScrollingMovementMethod()

        clearTextButton.setOnClickListener {
            longText.text = ""
        }

        showTextButton.setOnClickListener {
            longText.setText(R.string.precomputed_text_lorem_ipsum)
        }

        showPrecomputedTextButton.setOnClickListener {
            if (precomputedText != null) {
                longText.text = precomputedText
            }
        }
    }

    private fun preComputeLongText() {
        val params = longText.textMetricsParams
        GlobalScope.launch(Dispatchers.Default) {
            precomputedText = PrecomputedText.create(getText(R.string.precomputed_text_lorem_ipsum), params)
        }
    }

    companion object {

        fun getInstance(): Fragment {
            return PrecomputedTextFragment()
        }
    }
}
