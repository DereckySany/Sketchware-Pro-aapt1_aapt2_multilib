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
import android.widget.SearchView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
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

public class ManageLocalLibraryActivity extends Activity
        implements View.OnClickListener, LibraryDownloader.OnCompleteListener {

    private static final String RESET_LOCAL_LIBRARIES_TAG = "reset_local_libraries";

    private LibraryAdapter adapter;
    //    private List<String> arrayList = new ArrayList<>();
    private ArrayList<String> arrayList = new ArrayList<>();
    private boolean notAssociatedWithProject = false;
    private ListView listview;
    private SearchView searchview;
    private String configurationFilePath = "";
    private String local_libs_path = "";
    private ArrayList<HashMap<String, Object>> lookup_list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> project_used_libs = new ArrayList<>();


    private void setUpSearchView() {
        searchview.setActivated(true);
        searchview.setQueryHint("Search for a library");
        searchview.onActionViewExpanded();
        searchview.setIconifiedByDefault(false);
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
          }
        );
    }

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
                (int) getDip(2));
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
                    .setTitle("Escolha o compilador")
                    .setMessage("Você gostaria de usar DX, D8 ou R8 para compilar a biblioteca?\n" +
                            "D8 suporta Java 8, enquanto que o DX não suporta. Limitação: o D8 só funciona no Android 8 e acima.\n" +
                            "O R8 é o novo compilador oficial do Android Studio.")
                    .setPositiveButton("D8", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, true,
                            "D8").showDialog(ManageLocalLibraryActivity.this))
                    .setNegativeButton("DX", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, false,
                            "Dx").showDialog(ManageLocalLibraryActivity.this))
                    .setNeutralButton("R8", (dialog, which) -> new LibraryDownloader(ManageLocalLibraryActivity.this, true,
                            "R8").showDialog(ManageLocalLibraryActivity.this))
                    .setCancelable(true)
                    .show();

            /*
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
            */
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
        searchViewContainer.setVisibility(View.VISIBLE);
        searchViewContainer.setBackground(getDrawable(R.drawable.bg_rectangle_white));
        searchview = findViewById(R.id.search_perm);
        listview = findViewById(R.id.main_content);
        ViewGroup mainContent = (ViewGroup) searchViewContainer.getParent();
        ViewGroup root = (ViewGroup) mainContent.getParent();
        root.removeView(mainContent);
        root.addView(mainContent);

        if (getIntent().hasExtra("sc_id")) {
            String sc_id = getIntent().getStringExtra("sc_id");
            notAssociatedWithProject = sc_id.equals("system");
            configurationFilePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/")
                    .concat(sc_id.concat("/local_library"));
            local_libs_path = FileUtil.getExternalStorageDir().concat("/.sketchware/libs/local_libs/");
            // Carregar arquivos
            loadFiles();
            // Inicializar o SearchView
            setUpSearchView();
            initToolbar();
        } else {
            finish();
        }
    }

    private void loadFiles() {
        arrayList.clear();
        if (!notAssociatedWithProject) {
            String fileContent;
            if (!FileUtil.isExistFile(configurationFilePath)
                    || (fileContent = FileUtil.readFile(configurationFilePath)).equals("")) {
                FileUtil.writeFile(configurationFilePath, "[]");
            } else {
                project_used_libs = new Gson().fromJson(fileContent, Helper.TYPE_MAP_LIST);
            }
        }
        ArrayList<String> arrayList = new ArrayList<>();
        FileUtil.listDir(local_libs_path, arrayList);
        Collections.sort(arrayList, String.CASE_INSENSITIVE_ORDER);

        List<String> localLibraryNames = new LinkedList<>();
        for (String filename : arrayList) {
            if (FileUtil.isDirectory(filename)) {
                localLibraryNames.add(Uri.parse(filename).getLastPathSegment());
            }
        }
        arrayList.addAll(localLibraryNames);
        adapter = new LibraryAdapter(localLibraryNames);
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
                    int i = project_used_libs.indexOf(localLibrary);
                    project_used_libs.remove(i);
                } else {
                    project_used_libs.remove(localLibrary);
                    project_used_libs.add(localLibrary);
                }
                FileUtil.writeFile(configurationFilePath, new Gson().toJson(project_used_libs));
            });

            setColorIdicator(indicator, libconfig);

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
            localLibrary.put("config", local_libs_path + libname + "/config");
            return localLibrary;
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