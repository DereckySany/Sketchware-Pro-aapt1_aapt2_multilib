package mod.jbk.build.compiler.resource;

import android.content.Context;
import android.content.pm.PackageManager;

import com.besome.sketch.SketchApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import a.a.a.Dp;
import a.a.a.Jp;
import a.a.a.zy;
import mod.agus.jcoderz.editor.manage.library.locallibrary.ManageLocalLibrary;
import mod.agus.jcoderz.lib.BinaryExecutor;
import mod.agus.jcoderz.lib.FileUtil;
import mod.hey.studios.build.BuildSettings;
import mod.hey.studios.project.ProjectSettings;
import mod.jbk.build.BuildProgressReceiver;
import mod.jbk.build.BuiltInLibraries;
import mod.jbk.diagnostic.MissingFileException;
import mod.jbk.util.LogUtil;

/**
 * A class responsible for compiling a Project's resources.
 * Supports AAPT2.
 */
public class ResourceCompiler {

    /**
     * About log tags: add ":" and the first letter of the function's name camelCase'd.
     * For example, in thisIsALongFunctionName, you should use this:
     * <pre>
     *     TAG + ":tIALFN"
     * </pre>
     */
    private static final String TAG = "AppBuilder";
    private final boolean willBuildAppBundle;
    private final File aaptFile;
    private final BuildProgressReceiver progressReceiver;
    private final Dp dp;

    public ResourceCompiler(Dp dp, File aapt, boolean willBuildAppBundle, BuildProgressReceiver receiver) {
        this.willBuildAppBundle = willBuildAppBundle;
        aaptFile = aapt;
        progressReceiver = receiver;
        this.dp = dp;
    }
    public void compile() throws IOException, zy, MissingFileException {
        Compiler resourceCompiler = new Aapt2Compiler(dp, aaptFile, willBuildAppBundle);
        //resourceCompiler.setProgressListener(progressReceiver::onProgress);
        resourceCompiler.setProgressListener(new Compiler.ProgressListener() {
            @Override
            void onProgressUpdate(String newProgress) {
                if (progressReceiver != null) progressReceiver.onProgress(newProgress);
            }
        });
        resourceCompiler.compile();
    }

    /*
    public void compile() throws IOException, zy, MissingFileException {
        Compiler resourceCompiler;
        resourceCompiler = new Aapt2Compiler(dp, aaptFile, willBuildAppBundle);

        resourceCompiler.setProgressListener(new Compiler.ProgressListener() {
            @Override
            void onProgressUpdate(String newProgress) {
                if (progressReceiver != null) progressReceiver.onProgress(newProgress);
            }
        });
        resourceCompiler.compile();
    }*/

    /**
     * A base class of a resource compiler.
     */
    interface Compiler {

        /**
         * Compile a project's resources fully.
         */
        void compile() throws zy, MissingFileException;

        /**
         * Set a progress listener to compiling.
         *
         * @param listener The listener object
         */
        void setProgressListener(ProgressListener listener);

        /**
         * A listener for progress on compilation.
         */
        abstract class ProgressListener {
            /**
             * The compiler has reached a new phase the user should know about.
             *
             * @param newProgress A String provided by the resource compiler the user should see.
             */
            abstract void onProgressUpdate(String newProgress);
        }
    }

    /*
    // original
    static class Aapt2Compiler implements Compiler {

        private final boolean buildAppBundle;

        private final File aapt2;
        private final Dp buildHelper;
        private final File compiledBuiltInLibraryResourcesDirectory;
        private ProgressListener progressListener;

        public Aapt2Compiler(Dp buildHelper, File aapt2, boolean buildAppBundle) {
            this.buildHelper = buildHelper;
            this.aapt2 = aapt2;
            this.buildAppBundle = buildAppBundle;
            compiledBuiltInLibraryResourcesDirectory = new File(SketchApplication.getContext().getCacheDir(), "compiledLibs");
        }
    }
    */

    /**
     * A {@link Compiler} implementing AAPT2.
     */
    static class Aapt2Compiler implements Compiler {

        private final boolean buildAppBundle;
        private final File aapt2;
        private final Dp buildHelper;
        private ProgressListener progressListener;

        public Aapt2Compiler(final Dp buildHelper, final File aapt2, final boolean buildAppBundle) {
            this.buildHelper = buildHelper;
            this.aapt2 = aapt2;
            this.buildAppBundle = buildAppBundle;
        }

