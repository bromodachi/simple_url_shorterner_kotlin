package bromodachi.urlshortner

enum class ApiErrorCodes(val errorCode: String, val errorMessage: String) {
    INVALID_REQUEST(getErrorCode(1), "INVALID_REQUEST"),
    DUPLICATE_REQUEST(getErrorCode(2), "DUPLICATE_REQUEST"),
    NOT_FOUND(getErrorCode(3), "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(getErrorCode(4), "INTERNAL_SERVER_ERROR"),
    DATABASE_ERROR(getErrorCode(5), "DATABASE_ERROR");

    companion object {
        const val SERVER_ERROR_CODE = "911"
    }
}
private fun getErrorCode(value: Int): String {
    return ApiErrorCodes.SERVER_ERROR_CODE + value.toString().padStart(4, '0')
}

const val LONG_URL = "long_url"
const val SHORT_URL = "short_url"