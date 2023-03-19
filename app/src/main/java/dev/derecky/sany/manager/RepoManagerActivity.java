package dev.derecky.sany.manager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sketchware.remod.R;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RepoManagerActivity extends AppCompatActivity {
    public static final File CONFIGURED_REPOSITORIES_FILE = new File(Environment.getExternalStorageDirectory(),
            ".sketchware" + File.separator + "libs" + File.separator + "repositories.json");
    private ArrayList<HashMap<String, Object>> repositoryList = new ArrayList<>();
    private RepositoryListAdapter adapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        loadRepositories();
        showRepositoryListDialog(getApplicationContext());
    }

    private void showRepositoryListDialog(Context context) {
        Dialog repositoryListDialog = new Dialog(context);
        repositoryListDialog.setContentView(R.layout.repository_list_dialog);

        ListView listview = repositoryListDialog.findViewById(R.id.list_view);
        EditText searchEditText = repositoryListDialog.findViewById(R.id.search_edit_text);
        FloatingActionButton addFab = repositoryListDialog.findViewById(R.id.add_fab);
        adapter = new RepositoryListAdapter(context, repositoryList);
        listview.setAdapter(adapter);
        addFab.setOnClickListener(v -> {
            showAddRepositoryDialog();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        repositoryListDialog.show();
    }

    private void showAddRepositoryDialog() {
        Dialog addRepositoryDialog = new Dialog(getApplicationContext());
        addRepositoryDialog.setContentView(R.layout.add_repository_dialog);

        EditText nameEditText = addRepositoryDialog.findViewById(R.id.name_edit_text);
        EditText urlEditText = addRepositoryDialog.findViewById(R.id.url_edit_text);
        Button addButton = addRepositoryDialog.findViewById(R.id.add_button);
        Button cancelButton = addRepositoryDialog.findViewById(R.id.cancel_button);

        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String url = urlEditText.getText().toString().trim();

            if (!name.isEmpty() && !url.isEmpty()) {
                HashMap<String, Object> repositoryAdd = new HashMap<>();
                repositoryAdd.put("name", name);
                repositoryAdd.put("url", url);
                repositoryList.add(repositoryAdd);
                saveRepositories();
                adapter.notifyDataSetChanged();
                addRepositoryDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "Name and URL cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            addRepositoryDialog.dismiss();
        });

        addRepositoryDialog.show();
    }

    private void saveRepositories() {
        try {
            File file = new File(getApplicationContext().getExternalFilesDir(null), "repositories.json");
            FileWriter fileWriter = new FileWriter(file);
            Gson gson = new Gson();
            gson.toJson(repositoryList, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRepositories() {
        try {
            FileReader repositories = new FileReader(CONFIGURED_REPOSITORIES_FILE);
            repositoryList = new Gson().fromJson(repositories, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            repositories.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (repositoryList == null) {
            repositoryList = new ArrayList<>();
        }
    }
    public class RepositoryListAdapter extends BaseAdapter {

        private ArrayList<HashMap<String, Object>> repositoryList;
        private LayoutInflater inflater;

        public RepositoryListAdapter(Context context, ArrayList<HashMap<String, Object>> repositoryList) {
            this.repositoryList = repositoryList;
            this.inflater = LayoutInflater.from(RepoManagerActivity.this.getApplicationContext());
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

            HashMap<String, Object> repository = repositoryList.get(position);
            String name = (String) repository.get("name");
            String url = (String) repository.get("url");

            nameTextView.setText(name);
            urlTextView.setText(url);

            return convertView;
        }
    }
}
