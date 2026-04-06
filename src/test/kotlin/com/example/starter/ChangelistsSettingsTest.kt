package com.example.starter

import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.checkBoxWithName
import com.intellij.driver.sdk.ui.components.elements.waitForNoOpenedDialogs
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.driver.sdk.ui.shouldBe
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.config.ConfigurationStorage
import com.intellij.ide.starter.config.splitMode
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.junit5.hyphenateWithClass
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import com.intellij.ide.starter.sdk.JdkDownloaderFacade.jdk21
import kotlin.time.Duration.Companion.minutes
import org.junit.jupiter.api.Test

class ChangelistsSettingsTest {

    private val testProject = GitHubProject.fromGithub(
        branchName = "master",
        repoRelativeUrl = "JetBrains/intellij-ide-starter"
    )

    @Test
    fun createChangelistsAutomaticallyIsEnabled() {
        val iu2026 = IdeProductProvider.IU.copy(buildNumber = "261.22158.277", version = "2026.1")
        ConfigurationStorage.splitMode(false)

        Starter.newContext(
            CurrentTestMethod.hyphenateWithClass(),
            TestCase(iu2026, testProject)
        )
            .setupSdk(jdk21.toSdk())
            .prepareProjectCleanImport()
            .runIdeWithDriver()
            .useDriverAndCloseIde {
                waitForIndicators(5.minutes)

            ideFrame {
                openSettingsDialog()

                settingsDialog {
                    openTreeSettingsSection("Version Control", "Changelists")

                    val checkbox = checkBoxWithName("Create changelists automatically")
                    checkbox.check()

                    shouldBe("Checkbox is selected") {
                        checkbox.isSelected()
                    }

                    okButton.click()
                }

                waitForNoOpenedDialogs()
            }
            }
    }
}