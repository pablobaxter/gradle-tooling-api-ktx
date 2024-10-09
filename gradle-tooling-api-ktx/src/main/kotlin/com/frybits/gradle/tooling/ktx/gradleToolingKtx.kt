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
import org.gradle.tooling.BuildActionFailureException
import org.gradle.tooling.BuildCancelledException
import org.gradle.tooling.BuildException
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.ModelBuilder
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.ResultHandler
import org.gradle.tooling.TestExecutionException
import org.gradle.tooling.TestLauncher
import org.gradle.tooling.UnknownModelException
import org.gradle.tooling.UnsupportedVersionException
import org.gradle.tooling.exceptions.UnsupportedBuildArgumentException
import org.gradle.tooling.exceptions.UnsupportedOperationConfigurationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Fetch the model, suspending until it is available.
 *
 * @return The model.
 * @throws UnsupportedVersionException When the target Gradle version does not support building models.
 * @throws UnknownModelException When the target Gradle version or build does not support the requested model.
 * @throws UnsupportedOperationConfigurationException When the target Gradle version does not support some requested configuration option such as [ModelBuilder.withArguments].
 * @throws UnsupportedBuildArgumentException When there is a problem with build arguments provided by [ModelBuilder.withArguments].
 * @throws BuildException On some failure executing the Gradle build.
 * @throws BuildCancelledException When the operation was cancelled before it completed successfully.
 * @throws GradleConnectionException On some other failure using the connection.
 * @throws IllegalStateException When the connection has been closed or is closing.
 */
suspend fun <T> ModelBuilder<T>.await(): T = suspendCancellableCoroutine { cont ->
    get(CoroutineResultHandler(cont))
}

/**
 * Runs the action, suspending until its result is available.
 *
 * @throws UnsupportedVersionException When the target Gradle version does not support build action execution.
 * @throws UnsupportedOperationConfigurationException When the target Gradle version does not support some requested configuration option.
 * @throws UnsupportedBuildArgumentException When there is a problem with build arguments provided by [BuildActionExecuter.withArguments].
 * @throws BuildActionFailureException When the build action fails with an exception.
 * @throws BuildCancelledException When the operation was cancelled before it completed successfully.
 * @throws BuildException On some failure executing the Gradle build.
 * @throws GradleConnectionException On some other failure using the connection.
 * @throws IllegalStateException When the connection has been closed or is closing.
 */
suspend fun <T> BuildActionExecuter<T>.await() : T = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

/**
 * Executes the build, suspending until it is complete.
 *
 * @throws UnsupportedVersionException When the target Gradle version does not support build execution.
 * @throws UnsupportedOperationConfigurationException When the target Gradle version does not support some requested configuration option such as [BuildLauncher.withArguments].
 * @throws UnsupportedBuildArgumentException When there is a problem with build arguments provided by [BuildLauncher.withArguments].
 * @throws BuildException On some failure executing the Gradle build.
 * @throws BuildCancelledException When the operation was cancelled before it completed successfully.
 * @throws GradleConnectionException On some other failure using the connection.
 * @throws IllegalStateException When the connection has been closed or is closing.
 */
suspend fun BuildLauncher.await(): Void = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

/**
 * Fetches a snapshot of the model of the given type for this project. This method blocks until the model is available.
 *
 * This method is simply a convenience for calling `model(modelType).get()`
 *
 * @return The model.
 * @throws UnsupportedVersionException When the target Gradle version does not support the given model.
 * @throws UnknownModelException When the target Gradle version or build does not support the requested model.
 * @throws BuildException On some failure executing the Gradle build, in order to build the model.
 * @throws GradleConnectionException On some other failure using the connection.
 * @throws IllegalStateException When this connection has been closed or is closing.
 */
inline fun <reified T> ProjectConnection.getModel() : T {
    return getModel(T::class.java)
}

/**
 * Fetches a snapshot of the model of the given type for this project. This method suspends until the model is available.
 *
 * This method is simply a convenience for calling `model<ModelType>().await()`
 *
 * @return The model.
 * @throws UnsupportedVersionException When the target Gradle version does not support the given model.
 * @throws UnknownModelException When the target Gradle version or build does not support the requested model.
 * @throws BuildException On some failure executing the Gradle build, in order to build the model.
 * @throws GradleConnectionException On some other failure using the connection.
 * @throws IllegalStateException When this connection has been closed or is closing.
 */
suspend inline fun <reified T> ProjectConnection.awaitModel() : T = suspendCancellableCoroutine { cont ->
    getModel(T::class.java, CoroutineResultHandler(cont))
}

/**
 * Executes the tests, suspending until complete.
 *
 * @throws TestExecutionException when one or more tests fail, or no tests for execution declared or no matching tests can be found.
 * @throws UnsupportedVersionException When the target Gradle version does not support test execution.
 * @throws UnsupportedBuildArgumentException When there is a problem with build arguments provided by [TestLauncher.withArguments].
 * @throws UnsupportedOperationConfigurationException When the target Gradle version does not support some requested configuration option.
 * @throws BuildException On some failure while executing the tests in the Gradle build.
 * @throws BuildCancelledException When the operation was cancelled before it completed successfully.
 * @throws GradleConnectionException On some other failure using the connection.
 * @throws IllegalStateException When the connection has been closed or is closing.
 */
suspend fun TestLauncher.await(): Void = suspendCancellableCoroutine { cont ->
    run(CoroutineResultHandler(cont))
}

/**
 * Creates a builder which can be used to query the model of the given type.
 *
 * Any of following models types may be available, depending on the version of Gradle being used by the target
 * build:
 *
 *   * [org.gradle.tooling.model.gradle.GradleBuild]
 *   * [org.gradle.tooling.model.build.BuildEnvironment]
 *   * [org.gradle.tooling.model.GradleProject]
 *   * [org.gradle.tooling.model.gradle.BuildInvocations]
 *   * [org.gradle.tooling.model.gradle.ProjectPublications]
 *   * [org.gradle.tooling.model.idea.IdeaProject]
 *   * [org.gradle.tooling.model.idea.BasicIdeaProject]
 *   * [org.gradle.tooling.model.eclipse.EclipseProject]
 *   * [org.gradle.tooling.model.eclipse.HierarchicalEclipseProject]
 *
 * A build may also expose additional custom tooling models. You can use this method to query these models.
 *
 * @return The builder.
 */
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
