package bromodachi.urlshortner.exceptions

import bromodachi.urlshortner.ApiErrorCodes
import org.springframework.http.HttpStatus

sealed class ServerRuntimeException(
    open val status: HttpStatus,
    open val errorCode: ApiErrorCodes,
    override val message: String
): RuntimeException()

class DuplicateException(
    override val status: HttpStatus = HttpStatus.BAD_REQUEST,
    override val errorCode: ApiErrorCodes = ApiErrorCodes.INVALID_REQUEST,
    override val message: String = "DUPLICATE REQUEST RECEIVED",
    val field: String
) : ServerRuntimeException(status, errorCode, message)

class InternalServerError(
    override val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    override val errorCode: ApiErrorCodes = ApiErrorCodes.INTERNAL_SERVER_ERROR,
    override val message: String = errorCode.errorMessage,
) : ServerRuntimeException(status, errorCode, message)

class BadRequestException(
    override val status: HttpStatus = HttpStatus.BAD_REQUEST,
    override val errorCode: ApiErrorCodes = ApiErrorCodes.INVALID_REQUEST,
    override val message: String = "DUPLICATE REQUEST RECEIVED",
) : ServerRuntimeException(status, errorCode, message)