package mod.hey.studios.project.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tools.r8.D8;
import com.android.tools.r8.R8;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sketchware.remod.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import a.a.a.bB;
import mod.SketchwareUtil;
import mod.agus.jcoderz.dx.command.dexer.Main;
import mod.agus.jcoderz.lib.FileUtil;
import mod.hey.studios.lib.JarCheck;
import mod.hey.studios.lib.prdownloader.PRDownloader;
import mod.hey.studios.lib.prdownloader.PRDownloader.OnDownloadListener;
import mod.hey.studios.lib.prdownloader.PRDownloader.Status;
import mod.hey.studios.util.Helper;
import mod.jbk.build.BuiltInLibraries;

import mod.hey.studios.project.ProjectSettings;

//changed in 6.3.0

public class LibraryDownloader {

    private static final String SEARCHING_MESSAGE = "Searching...";
    
    public static final File CONFIGURED_REPOSITORIES_FILE = new File(Environment.getExternalStorageDirectory(),
            ".sketchware" + File.separator + "libs" + File.separator + "repositories.json");
    private static final String DEFAULT_REPOSITORIES_FILE_CONTENT = "[{\"url\":\"https://repo.hortonworks.com/content/repositories/releases\",\"name\":\"HortanWorks\"},{\"url\":\"https://maven.atlassian.com/content/repositories/atlassian-public\",\"name\":\"Atlassian\"},{\"url\":\"https://jitpack.io\",\"name\":\"JitPack\"},{\"url\":\"https://jcenter.bintray.com\",\"name\":\"JCenter\"},{\"url\":\"https://oss.sonatype.org/content/repositories/releases\",\"name\":\"Sonatype\"},{\"url\":\"https://repo.spring.io/plugins-release\",\"name\":\"Spring Plugins\"},{\"url\":\"https://repo.spring.io/libs-milestone\",\"name\":\"Spring Milestone\"},{\"url\":\"https://repo.maven.apache.org/maven2\",\"name\":\"Apache Maven\"},{\"url\":\"https://dl.google.com/dl/android/maven2\",\"name\":\"Google Maven\"},{\"url\":\"https://repo1.maven.org/maven2\",\"name\":\"Maven Central\"}]";
    private final String downloadPath;
    private final ArrayList<String> repoUrls = new ArrayList<>();
    private final ArrayList<String> repoNames = new ArrayList<>();
    Activity context;
    boolean use_d8;
    String tool;
    private OnCompleteListener listener;
    private AlertDialog dialog;
    private boolean isAarAvailable = false, isAarDownloaded = false;
    private boolean isJarAvailable = false, isJarDownloaded = false;
    private boolean Use_Aar = true;
    private int downloadId;
    private String libName = "";
    private String currentRepo = "";
    private int counter = 0;
    private ArrayList<HashMap<String, Object>> repoMap = new ArrayList<>();
    private ProgressDialog progressDialog;

    public ProjectSettings settings;

    public LibraryDownloader(Activity context, boolean use_d8,String tool) {
        this.context = context;
        this.use_d8 = use_d8;
        this.tool = tool;

        downloadPath = FileUtil.getExternalStorageDir() + "/.sketchware/libs/local_libs/";
    }

    private static void mkdirs(File file, String str) {
        File file2 = new File(file, str);
        if (!file2.exists())
            file2.mkdirs();
    }

    public static void copyFile(String sourcePath, String destPath) {
        if (!FileUtil.isExistFile(sourcePath)) return;
        createNewFile(destPath);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourcePath);
            fos = new FileOutputStream(destPath, false);

            byte[] buff = new byte[1024];
            int length = 0;

