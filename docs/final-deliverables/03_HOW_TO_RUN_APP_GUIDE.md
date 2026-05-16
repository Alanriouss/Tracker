# 03 — How to Run the Academic Tracker App

This guide is for classmates who want to download the project and run it on their own machines.

## 1. What does this app need to run?

You need:

- JDK 21
- Internet access for the first dependency download
- VS Code or IntelliJ

You do not need to install Maven manually if you use:

- `mvnw` on macOS/Linux
- `mvnw.cmd` on Windows

## 2. What files/folders must the zip contain?

The shared zip should contain a structure like this:

```text
academic-tracker-master-2/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
├── src/
├── student_data.json
└── docs/
```

Important:

- Do not run the project directly inside the zip file.
- Extract it first.
- Open the folder that contains `pom.xml`.

## 3. Important: the external prediction-core folder is NOT needed

After integration, prediction-core is already inside the app project:

```text
src/main/java/com/academictracker/prediction2
```

That means classmates do not need a separate `prediction-core-2.0` folder.

If the app fails because prediction-core classes are missing, check that this folder exists inside the extracted project.

## 4. How to run on macOS/Linux with VS Code

1. Extract the zip.
2. Open VS Code.
3. Choose `File -> Open Folder -> academic-tracker-master-2`.
4. Open the terminal.
5. Check Java:

```bash
java -version
```

6. If needed, make `mvnw` executable:

```bash
chmod +x ./mvnw
```

7. Run the app:

```bash
./mvnw clean javafx:run
```

Quick run after the first build:

```bash
./mvnw javafx:run
```

## 5. How to run on Windows with VS Code

1. Extract the zip.
2. Open VS Code.
3. Choose `File -> Open Folder -> academic-tracker-master-2`.
4. Open a PowerShell terminal.
5. Check Java:

```powershell
java -version
```

6. Run the app:

```powershell
.\mvnw.cmd clean javafx:run
```

Quick run after the first build:

```powershell
.\mvnw.cmd javafx:run
```

## 6. How to run on Windows with IntelliJ

1. Extract the zip.
2. Open IntelliJ.
3. Choose `File -> Open`.
4. Select the `academic-tracker-master-2` folder.
5. Trust the project if prompted.
6. Let IntelliJ load the Maven project.
7. Set the Project SDK to JDK 21:

```text
File -> Project Structure -> Project SDK -> JDK 21
```

8. Reload Maven if needed.
9. Open the IntelliJ terminal.
10. Run:

```powershell
.\mvnw.cmd clean javafx:run
```

## 7. How to check that the app is running correctly

After the app opens:

- The dashboard should load.
- Data from `student_data.json` should appear.
- Course Roster should open.
- Course Details should open.
- Target course grade prediction should work.
- Target GPA prediction should work.
- Settings should open.

## 8. How to test prediction-core quickly

### Test score normalization

Add an assessment with:

- Weight = 50
- Score = 8
- Max Score = 10

Expected:

- The current grade should be around 80%, not 8%.

### Test course target

If 50% of the course already has a score of `8/10 = 80%`:

- Target 90 should require 100% on the remaining 50%.
- Target 95 should be unreachable/impossible.

### Test invalid weights

Add assessment weights:

- 60
- 60

Expected:

- The app should not crash.
- The UI should show an invalid-data or check-weights warning.

## 9. Common errors and fixes

### Error: `zsh: permission denied: ./mvnw`

Fix:

```bash
chmod +x ./mvnw
./mvnw clean javafx:run
```

### Error: `zsh: command not found: .mvnw.cmd`

Explanation:

- `.mvnw.cmd` is not the correct command.
- `mvnw.cmd` is the Windows wrapper.
- On macOS/Linux, use:

```bash
./mvnw clean javafx:run
```

### Error: `mvn is not recognized`

Fix:

- Use Maven Wrapper instead:

```powershell
.\mvnw.cmd clean javafx:run
```

### Error: Java version is not 21

Fix:

- Install/select JDK 21.
- In IntelliJ: `File -> Project Structure -> SDK 21`
- In VS Code: use `Java: Configure Java Runtime`

### Error: the app opens but data is missing

Fix:

- Make sure `student_data.json` is next to `pom.xml`.
- Open the correct folder.
- Do not run from a nested folder by mistake.

### Error: module not found

Check `pom.xml` and confirm the current main class is:

```text
com.tracker.academictracker.app.Launcher
```

This project does not use `module-info.java`, so it should run through the configured non-modular/classpath setup in the Maven plugin.

### Error: prediction-core package missing

Check this folder:

```text
src/main/java/com/academictracker/prediction2
```

If that folder is missing, the zip is incomplete.

## 10. Clean zip checklist before sharing

Keep:

- `pom.xml`
- `mvnw`
- `mvnw.cmd`
- `.mvn/`
- `src/`
- `student_data.json`
- `docs/`

Remove:

- `target/`
- `.idea/` if not needed
- `.DS_Store`
- `__MACOSX/`
- `*.class`

Do not remove:

- `src/main/java/com/academictracker/prediction2`
- `src/main/resources/com/tracker/academictracker`
- `student_data.json`

## 11. Final quick command summary

macOS/Linux:

```bash
chmod +x ./mvnw
./mvnw clean javafx:run
```

Windows:

```powershell
.\mvnw.cmd clean javafx:run
```
