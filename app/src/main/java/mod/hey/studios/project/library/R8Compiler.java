package mod.hey.studios.project.library;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.CompilationMode;
import com.android.tools.r8.OutputMode;
import com.android.tools.r8.R8;
import com.android.tools.r8.R8Command;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import mod.hey.studios.project.ProjectSettings;

public class R8Compiler {
    private final String ANDROID_PROGUARD_RULES_PATH;
    private final String inputFilePath;
    private final String outputFilePath;
    private final ProjectSettings settings;

    public R8Compiler(String inputFilePath, String outputFilePath) {
        // Construtor que inicializa as variáveis de entrada e saída do arquivo.
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void compile() throws IOException {
        // Método que compila o arquivo de entrada com o R8.
        
        Path inputFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Verifica se a versão do Android é maior ou igual ao Android 8.0 (Oreo).
            // Se for, o caminho para o arquivo de entrada é obtido com o Paths.get().
            inputFile = Paths.get(inputFilePath);
        }
        Path outputFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Verifica se a versão do Android é maior ou igual ao Android 8.0 (Oreo).
            // Se for, o caminho para o arquivo de saída é obtido com o Paths.get().
            outputFile = Paths.get(outputFilePath);
        }
        ANDROID_PROGUARD_RULES_PATH = FileUtil.getExternalStorageDir() + "/.sketchware/libs/android-proguard-rules.pro";
        R8Command command;
        try {
            // Tenta criar o objeto R8Command, que define as configurações para a compilação.
            command = R8Command.builder()
                .addProgramFiles(inputFile)
                .setMinApiLevel(21)
                .setOutput(outputFile, OutputMode.DexIndexed)
                .setProguardConfig(new File(ANDROID_PROGUARD_RULES_PATH))
                .setMode(CompilationMode.RELEASE)
                .build();
        } catch (CompilationFailedException e) {
            // Captura a exceção CompilationFailedException e lança uma nova exceção do tipo RuntimeException.
            throw new RuntimeException(e);
        }
        try {
            R8.run(command);
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }
}