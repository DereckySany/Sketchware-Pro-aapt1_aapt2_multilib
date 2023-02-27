package dev.aldi.sayuti.editor.manage;

//import android.annotation.SuppressLint;
//import android.app.Activity;
import android.app.AlertDialog;
//import android.graphics.Color;
import android.app.SearchManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.appcompat.widget.AlertDialogLayout;
//import androidx.appcompat.widget.SearchView;
//import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
//import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
//import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toolbar;

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

public class ManageLocalLibraryActivity extends Activity
        implements LibraryDownloader.OnCompleteListener {

    private static final String RESET_LOCAL_LIBRARIES_TAG = "reset_local_libraries";

    private LibraryAdapter adapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private boolean notAssociatedWithProject = false;
    private ListView listview;
    //    private SearchView searchview;
    private String configurationFilePath = "";
    private String local_libs_path = "";
    private ArrayList<HashMap<String, Object>> lookup_list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> project_used_libs = new ArrayList<>();

    /*
    private void setUpSearchView() {
//        searchview.setActivated(true);
        searchview.setQueryHint("Search for a library");
        searchview.onActionViewExpanded();
        searchview.setIconifiedByDefault(true);
        searchview.clearFocus();
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter(newText);
                return true;
            }
        });
    }
    */

    /*
    private void initToolbar() {
        ImageView back_icon = findViewById(R.id.ig_toolbar_back);
        TextView title = findViewById(R.id.tx_toolbar_title);
        LinearLayout toolbar = (LinearLayout) back_icon.getParent();

        // Adiciona o ícone de busca
        SearchView searchView = new SearchView(this);
        searchView.setQueryHint("Search for a library");
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter(newText);
                return true;
            }
        });
        searchView.setIconified(false);
        searchView.clearFocus();
        toolbar.addView(searchView, 0);
        if (!notAssociatedWithProject) {
            // Adiciona o ícone de reset
            ImageView reset = new ImageView(this);
            reset.setImageResource(R.drawable.ic_restore_white_24dp);
            reset.setTag(RESET_LOCAL_LIBRARIES_TAG);
            reset.setOnClickListener(this);
            reset.setVisibility(View.GONE); // Torna o ícone invisível inicialmente
            toolbar.addView(reset);
        }
        // Adiciona o ícone de importação
        ImageView importIcon = new ImageView(this);
        importIcon.setImageResource(R.drawable.download_80px);
        importIcon.setOnClickListener(this);
        toolbar.addView(importIcon);

        Helper.applyRippleToToolbarView(back_icon);
        back_icon.setOnClickListener(Helper.getBackPressedClickListener(this));

        title.setText("Local library Manager");
    }
    */
    /*
    public void onClick(View v) {
        if (v.getId() == R.id.ig_toolbar_load_file) {
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
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.manage_permission);
        setContentView(R.layout.manage_local_library);
        //Toolbar toolbar = findViewById(R.id._toolbar);
        //setActionBar(toolbar);
        //toolbar.setSubtitle("Local library Manager");
        //findViewById(R.id._app_bar).setVisibility(View.GONE);
        //toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        //toolbar.setPopupTheme(R.style.ThemeOverlay_ToolbarMenu);
        //LinearLayout searchViewContainer = findViewById(R.id.managepermissionLinearLayout1);
        //searchViewContainer.setVisibility(View.GONE);
        //searchViewContainer.setBackground(getDrawable(R.drawable.bg_rectangle_white));
        //searchview = findViewById(R.id.search_perm);
        //listview = findViewById(R.id.main_content);
        //CoordinatorLayout coordinatorLayout = findViewById(R.id._coordinator);
        listview = findViewById(R.id.list_local_librarys);
        //ViewGroup mainContent = (ViewGroup) searchViewContainer.getParent();
        //ViewGroup root = (ViewGroup) mainContent.getParent();
        //root.removeView(mainContent);
        //root.addView(mainContent);
        getSupportActionBar();

        if (getIntent().hasExtra("sc_id")) {
            String sc_id = getIntent().getStringExtra("sc_id");
            notAssociatedWithProject = sc_id.equals("system");
            configurationFilePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/")
                    .concat(sc_id.concat("/local_library"));
            local_libs_path = FileUtil.getExternalStorageDir().concat("/.sketchware/libs/local_libs/");
            // Inicializar o SearchView
            //initToolbar();
            // Carregar arquivos
            loadFiles();
            // setUpSearchView();
        } else {
            finishAfterTransition();
        }
    }

    @Override
    public void onComplete() {
        loadFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            showDialogResetLibrary();
        }
        if (id == R.id.action_import) {
            showDialogImportLibrary();
        }
        if (id == R.id.action_search) {
            showSearchOnActionBar(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem resetItem = menu.findItem(R.id.action_reset);
        resetItem.setVisible(!notAssociatedWithProject);
        return true;
    }

    private void showSearchOnActionBar(MenuItem item) {
//        MenuItem menuIMenu1 = menu.findItem(R.id.search_menu_item);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
                loadFiles();
                dialog.dismiss();
            });
            dialog.show();
        }
    }
    private void loadFiles() {
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
                    //int i = project_used_libs.indexOf(localLibrary);
                    //project_used_libs.remove(i);
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
                                        loadFiles();
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
                                        FileUtil.deleteFile(lib);
                                        if (FileUtil.isExistFile(lib)) {
                                            SketchwareUtil.toastError("Failed to remove library");
                                        }
                                        SketchwareUtil.toast("NOTE: Removed library from local libraries");
                                        loadFiles();
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
