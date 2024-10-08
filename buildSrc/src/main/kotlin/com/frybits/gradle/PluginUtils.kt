package com.frybits.gradle

import com.autonomousapps.DependencyAnalysisPlugin
import com.autonomousapps.DependencyAnalysisSubExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

internal fun Project.configureCommon() {
    applyKtlint()
    applyDependencyAnalysis()
}

internal fun Project.applyKtlint() {
    apply<KtlintPlugin>()
    configure<KtlintExtension> {
        version.set("0.48.2")
        outputToConsole.set(true)
        outputColorName.set("RED")
    }
}

internal fun Project.applyDependencyAnalysis() {
    apply<DependencyAnalysisPlugin>()
    configure<DependencyAnalysisSubExtension> {
        issues {
            onAny {
                severity("fail")
            }
        }
    }
}
