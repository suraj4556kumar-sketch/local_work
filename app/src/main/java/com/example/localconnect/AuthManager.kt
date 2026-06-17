package com.example.localconnect

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

object AuthManager {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onVerificationFailed: (Exception) -> Unit,
        onVerificationCompleted: (PhoneAuthCredential) -> Unit
    ) {
        // Disable app verification so test numbers work on emulators without Play Integrity / reCAPTCHA
        try {
            // New Firebase Auth SDK API
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        } catch (e: Exception) {
            // Ignore if signature varies
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)             // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    onVerificationCompleted(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onVerificationFailed(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    onCodeSent(verificationId)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        verificationId: String,
        code: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential, onSuccess, onError)
    }

    fun signInWithCredential(
        credential: PhoneAuthCredential,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception ?: Exception("Unknown error"))
                }
            }
    }
}
