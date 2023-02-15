package mod.hey.studios.project.library;

import com.android.tools.r8.CompilationFailedException;
import com.android.tools.r8.CompilationMode;
import com.android.tools.r8.OutputMode;
import com.android.tools.r8.R8;
import com.android.tools.r8.R8Command;
import com.android.tools.r8.R8Command.Builder;
import com.android.tools.r8.utils.AndroidApiLevel;
import com.android.tools.r8.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import mod.agus.jcoderz.lib.FileUtil;
import mod.jbk.build.BuiltInLibraries;

public class R8Compiler {
    private final String inputFilePath;
    private final String outputFilePath;

    public R8Compiler(String inputFilePath, String outputFilePath) {
        /* Construtor que inicializa as variáveis de entrada e saída do arquivo. */
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void compile() throws IOException {
        /* Método que compila o arquivo de entrada com o R8.*/
        Path inputFile = null;
        Path outputFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            /* Verifica se a versão do Android é maior ou igual ao Android 8.0 (Oreo).
             Se for, o caminho para o arquivo de entrada é obtido com o Paths.get().*/
            inputFile = Paths.get(inputFilePath);
            outputFile = Paths.get(outputFilePath);
        }
                // Caminho dos arquivos
        Path coreStubsJarPath = Paths.get(BuiltInLibraries.EXTRACTED_COMPILE_ASSETS_PATH, "core-lambda-stubs.jar");
        Path androidJarPath = Paths.get(BuiltInLibraries.EXTRACTED_COMPILE_ASSETS_PATH, "android.jar");

        // Lista de classpaths
        List<String> classpath = Arrays.asList(coreStubsJarPath.toString());

        // Lista de libs
        List<String> library = Arrays.asList(androidJarPath.toString());

        R8Command command;
        try {
            /* Tenta criar o objeto R8Command, que define as configurações para a compilação.*/
            command = R8Command.builder()
                .setMode(CompilationMode.RELEASE)
                .setIntermediate(true)
                .setDisableDesugaring(true)
                .setMinApiLevel(26)
                .setClasspathFiles(classpath)
                .addLibraryFiles(library)
                .setOutput(outputFile, OutputMode.DexIndexed)
                .addProgramFiles(inputFile)
                .build();
        } catch (CompilationFailedException e) {
            /* Captura a exceção CompilationFailedException e lança uma nova exceção do tipo RuntimeException.*/
            throw new RuntimeException(e);
        }
        try {
            R8.run(command);
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }
}