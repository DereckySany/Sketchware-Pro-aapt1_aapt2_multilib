package mod.jbk.diagnostic;

import static mod.SketchwareUtil.getDip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sketchware.remod.R;
import android.content.Intent;

import com.besome.sketch.tools.CompileLogActivity;
import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FilePathUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.hey.studios.util.CompileLogHelper;

public class CompileErrorSaver {

    private static final String MESSAGE_NO_COMPILE_ERRORS_SAVED = "No compile errors have been saved yet.";

    private final String sc_id;
    private final String path;

    /**
     * Create this helper class for saving compile errors.
     *
     * @param sc_id The Sketchware project ID for the project to operate on, like 605
     */
    public CompileErrorSaver(String sc_id) {
        this.sc_id = sc_id;
        path = FilePathUtil.getLastCompileLogPath(sc_id);
    }

    /**
     * Save a compile error in the project's last compile error file.
     *
     * @param errorText The text to save, if possible, with detailed messages
     */
    public void writeLogsToFile(String errorText) {
        if (logFileExists()) FileUtil.deleteFile(path);
        FileUtil.writeFile(path, errorText);
    }

    /**
     * Opens {@link CompileLogActivity} and shows the user the last compile error.
     */
    public void showLastErrors(Context context) {
        Intent intent = new Intent(context, CompileLogActivity.class);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("showingLastError", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    /**
     * Show an {@link AlertDialog} that displays the last saved error to the user.
     *
     * @param context The context to show the dialog on
     */
    public void showDialog(Context context) {
        ScrollView scrollView = new ScrollView(context);
        TextView errorLogTxt = new TextView(context);
        errorLogTxt.setText(CompileLogHelper.colorErrsAndWarnings(getLogsFromFile()));
        errorLogTxt.setTextIsSelectable(true);
        scrollView.addView(errorLogTxt);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Last compile log")
                .setPositiveButton(R.string.common_word_ok, null)
                .setNegativeButton("Clear", (dialog1, which) -> {
                    deleteSavedLogs();
                    SketchwareUtil.toast("Cleared log");
                })
                .setNeutralButton("Show longView", (dialog1, which) -> {
                    new CompileErrorSaver(sc_id).showLastErrors(context);
                })
                .create();

        dialog.setView(scrollView,
                (int) getDip(24),
                (int) getDip(8),
                (int) getDip(24),
                (int) getDip(8));

        dialog.show();

        if (errorLogTxt.getText().toString().equals(MESSAGE_NO_COMPILE_ERRORS_SAVED)) {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.GONE);
        }
    }

    /**
     * Clear the last saved error text.
     */
    public void deleteSavedLogs() {
        FileUtil.deleteFile(path);
    }

    /**
     * @return The last saved error text
     */
    public String getLogsFromFile() {
        if (!logFileExists()) return MESSAGE_NO_COMPILE_ERRORS_SAVED;
        return FileUtil.readFile(path);
    }

    /**
     * Check if the last saved error text file exists.
     */
    public boolean logFileExists() {
        return FileUtil.isExistFile(path);
    }
}
