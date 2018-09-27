package com.appnroll.box.ui.components.magnifier

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Magnifier
import com.appnroll.box.R
import com.appnroll.box.ui.components.FeatureNonAvailableFragment
import com.appnroll.box.utils.isAtLeastPie
import kotlinx.android.synthetic.main.fragment_magnifier.*


@RequiresApi(Build.VERSION_CODES.P)
class MagnifierFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_magnifier, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        displayMagnifierCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loremIpsumText.setOnTouchListener(createOnTouchListenerWithMagnifier(Magnifier(loremIpsumText)))
                androidView.setOnTouchListener(createOnTouchListenerWithMagnifier(Magnifier(androidView)))
            } else {
                loremIpsumText.setOnTouchListener(null)
                androidView.setOnTouchListener(null)
            }
        }
    }

    private fun createOnTouchListenerWithMagnifier(magnifier: Magnifier) = View.OnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val viewPosition = IntArray(2)
                v.getLocationOnScreen(viewPosition)
                magnifier.show(event.rawX - viewPosition[0], event.rawY - viewPosition[1])
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                magnifier.dismiss()
            }
        }
        true
    }

    companion object {

        fun getInstance() = if (isAtLeastPie()) MagnifierFragment() else FeatureNonAvailableFragment.getInstance()
    }
}
