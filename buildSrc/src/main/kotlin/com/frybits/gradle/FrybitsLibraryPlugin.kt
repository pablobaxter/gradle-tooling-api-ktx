package com.frybits.gradle

import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.dokka.gradle.GradleExternalDocumentationLinkBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import java.net.URI

class FrybitsLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyLibraryPlugins()

        target.configureDokka()
        target.apply<MavenPublishPlugin>()
    }
}

private fun Project.applyLibraryPlugins() {
    apply<KotlinPluginWrapper>()

    configureCommon()
}

private fun Project.configureDokka() {
    apply<DokkaPlugin>()
    tasks.withType<DokkaTaskPartial> {
        dokkaSourceSets.configureEach {
//            sourceLink {
//                localDirectory.set(this@configureDokka.projectDir.resolve("src").resolve("main").resolve("kotlin"))
//                remoteUrl.set(URI("https://github.com/pablobaxter/rx-preferences/tree/master/${this@configureDokka.name}/src/main/kotlin/").toURL())
//                remoteLineSuffix.set("#L")
//            }
            externalDocumentationLinks.add(
                GradleExternalDocumentationLinkBuilder(this@configureDokka).apply {
                    url.set(URI("https://reactivex.io/RxJava/2.x/javadoc/").toURL())
                }
            )
        }
    }
}
