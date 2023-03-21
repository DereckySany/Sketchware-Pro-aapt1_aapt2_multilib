package dev.derecky.sany.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sketchware.remod.R;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import mod.SketchwareUtil;
import mod.hey.studios.util.Helper;

public class RepoManagerActivity extends AppCompatActivity {
    public static final File CONFIGURED_REPOSITORIES_FILE = new File(Environment.getExternalStorageDirectory(),
            ".sketchware" + File.separator + "libs" + File.separator + "repositories.json");
    private ArrayList<HashMap<String, Object>> REPOSITORY_LIST = new ArrayList<>();
    private RepositoryListAdapter adapter;
    private ListView listview;
    private EditText searchEditText;
    private TextView index_size;
    private FloatingActionButton addFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repository_list_dialog);
        index_size = findViewById(R.id.repo_index);
        listview = findViewById(R.id.list_view);
        searchEditText = findViewById(R.id.search_edit_text);
        addFab = findViewById(R.id.add_fab);

        loadRepositories();
        setUpSearchView();
        adapter = new RepositoryListAdapter(this, REPOSITORY_LIST);
        listview.setAdapter(adapter);

        addFab.setOnClickListener(v -> showAddRepositoryDialog());

    }

    private void setUpSearchView() {
        searchEditText.setActivated(true);
        searchEditText.setHint("Search for repository name");
        searchEditText.clearFocus();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                applyFilter(s.toString());
            }
        });
    }

    private void applyFilter(String query) {
        if (query.isEmpty()) {
            adapter.updateData(REPOSITORY_LIST);
            adapter.notifyDataSetChanged();
//            listview.setAdapter(adapter);
            return;
        }
        final ArrayList<HashMap<String, Object>> repositoryFilter = new ArrayList<>();
        for (HashMap<String, Object> search : REPOSITORY_LIST) {
            if (search.values().toString().toLowerCase().contains(query.toLowerCase())) {
                repositoryFilter.add(search);
            } else if (search.toString().toLowerCase().contains(query.toLowerCase())) {
                repositoryFilter.add(search);
            }
        }
        adapter.updateData(repositoryFilter);
        adapter.notifyDataSetChanged();
    }

    public void showAddRepositoryDialog() {
        final AlertDialog addRepositoryDialog = new AlertDialog.Builder(RepoManagerActivity.this).create();
        final View addRoot = getLayoutInflater().inflate(R.layout.add_repository_dialog, null);

        final EditText nameEditText = addRoot.findViewById(R.id.repo_name_edit_text);
        final EditText urlEditText = addRoot.findViewById(R.id.repo_url_edit_text);

        addRoot.findViewById(R.id.add_repo_button).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String url = urlEditText.getText().toString().trim();

            if (name.isEmpty()) {
                nameEditText.setError("Name cannot be empty");
                return;
            }

            if (url.isEmpty()) {
                urlEditText.setError("URL cannot be empty");
                return;
            }

            if (!Patterns.WEB_URL.matcher(url).matches()) {
                urlEditText.setError("Invalid URL format");
                return;
            }

            HashMap<String, Object> repositoryAdd = new HashMap<>();
            repositoryAdd.put("name", name);
            repositoryAdd.put("url", url);
            REPOSITORY_LIST.add(repositoryAdd);
            saveRepositories();
            adapter.notifyDataSetChanged();
            addRepositoryDialog.dismiss();
            SketchwareUtil.showMessage(getApplicationContext(),"Added if Sucessful!");
        });
        addRoot.findViewById(R.id.cancel_repo_button).setOnClickListener(Helper.getDialogDismissListener(addRepositoryDialog));
        addRepositoryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addRepositoryDialog.setView(addRoot);
        addRepositoryDialog.show();
    }

    private void getRepositoriesIndex() {
        index_size.setText("index: " + REPOSITORY_LIST.size());
    }

    private void saveRepositories() {
        try {
            FileWriter fileWriter = new FileWriter(CONFIGURED_REPOSITORIES_FILE);
            Gson gson = new Gson();
            gson.toJson(REPOSITORY_LIST, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRepositoriesIndex();
    }

    private void loadRepositories() {
        try {
            FileReader repositories = new FileReader(CONFIGURED_REPOSITORIES_FILE);
            REPOSITORY_LIST = new Gson().fromJson(repositories, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            repositories.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (REPOSITORY_LIST == null) {
            REPOSITORY_LIST = new ArrayList<>();
        }
        getRepositoriesIndex();
    }

    public class RepositoryListAdapter extends BaseAdapter {

        private ArrayList<HashMap<String, Object>> repositoryList;
        private final LayoutInflater inflater;

        public RepositoryListAdapter(Context context, ArrayList<HashMap<String, Object>> repositoryList) {
            this.repositoryList = repositoryList;
            this.inflater = LayoutInflater.from(context.getApplicationContext());
        }

        public void updateData(ArrayList<HashMap<String, Object>> repositoryList) {
            this.repositoryList = repositoryList;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return repositoryList.get(position);
        }

        @Override
        public int getCount() {
            return repositoryList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.repository_item, parent, false);
            }

            TextView nameTextView = convertView.findViewById(R.id.name_text_view);
            TextView urlTextView = convertView.findViewById(R.id.url_text_view);
            ImageButton expand_button = convertView.findViewById(R.id.expand_button_view);
            LinearLayout expand_options = convertView.findViewById(R.id.expand_options_view);

            ImageButton expand_options_delete = convertView.findViewById(R.id.expand_options_view_delete);
            ImageButton expand_options_edit = convertView.findViewById(R.id.expand_options_view_edit);

            HashMap<String, Object> repository = repositoryList.get(position);
            String name = (String) repository.get("name");
            String url = (String) repository.get("url");

            nameTextView.setText(name);
            urlTextView.setText(url);
            expand_button.setOnClickListener(v -> {
                if (expand_options.getVisibility() == View.GONE) {
                    expand_button.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_less_24));
                    expand_options.setVisibility(View.VISIBLE);
                    expand_options_delete.setOnClickListener(view -> {
                        final AlertDialog deleteDialog = new AlertDialog.Builder(RepoManagerActivity.this).create();
                        final View deleteRoot = getLayoutInflater().inflate(R.layout.dialog_delete_layout, null);

                        final LinearLayout deleteTitle = deleteRoot.findViewById(R.id.dialogdeletelayoutLinearLayout1);
                        final TextInputLayout deleteFileName = deleteRoot.findViewById(R.id.dialogdeletelayoutLinearLayout2);
                        final EditText fileNameToDelete = deleteRoot.findViewById(R.id.edittext_delete_name);

                        final View deleteTitleChildAt1 = deleteTitle.getChildAt(1);
                        if (deleteTitleChildAt1 instanceof TextView) {
                            final TextView deleteTitleTextView = (TextView) deleteTitleChildAt1;
                            deleteTitleTextView.setText("Delete Repository");
                        }
                        deleteFileName.setHint("That local Repository will be permanently removed!");
                        fileNameToDelete.setText(nameTextView.getText().toString());
                        fileNameToDelete.setEnabled(false);
                        deleteRoot.findViewById(R.id.text_del_delete)
                                .setOnClickListener(view1 -> {
                                    repository.clear();
                                    saveRepositories();
                                    adapter.notifyDataSetChanged();
                                    deleteDialog.dismiss();
                                    SketchwareUtil.showMessage(getApplicationContext(),"Removed if Sucessful!");
                                });
                        deleteRoot.findViewById(R.id.text_del_cancel).setOnClickListener(Helper.getDialogDismissListener(deleteDialog));
                        deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        deleteDialog.setView(deleteRoot);
                        deleteDialog.show();

                    });
                    expand_options_edit.setOnClickListener(view -> {
                        final AlertDialog repositoryDialogEdit = new AlertDialog.Builder(RepoManagerActivity.this).create();
                        final View editRoot = getLayoutInflater().inflate(R.layout.add_repository_dialog, null);

                        final TextView nameTitle = editRoot.findViewById(R.id.dialog_repo_title);
                        final EditText nameEditText = editRoot.findViewById(R.id.repo_name_edit_text);
                        final EditText urlEditText = editRoot.findViewById(R.id.repo_url_edit_text);

                        // Get the repository data at the specified position
                        HashMap<String, Object> repositoryData = repositoryList.get(position);
                        String currentName = (String) repositoryData.get("name");
                        String currentUrl = (String) repositoryData.get("url");

                        // Set the current name and URL values in the EditText fields
                        nameTitle.setText("Rename Repository");
                        nameEditText.setText(currentName);
                        urlEditText.setText(currentUrl);

                        editRoot.findViewById(R.id.add_repo_button).setOnClickListener(view1 -> {
                            String editName = nameEditText.getText().toString().trim();
                            String editUrl = urlEditText.getText().toString().trim();

                            if (editName.isEmpty()) {
                                nameEditText.setError("Name cannot be empty");
                                return;
                            }

                            if (editUrl.isEmpty()) {
                                urlEditText.setError("URL cannot be empty");
                                return;
                            }

                            if (!Patterns.WEB_URL.matcher(editUrl).matches()) {
                                urlEditText.setError("Invalid URL format");
                                return;
                            }

                            // Update the name and URL values in the repository data
                            repositoryData.put("name", editName);
                            repositoryData.put("url", editUrl);
                            saveRepositories();
                            adapter.notifyDataSetChanged();
                            repositoryDialogEdit.dismiss();
                            SketchwareUtil.showMessage(getApplicationContext(),"Rename if Sucessful!");
                        });
                        editRoot.findViewById(R.id.cancel_repo_button).setOnClickListener(Helper.getDialogDismissListener(repositoryDialogEdit));
                        repositoryDialogEdit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        repositoryDialogEdit.setView(editRoot);
                        repositoryDialogEdit.show();
                    });
                } else {
                    expand_button.setImageDrawable(getDrawable(R.drawable.selector_ic_expand_more_24));
                    expand_options.setVisibility(View.GONE);
                }
            });

            return convertView;
        }
    }
}
