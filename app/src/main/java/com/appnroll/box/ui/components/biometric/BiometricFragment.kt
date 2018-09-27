package com.appnroll.box.ui.components.biometric

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appnroll.box.R
import com.appnroll.box.ui.components.FeatureNonAvailableFragment
import com.appnroll.box.ui.components.biometric.fingerprintdialog.FingerprintAuthenticationDialogFragment
import com.appnroll.box.utils.isAtLeastMarshamallow
import com.appnroll.box.utils.isAtLeastPie
import kotlinx.android.synthetic.main.fragment_biometric_prompt.*

@RequiresApi(Build.VERSION_CODES.M)
class BiometricFragment : Fragment(), FingerprintAuthenticationDialogFragment.Callback {

    private val fingerprintManager by lazy { requireActivity().getSystemService(FingerprintManager::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_biometric_prompt, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        showFingerprintDialogButton.setOnClickListener {
            useOldFingerprintDialog()
        }

        showBiometricPromptButton.setOnClickListener {
            if (isAtLeastPie()) {
                showBiometricPrompt()
            } else {
                showBiometricPromptCompat()
            }
        }

        addFingerprintButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }

        newTransactionButton.setOnClickListener {
            transactionPendingLayout.visibility = View.VISIBLE
            transactionConfirmedLayout.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val isFingerprintSupported = fingerprintManager.isHardwareDetected
        val isFingerprintSetUp = fingerprintManager.hasEnrolledFingerprints()

        transactionConfirmedLayout.visibility = View.GONE
        fingerprintNotSupportedLayout.visibility = if (!isFingerprintSupported) View.VISIBLE else View.GONE
        fingerprintNotSetUpLayout.visibility = if (isFingerprintSupported && !isFingerprintSetUp) View.VISIBLE else View.GONE
        transactionPendingLayout.visibility = if (isFingerprintSupported && isFingerprintSetUp) View.VISIBLE else View.GONE
    }

    override fun authenticated() {
        transactionPendingLayout.visibility = View.GONE
        transactionConfirmedLayout.visibility = View.VISIBLE
    }

    override fun error() {
        Toast.makeText(context, R.string.biometric_error_msg, Toast.LENGTH_SHORT).show()
    }

    /*
    Old fingerprint dialog implementation is based on https://github.com/googlesamples/android-FingerprintDialog
    For simplicity both crypto object and backup password was removed
     */
    private fun useOldFingerprintDialog() {
        val fragment = FingerprintAuthenticationDialogFragment()
        fragment.setCallback(this)
        fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showBiometricPrompt() {
        if (!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            Toast.makeText(context, R.string.biometric_not_supported_msg, Toast.LENGTH_SHORT).show()
            return
        }

        val biometricPrompt = BiometricPrompt.Builder(context)
                .setTitle(getString(R.string.biometric_transaction_title))
                .setSubtitle(getString(R.string.biometric_transaction_subtitle))
                .setDescription(getString(R.string.biometric_transaction_description))
                .setNegativeButton(getString(R.string.button_cancel), requireActivity().mainExecutor,
                        DialogInterface.OnClickListener { _, _ -> })
                .build()

        biometricPrompt.authenticate(CancellationSignal(), requireActivity().mainExecutor, object: BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                authenticated()
            }
        })
    }

    private fun showBiometricPromptCompat() {
        // TODO: implement
    }

    companion object {

        fun getInstance() = if (isAtLeastMarshamallow()) BiometricFragment() else FeatureNonAvailableFragment.getInstance()

        const val DIALOG_FRAGMENT_TAG = "DIALOG_FRAGMENT_TAG"
    }
}
