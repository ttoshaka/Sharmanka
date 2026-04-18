package network.factory

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Фабрика для создания Retrofit-клиентов и OkHttp-клиентов.
 *
 * Уровень HTTP-логирования управляется переменной окружения `HTTP_LOG_LEVEL`.
 * Допустимые значения: `NONE`, `BASIC`, `HEADERS`, `BODY`.
 * Если переменная не задана или содержит неизвестное значение — используется `NONE`.
 */
object RetrofitFactory {

    private val logger = LoggerFactory.getLogger(RetrofitFactory::class.java)

    private const val CONNECT_TIMEOUT = 60L
    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 5L

    private const val ENV_HTTP_LOG_LEVEL = "HTTP_LOG_LEVEL"

    /**
     * Возвращает уровень HTTP-логирования на основе переменной окружения [ENV_HTTP_LOG_LEVEL].
     *
     * - Если переменная не задана — возвращает [HttpLoggingInterceptor.Level.NONE].
     * - При неизвестном значении — логирует предупреждение и возвращает [HttpLoggingInterceptor.Level.NONE].
     */
    private fun resolveLogLevel(): HttpLoggingInterceptor.Level {
        val raw = System.getenv(ENV_HTTP_LOG_LEVEL)
            ?: return HttpLoggingInterceptor.Level.NONE

        return try {
            enumValueOf<HttpLoggingInterceptor.Level>(raw.uppercase())
        } catch (e: IllegalArgumentException) {
            logger.warn(
                "Unknown HTTP_LOG_LEVEL value \"{}\". Allowed: NONE, BASIC, HEADERS, BODY. Falling back to NONE.",
                raw,
            )
            HttpLoggingInterceptor.Level.NONE
        }
    }

    /**
     * Создаёт [OkHttpClient] с HTTP-логированием и дополнительными перехватчиками.
     *
     * @param interceptors дополнительные [Interceptor], добавляемые после логирующего
     */
    fun createOkHttpClient(vararg interceptors: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(resolveLogLevel()))
            .apply {
                interceptors.forEach { addInterceptor(it) }
            }
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Создаёт [Retrofit]-инстанс с заданным базовым URL и OkHttp-клиентом.
     *
     * @param baseUrl базовый URL API
     * @param client  настроенный [OkHttpClient]
     */
    fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(baseUrl)
            .build()
    }

    /**
     * Создаёт типизированный Retrofit API-интерфейс [T] для заданного базового URL.
     *
     * @param baseUrl      базовый URL API
     * @param interceptors дополнительные перехватчики OkHttp
     */
    inline fun <reified T> createApi(baseUrl: String, vararg interceptors: Interceptor): T {
        val client = createOkHttpClient(*interceptors)
        val retrofit = createRetrofit(
            baseUrl = baseUrl,
            client = client,
        )
        return retrofit.create(T::class.java)
    }
}
