package interface_adapter.save_export;

import interface_adapter.ViewModel;

/**
 * The ViewModel for the Save/Export View.
 */
public class SaveExportViewModel extends ViewModel<SaveExportState> {

    public static final String TITLE_LABEL = "Save & Export Project";
    public static final String PROJECT_ID_LABEL = "Project ID";
    public static final String PROJECT_NAME_LABEL = "Project Name";
    public static final String EXPORT_TYPE_LABEL = "Export Type";
    public static final String FILE_PATH_LABEL = "File Path (optional)";
    public static final String SAVE_EXPORT_BUTTON_LABEL = "Save & Export";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public SaveExportViewModel() {
        super("save export");
        setState(new SaveExportState());
    }

}



