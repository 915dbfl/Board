package com.example.my_board.ListView
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.example.my_board.R
import java.util.*


class ListViewAdapter : BaseAdapter(), Filterable {
    private var board_icon: ImageView? = null
    private var listFiter : Filter? = null
    private var board_title: TextView? = null
    private var board_uid: TextView? = null
    private var countLike: TextView? = null
    private var isboard = 0
    private var listViewItemList = ArrayList<ListViewItem>()
    private var filteredItemList = listViewItemList

    override fun getCount(): Int {
        return filteredItemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val context = parent?.context
        val listViewItem = filteredItemList[position!!]

        if (convertView == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.listview_item, parent, false)
        }
        if(convertView != null){
            board_icon = convertView.findViewById<View>(R.id.character_image) as ImageView
            board_title = convertView.findViewById<View>(R.id.board_title) as TextView
            board_uid = convertView.findViewById<View>(R.id.board_uid) as TextView
            countLike = convertView.findViewById<View>(R.id.countLike) as TextView
            board_icon!!.setImageDrawable(listViewItem.character)
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
        return filteredItemList[position]
    }

    fun addItem(title: String?, content: String?, uid: String?, countLike: String?, icon: BitmapDrawable) {
        val item = ListViewItem(title!!, uid!!, icon)
        item.board_content = content
        if(countLike != null){
            item.countLike = countLike!!
        }else{
            item.countLike = ""
        }
        listViewItemList.add(item)
    }

    fun clear() {
        filteredItemList.clear()
    }

    fun setIsboard(board: Int) {
        isboard = board
    }

    override fun getFilter(): Filter {
        if(listFiter == null){
            listFiter = ListFilter()
        }
        return listFiter as Filter
    }

    inner class ListFilter : Filter(){
        private val results = FilterResults()
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if(constraint == null || constraint.length == 0){
                results.values = listViewItemList
                results.count = listViewItemList.size
            }else{
                val itemList = ArrayList<ListViewItem>()
                for(item in listViewItemList){
                    if (item.board_uid!!.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.board_title!!.toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }
                results.values = itemList
                results.count = itemList.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredItemList = results!!.values as ArrayList<ListViewItem>
            if(results.count > 0){
                notifyDataSetChanged()
            }else{
                notifyDataSetInvalidated()
            }
        }
    }

}

