package tube.chikichiki.sako.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import tube.chikichiki.sako.R
import tube.chikichiki.sako.Utils
import tube.chikichiki.sako.adapter.CommentAdapter
import tube.chikichiki.sako.api.ChikiCommentsFetcher
import java.util.*

private const val ARG_VIDEO_ID:String = "VIDEOID"
class CommentsFragment:Fragment(R.layout.comments_fragment_motion_layout),CommentAdapter.LikeClick,CommentAdapter.DisLikeClick {
    private val commentAdapter = CommentAdapter()
    private lateinit var commentsRecyclerView:RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val closeBtn:ImageButton = view.findViewById(R.id.close_comments_image_btn)
        val fab:FloatingActionButton = view.findViewById(R.id.post_comment_fab)
        val commentsMotionLayout=view.findViewById<MotionLayout>(R.id.comments_motion_layout)
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view)
        val progressBar:ProgressBar=view.findViewById(R.id.progressBar)

        setMotionLayoutTransitionListener(commentsMotionLayout)


        //get video id from args
        val videoId = arguments?.getSerializable(ARG_VIDEO_ID) as UUID

        //set up recycler view layout manager
        commentsRecyclerView.layoutManager= LinearLayoutManager(context)

        //get videos from database and load
        loadComments(videoId)


        //close button click listener
        closeBtn.setOnClickListener {
            closePostComment()
            commentsMotionLayout.transitionToState(R.id.commentsEnd)
        }

        fab.setOnClickListener {

            parentFragmentManager.beginTransaction().apply {
                add(R.id.video_title_fragment_container,PostCommentFragment.newInstance(),"postCommentTag")
                commit()
            }
        }

        //get back posted comment info from post comment fragment
        parentFragmentManager.setFragmentResultListener("POSTCOMMENT",viewLifecycleOwner
        ) { _, result ->

            //show progress bar
            progressBar.visibility=View.VISIBLE

            val nickname = result.getString("nickname")
            val comment = result.getString("comment")?.trim()

            //post comment through api
            if(nickname != null && comment != null) {
                ChikiCommentsFetcher().addAComment(videoId, nickname, comment).observe(viewLifecycleOwner){
                    if(it){
                        loadComments(videoId)
                        Toast.makeText(activity,"Comment added",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        progressBar.visibility=View.INVISIBLE
                        Toast.makeText(activity,"Failed to submit comment",Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(activity,"Incorrect nickname or comment!",Toast.LENGTH_LONG).show()
            }

        }


    }

    private fun loadComments(videoId:UUID){
        val noCommentsTextView:TextView?=view?.findViewById(R.id.no_comments_text_view)
        val progressBar:ProgressBar?=view?.findViewById(R.id.progressBar)

        ChikiCommentsFetcher().fetchCommentsOfAVideo(videoId).observe(viewLifecycleOwner){
            //hide progress bar after receiving comments
            progressBar?.visibility = View.GONE

            //show no comments text view if there are no comments
            if(it.isEmpty()){
                noCommentsTextView?.visibility=View.VISIBLE
            }
            else {

                commentsRecyclerView.apply {
                    commentAdapter.submitList(it)
                    commentAdapter.setLikeClickListener(this@CommentsFragment)
                    commentAdapter.setDisLikeClickListener(this@CommentsFragment)
                    adapter = commentAdapter
                }

                //hide no comments text view after loading
                if(noCommentsTextView?.visibility == View.VISIBLE){
                    noCommentsTextView.visibility=View.INVISIBLE
                }

            }
        }
    }

    private fun setMotionLayoutTransitionListener(motionLayout: MotionLayout){

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {

            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                if(currentId == R.id.commentsEnd){
                    closePostComment()
                    parentFragmentManager.beginTransaction().remove(this@CommentsFragment).commit()
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }


        })

    }

    private fun closePostComment(){
        if(parentFragmentManager.findFragmentByTag("postCommentTag") != null){
            val fragment = parentFragmentManager.findFragmentByTag("postCommentTag")
            val postMotionLayout = fragment?.view?.findViewById<MotionLayout>(R.id.post_comment_motion_layout)
            postMotionLayout?.transitionToState(R.id.postCommentEnd)
        }
    }
    
    
    companion object{
        fun newInstance(videoId:UUID): CommentsFragment {
            val args = Bundle()
            args.putSerializable(ARG_VIDEO_ID,videoId)
            val fragment = CommentsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onLikeClick(commentId: Int, likeBtn: ImageButton, disLikeBtn:ImageButton,progressBar: ProgressBar,likeCount:TextView) {
        // show progress bar when button is clicked and disables buttons
        progressBar.visibility=View.VISIBLE
        likeBtn.isEnabled=false
        disLikeBtn.isEnabled=false

        if(likeBtn.isSelected){
            ChikiCommentsFetcher().removeLike(commentId).observe(viewLifecycleOwner){
                //hide progress bar and re enable button once request is finished
                progressBar.visibility=View.INVISIBLE
                likeBtn.isEnabled=true
                disLikeBtn.isEnabled=true

                if(it){
                    likeBtn.isSelected = false
                    disLikeBtn.isEnabled=true

                    //decrement likes from text view
                    likeCount.text = (likeCount.text.toString().toInt() -1).toString()

                    //remove like comment id from shared preferences
                    Utils.removeLikeFromShredPref(requireActivity(),commentId)
                }
                else{
                    Toast.makeText(activity,"Failed to remove like, Please try again!",Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            ChikiCommentsFetcher().postLike(commentId).observe(viewLifecycleOwner){
                //hide progress bar and re enable button once request is finished
                progressBar.visibility=View.INVISIBLE
                likeBtn.isEnabled=true
                disLikeBtn.isEnabled=true

                if(it){
                    likeBtn.isSelected = true
                    disLikeBtn.isEnabled=false

                    //increment likes from text view
                    likeCount.text = (likeCount.text.toString().toInt() +1).toString()

                    //add like comment id from shared preferences
                    Utils.addLikeToShredPref(requireActivity(),commentId)
                }
                else{
                    Toast.makeText(activity,"Failed to like, Please try again!",Toast.LENGTH_LONG).show()
                }
            }
        }


    }

    override fun onDisLikeClick(commentId: Int, likeBtn: ImageButton, disLikeBtn: ImageButton, progressBar: ProgressBar, dislikeCount: TextView) {

        // show progress bar when button is clicked and disables buttons
        progressBar.visibility=View.VISIBLE
        likeBtn.isEnabled=false
        disLikeBtn.isEnabled=false

        if(disLikeBtn.isSelected){
            ChikiCommentsFetcher().removeDislike(commentId).observe(viewLifecycleOwner){
                //hide progress bar and re enable button once request is finished
                progressBar.visibility=View.INVISIBLE
                likeBtn.isEnabled=true
                disLikeBtn.isEnabled=true


                if(it){
                    disLikeBtn.isSelected = false
                    likeBtn.isEnabled=true

                    //decrement dislikes from text view
                    dislikeCount.text = (dislikeCount.text.toString().toInt() -1).toString()

                    //remove dislike comment id from shared preferences
                    Utils.removeDisLikeFromShredPref(requireActivity(),commentId)
                }
                else{
                    Toast.makeText(activity,"Failed to remove Dislike, Please try again!",Toast.LENGTH_LONG).show()
                }
            }
        }
        else{

            ChikiCommentsFetcher().postDislike(commentId).observe(viewLifecycleOwner){
                //hide progress bar and re enable button once request is finished
                progressBar.visibility=View.INVISIBLE
                likeBtn.isEnabled=true
                disLikeBtn.isEnabled=true



                if(it){
                    disLikeBtn.isSelected = true
                    likeBtn.isEnabled=false

                    //increment dislikes from text view
                    dislikeCount.text = (dislikeCount.text.toString().toInt() +1).toString()

                    //add dislike comment id from shared preferences
                    Utils.addDisLikeToShredPref(requireActivity(),commentId)
                }
                else{
                    Toast.makeText(activity,"Failed to Dislike, Please try again!",Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}