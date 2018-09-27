/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.appnroll.box.ui.components.biometric.fingerprintdialog

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.widget.ImageView
import android.widget.TextView
import com.appnroll.box.R

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 * Based on: https://github.com/googlesamples/android-FingerprintDialog/blob/master/kotlinApp/app/src/main/java/com/example/android/fingerprintdialog/FingerprintUiHelper.kt
 */
@RequiresApi(Build.VERSION_CODES.M)
class FingerprintUiHelper

/**
 * Constructor for [FingerprintUiHelper].
 */
internal constructor(private val fingerprintMgr: FingerprintManager,
        private val icon: ImageView,
        private val errorTextView: TextView,
        private val callback: Callback
) : FingerprintManager.AuthenticationCallback() {

    private var cancellationSignal: CancellationSignal? = null
    private var selfCancelled = false

    val isFingerprintAuthAvailable: Boolean
        get() = fingerprintMgr.isHardwareDetected && fingerprintMgr.hasEnrolledFingerprints()

    private val resetErrorTextRunnable = Runnable {
        icon.setImageResource(R.drawable.ic_fp_40px)
        errorTextView.run {
            setTextColor(errorTextView.resources.getColor(R.color.hint_color, null))
            text = errorTextView.resources.getString(R.string.fingerprint_hint)
        }
    }

    fun startListening() {
        if (!isFingerprintAuthAvailable) return
        cancellationSignal = CancellationSignal()
        selfCancelled = false
        fingerprintMgr.authenticate(null, cancellationSignal, 0, this, null)
        icon.setImageResource(R.drawable.ic_fp_40px)
    }

    fun stopListening() {
        cancellationSignal?.also {
            selfCancelled = true
            it.cancel()
        }
        cancellationSignal = null
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        if (!selfCancelled) {
            showError(errString)
            icon.postDelayed({ callback.onError() }, ERROR_TIMEOUT_MILLIS)
        }
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) =
            showError(helpString)

    override fun onAuthenticationFailed() =
            showError(icon.resources.getString(R.string.fingerprint_not_recognized))

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        errorTextView.run {
            removeCallbacks(resetErrorTextRunnable)
            setTextColor(errorTextView.resources.getColor(R.color.success_color, null))
            text = errorTextView.resources.getString(R.string.fingerprint_success)
        }
        icon.run {
            setImageResource(R.drawable.ic_fingerprint_success)
            postDelayed({ callback.onAuthenticated() }, SUCCESS_DELAY_MILLIS)
        }
    }

    private fun showError(error: CharSequence) {
        icon.setImageResource(R.drawable.ic_fingerprint_error)
        errorTextView.run {
            text = error
            setTextColor(errorTextView.resources.getColor(R.color.warning_color, null))
            removeCallbacks(resetErrorTextRunnable)
            postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS)
        }
    }

    interface Callback {
        fun onAuthenticated()
        fun onError()
    }

    companion object {
        const val ERROR_TIMEOUT_MILLIS: Long = 1600
        const val SUCCESS_DELAY_MILLIS: Long = 1300
    }
}
