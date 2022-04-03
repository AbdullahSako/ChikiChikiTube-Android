package tube.chikichiki.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider

class CastProvider : OptionsProvider {
    override fun getCastOptions(p0: Context): CastOptions {
        return CastOptions.Builder().setReceiverApplicationId("716A8087").build()
    }

    override fun getAdditionalSessionProviders(p0: Context): MutableList<SessionProvider>? {
        return null
    }

}