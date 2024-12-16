package com.example.imageEditor.firebase

import com.example.imageEditor.SetUpActivity
import com.example.imageEditor.chat.model.User
import com.example.imageEditor.utils.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStore {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SetUpActivity, userInfo: User) {
        mFireStore.collection(USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                //e -> Log.e(activity.javaClass.simpleName, )
            }
    }

    fun registerUser(userInfo: User) {
        mFireStore.collection(USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
            }.addOnFailureListener {
                //e -> Log.e(activity.javaClass.simpleName, )
            }
    }

    fun updatePublicKeyUser(publicKey: String) {
        val userRef = mFireStore.collection(USERS).document(getCurrentUserId())
        val update = hashMapOf<String, Any>(
            "publicKey" to publicKey
        )
        userRef.update(update)
    }

//    fun signInUser(activity: SignInActivity) {
//        mFireStore.collection(Constants.USERS)
//            .document(getCurrentUserId())
//            .get()
//            .addOnSuccessListener { document ->
//                val loggedInUser = document.toObject(User::class.java)
//                if (loggedInUser != null) {
//                    activity.signInSuccess(loggedInUser)
//                }
//            }.addOnFailureListener {
//
//            }
//    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}