package tube.chikichiki.sako.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import tube.chikichiki.sako.R

class CastProvider : OptionsProvider {
    override fun getCastOptions(p0: Context): CastOptions {
        return CastOptions.Builder().setReceiverApplicationId(p0.getString(R.string.reciever_id)).build()
    }

    override fun getAdditionalSessionProviders(p0: Context): MutableList<SessionProvider>? {
        return null
    }

}