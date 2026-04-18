package network

import java.io.IOException

/**
 * Исключение, выбрасываемое при неуспешном HTTP-ответе от сетевого API.
 *
 * @param message описание ошибки, включая HTTP-код и тело ошибки
 */
class NetworkException(message: String) : IOException(message)
