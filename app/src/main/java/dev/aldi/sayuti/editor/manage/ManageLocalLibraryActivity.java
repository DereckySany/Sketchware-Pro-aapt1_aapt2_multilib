package dev.aldi.sayuti.editor.manage;

import static mod.SketchwareUtil.getDip;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sketchware.remod.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import a.a.a.aB;
import a.a.a.xB;
import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.hey.studios.project.library.LibraryDownloader;
import mod.hey.studios.util.Helper;

public class ManageLocalLibraryActivity extends Activity implements View.OnClickListener, LibraryDownloader.OnCompleteListener {

    private static final String RESET_LOCAL_LIBRARIES_TAG = "reset_local_libraries";

    private boolean notAssociatedWithProject = false;
    private ListView listview;
    private String configurationFilePath = "";
    private String local_libs_path = "";
    private ArrayList<HashMap<String, Object>> lookup_list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> project_used_libs = new ArrayList<>();

    private void initToolbar() {
        ImageView back_icon = findViewById(R.id.ig_toolbar_back);
        TextView title = findViewById(R.id.tx_toolbar_title);
        ImageView importLibrary_icon = findViewById(R.id.ig_toolbar_load_file);

        Helper.applyRippleToToolbarView(back_icon);
        back_icon.setOnClickListener(Helper.getBackPressedClickListener(this));
        
        title.setText("Local library Manager");
        importLibrary_icon.setPadding(
            (int) getDip(2), 
            (int) getDip(2), 
            (int) getDip(2), 
            (int) getDip(2)
        );
        importLibrary_icon.setImageResource(R.drawable.download_80px);
        importLibrary_icon.setVisibility(View.VISIBLE);
        Helper.applyRippleToToolbarView(importLibrary_icon);
        importLibrary_icon.setOnClickListener(this);

        if (!notAssociatedWithProject) {
            ImageView reset = new ImageView(ManageLocalLibraryActivity.this);
            LinearLayout toolbar = (LinearLayout) back_icon.getParent();
            toolbar.addView(reset, 2);

            reset.setTag(RESET_LOCAL_LIBRARIES_TAG);
            {
                ViewGroup.LayoutParams layoutParams = importLibrary_icon.getLayoutParams();
                if (layoutParams != null) {
                    reset.setLayoutParams(layoutParams);
                }
            }
            reset.setImageResource(R.drawable.ic_restore_white_24dp);
            reset.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Helper.applyRippleToToolbarView(reset);
            reset.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ig_toolbar_load_file) {
            new AlertDialog.Builder(this)
                .setTitle("Dexer")
                .setMessage("Would you like to use Dx or D8 to dex the library?\n" +
                        "D8 supports Java 8, whereas Dx does not. Limitation: D8 only works on Android 8 and above.")
                .setPositiveButton("D8", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this,
                        true).showDialog(ManageLocalLibraryActivity.this))
                .setNegativeButton("Dx", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this,
                        false).showDialog(ManageLocalLibraryActivity.this))
                .setNeutralButton("Cancel", null)
                .show();
        } else if (RESET_LOCAL_LIBRARIES_TAG.equals(v.getTag())) {
            if (!notAssociatedWithProject) {
                aB dialog = new aB(this);
                dialog.a(R.drawable.rollback_96);
                dialog.b("Reset libraries?");
                dialog.a("This will reset all used local libraries for this project. Are you sure?");
                dialog.a(xB.b().a(getApplicationContext(), R.string.common_word_cancel),
                        Helper.getDialogDismissListener(dialog));
                dialog.b(xB.b().a(getApplicationContext(), R.string.common_word_reset), view -> {
                    FileUtil.writeFile(configurationFilePath, "[]");
                    SketchwareUtil.toast("Successfully reset local libraries");
                    loadFiles();
                    dialog.dismiss();
                });
                dialog.show();
            }
        }
    }

    @Override
    public void onComplete() {
        loadFiles();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_permission);

        LinearLayout searchViewContainer = findViewById(R.id.managepermissionLinearLayout1);
