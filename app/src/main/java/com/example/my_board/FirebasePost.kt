package com.example.my_board

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
class FirebasePost {
    var id: String? = null
    var pw: String? = null

    constructor(id: String?, pw: String?) {
        this.id = id
        this.pw = pw
    }

    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["id"] = id
        result["pw"] = pw
        return result
    }

}
