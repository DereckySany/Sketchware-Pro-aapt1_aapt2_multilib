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
    private final String inputFilePath;
    private final String outputFilePath;
    public ProjectSettings settings;

    public R8Compiler(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void compile() throws IOException {
        Path inputFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFile = Paths.get(inputFilePath);
        }
        Path outputFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            outputFile = Paths.get(outputFilePath + ".dex");
        }
        R8Command command;
        try {
            command = R8Command.builder()
                .addProgramFiles(inputFile)
                .setMinApiLevel(21)
                .setOutput(outputFile, OutputMode.DexIndexed)
                .setMode(CompilationMode.RELEASE)
                .build();
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
        try {
            R8.run(command);
        } catch (CompilationFailedException e) {
            throw new RuntimeException(e);
        }
    }
}