        private File getCompiledBuiltInLibraryResourcesDirectory() {
            return new File(SketchApplication.getContext().getCacheDir(), "compiledLibs");
        }
        // Restante do código

        @Override
        public void compile() throws zy, MissingFileException {
            String outputPath = buildHelper.yq.binDirectoryPath + File.separator + "res";
            emptyOrCreateDirectory(outputPath);

            long startTime = System.currentTimeMillis();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            List<Future<Void>> futures = new ArrayList<>();

            futures.add(executor.submit(() -> {
                compileBuiltInLibraryResources();
                return null;
            }));

            futures.add(executor.submit(() -> {
                compileLocalLibraryResources(outputPath);
                return null;
            }));

            futures.add(executor.submit(() -> {
                compileProjectResources(outputPath);
                return null;
            }));

            futures.add(executor.submit(() -> {
                compileImportedResources(outputPath);
                return null;
            }));

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // Handle exception
                }
            }

            executor.shutdown();

            long totalTime = System.currentTimeMillis() - startTime;
            LogUtil.d(TAG + ":c", "Resource compilation completed in " + totalTime + " ms");
            link();
        }

        /*
        // original
        @Override
        public void compile() throws zy, MissingFileException {
            String outputPath = buildHelper.yq.binDirectoryPath + File.separator + "res";
            emptyOrCreateDirectory(outputPath);

            long savedTimeMillis = System.currentTimeMillis();
            if (progressListener != null) {
                progressListener.onProgressUpdate("Compiling resources with AAPT2...");
            }
            compileBuiltInLibraryResources();
            LogUtil.d(TAG + ":c", "Compiling built-in library resources took " + (System.currentTimeMillis() - savedTimeMillis) + " ms");
            savedTimeMillis = System.currentTimeMillis();
            compileLocalLibraryResources(outputPath);
            LogUtil.d(TAG + ":c", "Compiling local library resources took " + (System.currentTimeMillis() - savedTimeMillis) + " ms");
            savedTimeMillis = System.currentTimeMillis();
            compileProjectResources(outputPath);
            LogUtil.d(TAG + ":c", "Compiling project generated resources took " + (System.currentTimeMillis() - savedTimeMillis) + " ms");
            savedTimeMillis = System.currentTimeMillis();
            compileImportedResources(outputPath);
            LogUtil.d(TAG + ":c", "Compiling project imported resources took " + (System.currentTimeMillis() - savedTimeMillis) + " ms");

            savedTimeMillis = System.currentTimeMillis();
            link();
            LogUtil.d(TAG + ":c", "Linking resources took " + (System.currentTimeMillis() - savedTimeMillis) + " ms");
        }*/

        /**
         * Links the project's resources using AAPT2.
         *
         * @throws zy Thrown to be caught by DesignActivity to show an error Snackbar.
         */
        public void link() throws zy, MissingFileException {
            String resourcesPath = buildHelper.yq.binDirectoryPath + File.separator + "res";
            if (progressListener != null)
                progressListener.onProgressUpdate("Linking resources with AAPT2...");

            ArrayList<String> args = new ArrayList<>();
            args.add(aapt2.getAbsolutePath());
            args.add("link");

            if (buildAppBundle) {
                args.add("--proto-format");
            }

            args.addAll(Arrays.asList(
                "--allow-reserved-package-id",
                "--auto-add-overlay",
                "--no-version-vectors",
                "--no-version-transitions"
            ));

            args.addAll(Arrays.asList(
                "--min-sdk-version",
                String.valueOf(buildHelper.settings.getMinSdkVersion()),
                "--target-sdk-version",
                buildHelper.settings.getValue(ProjectSettings.SETTING_TARGET_SDK_VERSION, "28")
            ));

            args.addAll(Arrays.asList(
                "--version-code",
                Optional.ofNullable(buildHelper.yq.versionCode).filter(s -> !s.isEmpty()).orElse("1"),
                "--version-name",
                Optional.ofNullable(buildHelper.yq.versionName).filter(s -> !s.isEmpty()).orElse("1.0")
            ));

            String customAndroidSdk = buildHelper.build_settings.getValue(BuildSettings.SETTING_ANDROID_JAR_PATH, "");
            if (customAndroidSdk.isEmpty()) {
                args.add("-I");
                args.add(buildHelper.androidJarPath);
            } else {
                linkingAssertFileExists(customAndroidSdk);
                args.addAll(Arrays.asList("-I", customAndroidSdk));
            }

            /* Add assets imported by vanilla method */
            linkingAssertDirectoryExists(buildHelper.yq.assetsPath);
            args.addAll(Arrays.asList("-A", buildHelper.yq.assetsPath));

            /* Add imported assets */
            String importedAssetsPath = buildHelper.fpu.getPathAssets(buildHelper.yq.sc_id);
            if (FileUtil.isExistFile(importedAssetsPath)) {
                args.addAll(Arrays.asList("-A", importedAssetsPath));
            }

            /* Add built-in libraries' assets */
            for (Jp library : buildHelper.builtInLibraryManager.a()) {
                if (library.d()) {
                    String assetsPath = BuiltInLibraries.getLibraryAssetsPath(library.a());

                    linkingAssertDirectoryExists(assetsPath);
                    args.addAll(Arrays.asList("-A", assetsPath));
                }
            }

            /* Add local libraries' assets */
            for (String localLibraryAssetsDirectory : new ManageLocalLibrary(buildHelper.yq.sc_id).getAssets()) {
                linkingAssertDirectoryExists(localLibraryAssetsDirectory);
                args.addAll(Arrays.asList("-A", localLibraryAssetsDirectory));
            }

            /* Include compiled built-in library resources */
            for (Jp library : buildHelper.builtInLibraryManager.a()) {
                if (library.c()) {
                    args.addAll(Arrays.asList("-R", new File(getCompiledBuiltInLibraryResourcesDirectory(), library.a() + ".zip").getAbsolutePath()));
                }
            }

            /* Include compiled local libraries' resources */
            File[] filesInCompiledResourcesPath = new File(resourcesPath).listFiles();
            if (filesInCompiledResourcesPath != null) {
                for (File file : filesInCompiledResourcesPath) {
                    if (file.isFile() && (!file.getName().equals("project.zip") || !file.getName().equals("project-imported.zip"))) {
                        args.addAll(Arrays.asList("-R", file.getAbsolutePath()));
                    }
                }
            }
            /* Include compiled project resources */
            File projectArchive = new File(resourcesPath, "project.zip");
            if (projectArchive.exists()) {
                args.addAll(Arrays.asList("-R", projectArchive.getAbsolutePath()));
            }

            /* Include compiled imported project resources */
            File projectImportedArchive = new File(resourcesPath, "project-imported.zip");
            if (projectImportedArchive.exists()) {
                args.add("-R");
                args.add(projectImportedArchive.getAbsolutePath());
            }

            /* Add R.java */
            linkingAssertDirectoryExists(buildHelper.yq.rJavaDirectoryPath);
            args.add("--java");
            args.add(buildHelper.yq.rJavaDirectoryPath);

            /* Output AAPT2's generated ProGuard rules to a.a.a.yq.aapt_rules */
            args.add("--proguard");
            args.add(buildHelper.yq.aaptProGuardRules);

            /* Add AndroidManifest.xml */
            linkingAssertFileExists(buildHelper.yq.androidManifestPath);
            args.add("--manifest");
            args.add(buildHelper.yq.androidManifestPath);

            /* Use the generated R.java for used libraries */
            String extraPackages = buildHelper.getLibraryPackageNames();
            if (!extraPackages.isEmpty()) {
                args.add("--extra-packages");
                args.add(extraPackages);
            }

            /* Output the APK only with resources to a.a.a.yq.C */
            args.add("-o");
            args.add(buildHelper.yq.resourcesApkPath);

            LogUtil.d(TAG + ":l", args.toString());
            BinaryExecutor executor = new BinaryExecutor();
            executor.setCommands(args);
            String log = executor.execute();
            if (!log.isEmpty()) {
            LogUtil.e(TAG + ":l", log);
            throw new zy(log);
            }
        }

        /*
        // original
        private void compileProjectResources(String outputPath) throws zy, MissingFileException {
            compilingAssertDirectoryExists(buildHelper.yq.resDirectoryPath);

            ArrayList<String> commands = new ArrayList<>();
            commands.add(aapt2.getAbsolutePath());
            commands.add("compile");
            commands.add("--dir");
            commands.add(buildHelper.yq.resDirectoryPath);
            commands.add("-o");
            commands.add(outputPath + File.separator + "project.zip");
            LogUtil.d(TAG + ":cPR", "Now executing: " + commands);
            BinaryExecutor executor = new BinaryExecutor();
            executor.setCommands(commands);
            if (!executor.execute().isEmpty()) {
                LogUtil.e(TAG, executor.getLog());
                throw new zy(executor.getLog());
            }
        } */

        //
        private void compileProjectResources(String outputPath) throws zy, MissingFileException {
            ArrayList<String> commands = generateAapt2CompileCommand(buildHelper.yq.resDirectoryPath, outputPath + File.separator + "project.zip");
            LogUtil.d(TAG + ":cPR", "Now executing: " + commands);
            BinaryExecutor executor = new BinaryExecutor();
            executor.setCommands(commands);
            if (!executor.execute().isEmpty()) {
                LogUtil.e(TAG, executor.getLog());
                throw new zy(executor.getLog());
            }
        }

        private ArrayList<String> generateAapt2CompileCommand(String resDirectoryPath, String outputFilePath) {
            ArrayList<String> commands = new ArrayList<>();
            commands.add(aapt2.getAbsolutePath());
            commands.add("compile");
            commands.add("--dir");
            commands.add(resDirectoryPath);
            commands.add("-o");
            commands.add(outputFilePath);
            return commands;
        }

        //

        private void emptyOrCreateDirectory(String path) {
            FileUtil.deleteFile(path);
            FileUtil.makeDir(path);
        }

        /*
        // original
        private void emptyOrCreateDirectory(String path) {
            if (FileUtil.isExistFile(path)) {
                FileUtil.deleteFile(path);
            }
            FileUtil.makeDir(path);
        }
        */
        /*
        // original
        private void compileLocalLibraryResources(String outputPath) throws zy, MissingFileException {
            int localLibrariesCount = buildHelper.mll.getResLocalLibrary().size();
            LogUtil.d(TAG + ":cLLR", "About to compile " + localLibrariesCount
                    + " local " + (localLibrariesCount == 1 ? "library" : "libraries"));
            for (String localLibraryResDirectory : buildHelper.mll.getResLocalLibrary()) {
                File localLibraryDirectory = new File(localLibraryResDirectory).getParentFile();
                if (localLibraryDirectory != null) {
                    compilingAssertDirectoryExists(localLibraryResDirectory);

                    ArrayList<String> commands = new ArrayList<>();
                    commands.add(aapt2.getAbsolutePath());
                    commands.add("compile");
                    commands.add("--dir");
                    commands.add(localLibraryResDirectory);
                    commands.add("-o");
                    commands.add(outputPath + File.separator + localLibraryDirectory.getName() + ".zip");

                    LogUtil.d(TAG + ":cLLR", "Now executing: " + commands);
                    BinaryExecutor executor = new BinaryExecutor();
                    executor.setCommands(commands);
                    if (!executor.execute().isEmpty()) {
                        LogUtil.e(TAG, executor.getLog());
                        throw new zy(executor.getLog());
                    }
                }
            }
        }
        */
        //
        private void compileLocalLibraryResources(String outputPath) throws zy, MissingFileException {
            List<String> localLibraryResDirectories = buildHelper.mll.getResLocalLibrary();
            int localLibrariesCount = localLibraryResDirectories.size();
            LogUtil.d(TAG + ":cLLR", "About to compile " + localLibrariesCount
                    + " local " + (localLibrariesCount == 1 ? "library" : "libraries"));

            for (String localLibraryResDirectory : localLibraryResDirectories) {
                File localLibraryDirectory = new File(localLibraryResDirectory).getParentFile();
                if (localLibraryDirectory == null) {
                    continue;
                }

                compilingAssertDirectoryExists(localLibraryResDirectory);

                ArrayList<String> commands = new ArrayList<>();
                commands.add(aapt2.getAbsolutePath());
                commands.add("compile");
                commands.add("--dir");
                commands.add(localLibraryResDirectory);
                commands.add("-o");
                commands.add(outputPath + File.separator + localLibraryDirectory.getName() + ".zip");

                LogUtil.d(TAG + ":cLLR", "Now executing: " + commands);
                BinaryExecutor executor = new BinaryExecutor();
                executor.setCommands(commands);
                if (!executor.execute().isEmpty()) {
                    LogUtil.e(TAG, executor.getLog());
                    throw new zy(executor.getLog());
                }
            }
        }

        private void compileBuiltInLibraryResources() throws zy, MissingFileException {
            getCompiledBuiltInLibraryResourcesDirectory().mkdirs();
            List<Thread> threads = new ArrayList<>();

            for (Jp builtInLibrary : buildHelper.builtInLibraryManager.a()) {
                if (builtInLibrary.c()) {
                    String libraryName = builtInLibrary.a();
                    String libraryResources = BuiltInLibraries.getLibraryResourcesPath(libraryName);
                    Context context = SketchApplication.getContext();
                    File cachedCompiledResources = new File(getCompiledBuiltInLibraryResourcesDirectory(), libraryName + ".zip");
                    
                    compilingAssertDirectoryExists(libraryResources);
                    
                    if (isBuiltInLibraryRecompilingNeeded(cachedCompiledResources,context)) {
                        ArrayList<String> commands = new ArrayList<>();
                        commands.add(aapt2.getAbsolutePath());
                        commands.add("compile");
                        commands.add("--dir");
                        commands.add(libraryResources);
                        commands.add("-o");
                        commands.add(cachedCompiledResources.getAbsolutePath());
                        
                        LogUtil.d(TAG + ":cBILR", "Now executing: " + commands);
                        
                        Thread thread = new Thread(() -> {
                            try {
                                BinaryExecutor executor = new BinaryExecutor();
                                executor.setCommands(commands);
                                String log = executor.execute();
                                
                                if (!log.isEmpty()) {
                                    LogUtil.e(TAG + ":cBILR", log);
                                    throw new zy(log);
                                }
                            } catch (zy e) {
                                throw new RuntimeException(e);
                            }
                        });
                        
                        threads.add(thread);
                        thread.start();
                    } else {
                        LogUtil.d(TAG + ":cBILR", "Skipped resource recompilation for built-in library " + libraryName);
                    }
                }
            }

            // Espera todas as threads terminarem antes de continuar
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LogUtil.e(TAG + ":cBILR", "Thread interrupted while waiting for compilation to finish: " + e.getMessage(), e);
                }
            }
        }

        //
        /*
        private void compileBuiltInLibraryResources() throws zy, MissingFileException {
            compiledBuiltInLibraryResourcesDirectory.mkdirs();
            for (Jp builtInLibrary : buildHelper.builtInLibraryManager.a()) {
                if (builtInLibrary.c()) {
                    Context context = SketchApplication.getContext();
                    File cachedCompiledResources = new File(compiledBuiltInLibraryResourcesDirectory, builtInLibrary.a() + ".zip");
                    String libraryResources = BuiltInLibraries.getLibraryResourcesPath(builtInLibrary.a());

                    compilingAssertDirectoryExists(libraryResources);

                    if (isBuiltInLibraryRecompilingNeeded(cachedCompiledResources,context)) {
                        ArrayList<String> commands = new ArrayList<>();
                        commands.add(aapt2.getAbsolutePath());
                        commands.add("compile");
                        commands.add("--dir");
                        commands.add(libraryResources);
                        commands.add("-o");
                        commands.add(cachedCompiledResources.getAbsolutePath());

                        LogUtil.d(TAG + ":cBILR", "Now executing: " + commands);
                        BinaryExecutor executor = new BinaryExecutor();
                        executor.setCommands(commands);
                        if (!executor.execute().isEmpty()) {
                            LogUtil.e(TAG + ":cBILR", executor.getLog());
                            throw new zy(executor.getLog());
                        }
                    } else {
                        LogUtil.d(TAG + ":cBILR", "Skipped resource recompilation for built-in library " + builtInLibrary.a());
                    }
                }
            }
        }
        */

        private boolean isBuiltInLibraryRecompilingNeeded(File cachedCompiledResources, Context context) {
            if (cachedCompiledResources.exists()) {
                try {
                    return context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                            .lastUpdateTime > cachedCompiledResources.lastModified();
                } catch (PackageManager.NameNotFoundException e) {
                    LogUtil.e(TAG + ":iBILRN", "Couldn't get package info about ourselves: " + e.getMessage(), e);
                }
            } else {
                LogUtil.d(TAG + ":iBILRN", "File " + cachedCompiledResources.getAbsolutePath()
                        + " doesn't exist, forcing compilation");
            }
            return true;
        }
        /*
        //original
        private boolean isBuiltInLibraryRecompilingNeeded(File cachedCompiledResources) {
            if (cachedCompiledResources.exists()) {
                try {
                    Context context = SketchApplication.getContext();
                    return context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                            .lastUpdateTime > cachedCompiledResources.lastModified();
                } catch (PackageManager.NameNotFoundException e) {
                    LogUtil.e(TAG + ":iBILRN", "Couldn't get package info about ourselves: " + e.getMessage(), e);
                }
            } else {
                LogUtil.d(TAG + ":iBILRN", "File " + cachedCompiledResources.getAbsolutePath()
                        + " doesn't exist, forcing compilation");
            }
            return true;
        }
        */

        /*
        // original
        private void compileImportedResources(String outputPath) throws zy {
            if (FileUtil.isExistFile(buildHelper.fpu.getPathResource(buildHelper.yq.sc_id))
                    && new File(buildHelper.fpu.getPathResource(buildHelper.yq.sc_id)).length() != 0) {
                ArrayList<String> commands = new ArrayList<>();
                commands.add(aapt2.getAbsolutePath());
                commands.add("compile");
                commands.add("--dir");
                commands.add(buildHelper.fpu.getPathResource(buildHelper.yq.sc_id));
                commands.add("-o");
                commands.add(outputPath + File.separator + "project-imported.zip");
                LogUtil.d(TAG + ":cIR", "Now executing: " + commands);
                BinaryExecutor executor = new BinaryExecutor();
                executor.setCommands(commands);
                if (!executor.execute().isEmpty()) {
                    LogUtil.e(TAG, executor.getLog());
                    throw new zy(executor.getLog());
                }
            }
        } 
        // new
        private void compileImportedResources(String outputPath) throws zy {
            if (FileUtil.isExistFile(buildHelper.fpu.getPathResource(buildHelper.yq.sc_id))
                    && new File(buildHelper.fpu.getPathResource(buildHelper.yq.sc_id)).length() != 0) {
                //String aapt2 = "path/to/aapt2/binary";
                String binAapt2 = aapt2.getAbsolutePath();
                String resourceDir = buildHelper.fpu.getPathResource(buildHelper.yq.sc_id);
                String outputZip = outputPath + File.separator + "project-imported.zip";
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(binAapt2, "compile", "--dir", resourceDir, "-o", outputZip);
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        throw new zy("aapt2 compilation failed with exit code " + exitCode);
                    }
                } catch (IOException | InterruptedException e) {
                    throw new zy("aapt2 compilation failed", e);
                }
            }
        }
        */
        private void compileImportedResources(String outputPath) throws zy {
            String resourceDir = buildHelper.fpu.getPathResource(buildHelper.yq.sc_id);
            if (!FileUtil.isExistFile(resourceDir) || new File(resourceDir).length() == 0) {
                return;
            }
            String outputZip = outputPath + File.separator + "project-imported.zip";
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        aapt2.getAbsolutePath(),
                        "compile",
                        "--dir",
                        resourceDir,
                        "-o",
                        outputZip
                );
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        LogUtil.d(TAG + ":cIR", line);
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new zy("aapt2 compilation failed with exit code " + exitCode);
                }
            } catch (IOException | InterruptedException exception) {
                throw new zy("aapt2 compilation failed",exception);
            }
        }

        private void compilingAssertDirectoryExists(String directoryPath) throws MissingFileException {
            Objects.requireNonNull(directoryPath, "Compiling Assert Directory cannot be null");
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                throw new MissingFileException(directory, MissingFileException.STEP_RESOURCE_COMPILING, true);
            }
        }

        public void linkingAssertFileExists(String filePath) throws MissingFileException {
            Objects.requireNonNull(filePath, "Linking Assert File path cannot be null");
            File file = new File(filePath);
            if (!file.exists()) {
                throw new MissingFileException(file, MissingFileException.STEP_RESOURCE_LINKING, false);
            }
        }

        public void linkingAssertDirectoryExists(String filePath) throws MissingFileException {
            Objects.requireNonNull(filePath, "Linking Assert Directory path cannot be null");
            File file = new File(filePath);
            if (!file.exists()) {
                throw new MissingFileException(file, MissingFileException.STEP_RESOURCE_LINKING, true);
            }
        }

        @Override
        public void setProgressListener(ProgressListener listener) {
            progressListener = listener;
        }
    }
}
