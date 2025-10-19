package com.sgmobile.earthquake.core.network.ktorfit

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

class KtorfitResponseConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Result::class) {

            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(result: KtorfitResult): Any {
                    return when (result) {
                        is KtorfitResult.Failure -> {
                            Result.failure<Any>(result.throwable)
                        }

                        is KtorfitResult.Success -> {
                            Result.success(result.response.body(typeData.typeArgs.first().typeInfo))
                        }
                    }
                }
            }
        }
        return null
    }
}