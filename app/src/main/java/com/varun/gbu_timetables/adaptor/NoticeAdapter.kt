package com.varun.gbu_timetables.adaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.varun.gbu_timetables.R
import com.varun.gbu_timetables.data.model.Notice

class NoticeAdapter(private val notices: ArrayList<Notice>) :
    RecyclerView.Adapter<NoticeAdapter.MyViewHolder>() {

    override fun getItemCount() = notices.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView
        private var notice: Notice? = null
        override fun onClick(itemView: View) {
            Log.d("RecyclerVew", "CLICK!")
        }

        companion object {
            private val Notice_Id = 0
        }
        //val nameTextView = itemView.findViewById<TextView>(R.id.notice_title)

        //public val messageButton = itemView.findViewById<Button>(R.id.notice_file)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Glide.with(context).load(movieList.get(position).image)
        //        .apply(RequestOptions().centerCrop())
        //        .into(holder.image)
        //  holder.messageButton.text = noticeList.toString()
        //  holder.nameTextView.text = "Hello" // noticeList.get(position).title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflatview = parent.inflate(R.layout.item_notice, false)
        // LayoutInflater.from(parent.context).inflate(R.layout.item_notice,parent,false)
        return MyViewHolder(inflatview)
    }


    //  var noticeList : List<Notice> = listOf()
    //  fun setNoticeListItems(movieList: List<Notice>){
    //      this.noticeList = movieList;
    //     notifyDataSetChanged()
    //  }


    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }


}


