# UC-4 Save/Export Use Case – Full Design & Data Flow

This document explains exactly what UC-4 (Save/Export) does, which components it touches, what data it consumes, and what it produces.

---

## 1. Goal
Give the user a way to **archive and share a complete backtesting project** by exporting it to CSV, HTML, JSON (or CSV+HTML). UC-4 reads the project that UC-2 (Factor Configuration) and UC-3 (Backtest Results) recorded, writes it to disk, and reports success/errors back to the UI.

---

## 2. Clean Architecture Overview

| Layer | Files | Responsibilities |
|-------|-------|------------------|
| **Entity** | `Project`, `BacktestConfig`, `BacktestResult`, `FactorScore`, `Universe`, `Trade`, `Side` | Define immutable data structures describing the full project state |
| **Use Case** | `SaveExportInteractor`, `SaveExportInputData/OutputData`, `SaveExportInputBoundary/OutputBoundary`, `ProjectRepository`, `FileExportGateway` | Business logic: load project, persist it, export to chosen format, report outcome |
| **Interface Adapter** | `SaveExportController`, `SaveExportPresenter`, `SaveExportViewModel`, `SaveExportState` | Convert View input → interactor request, convert output → UI-friendly messages |
| **Framework/UI** | `SaveExportView`, `LoggedInView` button, `ConfigureFactorsView` button, `FileProjectRepository`, `FileExportGatewayImpl` | Concrete Swing UI and file system implementations |

---

## 3. Step-by-Step Flow

1. **User opens Save/Export screen** from LoggedInView or ConfigureFactorsView.
2. User enters:
   - Project ID
   - Project Name (used in default file names)
   - Export Type (`CSV`, `HTML`, `BOTH`, `JSON`)
   - Optional custom file path
3. User presses **“Save & Export”**. `SaveExportView` calls `SaveExportController.execute(...)`.
4. `SaveExportController` builds `SaveExportInputData` and calls `SaveExportInteractor.execute(inputData)`.
5. `SaveExportInteractor`:
   - Loads project with `projectRepository.load(projectId)`
   - Saves latest snapshot with `projectRepository.save(project)`
   - Selects export routine based on `exportType`
   - Delegates to `FileExportGateway` (CSV/HTML/JSON)
   - Builds `SaveExportOutputData` with message + file paths
   - On error, calls presenter with fail message
6. `SaveExportPresenter` updates `SaveExportState`:
   - `message` (success text + file list) or
   - `errorMessage`
7. `SaveExportView` listens for property changes and updates labels so the user sees the result.

---

## 4. Inputs (Where They Come From)

| Name | Source | Details |
|------|--------|---------|
| `projectId` | Text field in `SaveExportView` | Primary key to load project from `ProjectRepository` |
| `projectName` | Text field | Used to build default filenames (`exports/<name>_<type>`) |
| `exportType` | Combo box (CSV/HTML/BOTH/JSON) | Determines which export methods run |
| `filePath` (optional) | Text field | Overrides default output directory |

All of the above are packaged into `SaveExportInputData` by the controller.

---

## 5. Use Case Logic (`SaveExportInteractor`)

```text
Input  -> SaveExportInputData(projectId, projectName, exportType, filePath?)
DAO    -> ProjectRepository (load/save)
Gateway-> FileExportGateway (CSV/HTML/JSON)
Output -> SaveExportOutputData(message, exportedPaths[]) OR failure string
```

Algorithm in `execute()`:
1. `project = projectRepository.load(projectId)`
   - If null → `prepareFailView("Project ... not found.")`
2. `projectRepository.save(project)` to persist current state.
3. Determine `basePath`:
   - custom path if provided
   - else `exports/<sanitizedName>_<type>`
4. Call matching gateway method:
   - CSV → `exportCSV(project, basePath + ".csv")`
   - HTML → `exportHTML(project, basePath + ".html")`
   - BOTH → both methods
   - JSON → `exportJSON(project, basePath + ".json")`
5. Build success message and call `prepareSuccessView(...)`
6. Catch any `RuntimeException` and call `prepareFailView("Error during save/export: ...")`

---

## 6. Outputs (Where They Go)

| Output | Destination | Description |
|--------|-------------|-------------|
| Success message + list of exported files | `SaveExportPresenter → SaveExportState.message` → `SaveExportView` | Displayed in green text, includes file paths |
| Error message | `SaveExportPresenter → SaveExportState.errorMessage` | Displayed in red text |
| Physical files | `FileExportGatewayImpl` writes to disk | CSV/HTML/JSON files stored under provided path |

---

## 7. Gateways & Data Sources

### ProjectRepository (`FileProjectRepository`)
- In-memory `Map<String, Project>`
- `load(id)`, `save(project)`, `exists(id)`
- Placeholder until UC-2/UC-3 persist data permanently

### FileExportGateway (`FileExportGatewayImpl`)
- `exportCSV`:
  - Writes metadata, universe tickers, backtest config, factor weights, metrics, equity curve
- `exportHTML`:
  - Builds styled HTML report with tables for config, factor weights, metrics
- `exportJSON`:
  - Serializes `Project` (metadata, universe, config, metrics) for saving/reloading

Each method ensures directories exist and returns the absolute path of the file created.

---

## 8. UI Integration

- **Entry points**: Buttons in `LoggedInView` and `ConfigureFactorsView` set the ViewManager state to `"save export"`.
- `AppBuilder.addSaveExportView()` registers the card, creates `SaveExportViewModel`, wires property listeners.
- `AppBuilder.addSaveExportUseCase()` wires controller/presenter/interactor and injects the shared `projectRepository`.
- `SaveExportView` handles:
  - Form layout
  - Real-time state updates via DocumentListeners
  - Showing messages from the view model

---

## 9. Testing
`SaveExportInteractorTest` (10 tests, 100% coverage) validates:
- CSV, HTML, BOTH, JSON success paths
- Custom file path usage
- Lowercase export type accepted (`"csv"`)
- Error paths: project missing, invalid type, repository failure, gateway failure

All dependencies are mocked so tests run quickly without touching disk.

---

## 10. Dependencies on Other Use Cases

| Upstream Producer | What UC-4 reads | Status |
|-------------------|-----------------|--------|
| **UC-2 (Factor Configuration)** | `BacktestConfig` and `FactorScore` stored inside `Project` | When UC-2 saves a configuration, it must call `projectRepository.save(project)` so UC-4 can export it |
| **UC-3 (Run Backtest)** | `BacktestResult` metrics, equity curve, drawdown | UC-3 writes results into the same `Project` instance |

Until UC-2/UC-3 are integrated, `createTestProject()` in `AppBuilder` seeds `projectRepository` with `demo-project-1` for demo purposes.

---

## 11. Quick Reference Table

| Step | Class | Method |
|------|-------|--------|
| Capture user input | `SaveExportView` | Form listeners update `SaveExportState` |
| Trigger use case | `SaveExportController` | `execute(projectId, projectName, exportType, filePath)` |
| Business logic | `SaveExportInteractor` | `execute(inputData)` |
| File writing | `FileExportGatewayImpl` | `exportCSV/HTML/JSON` |
| Update UI state | `SaveExportPresenter` | `prepareSuccessView / prepareFailView` |
| Show message | `SaveExportView` | `propertyChange(...)` |

---

## 12. TL;DR
UC-4 takes **Project ID + export preferences** from the SaveExportView, **loads the project** from `ProjectRepository`, **writes CSV/HTML/JSON** files through `FileExportGatewayImpl`, and **pushes success/error messages back** to the UI so the user immediately sees where their exports were saved.

