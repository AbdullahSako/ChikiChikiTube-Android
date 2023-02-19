package tube.chikichiki.sako.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import tube.chikichiki.sako.R

private const val ARG_VIDEO_NAME: String = "VIDEOTITLE"
private const val ARG_VIDEO_DESCRIPTION: String = "VIDEODESCRIPTION"

class DescriptionFragment : Fragment(R.layout.description_motion_layout) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title: TextView = view.findViewById(R.id.description_video_title)
        val descriptionText: TextView = view.findViewById(R.id.description_text)
        val closeBtn: ImageButton = view.findViewById(R.id.description_close_button)
        val motion: MotionLayout = view.findViewById(R.id.description_motion_layout)

        //get argument
        val videoName = arguments?.get(ARG_VIDEO_NAME) as String
        val videoDescription = arguments?.get(ARG_VIDEO_DESCRIPTION) as String

        //setup title and description
        title.text = videoName
        descriptionText.text = videoDescription

        //setup motion layout listener
        setMotionLayoutTransitionListener(motion)

        closeBtn.setOnClickListener {
            motion.transitionToState(R.id.descriptionEnd)
        }

    }


    private fun setMotionLayoutTransitionListener(motionLayout: MotionLayout) {

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
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

                if (currentId == R.id.descriptionEnd) {

                    parentFragmentManager.beginTransaction().remove(this@DescriptionFragment)
                        .commit()
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

    companion object {
        fun newInstance(title: String, description: String): DescriptionFragment {
            val args = Bundle()
            args.putString(ARG_VIDEO_NAME, title)
            args.putString(ARG_VIDEO_DESCRIPTION, description)
            val fragment = DescriptionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}