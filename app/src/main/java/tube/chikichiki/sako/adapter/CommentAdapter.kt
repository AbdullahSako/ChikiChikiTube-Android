package tube.chikichiki.sako.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar

import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.model.Comment


class CommentAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var likeClick: LikeClick? = null
    private var disLikeClick: DisLikeClick? = null

    inner class CommentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nicknameTextView: TextView = itemView.findViewById(R.id.nickname_text_view)
        val commentTextView: TextView = itemView.findViewById(R.id.comment_text_view)
        val likeCount: TextView = itemView.findViewById(R.id.like_count_text_view)
        val disLikeCount: TextView = itemView.findViewById(R.id.dislike_count_text_view)
        val likeBtn: ImageButton = itemView.findViewById(R.id.like_image_btn)
        val disLikeBtn: ImageButton = itemView.findViewById(R.id.disLike_Image_btn)
        val likeDislikeProgressBar:ProgressBar = itemView.findViewById(R.id.likeDislikeProgressBar)

    }


    fun setLikeClickListener(clickListener: LikeClick) {
        likeClick = clickListener
    }
    fun setDisLikeClickListener(clickListener: DisLikeClick) {
        disLikeClick = clickListener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }


    }

    private val diff = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val commentItem = diff.currentList[position]
                val videoHolder = holder as CommentHolder
                videoHolder.apply {
                    commentTextView.text = commentItem.comment
                    nicknameTextView.text = commentItem.nickname
                    likeCount.text = commentItem.commentlike.toString()
                    disLikeCount.text = commentItem.dislike.toString()


                    //Set like/dislikes button interface
                    likeBtn.setOnClickListener {
                        likeClick?.onLikeClick(commentItem.id,likeBtn,disLikeBtn,likeDislikeProgressBar,likeCount)
                    }

                    disLikeBtn.setOnClickListener {
                        disLikeClick?.onDisLikeClick(commentItem.id,likeBtn,disLikeBtn,likeDislikeProgressBar,disLikeCount)
                    }

                    //check if user already liked/disliked this comment and change icon based on that
                    Utils.likes?.forEach {
                        if(it.toInt() == commentItem.id){
                            likeBtn.isSelected=true
                            disLikeBtn.isEnabled=false
                        }
                    }

                    Utils.disLikes?.forEach {
                        if(it.toInt()==commentItem.id){
                            disLikeBtn.isSelected=true
                            likeBtn.isEnabled=false
                        }
                    }

                }
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    fun submitList(list: List<Comment>) {
        diff.submitList(list)
    }

    interface LikeClick {
        fun onLikeClick(commentId: Int, likeBtn:ImageButton,disLikeBtn:ImageButton,progressBar: ProgressBar,likeCount:TextView)
    }
    interface DisLikeClick {
        fun onDisLikeClick(commentId: Int,likeBtn:ImageButton, disLikeBtn:ImageButton,progressBar: ProgressBar,dislikeCount:TextView)
    }
}