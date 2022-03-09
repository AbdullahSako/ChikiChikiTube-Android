package tube.chikichiki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tube.chikichiki.R
import tube.chikichiki.model.VideoChannel

class ChannelAdapter(private val channelItems: List<VideoChannel>) :
    RecyclerView.Adapter<ChannelAdapter.ChannelHolder>() {

    private var channelViewClick: ChannelViewClick? = null

    inner class ChannelHolder(view: View) : RecyclerView.ViewHolder(view) {
        val banner: ImageView = itemView.findViewById(R.id.channel_banner)
        val channelName: TextView = itemView.findViewById(R.id.channel_name)
        val desc: TextView = itemView.findViewById(R.id.channel_desc)


    }

    fun setChannelViewClickListener(clickListener: ChannelViewClick) {
        channelViewClick = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_channel, parent, false)
        return ChannelHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        val channelItem = channelItems[position]
        holder.apply {
            //if there is no banner for a channel
            channelItem.banner?.let {
                Glide.with(itemView.context).load(it.getFullPath()).into(banner)
            }

            channelName.text = channelItem.displayName.trim('"')
            desc.text = channelItem.description

            itemView.setOnClickListener {
                channelViewClick?.onItemClick(it, channelItem.id, channelItem.channelHandle)
            }
        }
    }

    override fun getItemCount(): Int {
        return channelItems.size
    }

    interface ChannelViewClick {
        fun onItemClick(view: View, channelId: Int, channelHandle: String)
    }
}



