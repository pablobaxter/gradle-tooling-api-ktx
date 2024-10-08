package com.frybits.gradle.tooling.ktx

import kotlinx.coroutines.suspendCancellableCoroutine
import org.gradle.tooling.BuildActionExecuter
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.ModelBuilder
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.ResultHandler
import org.gradle.tooling.TestLauncher
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> ModelBuilder<T>.await(): T = suspendCancellableCoroutine { cont ->
    this.
    get(CoroutineResultHandler(cont))
}

suspend fun <T> BuildActionExecuter<T>.await() : T = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

suspend fun BuildLauncher.await() = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

inline fun <reified T> ProjectConnection.model() : T {
    return getModel(T::class.java)
}

suspend inline fun <reified T> ProjectConnection.getModel() : T = suspendCancellableCoroutine { cont ->
    getModel(T::class.java, object : ResultHandler<T> {
        override fun onComplete(result: T) {
            cont.resume(result)
        }

        override fun onFailure(e: GradleConnectionException) {
            cont.resumeWithException(e)
        }
    })
}

suspend fun TestLauncher.await() = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

private class CoroutineResultHandler<T>(private val continuation: Continuation<T>) : ResultHandler<T> {
    override fun onComplete(result: T) {
        continuation.resume(result)
    }

    override fun onFailure(e: GradleConnectionException) {
        continuation.resumeWithException(e)
    }
}
