package tube.chikichiki.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import tube.chikichiki.R

class SupportFragment : Fragment(R.layout.fragment_support) {
    private lateinit var grainAnimation: AnimationDrawable


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val constraint: ConstraintLayout =view.findViewById(R.id.support_constraint_layout)
        val silentLibrary:TextView=view.findViewById(R.id.silent_library_link)
        val peerTubeLink:TextView=view.findViewById(R.id.peertube_server_link)
        val gakiReddit:TextView=view.findViewById(R.id.gaki_reddit)
        val gakiDiscord:TextView=view.findViewById(R.id.gaki_discord)
        val wacast:TextView=view.findViewById(R.id.wacast)
        val knightscoop:TextView=view.findViewById(R.id.knightscoop)
        val chikiChikiTube:TextView=view.findViewById(R.id.app_for_website_text)
        val githubText:TextView=view.findViewById(R.id.github_link)


        //set up clickable text that redirects to a link
        val linkMovementMethod=LinkMovementMethod.getInstance()
        silentLibrary.movementMethod=linkMovementMethod
        peerTubeLink.movementMethod=linkMovementMethod
        gakiReddit.movementMethod=linkMovementMethod
        gakiDiscord.movementMethod=linkMovementMethod
        wacast.movementMethod=linkMovementMethod
        knightscoop.movementMethod=linkMovementMethod
        chikiChikiTube.movementMethod=linkMovementMethod
        githubText.movementMethod=linkMovementMethod

        constraint.apply {
            setBackgroundResource(R.drawable.grain_animation)
            grainAnimation= background as AnimationDrawable
        }

        grainAnimation.start()
    }




}