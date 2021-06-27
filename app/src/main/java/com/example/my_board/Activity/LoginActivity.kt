package com.example.my_board.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_board.R
import com.example.my_board.User
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.login.*
import java.util.*


class LoginActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val database = FirebaseDatabase.getInstance()
        val user : User = applicationContext as User
        firebaseAuth = FirebaseAuth.getInstance()
        Button_login.setOnClickListener {
            if (TextUtils.isEmpty(TextInputEditText_id.text) || TextUtils.isEmpty(TextInputEditText_password.text)) {
                Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val id = TextInputEditText_id.text.toString().trim { it <= ' ' }
                val pw = TextInputEditText_password.text.toString().trim { it <= ' ' }
                firebaseAuth!!.signInWithEmailAndPassword(id, pw).addOnCompleteListener(this@LoginActivity, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val cuser = firebaseAuth!!.currentUser
                        user.setUId(cuser!!.email!!)
                        Log.d("=================================", user.gender + user.job + user.uId)
                        val refUserGender = database.getReference("User/"+user.uId+"/gender")
                        val refUserJob = database.getReference("User/"+user.uId+"/job")
                        refUserGender.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                user.gender = dataSnapshot.getValue().toString()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // ...
                            }
                        })
                        refUserJob.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                user.job = dataSnapshot.getValue().toString()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // ...
                            }
                        })
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 오류", Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }
                })
            }
        }
        Button_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        google_signin_button.setOnClickListener {
            //first step
            googleLogin()
        }
        facebook_signin_button.setOnClickListener {
            facebookLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //printHashKey()
        //MlFIy28UJzCLwa3kHs21wADkxYY= 해당 해쉬값을 페이스북에 로그인해야 페이스북 로그인 연동 가능
        callbackManager = CallbackManager.Factory.create()
    }
    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    fun facebookLogin(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handlerFacebookAccessToken(result?.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }

        })
    }

    fun handlerFacebookAccessToken(token: AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        val user: User = applicationContext as User
        firebaseAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.setUId(firebaseAuth!!.currentUser?.uid.toString())
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        //Show the error message
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                var account = result?.signInAccount
                //second step
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        val user : User = applicationContext as User
        firebaseAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user!!.setUId(task.result?.user!!.email.toString())
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        //Show the error message
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }
//    fun printHashKey() {
//        try {
//            val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey: String = String(Base64.encode(md.digest(), 0))
//                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            Log.e("TAG", "printHashKey()", e)
//        } catch (e: Exception) {
//            Log.e("TAG", "printHashKey()", e)
//        }
//    }

}