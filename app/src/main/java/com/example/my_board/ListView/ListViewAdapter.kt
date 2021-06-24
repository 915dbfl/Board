package com.example.my_board.ListView
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.my_board.R
import java.util.*


class ListViewAdapter : BaseAdapter() {
//    private var board_icon: ImageView? = null
    private var board_title: TextView? = null
    private var board_uid: TextView? = null
    private var countLike: TextView? = null
    private var uid: String? = null
    private var title: String? = null
    private var isboard = 0

    private val listViewItemList = ArrayList<ListViewItem>()
    override fun getCount(): Int {
        return listViewItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val context = parent?.context
        val listViewItem = listViewItemList[position!!]

        if (convertView == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.listview_item, parent, false)
        }
        if(convertView != null){
//            board_icon = convertView.findViewById<View>(R.id.board_img) as ImageView
            board_title = convertView.findViewById<View>(R.id.board_title) as TextView
            board_uid = convertView.findViewById<View>(R.id.board_uid) as TextView
            countLike = convertView.findViewById<View>(R.id.countLike) as TextView
//            board_icon!!.setImageDrawable(listViewItem.board_icon)
            board_title!!.text = listViewItem.board_title
            board_uid!!.text = listViewItem.board_uid
            countLike!!.text = listViewItem.countLike

        }
        return convertView!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return listViewItemList[position]
    }

    fun addItem(title: String?, content: String?, uid: String?, countLike: String?) {
        val item = ListViewItem(title!!, uid!!)
        item.board_content = content
        if(countLike != null){
            item.countLike = countLike!!
        }else{
            item.countLike = ""
        }
        listViewItemList.add(item)
    }

    fun clear() {
        listViewItemList.clear()
    }

    fun setIsboard(board: Int) {
        isboard = board
    }
}

