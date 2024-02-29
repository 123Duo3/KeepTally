package com.konyaco.keeptally.api.exception

open class KeepTallyException(
    override val message: String?,
    override val cause: Throwable?
): Exception(message, cause)