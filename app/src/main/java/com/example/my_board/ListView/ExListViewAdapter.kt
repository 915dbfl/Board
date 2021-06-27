package com.example.my_board.ListView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.my_board.Holder
import com.example.my_board.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ExListViewAdapter(private var mParentList: ArrayList<ListViewItem>, private var mChildHashMap: HashMap<ListViewItem, ArrayList<ListViewItem>?>, private val listView: ExpandableListView) : BaseExpandableListAdapter() {
    private var board_icon: ImageView? = null
    private var board_title: TextView? = null
    private var board_uid: TextView? = null
    private var uid: String? = null
    private var character: String? = null
    private var title: String? = null
    fun setmParentList(parentList: ArrayList<ListViewItem>) {
        mParentList = parentList
    }

    fun setmChildHashMap(childHashMap: HashMap<ListViewItem, ArrayList<ListViewItem>?>) {
        mChildHashMap = childHashMap
    }

    fun setUId(uid: String?) {
        this.uid = uid
    }

    fun setCharacter(character: String?){
        this.character = character
    }

    fun setBoard_title(title: String?) {
        this.title = title
    }

    override fun getGroupCount(): Int {
        return mParentList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (mChildHashMap[mParentList[groupPosition]] != null) {
            mChildHashMap[mParentList[groupPosition]]!!.size
        } else {
            0
        }
    }

    override fun getGroup(groupPosition: Int): ListViewItem {
        return mParentList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ListViewItem {
        return mChildHashMap[mParentList[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val listViewItem = mParentList[groupPosition]
        val holder: Holder
        if (convertView == null) {
            val context = parent?.context
            val database = FirebaseDatabase.getInstance()
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            holder = Holder()
            holder.boardTitle = listViewItem.board_title
            convertView = inflater.inflate(R.layout.listview_mcitem, parent, false)
            holder.deleteBtn = convertView.findViewById<View>(R.id.content_delete) as Button
            if (listViewItem.board_uid != uid) {
                holder.deleteBtn!!.visibility = View.INVISIBLE
            }
            holder.editBtn = convertView.findViewById<View>(R.id.cComent) as Button
            holder.editText = convertView.findViewById<View>(R.id.TextInputEditText_cComment) as TextInputEditText
            convertView.tag = holder
        } else {
            holder = convertView.tag as Holder
            if (listViewItem.board_uid != uid) {
                holder.deleteBtn!!.visibility = View.INVISIBLE
            } else {
                holder.deleteBtn!!.visibility = View.VISIBLE
            }
        }
        holder.position = groupPosition
        holder.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                Log.d("TAG", "새로운 값이 업데이트되었다.")
                mParentList[holder.position].board_cComment = s.toString()
            }
        })
        if (listViewItem.board_uid == uid) {
            holder.deleteBtn!!.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val removeContent = database.getReference("Content/" + title + "/comment/" + uid + mParentList[holder.position].board_title + "/")
                removeContent.removeValue()
            }
        }
        holder.editBtn!!.setOnClickListener {
            println("클릭된 것의 제목은 " + mParentList[holder.position].board_title)
            val content = mParentList[holder.position].board_cComment
            println("content 내용은 $content")
            if (content == null || content.isEmpty()) {
                holder.editText!!.setText("please,write!")
            } else {
                println("대댓글은 " + content + ", 댓글은 " + mParentList[holder.position].board_title)
                val hashMap = HashMap<Any, String?>()
                hashMap["comment"] = content
                hashMap["uid"] = uid
                hashMap["character"]=character
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("Content/" + title + "/comment/" + mParentList[holder.position].board_uid + mParentList[holder.position].board_title + "/")
                reference.child("ccomment/$uid$content/").setValue(hashMap)
            }
        }

        board_icon = convertView!!.findViewById<View>(R.id.board_img) as ImageView
        board_title = convertView!!.findViewById<View>(R.id.board_title) as TextView
        board_uid = convertView.findViewById<View>(R.id.board_uid) as TextView

        //아이템 내 각 위젯에 데이터 반영
        board_icon!!.setImageDrawable(listViewItem.character)
        board_title!!.text = listViewItem.board_title
        board_uid!!.text = listViewItem.board_uid
        return convertView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val listViewItem = getChild(groupPosition, childPosition)
        if (convertView == null) {
            val context = parent?.context
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.listview_citem, parent, false)
        }

        //화면에 표시될 View(Layout이 infate된)으로부터 위젯에 대한 참조 획득
        board_icon = convertView!!.findViewById<View>(R.id.board_img) as ImageView
        board_title = convertView!!.findViewById<View>(R.id.board_title) as TextView
        board_uid = convertView.findViewById<View>(R.id.board_uid) as TextView

        //아이템 내 각 위젯에 데이터 반영
        board_icon!!.setImageDrawable(listViewItem.character)
        board_title!!.text = listViewItem.board_title
        println("제목, 내용 확인해보기" + listViewItem.board_title + ", " + listViewItem.board_uid)
        board_uid!!.text = listViewItem.board_uid
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}