package dev.aldi.sayuti.editor.manage;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;

import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.sketchware.remod.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import a.a.a.aB;
import a.a.a.xB;
import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FileUtil;
import mod.hey.studios.project.library.LibraryDownloader;
import mod.hey.studios.util.Helper;

public class ManageLocalLibraryActivity extends AppCompatActivity implements LibraryDownloader.OnCompleteListener {

    private static final String RESET_LOCAL_LIBRARIES_TAG = "reset_local_libraries";
    private CharSequence originalTitle = "Manage Local Library";
    private ImageView iconSearch;
    private LibraryAdapter adapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private boolean notAssociatedWithProject = false;
    private ListView listview;
    private String configurationFilePath = "";
    private String local_libs_path = "";
    private ArrayList<HashMap<String, Object>> lookup_list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> project_used_libs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_local_library);
        setTitle(originalTitle);
        setTitleColor(R.color.white);
        listview = findViewById(R.id.list_local_librarys);

        if (getIntent().hasExtra("sc_id")) {
            String sc_id = getIntent().getStringExtra("sc_id");
            notAssociatedWithProject = sc_id.equals("system");
            configurationFilePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/")
                    .concat(sc_id.concat("/local_library"));
            local_libs_path = FileUtil.getExternalStorageDir().concat("/.sketchware/libs/local_libs/");
            // Carregar lista de arquivos
            loadLocalLibraryList();

        } else {
            finishAfterTransition();

        }

    }

    @Override
    public void onComplete() {
        loadLocalLibraryList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        showSearchOnActionBar(menuItem);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
             if (item.expandActionView()){
                 setTitle("");
             } else {
                 setTitle(originalTitle);
             }
        }
        if (id == R.id.action_reset) {
            showDialogResetLibrary();
        }
        if (id == R.id.action_import) {
            showDialogImportLibrary();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem resetItem = menu.findItem(R.id.action_reset);
        resetItem.setVisible(!notAssociatedWithProject);
        return true;
    }

    private void showSearchOnActionBar(MenuItem item) {
        // iconSearch = findViewById(R.id.search_button);
        // iconSearch.setImageResource(R.drawable.search_icon_white);
        // SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) item.getActionView();
        // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for a library");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter(newText);
                return false;
            }
        });
    }

    private void showDialogImportLibrary() {
        if (Build.VERSION.SDK_INT > 26) {
            new AlertDialog.Builder(this)
                    .setTitle("Choose compiler")
                    .setMessage("Would you like to use DX, D8 or R8 to compile the library?\n" +
                            "D8 supports Java 8, while DX does not. Limitation: D8 only works on Android 8 and above.\n" +
                            "R8 is the new official Android Studio compiler.(but in alpha here!)")
                    .setPositiveButton("D8", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, true,
                            "D8").showDialog(ManageLocalLibraryActivity.this))
                    .setNegativeButton("DX", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, false,
                            "Dx").showDialog(ManageLocalLibraryActivity.this))
                    .setNeutralButton("R8", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, true,
                            "R8").showDialog(ManageLocalLibraryActivity.this))
                    .setCancelable(true)
                    .show();

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Choose compiler")
                    .setMessage("Would you like to use Dx or D8 to dex the library?\n" +
                            "D8 supports Java 8, whereas Dx does not. Limitation: D8 only works on Android 8 and above.")
                    .setPositiveButton("D8", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, true,
                            "D8").showDialog(ManageLocalLibraryActivity.this))
                    .setNegativeButton("DX", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, false,
                            "Dx").showDialog(ManageLocalLibraryActivity.this))
                    .setNeutralButton("Cancel", null)
                    .show();
        }
    }

    private void showDialogResetLibrary() {
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
                loadLocalLibraryList();
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteLibrary(String lib) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Removing library...");
        dialog.setCancelable(false);
        dialog.show();

        new FileUtil.DeleteFileTask() {
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                dialog.dismiss();
                SketchwareUtil.toast("Library removed successfully");
                loadLocalLibraryList();
                // loadFiles();
            }
        }.execute(lib);
    }

    private void loadLocalLibraryList() {
        arrayList.clear();
        if (!notAssociatedWithProject) {
            if (!FileUtil.isExistFile(configurationFilePath) || FileUtil.readFile(configurationFilePath).equals("")) {
                FileUtil.writeFile(configurationFilePath, "[]");
            } else {
                project_used_libs = new Gson().fromJson(FileUtil.readFile(configurationFilePath), Helper.TYPE_MAP_LIST);
            }
        }

        List<String> localLibraryNames = new LinkedList<>();
        FileUtil.listDir(local_libs_path, localLibraryNames);

        Set<String> uniqueDirectories = new HashSet<>();
        for (String filename : localLibraryNames) {
            if (FileUtil.isDirectory(filename)) {
                String directoryName = Uri.parse(filename).getLastPathSegment();
                uniqueDirectories.add(directoryName);
            }
        }

        List<String> directories = new LinkedList<>(uniqueDirectories);
        Collections.sort(directories, String.CASE_INSENSITIVE_ORDER);

        adapter = new LibraryAdapter(directories);
        arrayList.addAll(directories);
        adapter.updateData(arrayList);
        listview.setAdapter(adapter);
    }

    private void applyFilter(String query) {
        if (query.isEmpty()) {
            adapter.updateData(arrayList);
            listview.setAdapter(adapter);
            return;
        }

        List<String> filteredList = new ArrayList<>();
        for (String library : arrayList) {
            if (library.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(library);
            }
        }
        adapter.updateData(filteredList);
    }


    public class LibraryAdapter extends BaseAdapter {

        private List<String> localLibraries;

        public LibraryAdapter(List<String> localLibraries) {
            this.localLibraries = localLibraries;
        }

        public void updateData(List<String> localLibraries) {
            this.localLibraries = localLibraries;
            notifyDataSetChanged();
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
                HashMap<String, Object> localLibrary = getLocalLibraryData(libname);
                if (!enabled.isChecked()) {
                    project_used_libs.remove(localLibrary);
                } else {
                    project_used_libs.remove(localLibrary);
                    project_used_libs.add(localLibrary);
                }
                FileUtil.writeFile(configurationFilePath, new Gson().toJson(project_used_libs));
            });

            setColorIndicator(indicator, libconfig);

            enabled.setChecked(false);
            if (!notAssociatedWithProject) {
                lookup_list = new Gson().fromJson(FileUtil.readFile(configurationFilePath), Helper.TYPE_MAP_LIST);
                enabled.setChecked(lookup_list.contains(getLocalLibraryData(libname)));
            } else {
                enabled.setEnabled(false);
            }

            convertView.findViewById(R.id.img_delete).setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(ManageLocalLibraryActivity.this, v);
                popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Info");
                popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Rename");
                popupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Delete");

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getTitle().toString()) {
                        case "Info":
                            final String libraryName = enabled.getText().toString();
                            final String configPath = local_libs_path.concat(libraryName + "/config");
                            final String versionPath = local_libs_path.concat(libraryName + "/version");
                            final String manifastPath = local_libs_path.concat(libraryName + "/AndroidManifest.xml");

                            final File infoName = new File(configPath);
                            final File infoImport = new File(versionPath);
                            final File infoManifast = new File(manifastPath);

                            final AlertDialog infoDialog = new AlertDialog.Builder(ManageLocalLibraryActivity.this)
                                    .create();

                            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_info_layout, null);
                            final LinearLayout titleLayout = dialogView
                                    .findViewById(R.id.dialoginfolayoutLinearLayout0);
                            final TextInputLayout tilName = dialogView.findViewById(R.id.dialoginfolayoutLinearLayout1);
                            final TextInputLayout tilImport = dialogView
                                    .findViewById(R.id.dialoginfolayoutLinearLayout2);
                            final TextInputLayout tilManifast = dialogView
                                    .findViewById(R.id.dialoginfolayoutLinearLayout3);
                            final EditText etName = dialogView.findViewById(R.id.edittext_info_name);
                            final EditText etImport = dialogView.findViewById(R.id.edittext_info_import);
                            final EditText etManifast = dialogView.findViewById(R.id.edittext_info_manifast);

                            final View titleChild = titleLayout.getChildAt(1);
                            if (titleChild instanceof TextView) {
                                final TextView titleTextView = (TextView) titleChild;
                                titleTextView.setText("Information Library");
                            }

                            tilName.setHint("Name Library");
                            tilImport.setHint("Import library name");
                            tilManifast.setHint("Manifast");
                            etName.setEnabled(true);
                            etImport.setTextIsSelectable(true);
                            etName.setText(
                                    (infoName.exists() && !isEmpty() ? FileUtil.readFile(infoName.getAbsolutePath())
                                            : "Not avaliable!"));
                            etImport.setEnabled(true);
                            etImport.setTextIsSelectable(true);
                            etImport.setText(
                                    (infoImport.exists() && !isEmpty() ? FileUtil.readFile(infoImport.getAbsolutePath())
                                            : "Not avaliable!"));
                            etManifast.setEnabled(true);
                            etImport.setTextIsSelectable(true);
                            etManifast.setText((infoManifast.exists() && !isEmpty()
                                    ? FileUtil.readFile(infoManifast.getAbsolutePath())
                                    : "Not avaliable!"));

                            dialogView.findViewById(R.id.text_info_ok)
                                    .setOnClickListener(Helper.getDialogDismissListener(infoDialog));
                            infoDialog.getWindow()
                                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            infoDialog.setView(dialogView);
                            infoDialog.show();
                            break;
                        case "Rename":
                            final AlertDialog realog = new AlertDialog.Builder(ManageLocalLibraryActivity.this)
                                    .create();

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
                                        loadLocalLibraryList();
                                        realog.dismiss();
                                    });
                            realog.getWindow()
                                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            filename.requestFocus();
                            realog.setView(root);
                            realog.show();
                            break;

                        case "Delete":
                            final AlertDialog deleteDialog = new AlertDialog.Builder(ManageLocalLibraryActivity.this)
                                    .create();

                            final View deleteRoot = getLayoutInflater().inflate(R.layout.dialog_delete_layout, null);
                            final LinearLayout deleteTitle = deleteRoot
                                    .findViewById(R.id.dialogdeletelayoutLinearLayout1);
                            final TextInputLayout deleteFileName = deleteRoot
                                    .findViewById(R.id.dialogdeletelayoutLinearLayout2);
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
                                        final String lib = local_libs_path.concat(enabled.getText().toString());
                                        deleteLibrary(lib);
                                        deleteDialog.dismiss();
                                    });
                            deleteDialog.getWindow()
                                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

        private HashMap<String, Object> getLocalLibraryData(String libname) {
            HashMap<String, Object> localLibrary = new HashMap<>();
            localLibrary.put("name", libname);

            File configPathFile = new File(local_libs_path, libname + "/config");
            if (configPathFile.exists()) {
                String packageName = null;
                try (BufferedReader reader = new BufferedReader(new FileReader(configPathFile))) {
                    packageName = reader.readLine();
                } catch (IOException e) {
                    // Handle exception
                }
                localLibrary.put("packageName", packageName);
            }

            File resPathFile = new File(local_libs_path, libname + "/res");
            if (resPathFile.exists()) {
                localLibrary.put("resPath", resPathFile.getPath());
            }

            File jarPathFile = new File(local_libs_path, libname + "/classes.jar");
            if (jarPathFile.exists()) {
                localLibrary.put("jarPath", jarPathFile.getPath());
            }

            File dexPathFile = new File(local_libs_path, libname + "/classes.dex");
            if (dexPathFile.exists()) {
                localLibrary.put("dexPath", dexPathFile.getPath());
            }

            File manifestPathFile = new File(local_libs_path, libname + "/AndroidManifest.xml");
            if (manifestPathFile.exists()) {
                localLibrary.put("manifestPath", manifestPathFile.getPath());
            }

            File pgRulesPathFile = new File(local_libs_path, libname + "/proguard.txt");
            if (pgRulesPathFile.exists()) {
                localLibrary.put("pgRulesPath", pgRulesPathFile.getPath());
            }

            File assetsPathFile = new File(local_libs_path, libname + "/assets");
            if (assetsPathFile.exists()) {
                localLibrary.put("assetsPath", assetsPathFile.getPath());
            }

            return localLibrary;
        }

        private void setColorIndicator(LinearLayout indicator, String configname) {
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
