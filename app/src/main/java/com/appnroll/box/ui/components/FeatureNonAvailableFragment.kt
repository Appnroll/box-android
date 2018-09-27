package com.appnroll.box.ui.components

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appnroll.box.R


class FeatureNonAvailableFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feature_non_available, container, false)
    }

    companion object {

        fun getInstance() = FeatureNonAvailableFragment()
    }
}