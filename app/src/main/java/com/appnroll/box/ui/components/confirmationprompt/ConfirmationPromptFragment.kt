package com.appnroll.box.ui.components.confirmationprompt

import android.content.Context
import android.os.Bundle
import android.security.ConfirmationCallback
import android.security.ConfirmationPrompt
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appnroll.box.R
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.util.concurrent.Executor


class ConfirmationPromptFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirmation_prompt, container, false)
    }

    // TODO: implement logic

    /*
    First step in order to provide Android Protected Confirmation is to create a private public key pair.
    Public key will be having certificate signed by Google and private key can only be used when user confirms it
    on the Android Confirmation Prompt (secure non configurable UI which is provided by Android). What is more after
    user confirms the message on the prompt only the data which was passed to the prompt can be signed with this private key.
     */
    private fun generateKey() {
        val keyStore = KeyStore.getInstance(KEYSTORE_TYPE)
        keyStore.load(null)
        val certificate = keyStore.getCertificate(KEY_ALIAS)

        if (certificate != null) {
            Toast.makeText(requireContext(), "Key already generated", Toast.LENGTH_SHORT).show()
            return
        }
        val keyGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_TYPE)

        /*
        setAttestationChallenge - generated public key will be signed with Android Keystore Software Attestation Root certificate.
        In the end we will be having 3 certificates in certificate chain:
        1. Public key cert signed by Android Keystore Software Attestation Intermediate
        2. Intermediate cert signed by Android Keystore Software Attestation Root
        3. Root cert sef signed by Android Keystore Software Attestation Root
         */
        keyGenerator.initialize(KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setAttestationChallenge(ATTESTATION_CHALLENGE)
                .setUserConfirmationRequired(true)
                .build())
        keyGenerator.generateKeyPair()
    }


    private fun confirmTransaction() {
        // This data structure varies by app type. This is just an example.
        data class ConfirmationPromptData(val sender: String, val receiver: String, val amount: String)

        val myExtraData: ByteArray = byteArrayOf()
        val myDialogData = ConfirmationPromptData("Ashlyn", "Jordan", "$500")
        val threadReceivingCallback = Executor { runnable -> runnable.run() }
        val callback = MyConfirmationCallback(requireContext())

        val dialog = ConfirmationPrompt.Builder(requireContext())
                .setPromptText("${myDialogData.sender}, send ${myDialogData.amount} to ${myDialogData.receiver}?")
                .setExtraData(myExtraData)
                .build()

        dialog.presentPrompt(threadReceivingCallback, callback)
    }

    private fun encryptSomething() {
        val keystore = KeyStore.getInstance(KEYSTORE_TYPE)
        keystore.load(null)
        val privateKey = keystore.getKey(KEY_ALIAS, null) as PrivateKey
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        val messageToEncrypt = "SampleMessage"
        signature.update(messageToEncrypt.toByteArray())
        val signatureBytes = signature.sign()
        val b = signatureBytes
    }

    class MyConfirmationCallback(private val context: Context) : ConfirmationCallback() {
        override fun onConfirmed(dataThatWasConfirmed: ByteArray) {
            super.onConfirmed(dataThatWasConfirmed)
            // Sign dataThatWasConfirmed using your generated signing key.
            // By completing this process, you generate a "signed statement".
            Toast.makeText(context, "onConfirmed", Toast.LENGTH_SHORT).show()
        }

        override fun onDismissed() {
            super.onDismissed()
            // Handle case where user declined the prompt in the
            // confirmation dialog.
            Toast.makeText(context, "onDismissed", Toast.LENGTH_SHORT).show()
        }

        override fun onCanceled() {
            super.onCanceled()
            // Handle case where your app closed the dialog before the user
            // could respond to the prompt.
            Toast.makeText(context, "onCanceled", Toast.LENGTH_SHORT).show()
        }

        override fun onError(e: Throwable?) {
            super.onError(e)
            // Handle the exception that the callback captured.
            Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        fun getInstance(): Fragment {
            return ConfirmationPromptFragment()
        }

        const val KEYSTORE_TYPE = "AndroidKeyStore"
        const val KEY_ALIAS = "BoxKeyAlias"
        val ATTESTATION_CHALLENGE = byteArrayOf(1,2,3)
    }
}
