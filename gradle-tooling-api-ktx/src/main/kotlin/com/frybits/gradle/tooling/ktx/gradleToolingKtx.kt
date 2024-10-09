/*
 *  Copyright 2024 Pablo Baxter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Created by Pablo Baxter (Github: pablobaxter)
 * https://github.com/pablobaxter/gradle-tooling-api-ktx
 */

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
    get(CoroutineResultHandler(cont))
}

suspend fun <T> BuildActionExecuter<T>.await() : T = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

suspend fun BuildLauncher.await(): Void = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

inline fun <reified T> ProjectConnection.getModel() : T {
    return getModel(T::class.java)
}

suspend inline fun <reified T> ProjectConnection.awaitModel() : T = suspendCancellableCoroutine { cont ->
    getModel(T::class.java, CoroutineResultHandler(cont))
}

suspend fun TestLauncher.await(): Void = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

inline fun <reified T> ProjectConnection.model() : ModelBuilder<T> {
    return model(T::class.java)
}

@PublishedApi
internal class CoroutineResultHandler<T>(private val continuation: Continuation<T>) : ResultHandler<T> {
    override fun onComplete(result: T) {
        continuation.resume(result)
    }

    override fun onFailure(e: GradleConnectionException) {
        continuation.resumeWithException(e)
    }
}
