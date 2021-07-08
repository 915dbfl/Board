package com.example.my_board.navigation

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.my_board.Activity.MainActivity
import com.example.my_board.R
import com.example.my_board.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.myboard.view.*
import kotlinx.android.synthetic.main.write.*
import kotlinx.android.synthetic.main.write.view.*
import java.io.FileInputStream
import java.util.HashMap


class WriteFragment : Fragment() {
    var imgUri: Uri? = null
    var check: Uri? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.write, container, false)
        val applicationContext = activity?.applicationContext
        val user : User = applicationContext as User
        if (arguments?.getString("title") != null && arguments?.getString("content") != null) {
            val btitle = arguments?.getString("title").toString()
            val bcontent = arguments?.getString("content").toString()
            if(arguments?.getBoolean("image") != null){
                getFireBaseProfileImage(btitle, user.uId!!, false)
                view.TextInputEditText_title.isEnabled = false
            }
            view.TextInputEditText_content.setText(bcontent)
            view.TextInputEditText_title.setText(btitle)
            view.Button_write.setOnClickListener {
                val title = TextInputEditText_title.text.toString().trim { it <= ' ' }
                val content = TextInputEditText_content.text.toString().trim { it <= ' ' }
                if (title !== "" && content !== "") {
                    //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                    val hashMap = HashMap<String, Any>()
                    hashMap["uid"] = user.uId!!
                    hashMap["title"] = title
                    hashMap["content"] = content
                    val database = FirebaseDatabase.getInstance()
                    val contentRef = database.getReference("Content/")
                    if (btitle == title) {
                        hashMap["image"] = arguments?.getBoolean("image") != null
                        contentRef.child(user.uId + title).updateChildren(hashMap)
                    } else {
                        hashMap["image"] = false
                        hashMap["character"] = arguments!!.getString("character").toString()
                        contentRef.child(user.uId + title).setValue(hashMap)
                    }
                    contentRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (Snapshot in dataSnapshot.child(user.uId + btitle + "/comment/").children) {
                                val comment = Snapshot.child("comment").value.toString()
                                val uid = Snapshot.child("uid").value.toString()
                                val hashMap = HashMap<Any, String>()
                                hashMap["comment"] = comment
                                hashMap["uid"] = uid
                                val reference = database.getReference("Content/" + user.uId + title + "/comment/" + uid + comment + "/")
                                reference.setValue(hashMap)
                                for (childSnapshot in Snapshot.child("ccomment/").children) {
                                    val cComment = Snapshot.value.toString()
                                    val cCommentId = Snapshot.child("uid").value.toString()
                                    val cHashMap = HashMap<Any, String>()
                                    cHashMap["comment"] = cComment
                                    cHashMap["uid"] = cCommentId
                                    val cReference = reference.child("ccomment/$cCommentId$cComment/")
                                    reference.setValue(hashMap)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            Log.w("Failed to read value.", error.toException())
                        }
                    })
                    if (btitle != title) {
                        contentRef.child(user.uId + btitle + "/").removeValue()
                    }

                    //가입이 이루어졌을 시 가입 화면을 빠져나감.
                    Toast.makeText(context, "글이 게시되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                } else {
                    Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            view.Button_write.setOnClickListener {
                val title = TextInputEditText_title.text.toString().trim { it <= ' ' }
                val content = TextInputEditText_content.text.toString().trim { it <= ' ' }
                println("$title,$content")
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    val hashMap = HashMap<String, Any>()
                    if(imgUri != null){
                        val storage = FirebaseStorage.getInstance("gs://yuri-yotubu.appspot.com")
                        val filename = title + ".jpg"
                        val storageRef = storage.getReference("profile_img/" + user.uId + "/" + filename)
                        storageRef.putFile(imgUri!!)
                        hashMap["image"] = true
                    }else{
                        hashMap["image"] = false
                    }
                    //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                    hashMap["uid"] = user.uId!!
                    hashMap["title"] = title
                    hashMap["content"] = content
                    hashMap["character"] = user.gender + user.job
                    val database = FirebaseDatabase.getInstance()
                    val reference = database.getReference("Content")
                    reference.child(user.uId + title).setValue(hashMap)

                    //가입이 이루어졌을 시 가입 화면을 빠져나감.
                    Toast.makeText(context, "글이 게시되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                }
            }
        }
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
        view.layout_addPhoto.setOnClickListener(){
            if(checkPermission()){
                openGalleryForImage()
            }else{
                requestPermission()
            }
        }

        return view
    }
    val REQUEST_IMAGE_CAPTURE = 1
    fun requestPermission(){
        ActivityCompat.requestPermissions(context as Activity, arrayOf(READ_EXTERNAL_STORAGE, CAMERA),REQUEST_IMAGE_CAPTURE)
    }
    fun checkPermission(): Boolean{
        return (ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode ==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "허가 받음.")
        }else{
            Log.d("TAG", "허가 못받음.")
        }
    }
    fun openGalleryForImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        imgUri = data?.data!!
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            2->{
                if(requestCode == Activity.RESULT_OK || requestCode == 2){
                    view!!.image_content.setImageURI(data?.data)
                }
            }
        }
    }

    fun getFireBaseProfileImage(title: String, user: String, restore: Boolean){
        val file = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img")
        if(!file!!.isDirectory){
            file.mkdir()
        }
        downLoadImg(title, user, restore)
    }

    fun downLoadImg(title: String, user: String, restore: Boolean){
        val storage = FirebaseStorage.getInstance("gs://yuri-yotubu.appspot.com")
        val storageRef = storage.getReference("profile_img/" + user + "/" + title + ".jpg")
        storageRef.downloadUrl
                .addOnSuccessListener(){
                    check = it
                    if(restore != true){
                        Glide.with(context!!).load(it).into(view!!.image_content)
                    }
                }.addOnFailureListener {
                }
    }

//    fun deleteImage(btitle: String, user: String){
//        val storage = FirebaseStorage.getInstance("gs://yuri-yotubu.appspot.com")
//        val deleteRef = storage.getReference("profile_img/" + user + "/" + btitle + ".jpg")
//        deleteRef.downloadUrl.addOnSuccessListener(){
//            Log.d("*****************", it.toString())
//            uri = it
//        }.addOnFailureListener { }
//        deleteRef.delete().addOnSuccessListener {
//            Log.d("*****************", "이미지 삭제 완료!!!!")
//        }.addOnFailureListener { }
//    }
}