            while ((length = fis.read(buff)) > 0) {
                fos.write(buff, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            FileUtil.makeDir(dirPath);
        }

        File file = new File(path);

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String dirpart(String str) {
        int lastIndexOf = str.lastIndexOf(File.separatorChar);
        if (lastIndexOf == -1) {
            return null;
        }

        return str.substring(0, lastIndexOf);
    }

    private static void extractFile(ZipInputStream zipInputStream, File file, String str) throws IOException {
        byte[] bArr = new byte[4096];
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(file, str)));
        while (true) {
            int read = zipInputStream.read(bArr);

            if (read == -1) {
                bufferedOutputStream.close();
                return;
            }

            bufferedOutputStream.write(bArr, 0, read);
        }
    }

    public String getClipeBoard() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            //android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            //if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
            if (data != null) {
                return String.valueOf(data.getItemAt(0).getText());
            }
        }
        return null;
    }

    public void showDialog(OnCompleteListener listener) {
        this.listener = listener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.library_downloader_dialog, null);

        final LinearLayout linear1 = view.findViewById(R.id.linear1);
        final LinearLayout progressBarContainer = view.findViewById(R.id.linear3);
        final ProgressBar progressbar1 = view.findViewById(R.id.progressbar1);
        final LinearLayout libraryContainer = view.findViewById(R.id.linear4);
        final TextView message = view.findViewById(R.id.textview3);
        final LinearLayout start = view.findViewById(R.id.linear8);
        final LinearLayout pause = view.findViewById(R.id.linear9);
        final LinearLayout resume = view.findViewById(R.id.linear10);
        final LinearLayout cancel = view.findViewById(R.id.linear11);
        final EditText library = view.findViewById(R.id.edittext1);

        final ImageButton acao = view.findViewById(R.id.imageview1);
        final RadioGroup radiog = view.findViewById(R.id.choselibraryextesion);
        final RadioButton useAar = view.findViewById(R.id.liblaryformataar);
        final RadioButton useJar = view.findViewById(R.id.liblaryformatjar);

        linear1.removeView(progressBarContainer);

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.show();

        useAar.setEnabled(true);
        useJar.setEnabled(true);

        start.setEnabled(true);
        start.setVisibility(View.VISIBLE);
        pause.setEnabled(false);
        pause.setVisibility(View.GONE);
        pause.setEnabled(false);
        pause.setVisibility(View.GONE);
        resume.setEnabled(false);
        resume.setVisibility(View.GONE);
        cancel.setEnabled(true);
        cancel.setVisibility(View.VISIBLE);

        library.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
                final String _charSeq = _param1.toString();
                if (_charSeq.length() > 0) {
                    acao.setImageResource(R.drawable.ic_delete_grey);
                } else {
                    acao.setImageResource(R.drawable.ic_content_paste_grey);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        acao.setOnClickListener(acaoView -> {
            if (library.getText().toString().length() > 0) {
                library.setText("");
            } else {
                library.setText(getClipeBoard());
            }
        });

        start.setOnClickListener(startView -> {
            int status = 0;
            String dependency = library.getText().toString();
            
            if (dependency.isEmpty()) {
                status = 1;
            } else if (!dependency.contains(".")) {
                status = 2;
            } else if (dependency.contains("dependency") & dependency.contains("groupId")) {
                SketchwareUtil.toast("Maven");
                dependency = dependency.replace("<artifactId>", ":");
                dependency = dependency.replace("artifactId", "");
                dependency = dependency.replace("<version>", ":");
                dependency = dependency.replace("</version>", "");
                dependency = dependency.replace("dependency", "");
                dependency = dependency.replace("groupId", "");
                dependency = dependency.replace("<>", "");
                dependency = dependency.replace("</>", "");
                dependency = dependency.replaceAll(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("implementation") & dependency.contains("group:")) {
                //implementation group: 'com.google.code.gson', name: 'gson', version: '2.10.1'
                SketchwareUtil.toast("Gradle");
                dependency = dependency.replace("implementation", "");
                dependency = dependency.replace("group:", "");
                dependency = dependency.replace("name:", ":");
                dependency = dependency.replace("version:", ":");
                dependency = dependency.replace(",", "");
                dependency = dependency.replace("'", "");
                dependency = dependency.replace(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("implementation") & dependency.contains(":")) {
                if (dependency.contains("'")){
                    SketchwareUtil.toast("(Gradle (Short)");
                }else{
                    SketchwareUtil.toast("Gradle (Kotlin)");
                }
                // implementation 'io.github.amrdeveloper:codeview:1.3.4'
                dependency = dependency.replace("implementation", "");
                dependency = dependency.replace("'", "");
                dependency = dependency.replace("\"", "");
                dependency = dependency.replace("(", "");
                dependency = dependency.replace(")", "");
                dependency = dependency.replace(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("libraryDependencies") & dependency.contains("%")) {
                //libraryDependencies += "com.google.code.gson" % "gson" % "2.10.1"
                SketchwareUtil.toast("SBT");
                dependency = dependency.replace("libraryDependencies", "");
                dependency = dependency.replace("+=", "");
                dependency = dependency.replace("\"", "");
                dependency = dependency.replace("%", "");
                dependency = dependency.replace(" ", ":");
                dependency = dependency.replaceAll("(^::)", "");
                dependency = dependency.replace("::", ":");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("dependency") & dependency.contains("org=")) {
                //<dependency org="com.google.code.gson" name="gson" rev="2.10.1"/>
                SketchwareUtil.toast("Ivy");
                dependency = dependency.replace("dependency", "");
                dependency = dependency.replace("\"", "");
                dependency = dependency.replace("<", "");
                dependency = dependency.replace(">", "");
                dependency = dependency.replace("/", "");
                dependency = dependency.replace(":", "");
                dependency = dependency.replace("org=", "");
                dependency = dependency.replace("name=", ":");
                dependency = dependency.replace("rev=", ":");
                dependency = dependency.replace(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("@Grapes") & dependency.contains("version=")) {
                SketchwareUtil.toast("Grape");
                dependency = dependency.replace("\n", "");
                dependency = dependency.replace("@Grapes", "");
                dependency = dependency.replace("@Grab", "");
                dependency = dependency.replace("group=", "");
                dependency = dependency.replace("module=", ":");
                dependency = dependency.replace("version=", ":");
                dependency = dependency.replace("'", "");
                dependency = dependency.replace("(", "");
                dependency = dependency.replace(")", "");
                dependency = dependency.replace(",", "");
                dependency = dependency.replace(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("[") & dependency.contains("/") & dependency.contains("]")) {
                SketchwareUtil.toast("Leiningen");
                dependency = dependency.replace("[", "");
                dependency = dependency.replace("]", "");
                dependency = dependency.replace("/", ":");
                dependency = dependency.replaceAll("( \")", ":");
                dependency = dependency.replace("\"", "");
                dependency = dependency.replace(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains("'") & dependency.contains(":aar:") | dependency.contains(":jar:")) {
                SketchwareUtil.toast("Buildr");
                dependency = dependency.replace("'", "");
                dependency = dependency.replace(":aar:", ":");
                dependency = dependency.replace(":jar:", ":");
                dependency = dependency.replace(" ", "");
                dependency = dependency.replace("\n", "");
                status = 3;
            } else if (dependency.contains(":") & dependency.contains(".")) {
                dependency = dependency.replaceAll("\n", "");
                dependency = dependency.replaceAll(" ", "");
                if (dependency.contains(":")){
                    Pattern p = Pattern.compile("(\\w+.\\w+.?\\w+.\\w+:\\w+-?\\w*:\\d+.\\d+.\\d+-?\\w*\\d*)");
                    Matcher m = p.matcher(dependency);                
                    if (m.find()) {
                        dependency = m.group(1);
                        SketchwareUtil.toast("done");
                        status = 3;
                    }
                    status = 3;
                } else if (!dependency.contains("http") & dependency.contains(":") | dependency.contains(".")) {
                    status = 3;
                } else {
                    status = 2;
                }
            } else {
                status = 2;
                SketchwareUtil.toastError("Invalid dependency");
            }

            if (status == 1) {
                SketchwareUtil.toastError("Dependency can't be empty");
                library.setTextColor(0xFFFFFFFF);
            } else if (status == 2) {
                SketchwareUtil.toastError("Invalid dependency");
                library.setTextColor(0xFFf91010);
            } else if (status == 3) {
                if (dependency.contains(":") | dependency.contains(".")){
                    library.setTextColor(0xFF00E676);
                }else{
                    library.setTextColor(0xFFF91010);
                }                
                dependency = dependency.replaceAll("\n", "");
                dependency = dependency.replaceAll(" ", "");

                library.setText(dependency);
                libName = downloadPath + _getLibName(dependency).trim();

                if (!FileUtil.isExistFile(libName)) {
                    FileUtil.makeDir(libName);
                }
                Use_Aar = useAar.isChecked();

                isAarDownloaded = false;
                isAarAvailable = false;

                isJarDownloaded = false;
                isJarAvailable = false;

                library.setEnabled(false);

                acao.setEnabled(false);

                useAar.setEnabled(false);
                useJar.setEnabled(false);

                start.setEnabled(false);
                start.setVisibility(View.GONE);

                cancel.setEnabled(true);
                cancel.setVisibility(View.VISIBLE);

                _getRepository();
                counter = 0;
                currentRepo = repoUrls.get(counter);

                downloadId = _download(
                        currentRepo.concat(_getDownloadLink(dependency)),
                        downloadPath,
                        _getLibName(dependency + ".zip"),
                        library,
                        message,
                        progressBarContainer,
                        libraryContainer,
                        start,
                        pause,
                        resume,
                        cancel,
                        acao,
                        useAar,
                        useJar,
                        progressbar1
                );
            } else {
                SketchwareUtil.toastError("Invalid dependency");
                library.setTextColor(0xFFf91010);
            }
        });

        pause.setOnClickListener(pauseView -> {
            if (PRDownloader.getStatus(downloadId) == Status.RUNNING) {
                PRDownloader.pause(downloadId);
            }
        });

        resume.setOnClickListener(resumeView -> {
            if (PRDownloader.getStatus(downloadId) == Status.PAUSED) {
                PRDownloader.resume(downloadId);
            }
        });

        cancel.setOnClickListener(cancelView -> {
            PRDownloader.cancel(downloadId);
            library.setEnabled(false);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });
    }

    private String _getDownloadLink(String str) {
        String[] split = str.split(":");
        String str2 = "/";

        for (int i = 0; i < split.length - 1; i++) {
            str2 = str2.concat(split[i].replace(".", "/") + "/");
        }

        return str2.concat(split[split.length - 1]).concat("/").concat(_getExtencionName(str));
    }

    private String _getExtencionName(String str) {
        String[] split = str.split(":");
        return split[split.length - 2] + "-" + split[split.length - 1] + (Use_Aar ? ".aar" : ".jar");
    }

    private String _getLibName(String str) {
        String[] split = str.split(":");
        return split[split.length - 2] + "_v_" + split[split.length - 1];
    }

    private void _jar2dex(String _path) throws Exception {
        // 6.3.0
        if (use_d8) {
            if (tool.equals("D8")) {
                // File libs = new File(context.getFilesDir(), "libs");
                ArrayList<String> cmd = new ArrayList<>();
                //  Compile without debugging information.
                cmd.add("--release");
                //  Output result in <file>
                cmd.add("--output");
                cmd.add(new File(_path).getParentFile().getAbsolutePath());
                //  Add <file|jdk-home> as a library resource
                cmd.add("--lib");
                cmd.add(new File(BuiltInLibraries.EXTRACTED_COMPILE_ASSETS_PATH, "android.jar").getAbsolutePath());
                //  cmd.add(new File(libs, "android.jar").getAbsolutePath());
                //  Add <file> as a classpath resource
                cmd.add("--classpath");
                cmd.add(new File(BuiltInLibraries.EXTRACTED_COMPILE_ASSETS_PATH, "core-lambda-stubs.jar").getAbsolutePath());
                // cmd.add(new File(libs, "core-lambda-stubs.jar").getAbsolutePath());
                //  Compile an intermediate result intended for later merging
                cmd.add("--intermediate");
                // Input <file>
                cmd.add(_path);
                // run D8 with list commands
                D8.main(cmd.toArray(new String[0]));

            } else if (tool.equals("R8")) {
                // R8
                //ArrayList<String> options = new ArrayList<>();
                //options.add("--release"); 
                //options.add("--intermediate"); 
                //options.add("--no-desugaring"); 
                //options.add("--min-api"); 
                //options.add("26");
                // Output
                //options.add("--output");
                //options.add(new File(_path, "classes.zip").getParentFile().getAbsolutePath());                  
                //options.add("--lib");
                //options.add(new File(BuiltInLibraries.EXTRACTED_COMPILE_ASSETS_PATH, "android.jar").getAbsolutePath());
                //options.add("--classpath");
                //options.add(new File(BuiltInLibraries.EXTRACTED_COMPILE_ASSETS_PATH, "core-lambda-stubs.jar").getAbsolutePath());
                // Input
                //cmd.add("--input");
                //options.add(_path);
                //run D8 with list commands
                //R8.main(options.toArray(new String[0]));

                String[] cmd = new String[] {
                "--release",
                "--output", new File(_path,"classes.zip").getParentFile().getAbsolutePath(),
                _path,
                };
                R8.main(cmd);

                //R8Compiler compiler = new R8Compiler(_path, new File(_path).getParentFile().getAbsolutePath());
                //compiler.compile();
            }
        } else {
            // 6.3.0 fix2
            Main.clearInternTables();
            // dx
            // 6.3.0 fix1
            //  "--keep-classes",
            //  "--incremental",
            //  "--dex",
            Main.main(new String[]{
                    "--debug",
                    "--verbose",
                    "--multi-dex",
                    "--output=" + new File(_path).getParentFile().getAbsolutePath(),
                    _path
            });
        }
    }

    private void _unZipFile(String str, String str2) {
        try {
            File file = new File(str2);
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(str));

            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();

                if (nextEntry == null) {
                    zipInputStream.close();
                    return;
                }

                String name = nextEntry.getName();

                if (nextEntry.isDirectory()) {
                    mkdirs(file, name);
                } else {
                    String dirpart = dirpart(name);

                    if (dirpart != null) {
                        mkdirs(file, dirpart);
                    }

                    extractFile(zipInputStream, file, name);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLastSegment(String path) {
        return Uri.parse(path).getLastPathSegment();
    }

    private String findPackageName(String path, String defaultValue) {
        ArrayList<String> files = new ArrayList<>();
        FileUtil.listDir(path, files);

        // Method 1: use manifest
        for (String f : files) {
            if (getLastSegment(f).equals("AndroidManifest.xml")) {
                String content = FileUtil.readFile(f);

                Pattern p = Pattern.compile("<manifest.*package=\"(.*?)\"", Pattern.DOTALL);
                Matcher m = p.matcher(content);

                if (m.find()) {
                    return m.group(1);
                }
            }
        }

        // Method 2: use Gradle
        for (String f : files) {
            if (getLastSegment(f).endsWith(".gradle")) {
                String content = FileUtil.readFile(f);

                Pattern p = Pattern.compile("^\\s*applicationId\\s*[:=].*\"(.*)\"", Pattern.MULTILINE);
                Matcher m = p.matcher(content);

                if (m.find()) {
                    return m.group(1);
                }
            }
        }

        // Method 3: use META-INF
        for (String f : files) {
            if (f.endsWith("META-INF/MANIFEST.MF")) {
                String content = FileUtil.readFile(f);

                Pattern p = Pattern.compile("^\\s*Bundle-SymbolicName\\s*:\\s*(.*);", Pattern.MULTILINE);
                Matcher m = p.matcher(content);

                if (m.find()) {
                    return m.group(1);
                }
            }
        }

        // Method 4: use baseline-prof.txt
        for (String f : files) {
            if (getLastSegment(f).equals("baseline-prof.txt")) {
                String content = FileUtil.readFile(f);

                Pattern p = Pattern.compile("^\\s*package: (.*)", Pattern.MULTILINE);
                Matcher m = p.matcher(content);

                if (m.find()) {
                    return m.group(1);
                }
            }
        }

        // Method 5: screw manifest. use dependency
        if (defaultValue.contains(":")) {
            return defaultValue.split(":")[0];
        }

        // Method 6: ignore manifesto. ignore dependency, testing new ways
        if (defaultValue.contains(".")) {
            return defaultValue.split("\\.")[0];
        }

        // Method 7: nothing worked. return empty string (lmao) (yeah lmao)
        return "";
    }

    private void checkLibsDirectory(String path) {
        ArrayList<String> files = new ArrayList<>();
        FileUtil.listDir(path, files);

        for (String f : files) {
            String p = getLastSegment(f);
            if (p.equals("libs")) {
                String libsPath = f;
                ArrayList<String> libsFiles = new ArrayList<>();
                FileUtil.listDir(libsPath, libsFiles);

                for (String lib : libsFiles) {
                    if (lib.endsWith(".jar")) {
                        File libFile = new File(lib);
                        File classesJar = new File(path + File.separator + "classes.jar");
                        if (libFile.length() > classesJar.length()) {
                            classesJar.delete();
                            FileUtil.copyFile(lib, path + File.separator + "classes.jar");
                        }
                    }
                }
            }
        }
    }

    private void deleteUnnecessaryFiles(String path) {

        // 6.3.0
        String[] list = {
                "res",
                "classes.dex",
                "classes.jar",
                "config",
                "version",
                "AndroidManifest.xml",
                "jni",
                "assets",
                "proguard.txt"
        };

        List<String> validFiles = new ArrayList<>(Arrays.asList(list));
        ArrayList<String> files = new ArrayList<>();
        FileUtil.listDir(path, files);

        for (String f : files) {
            // 6.3.0
            // Skip all dex files
            String p = getLastSegment(f);

            if (p.startsWith("classes") && p.endsWith(".dex")) continue;
            if (!validFiles.contains(p)) FileUtil.deleteFile(f);
        }
    }

    @SuppressLint("SetTextI18n")
    private int _download(
            final String url,
            final String path,
            final String name,

            final EditText library,
            final TextView message,
            final LinearLayout progressBarContainer,
            final LinearLayout libraryContainer,

            final LinearLayout start,
            final LinearLayout pause,
            final LinearLayout resume,
            final LinearLayout cancel,

            final ImageButton acao,
            final RadioButton useAar,
            final RadioButton useJar,

            final ProgressBar progressbar1) {

        return PRDownloader
                .download(url, path, name)
                .build()
                .setOnStartOrResumeListener(() -> {
                    message.setText("Library found. Downloading...");

                    library.setEnabled(false);

                    acao.setEnabled(false);

                    useAar.setEnabled(false);
                    useJar.setEnabled(false);

                    libraryContainer.removeAllViews();
                    libraryContainer.addView(progressBarContainer);

                    start.setEnabled(false);
                    start.setVisibility(View.GONE);

                    pause.setEnabled(true);
                    pause.setVisibility(View.VISIBLE);

                    resume.setEnabled(false);
                    resume.setVisibility(View.GONE);

                    cancel.setEnabled(true);
                    cancel.setVisibility(View.VISIBLE);
                })
                .setOnPauseListener(() -> {
                    message.setText("Downloading paused.");

                    library.setEnabled(false);

                    acao.setEnabled(false);

                    useAar.setEnabled(false);
                    useJar.setEnabled(false);

                    start.setEnabled(false);
                    start.setVisibility(View.GONE);

                    pause.setEnabled(false);
                    pause.setVisibility(View.GONE);

                    resume.setEnabled(true);
                    resume.setVisibility(View.VISIBLE);

                    cancel.setEnabled(true);
                    cancel.setVisibility(View.VISIBLE);
                })
                .setOnCancelListener(() -> {
                    FileUtil.deleteFile(libName);
                    library.setEnabled(true);

                    acao.setEnabled(true);

                    useAar.setEnabled(true);
                    useJar.setEnabled(true);

                    start.setEnabled(true);
                    start.setVisibility(View.VISIBLE);

                    pause.setEnabled(false);
                    pause.setVisibility(View.GONE);

                    resume.setEnabled(false);
                    resume.setVisibility(View.GONE);

                    cancel.setEnabled(true);
                    cancel.setVisibility(View.VISIBLE);
                })
                .setOnProgressListener(progress -> {
                    int progressPercent = (int) (progress.currentBytes * 100 / progress.totalBytes);
                    progressbar1.setProgress(progressPercent);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // this is a default in Sketchware
                        isAarAvailable = Use_Aar;
                        isAarDownloaded = Use_Aar;
                        // this is the beginning of magic
                        isJarAvailable = !isAarAvailable;
                        isJarDownloaded = !isAarDownloaded;

                        StringBuilder path2 = new StringBuilder();
                        path2.append(downloadPath);
                        path2.append(_getLibName(library.getText().toString()).concat(".zip"));

                        if (Use_Aar) {
                            if (isAarDownloaded && isAarAvailable) {
                                _unZipFile(path2.toString(), libName);
                            }
                        } else {
                            if (isJarDownloaded && isJarAvailable) {
                                FileUtil.makeDir(path2.toString());
                                copyFile(path2.toString(), libName.concat("/classes.jar").toString());
                            }
                        }
                        if (FileUtil.isExistFile(libName.concat("/classes.jar"))) {
                            if (use_d8 || JarCheck.checkJarFast(libName.concat("/classes.jar"), 44, 51)) {
                                message.setText("Download completed!");
                                String[] test = new String[]{libName.concat("/classes.jar")};
                                new BackTask().execute(test);
                                FileUtil.deleteFile(path2.toString());
                                FileUtil.writeFile(libName + "/config", findPackageName(libName + "/", library.getText().toString()));
                                FileUtil.writeFile(libName + "/version", library.getText().toString());
                                checkLibsDirectory(libName + "/");
                                deleteUnnecessaryFiles(libName + "/");
                            } else {
                                message.setText("This jar is not supported by Dx since Dx only supports up to Java 1.7.\nIn order to proceed, " + 
                                (Build.VERSION.SDK_INT < 26 ? "D8 (Only supported by Android version is 8+)" : "you need Press Start to switch to D8") + ".");
                                //use_d8 = Build.VERSION.SDK_INT >= 26;
                                if (Build.VERSION.SDK_INT < 26){
                                    start.setEnabled(false);
                                    start.setVisibility(View.GONE);
                                }
                                FileUtil.deleteFile(libName);
                                FileUtil.deleteFile(path2.toString());
                                cancel.setEnabled(true);
                                cancel.setVisibility(View.VISIBLE);
                            }
                        } else {
                            message.setText("Library doesn't contain a jar file.");
                            FileUtil.deleteFile(libName);
                            FileUtil.deleteFile(path2.toString());
                            library.setEnabled(true);

                            acao.setEnabled(true);

                            useAar.setEnabled(true);
                            useJar.setEnabled(true);

                            start.setEnabled(true);
                            start.setVisibility(View.VISIBLE);

                            cancel.setEnabled(true);
                            cancel.setVisibility(View.VISIBLE);
                        }

                        library.setEnabled(true);

                        acao.setEnabled(true);

                        useAar.setEnabled(true);
                        useJar.setEnabled(true);

                        start.setEnabled(true);
                        start.setVisibility(View.VISIBLE);

                        pause.setEnabled(false);
                        pause.setVisibility(View.GONE);

                        resume.setEnabled(false);
                        resume.setVisibility(View.GONE);

                        cancel.setEnabled(true);
                        cancel.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(PRDownloader.Error e) {
                        if (e.isServerError()) {
                            if ((Use_Aar ? !(isAarDownloaded || isAarAvailable) : !(isJarDownloaded || isJarAvailable))) {
                                if (counter < repoUrls.size()) {
                                    currentRepo = repoUrls.get(counter);
                                    String name = repoNames.get(counter);

                                    counter++;
                                    String messageText = String.format("%s %d/%d [%s]", SEARCHING_MESSAGE, counter, repoUrls.size(), name);
                                    message.setText(messageText);

                                    downloadId = _download(
                                            currentRepo.concat(_getDownloadLink(library.getText().toString())),
                                            downloadPath,
                                            _getLibName(library.getText().toString()) + ".zip",
                                            library,
                                            message,
                                            progressBarContainer,
                                            libraryContainer,
                                            start,
                                            pause,
                                            resume,
                                            cancel,
                                            acao,
                                            useAar,
                                            useJar,
                                            progressbar1
                                    );

                                /*                                    
                                    currentRepo = repoUrls.get(counter);
                                    String name = repoNames.get(counter);

                                    counter++;
                                    message.setText("Searching... " + counter + "/" + repoUrls.size() + " [" + name + "]");

                                    downloadId = _download(
                                            currentRepo.concat(_getDownloadLink(library.getText().toString())),
                                            downloadPath,
                                            _getLibName(library.getText().toString()) + ".zip",
                                            library,
                                            message,
                                            progressBarContainer,
                                            libraryContainer,
                                            start,
                                            pause,
                                            resume,
                                            cancel,
                                            acao,
                                            useAar,
                                            useJar,
                                            progressbar1
                                    );
                                */
                                } else {
                                    FileUtil.deleteFile(libName);
                                    message.setText("Library was not found in loaded repositories");
                                    library.setEnabled(true);

                                    acao.setEnabled(true);

                                    useAar.setEnabled(true);
                                    useJar.setEnabled(true);

                                    start.setEnabled(true);
                                    start.setVisibility(View.VISIBLE);

                                    pause.setEnabled(false);
                                    pause.setVisibility(View.GONE);

                                    resume.setEnabled(false);
                                    resume.setVisibility(View.GONE);

                                    cancel.setEnabled(true);
                                    cancel.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if (e.isConnectionError()) {
                                FileUtil.deleteFile(libName);
                                message.setText("Downloading failed. No network");
                                library.setEnabled(true);

                                acao.setEnabled(true);

                                useAar.setEnabled(true);
                                useJar.setEnabled(true);

                                start.setEnabled(true);
                                start.setVisibility(View.VISIBLE);

                                pause.setEnabled(false);
                                pause.setVisibility(View.GONE);

                                resume.setEnabled(false);
                                resume.setVisibility(View.GONE);

                                cancel.setEnabled(true);
                                cancel.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });
    }

    private void _getRepository() {
        repoUrls.clear();
        repoMap.clear();
        repoNames.clear();
        int counter = 0;

        String jsonString = readFile(CONFIGURED_REPOSITORIES_FILE);
        if (jsonString.isEmpty()) {
            jsonString = DEFAULT_REPOSITORIES_FILE_CONTENT;
            writeFile(CONFIGURED_REPOSITORIES_FILE, jsonString);
        }

        repoMap = new Gson().fromJson(jsonString, Helper.TYPE_MAP_LIST);

        for (HashMap<String, Object> configuration : repoMap) {
            String repositoryUrl = (String) configuration.get("url");
            String repositoryName = (String) configuration.get("name");
            repoUrls.add(repositoryUrl);
            repoNames.add(repositoryName);
            counter++;
        }
    }

    private String readFile(File file) {
        if (!file.exists()) {
            return "";
        }
        return FileUtil.readFile(file.getAbsolutePath());
    }

    private void writeFile(File file, String content) {
        FileUtil.writeFile(file.getAbsolutePath(), content);
    }


    /*
    private void _getRepository() {
        repoUrls.clear();
        repoMap.clear();
        repoNames.clear();
        counter = 0;

        readRepositories:
        {
            String repositories;
            if (CONFIGURED_REPOSITORIES_FILE.exists() && !(repositories = FileUtil.readFile(CONFIGURED_REPOSITORIES_FILE.getAbsolutePath())).isEmpty()) {
                try {
                    repoMap = new Gson().fromJson(repositories, Helper.TYPE_MAP_LIST);

                    if (repoMap != null) {
                        break readRepositories;
                    }
                } catch (JsonParseException ignored) {
                    // fall-through to shared error toast
                }

                SketchwareUtil.toastError("Custom Repositories configuration file couldn't be read from. Using default repositories for now", Toast.LENGTH_LONG);
            } else {
                FileUtil.writeFile(CONFIGURED_REPOSITORIES_FILE.getAbsolutePath(), DEFAULT_REPOSITORIES_FILE_CONTENT);
            }

            repoMap = new Gson().fromJson(DEFAULT_REPOSITORIES_FILE_CONTENT, Helper.TYPE_MAP_LIST);
        }

        for (HashMap<String, Object> configuration : repoMap) {
            Object repoUrl = configuration.get("url");

            if (repoUrl instanceof String) {
                Object repoName = configuration.get("name");

                if (repoName instanceof String) {
                    repoUrls.add((String) repoUrl);
                    repoNames.add((String) repoName);
                }
            }

            counter++;
        }
    }
        */
    public interface OnCompleteListener {
        void onComplete();
    }

    private class BackTask extends AsyncTask<String, String, String> {
        boolean success = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait");
            //progressDialog.setMessage((use_d8 ? "D8" : "Dx") + " is running...");
            progressDialog.setMessage( tool + " is running...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                _jar2dex(params[0]);
                success = true;
            } catch (Exception e) {
                success = false;
                return e.toString();
            }
            return "true";
        }

        @Override
        protected void onPostExecute(String s) {
            if (success) {
                // make a Toast 
                bB.a(context, "The library has been downloaded and imported to local libraries successfully.\n"  + libName, 60).show();
                /*
                Snackbar snackbar = Snackbar.a(context.getParent().findViewById(R.id.managepermissionLinearLayout1), "Library: " + libName, -2 ); // BaseTransientBottomBar.LENGTH_INDEFINITE 
                snackbar.a(Helper.getResString(R.string.common_word_show), v -> {
                  snackbar.c();
                    bB.a(context, "The library has been downloaded and imported to local libraries successfully.\n"  + libName, 60).show();
                    // to imprementation go to library add recently 
                });
                //Set the text color to green
                snackbar.f(Color.GREEN);
                snackbar.n(); */

                listener.onComplete();
            } else {
                bB.a(context, "Dexing failed: " + s, 60).show();
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        protected void setSuccess(String success){
            bB.a(context, "Dexing finish: " + success, 25).show();
        }
    }
}
