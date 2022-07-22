package tube.chikichiki.sako.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import tube.chikichiki.sako.R

class PostCommentFragment:Fragment(R.layout.post_comment_motion_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val postCommentMotionLayout:MotionLayout = view.findViewById(R.id.post_comment_motion_layout)
        val nickname:EditText=view.findViewById(R.id.nickname_edit_text)
        val comment:EditText=view.findViewById(R.id.comment_edit_text)
        val closeBtn:ImageButton = view.findViewById(R.id.post_comment_close_btn)
        val postBtn:ImageButton= view.findViewById(R.id.post_comment_btn)

        //set nickname gotten from shared Preferences
        val sharedPref = requireActivity().getSharedPreferences("activity.MainActivity", Context.MODE_PRIVATE)
        nickname.setText(sharedPref.getString("nickname","none"))

        //motion layout listener
        setMotionLayoutTransitionListener(postCommentMotionLayout)

        //close button listener
        closeBtn.setOnClickListener {
            postCommentMotionLayout.transitionToState(R.id.postCommentEnd)
        }

        //post button listener
        postBtn.setOnClickListener {
            with (sharedPref.edit()) {
                putString("nickname",nickname.text.toString())
                apply()
            }

            parentFragmentManager.setFragmentResult("POSTCOMMENT", bundleOf("nickname" to nickname.text.toString() , "comment" to comment.text.toString()))

            postCommentMotionLayout.transitionToState(R.id.postCommentEnd)

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

                if(currentId == R.id.postCommentEnd){
                    parentFragmentManager.beginTransaction().remove(this@PostCommentFragment).commit()
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

    companion object{
        fun newInstance(): PostCommentFragment {
            val args = Bundle()

            val fragment = PostCommentFragment()
            fragment.arguments = args
            return fragment
        }
    }
}