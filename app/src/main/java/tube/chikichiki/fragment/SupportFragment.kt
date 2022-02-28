package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import tube.chikichiki.R

class SupportFragment : Fragment(R.layout.fragment_support) {
    private lateinit var grainAnimation: AnimationDrawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val constraint: ConstraintLayout =view.findViewById(R.id.support_constraint_layout)
        val silentLibrary:TextView=view.findViewById(R.id.silent_library_link)
        val peerTubeLink:TextView=view.findViewById(R.id.peertube_server_link)
        val gakiReddit:TextView=view.findViewById(R.id.gaki_reddit)
        val gakiDiscord:TextView=view.findViewById(R.id.gaki_discord)
        val wacast:TextView=view.findViewById(R.id.wacast)
        val knightscoop:TextView=view.findViewById(R.id.knightscoop)


        //set up clickable text that redirects to a link
        silentLibrary.movementMethod=LinkMovementMethod.getInstance()
        peerTubeLink.movementMethod=LinkMovementMethod.getInstance()
        gakiReddit.movementMethod=LinkMovementMethod.getInstance()
        gakiDiscord.movementMethod=LinkMovementMethod.getInstance()
        wacast.movementMethod=LinkMovementMethod.getInstance()
        knightscoop.movementMethod=LinkMovementMethod.getInstance()

        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }

        grainAnimation.start()
    }




}