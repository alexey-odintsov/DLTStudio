A very simple desktop DLT logs analyzing app written with Compose Multiplatform.
<img src="doc/preview_logs.png" width="600"> <img src="doc/preview_timeline.png" width="600">

# Features
- Logs view screen
  - Plain text and regex search by Payload (so far)
  - DLT message Simple and Detailed info panel
  - Warn, Error and Fatal messages color filters with toggle buttons
  - Customisable color filters
- Zoomable and movable timeline screen
  - Users switch state view
  - CPUC and CPUS views which show CPU usage
  - Custom timeline views

# Usage
Just drag and drop your dlt files to the app.

# Run

IntelliJ IDEA / Android Studio

1. New -> Project from Version Control -> Specify clone Url

2. Add new Gradle configuration and specify run task
<img src="doc/gradle_configuration.png" width="400">

3. Click 'Run'

or in terminal run command:
```
./gradlew :composeApp:run
```


# Distribute
```
./gradlew packageDmg
```

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
