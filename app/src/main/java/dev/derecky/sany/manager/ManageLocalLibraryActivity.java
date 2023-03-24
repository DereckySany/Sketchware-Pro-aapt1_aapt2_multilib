package dev.derecky.sany.manager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TextView;

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

    private static String IN_USE_LIBRARY_FILE_PATH = "";
    private static String LOCAL_LIBRARYS_PATH = "";
    private final CharSequence originalTitle = "Manage Local Library";
    private final ArrayList<String> arrayList = new ArrayList<>();
    private LibraryAdapter adapter;
    private boolean notAssociatedWithProject = false;
    private ListView listview;
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
            IN_USE_LIBRARY_FILE_PATH = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/local_library"));
            LOCAL_LIBRARYS_PATH = FileUtil.getExternalStorageDir().concat("/.sketchware/libs/local_libs/");
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
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        showSearchOnActionBar(menuItem);
        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        setTitle(originalTitle);
        super.onOptionsMenuClosed(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            setTitle("");
        }
        if (id == R.id.action_reset) {
            showDialogResetLibrary();
        }
        if (id == R.id.action_import) {
            showDialogImportLibrary();
        }
        if (id == R.id.menu_repo_manager) {
            Intent repoManagerIntent = new Intent(getApplicationContext(), RepoManagerActivity.class);
            startActivity(repoManagerIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem resetItem = menu.findItem(R.id.action_reset);
        resetItem.setVisible(!notAssociatedWithProject);
        return true;
    }

    private void showSearchOnActionBar(MenuItem item) {
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
                return true;
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
                FileUtil.writeFile(IN_USE_LIBRARY_FILE_PATH, "[]");
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
            }
        }.execute(lib);
    }

    private void loadLocalLibraryList() {
        arrayList.clear();
        if (!notAssociatedWithProject) {
            if (!FileUtil.isExistFile(IN_USE_LIBRARY_FILE_PATH) || FileUtil.readFile(IN_USE_LIBRARY_FILE_PATH).equals("")) {
                FileUtil.writeFile(IN_USE_LIBRARY_FILE_PATH, "[]");
            } else {
                project_used_libs = new Gson().fromJson(FileUtil.readFile(IN_USE_LIBRARY_FILE_PATH), Helper.TYPE_MAP_LIST);
            }
        }

        List<String> localLibraryNames = new LinkedList<>();
        FileUtil.listDir(LOCAL_LIBRARYS_PATH, localLibraryNames);

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
        listview.setAdapter(adapter);
    }

    private void applyFilter(String query) {
        if (query.isEmpty()) {
            adapter.updateData(arrayList);
            adapter.notifyDataSetChanged();
            return;
        }

        List<String> filteredList = new ArrayList<>();
        for (String library : arrayList) {
            if (library.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(library);
            }
        }
        adapter.updateData(filteredList);
        adapter.notifyDataSetChanged();
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
                convertView = getLayoutInflater().inflate(R.layout.view_item_local_lib_new, parent, false);
            }
            final LinearLayout main_content = convertView.findViewById(R.id.main_content_use_lib);
            final TextView name_text_lib = convertView.findViewById(R.id.name_text_content_use_lib);
            final RadioButton enable_this_lib = convertView.findViewById(R.id.radiobutton_content_use_lib);

            final LinearLayout status_indicator = convertView.findViewById(R.id.linearlayout_indicator_content_use_lib);
            final TextView status_text = convertView.findViewById(R.id.status_text_content_use_lib);

            final ImageButton show_expand_bar_options = convertView.findViewById(R.id.expand_view_content_use_lib);

            final LinearLayout expand_bar_options = convertView.findViewById(R.id.expand_options_view_content_use_lib);
            final ImageButton expand_delete_option = convertView.findViewById(R.id.delete_button_content_use_lib);
            final ImageButton expand_rename_option = convertView.findViewById(R.id.rename_button_content_use_lib);
            final ImageButton expand_info_option = convertView.findViewById(R.id.info_button_content_use_lib);

            name_text_lib.setText(localLibraries.get(position));

            String libname = name_text_lib.getText().toString();
            String libconfig = LOCAL_LIBRARYS_PATH + libname;

            main_content.setOnClickListener(v -> {
                HashMap<String, Object> localLibrary = getLocalLibraryData(libname);
                if (!enable_this_lib.isChecked()) {
                    project_used_libs.remove(localLibrary);
                } else {
                    project_used_libs.remove(localLibrary);
                    project_used_libs.add(localLibrary);
                }
                FileUtil.writeFile(IN_USE_LIBRARY_FILE_PATH, new Gson().toJson(project_used_libs));
            });

            setColorIndicator(status_indicator, status_text, libconfig);

            enable_this_lib.setChecked(false);
            if (!notAssociatedWithProject) {
                ArrayList<HashMap<String, Object>> lookup_list = new Gson().fromJson(FileUtil.readFile(IN_USE_LIBRARY_FILE_PATH), Helper.TYPE_MAP_LIST);
                enable_this_lib.setChecked(lookup_list.contains(getLocalLibraryData(libname)));
            } else {
                enable_this_lib.setEnabled(false);
            }

            show_expand_bar_options.setOnClickListener(view -> {
                if (expand_bar_options.getVisibility() == View.GONE) {
                    expand_bar_options.setVisibility(View.VISIBLE);
                    expand_bar_options.animate().translationY(0).start();
                    //show_expand_bar_options.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_less_24));
                    show_expand_bar_options.animate().rotationX(180).start();
                    expand_delete_option.setOnClickListener(v -> {
                        final AlertDialog deleteDialog = new AlertDialog.Builder(ManageLocalLibraryActivity.this).create();

                        final View root = getLayoutInflater().inflate(R.layout.dialog_delete_layout, null);
                        final LinearLayout deleteTitle = root.findViewById(R.id.dialogdeletelayoutLinearLayout1);
                        final TextInputLayout deleteFileName = root.findViewById(R.id.dialogdeletelayoutLinearLayout2);
                        final EditText fileNameToDelete = root.findViewById(R.id.edittext_delete_name);

                        final View deleteTitleChildAt1 = deleteTitle.getChildAt(1);
                        if (deleteTitleChildAt1 instanceof TextView) {
                            final TextView deleteTitleTextView = (TextView) deleteTitleChildAt1;
                            deleteTitleTextView.setText("Delete local library");
                        }
                        deleteFileName.setHint("That local library will be permanently removed!");
                        fileNameToDelete.setText(name_text_lib.getText().toString());
                        fileNameToDelete.setEnabled(false);
                        root.findViewById(R.id.text_del_cancel).setOnClickListener(Helper.getDialogDismissListener(deleteDialog));
                        root.findViewById(R.id.text_del_delete).setOnClickListener(view1 -> {
                            enable_this_lib.setChecked(false);
                            final String lib = LOCAL_LIBRARYS_PATH.concat(name_text_lib.getText().toString());
                            deleteLibrary(lib);
                            deleteDialog.dismiss();
                        });
                        deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        fileNameToDelete.requestFocus();
                        deleteDialog.setView(root);
                        deleteDialog.show();
                        expand_bar_options.animate().translationX(-50).start();
                        expand_bar_options.setVisibility(View.GONE);
                        //show_expand_bar_options.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_more_24));
                        show_expand_bar_options.animate().rotationX(0).start();
                    });
                    expand_rename_option.setOnClickListener(v -> {
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
                        filename.setText(name_text_lib.getText().toString());
                        root.findViewById(R.id.text_cancel).setOnClickListener(Helper.getDialogDismissListener(realog));
                        root.findViewById(R.id.text_save).setOnClickListener(view1 -> {
                            enable_this_lib.setChecked(false);
                            File input = new File(LOCAL_LIBRARYS_PATH.concat(name_text_lib.getText().toString()));
                            File output = new File(LOCAL_LIBRARYS_PATH.concat(filename.getText().toString()));
                            if (!input.renameTo(output)) {
                                SketchwareUtil.toastError("Failed to rename library");
                            }
                            SketchwareUtil.toast("NOTE: Removed library from used local libraries");
                            loadLocalLibraryList();
                            realog.dismiss();
                        });
                        realog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        filename.requestFocus();
                        realog.setView(root);
                        realog.show();
                        expand_bar_options.animate().translationX(-50).start();
                        expand_bar_options.setVisibility(View.GONE);
                        //show_expand_bar_options.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_more_24));
                        show_expand_bar_options.animate().rotationX(0).start();
                    });
                    expand_info_option.setOnClickListener(v -> {
                        final String libraryName = name_text_lib.getText().toString();
                        final String configPath = LOCAL_LIBRARYS_PATH.concat(libraryName + "/config");
                        final String versionPath = LOCAL_LIBRARYS_PATH.concat(libraryName + "/version");
                        final String manifastPath = LOCAL_LIBRARYS_PATH.concat(libraryName + "/AndroidManifest.xml");

                        final File infoName = new File(configPath);
                        final File infoImport = new File(versionPath);
                        final File infoManifast = new File(manifastPath);

                        final AlertDialog infoDialog = new AlertDialog.Builder(ManageLocalLibraryActivity.this).create();

                        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_info_layout, null);
                        final LinearLayout titleLayout = dialogView.findViewById(R.id.dialoginfolayoutLinearLayout0);
                        final TextInputLayout tilName = dialogView.findViewById(R.id.dialoginfolayoutLinearLayout1);
                        final TextInputLayout tilImport = dialogView.findViewById(R.id.dialoginfolayoutLinearLayout2);
                        final TextInputLayout tilManifast = dialogView.findViewById(R.id.dialoginfolayoutLinearLayout3);
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
                        etName.setTextIsSelectable(true);
                        etName.setKeyListener(null);
                        etName.setText((infoName.exists() && !isEmpty() ? FileUtil.readFile(infoName.getAbsolutePath()) : "Not avaliable!"));
                        etImport.setEnabled(true);
                        etImport.setTextIsSelectable(true);
                        etImport.setKeyListener(null);
                        etImport.setText((infoImport.exists() && !isEmpty() ? FileUtil.readFile(infoImport.getAbsolutePath()) : "Not avaliable!"));
                        etManifast.setEnabled(true);
                        etManifast.setTextIsSelectable(true);
                        etManifast.setKeyListener(null);
                        etManifast.setText((infoManifast.exists() && !isEmpty() ? FileUtil.readFile(infoManifast.getAbsolutePath()) : "Not avaliable!"));
                        dialogView.findViewById(R.id.text_info_ok).setOnClickListener(Helper.getDialogDismissListener(infoDialog));
                        infoDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        infoDialog.setView(dialogView);
                        infoDialog.show();
                        expand_bar_options.animate().translationX(-50).start();
                        expand_bar_options.setVisibility(View.GONE);
                        //show_expand_bar_options.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_more_24));
                        show_expand_bar_options.animate().rotationX(0).start();
                    });
                } else {
                    expand_bar_options.animate().translationX(-50).start();
                    expand_bar_options.setVisibility(View.GONE);
                    //show_expand_bar_options.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_more_24));
                    show_expand_bar_options.animate().rotationX(0).start();
                }
            });
            return convertView;
        }

        private HashMap<String, Object> getLocalLibraryData(String libname) {
            HashMap<String, Object> localLibrary = new HashMap<>();
            localLibrary.put("name", libname);

            File configPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/config");
            if (configPathFile.exists()) {
                String packageName = null;
                try (BufferedReader reader = new BufferedReader(new FileReader(configPathFile))) {
                    packageName = reader.readLine();
                } catch (IOException e) {
                    // Handle exception
                }
                localLibrary.put("packageName", packageName);
            }

            File resPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/res");
            if (resPathFile.exists()) {
                localLibrary.put("resPath", resPathFile.getPath());
            }

            File jarPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/classes.jar");
            if (jarPathFile.exists()) {
                localLibrary.put("jarPath", jarPathFile.getPath());
            }

            File dexPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/classes.dex");
            if (dexPathFile.exists()) {
                localLibrary.put("dexPath", dexPathFile.getPath());
            }

            File manifestPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/AndroidManifest.xml");
            if (manifestPathFile.exists()) {
                localLibrary.put("manifestPath", manifestPathFile.getPath());
            }

            File pgRulesPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/proguard.txt");
            if (pgRulesPathFile.exists()) {
                localLibrary.put("pgRulesPath", pgRulesPathFile.getPath());
            }

            File assetsPathFile = new File(LOCAL_LIBRARYS_PATH, libname + "/assets");
            if (assetsPathFile.exists()) {
                localLibrary.put("assetsPath", assetsPathFile.getPath());
            }

            return localLibrary;
        }

        private void setColorIndicator(LinearLayout linearLayout_indicator, TextView indicator, String lib_name) {
            String[] files = {"classes.jar", "classes.dex", "AndroidManifest.xml", "config"};
            StringBuilder status = new StringBuilder();
            // Verifica se todos os arquivos em files existem no caminho lib_name
            List<String> missingFiles = new ArrayList<>();
            for (String file : files) {
                if (!FileUtil.isExistFile(lib_name + File.separator + file)) {
                    missingFiles.add(file);
                }
            }

            if (!missingFiles.isEmpty()) {
                // Se houver arquivos ausentes, define o status como "Error: falta $arquivo1, $arquivo2..."
                status.append("Missing: ");
                for (int i = 0; i < missingFiles.size(); i++) {
                    if (i > 0) {
                        status.append(", ");
                    }
                    status.append(missingFiles.get(i));
                }
                // Verifica se os únicos arquivos ausentes são /config e /AndroidManifest.xml
                if (missingFiles.contains("config") || missingFiles.contains("AndroidManifest.xml")) {
                    // add warning
                    setIndicatorColor(linearLayout_indicator, 0xFF555555);

                } else {
                    //add fail
                    setIndicatorColor(linearLayout_indicator, 0xFFD50000);
                }
            } else {
                // Se todos os arquivos existirem, define o status como "Done!"
                status.append("Done!");
                setIndicatorColor(linearLayout_indicator, 0xFF00E676);
            }
            // Define o texto do indicador de status
            indicator.setText(status);
        }

        private void setIndicatorColor(LinearLayout linearLayout, int color) {
            linearLayout.setBackground(new GradientDrawable() {
                public GradientDrawable getIns(int a, int b) {
                    this.setCornerRadius(a);
                    this.setColor(b);
                    return this;
                }
            }.getIns((int) 15, color));
        }
    }
}
