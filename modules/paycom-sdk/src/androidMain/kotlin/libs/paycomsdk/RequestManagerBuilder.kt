package libs.paycomsdk

import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created at December 2019
 * @project libs
 * @author Dostonbek Kamalov (a.k.a @ddk9499)
 */

class RequestManagerBuilder {

    private var okHttpClient: OkHttpClient? = null
    private var defaults = DefaultRequestOptions()
    private var merchantId: String? = null
    private var merchantPassword: String? = null

    fun okHttpClient(client: OkHttpClient) = apply {
        okHttpClient = client
    }

    fun setMerchantData(id: String, password: String) = apply {
        merchantId = id
        merchantPassword = password
    }

    /**
     * The default [CoroutineDispatcher] to run image requests on.
     *
     * Default: [Dispatchers.IO]
     */
    fun dispatcher(dispatcher: CoroutineDispatcher) = apply {
        defaults = this.defaults.copy(dispatcher = dispatcher)
    }

    fun isLoggable(state: Boolean) = apply {
        defaults = this.defaults.copy(isLoggable = state)
    }

    fun setDemoMode(state: Boolean) = apply {
        defaults = this.defaults.copy(isSandBoxMode = state)
    }

    fun build(): RequestManager {
        assert(merchantId != null && merchantPassword != null) { "You must call setMerchantData() before calling build()." }

        return RealRequestManager(
            defaults.copy(merchantId = merchantId!!, merchantPassword = merchantPassword!!),
            okHttpClient ?: buildOkHttpClient()
        )
    }

    private fun buildOkHttpClient(): OkHttpClient = OkHttpClient.Builder().also {
        it.connectTimeout(30_000, TimeUnit.MILLISECONDS)
        if (defaults.isLoggable) {
            it.addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
    }.build()
}
