# IntelliJ IDE Starter - Changelists Settings UI Test

This project demonstrates how to write automated UI tests for JetBrains IDEs using the [intellij-tools-ide-starter](https://github.com/JetBrains/intellij-community/blob/master/tools/intellij.tools.ide.starter/README.md) framework.

## Test Scenario

The test automates the following user workflow:
1. Open an IntelliJ-based IDE (IntelliJ IDEA Ultimate 2026.1)
2. Open a public test project from GitHub (`JetBrains/intellij-ide-starter`)
3. Open **Settings...**
4. Navigate to **Version Control -> Changelists**
5. Enable **Create changelists automatically** checkbox
6. Verify that the checkbox is selected
7. Click **OK** to save settings

## How It Works

### Project Structure

```
sdet-intern-functional-automation/
├── build.gradle.kts                    # Gradle build configuration
├── settings.gradle.kts                 # Gradle settings
└── src/test/kotlin/com/example/starter/
    └── ChangelistsSettingsTest.kt      # Main UI test
```


#### 1. Test Setup (`ChangelistsSettingsTest.kt`)

The test begins by configuring the testing environment:

```kotlin
val testProject = GitHubProject.fromGithub(
    branchName = "master",
    repoRelativeUrl = "JetBrains/intellij-ide-starter"
)
```
- **GitHubProject**: Defines which project to open in the IDE. The test uses the `intellij-ide-starter` repository.

```kotlin
val iu2026 = IdeProductProvider.IU.copy(
    buildNumber = "261.22158.277", 
    version = "2026.1"
)
```
- **IdeProductProvider.IU**: Specifies IntelliJ IDEA Ultimate as the IDE to test
- **buildNumber & version**: Pins a specific IDE version for reproducible tests

```kotlin
Starter.newContext(
    CurrentTestMethod.hyphenateWithClass(),
    TestCase(iu2026, testProject)
)
.setupSdk(jdk21.toSdk())
.prepareProjectCleanImport()
```
- **Starter.newContext**: Creates a test execution context
- **setupSdk**: Configures JDK 21 for the test project

#### 2. IDE Launch and Driver Connection (`ChangelistsSettingsTest.kt`)

```kotlin
.runIdeWithDriver()
.useDriverAndCloseIde {
    waitForIndicators(5.minutes)
```
- **runIdeWithDriver()**: Launches the IDE with the remote driver protocol enabled
- **useDriverAndCloseIde**: Establishes a connection to the IDE's UI automation driver
- **waitForIndicators**: Waits up to 5 minutes for background tasks (indexing, project loading) to complete

#### 3. UI Interaction (`ChangelistsSettingsTest.kt`)

The test uses the **UI Driver SDK** to interact with IDE components:

```kotlin
ideFrame {
    openSettingsDialog()
```
- **ideFrame**: Root component representing the main IDE window
- **openSettingsDialog()**: Opens the Settings dialog

```kotlin
settingsDialog {
    openTreeSettingsSection("Version Control", "Changelists")
```
- **settingsDialog**: Component representing the Settings dialog
- **openTreeSettingsSection**: Navigates the settings tree to Version Control -> Changelists

```kotlin
val checkbox = checkBoxWithName("Create changelists automatically")
checkbox.check()
```
- **checkBoxWithName**: Locates a checkbox by its accessible name
- **check()**: Selects the checkbox if not already selected

```kotlin
shouldBe("Checkbox is selected") {
    checkbox.isSelected()
}
```
- **shouldBe**: Assertion that throws an exception if the condition fails
- **isSelected()**: Verifies the checkbox state

```kotlin
okButton.click()
}
waitForNoOpenedDialogs()
```
- **okButton.click()**: Closes the Settings dialog
- **waitForNoOpenedDialogs()**: Ensures the dialog closed successfully

## Prerequisites

- JDK 21
- Internet connection (to download IDE builds and clone test projects)
- ~2-3 GB of free disk space (for IDE installation and test project)

## Running the Tests


```bash
./gradlew test
```


### What Happens During Test Execution

1. The framework downloads IntelliJ IDEA Ultimate 2026.1 (if not cached)
2. Clones `JetBrains/intellij-ide-starter` repository
3. Starts the IDE with the cloned project
4. Waits for indexing and background tasks to complete
5. Executes the test scenario using the driver
6. Closes the IDE and cleans up temporary files




## References

- [IntelliJ IDE Starter Documentation](https://github.com/JetBrains/intellij-community/blob/master/tools/intellij.tools.ide.starter/README.md)
- [UI Test With Driver Example](https://github.com/JetBrains/intellij-ide-starter/blob/master/intellij.tools.ide.starter.examples/testSrc/com/intellij/ide/starter/examples/driver/UiTestWithDriver.kt)
- [Driver SDK UI Components](https://github.com/JetBrains/intellij-community/tree/master/platform/remote-driver/test-sdk/src/com/intellij/driver/sdk/ui)