//        searchViewContainer.setVisibility(View.GONE);
        searchViewContainer.setBackgroundDrawable(getDrawable(R.drawable.bg_rectangle_white));
        listview = findViewById(R.id.main_content);
        ViewGroup mainContent = (ViewGroup) searchViewContainer.getParent();
        ViewGroup root = (ViewGroup) mainContent.getParent();
        root.removeView(mainContent);
        root.addView(mainContent);


        if (getIntent().hasExtra("sc_id")) {
            String sc_id = getIntent().getStringExtra("sc_id");
            notAssociatedWithProject = sc_id.equals("system");
            configurationFilePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/local_library"));
            local_libs_path = FileUtil.getExternalStorageDir().concat("/.sketchware/libs/local_libs/");
            initToolbar();
            loadFiles();
        } else {
            finish();
        }
    }

    private void loadFiles() {
        project_used_libs.clear();
        lookup_list.clear();
        if (!notAssociatedWithProject) {
            String fileContent;
            if (!FileUtil.isExistFile(configurationFilePath) || (fileContent = FileUtil.readFile(configurationFilePath)).equals("")) {
                FileUtil.writeFile(configurationFilePath, "[]");
            } else {
                project_used_libs = new Gson().fromJson(fileContent, Helper.TYPE_MAP_LIST);
            }
        }
        ArrayList<String> arrayList = new ArrayList<>();
        FileUtil.listDir(local_libs_path, arrayList);
        //noinspection Java8ListSort
        Collections.sort(arrayList, String.CASE_INSENSITIVE_ORDER);

        List<String> localLibraryNames = new LinkedList<>();
        for (String filename : arrayList) {
            if (FileUtil.isDirectory(filename)) {
                localLibraryNames.add(Uri.parse(filename).getLastPathSegment());
            }
        }
        listview.setAdapter(new LibraryAdapter(localLibraryNames));
        ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
    }

    public class LibraryAdapter extends BaseAdapter {

        private final List<String> localLibraries;

        public LibraryAdapter(List<String> localLibraries) {
            this.localLibraries = localLibraries;
        }

        @Override
        public String getItem(int position) {
            return localLibraries.get(position);
        }

        @Override
        public int getCount() {
            return localLibraries.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_item_local_lib, parent, false);
            }
            final LinearLayout indicator = convertView.findViewById(R.id.linear_content_indicator);
            final CheckBox enabled = convertView.findViewById(R.id.checkbox_content);
            enabled.setText(localLibraries.get(position));

            String libname = enabled.getText().toString();
            String libconfig = local_libs_path + libname + "/config";
            
            enabled.setOnClickListener(v -> {
                String name = enabled.getText().toString();

                String configPath = local_libs_path + name + "/config";
                String versionPath = local_libs_path + name + "/version";
                String resPath = local_libs_path + name + "/res";
                String jarPath = local_libs_path + name + "/classes.jar";
                String dexPath = local_libs_path + name + "/classes.dex";
                String manifestPath = local_libs_path + name + "/AndroidManifest.xml";
                String pgRulesPath = local_libs_path + name + "/proguard.txt";
                String assetsPath = local_libs_path + name + "/assets";

                HashMap<String, Object> localLibrary = new HashMap<>();
                localLibrary.put("name", name);
                if (FileUtil.isExistFile(configPath)) {
                    localLibrary.put("packageName", FileUtil.readFile(configPath));
                }
                if (FileUtil.isExistFile(versionPath)) {
                    localLibrary.put("packageVersion", FileUtil.readFile(versionPath));
                }
                if (FileUtil.isExistFile(resPath)) {
                    localLibrary.put("resPath", resPath);
                }
                if (FileUtil.isExistFile(jarPath)) {
                    localLibrary.put("jarPath", jarPath);
                }
                if (FileUtil.isExistFile(dexPath)) {
                    localLibrary.put("dexPath", dexPath);
                }
                if (FileUtil.isExistFile(manifestPath)) {
                    localLibrary.put("manifestPath", manifestPath);
                }
                if (FileUtil.isExistFile(pgRulesPath)) {
                    localLibrary.put("pgRulesPath", pgRulesPath);
                }
                if (FileUtil.isExistFile(assetsPath)) {
                    localLibrary.put("assetsPath", assetsPath);
                }

                if (!enabled.isChecked()) {
                    int i = -1;
                    for (int j = 0; j < project_used_libs.size(); j++) {
                        HashMap<String, Object> nLocalLibrary = project_used_libs.get(j);
                        if (name.equals(nLocalLibrary.get("name"))) {
                            i = j;
                            break;
                        }
                    }
                    project_used_libs.remove(i);
                } else {
                    for (HashMap<String, Object> usedLibrary : project_used_libs) {
                        if (usedLibrary.get("name").toString().equals(name)) {
                            project_used_libs.remove(usedLibrary);
                            break;
                        }
                    }
                    project_used_libs.add(localLibrary);
                }
                FileUtil.writeFile(configurationFilePath, new Gson().toJson(project_used_libs));
            });
            setColorIdicator(indicator, libconfig);

            enabled.setChecked(false);
            if (!notAssociatedWithProject) {
                lookup_list = new Gson().fromJson(FileUtil.readFile(configurationFilePath), Helper.TYPE_MAP_LIST);
                for (HashMap<String, Object> localLibrary : lookup_list) {
                    if (enabled.getText().toString().equals(localLibrary.get("name").toString())) {
                        enabled.setChecked(true);
                    }
                }
            } else {
                enabled.setEnabled(false);
            }

            convertView.findViewById(R.id.img_delete).setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(ManageLocalLibraryActivity.this, v);

                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 1, Menu.NONE, "Info");
                menu.add(Menu.NONE, 2, Menu.NONE, "Rename");
                menu.add(Menu.NONE, 3, Menu.NONE, "Delete");

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getTitle().toString()) {
                        case "Info":
                            final File pkgName = new File(local_libs_path.concat(enabled.getText().toString() + "/config"));
                            final File pkgImport = new File(local_libs_path.concat(enabled.getText().toString() + "/version"));
                            aB infodialog = new aB(ManageLocalLibraryActivity.this);
                            infodialog.a(R.drawable.color_about_96);
                            infodialog.b("Info library!");
                            infodialog.a("This local library name:\n"
                                    + enabled.getText().toString() + "\nPackage Name:\n"
                                    + (pkgName.exists() && !isEmpty() ? FileUtil.readFile(pkgName.getAbsolutePath()) : "Not avaliable!")
                                    + "\nImport Package Name:\n"
                                    + (pkgImport.exists() && !isEmpty() ? FileUtil.readFile(pkgImport.getAbsolutePath()) : "Not avaliable!"));
                            infodialog.b(xB.b().a(getApplicationContext(), R.string.common_word_ok), view -> {
                                infodialog.dismiss();
                            });
                            infodialog.show();
                            break;
                        case "Rename":
                            final AlertDialog realog = new AlertDialog.Builder(ManageLocalLibraryActivity.this).create();

                            final View root = getLayoutInflater().inflate(R.layout.dialog_input_layout, null);
                            final LinearLayout title = root.findViewById(R.id.dialoginputlayoutLinearLayout1);
                            final TextInputLayout tilFilename = root.findViewById(R.id.dialoginputlayoutLinearLayout2);
                            final EditText filename = root.findViewById(R.id.edittext_change_name);

                            final View titleChildAt1 = title.getChildAt(1);
                            if (titleChildAt1 instanceof TextView) {
                                final TextView titleTextView = (TextView) titleChildAt1;
                                titleTextView.setText("Rename local library");
                            }

                            tilFilename.setHint("New local library name");
                            filename.setText(enabled.getText().toString());
                            root.findViewById(R.id.text_cancel)
                                    .setOnClickListener(Helper.getDialogDismissListener(realog));
                            root.findViewById(R.id.text_save)
                                    .setOnClickListener(view -> {
                                        enabled.setChecked(false);
                                        File input = new File(local_libs_path.concat(enabled.getText().toString()));
                                        File output = new File(local_libs_path.concat(filename.getText().toString()));
                                        if (!input.renameTo(output)) {
                                            SketchwareUtil.toastError("Failed to rename library");
                                        }
                                        SketchwareUtil.toast("NOTE: Removed library from used local libraries");
                                        realog.dismiss();
                                    });
                            realog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            filename.requestFocus();
                            realog.setView(root);
                            realog.show();
                            break;

                        case "Delete":
                            final AlertDialog deleteDialog = new AlertDialog.Builder(ManageLocalLibraryActivity.this).create();

                            final View deleteRoot = getLayoutInflater().inflate(R.layout.dialog_delete_layout, null);
                            final LinearLayout deleteTitle = deleteRoot.findViewById(R.id.dialogdeletelayoutLinearLayout1);
                            final TextInputLayout deleteFileName = deleteRoot.findViewById(R.id.dialogdeletelayoutLinearLayout2);
                            final EditText fileNameToDelete = deleteRoot.findViewById(R.id.edittext_delete_name);

                            final View deleteTitleChildAt1 = deleteTitle.getChildAt(1);
                            if (deleteTitleChildAt1 instanceof TextView) {
                                final TextView deleteTitleTextView = (TextView) deleteTitleChildAt1;
                                deleteTitleTextView.setText("Delete local library");
                            }
                            deleteFileName.setHint("That local library will be permanently removed!");
                            fileNameToDelete.setText(enabled.getText().toString());
                            fileNameToDelete.setEnabled(false);
                            deleteRoot.findViewById(R.id.text_del_cancel)
                                    .setOnClickListener(Helper.getDialogDismissListener(deleteDialog));
                            deleteRoot.findViewById(R.id.text_del_delete)
                                    .setOnClickListener(view -> {
                                        enabled.setChecked(false);
                                        final String lib = (local_libs_path + enabled.getText().toString());
                                        deleteFile(lib);
                                        loadFiles();
                                        if (FileUtil.isExistFile(lib)) {
                                            SketchwareUtil.toastError("Failed to rename library");
                                        }
                                        SketchwareUtil.toast("NOTE: Removed library from used local libraries");
                                        deleteDialog.dismiss();
                                    });
                            deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            fileNameToDelete.requestFocus();
                            deleteDialog.setView(deleteRoot);
                            deleteDialog.show();
                            break;

                        default:
                            return false;
                    }
                    return true;
                });
                popupMenu.show();
            });
            return convertView;
        }

        private void setColorIdicator(LinearLayout indicator, String configname) {
            if (FileUtil.isExistFile(configname)) {
                if (FileUtil.readFile(configname).getBytes().length > 0) {
                    indicator.setBackground(new GradientDrawable() {
                        public GradientDrawable getIns(int a, int b) {
                            this.setCornerRadius(a);
                            this.setColor(b);
                            return this;
                        }
                    }.getIns((int) 15, 0xFF00E676));
                } else {
                    indicator.setBackground(new GradientDrawable() {
                        public GradientDrawable getIns(int a, int b) {
                            this.setCornerRadius(a);
                            this.setColor(b);
                            return this;
                        }
                    }.getIns((int) 15, 0xFFD50000));
                }
            } else {
                indicator.setBackground(new GradientDrawable() {
                    public GradientDrawable getIns(int a, int b) {
                        this.setCornerRadius(a);
                        this.setColor(b);
                        return this;
                    }
                }.getIns((int) 15, 0xFF555555));
            }
        }
    }
}
