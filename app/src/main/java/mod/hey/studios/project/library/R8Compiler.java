package mod.hey.studios.project.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.android.tools.r8.CompilationMode;
import com.android.tools.r8.OutputMode;
import com.android.tools.r8.R8;
import com.android.tools.r8.R8Command;
import com.android.tools.r8.origin.Origin;
import com.android.tools.r8.shaking.ProguardRuleParserException;

import mod.hey.studios.project.proguard.ProguardHandler;
import mod.hey.studios.project.ProjectSettings;

public class R8Compiler {
    private final String inputFilePath;
    private final String outputFilePath;
    public ProjectSettings settings;

    public R8Compiler(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public void compile() throws IOException, ProguardRuleParserException {
        Path inputFile = Paths.get(inputFilePath);
        Path outputFile = Paths.get(outputFilePath);
        R8Command command = R8Command.builder()
            .addProgramFiles(inputFile)
            .setMinApiLevel(settings.getMinSdkVersion())
            .setOutput(outputFile, R8Command.OutputMode.DexIndexed)
            .addProguardConfiguration(ProguardHandler.ANDROID_PROGUARD_RULES_PATH, new String[0])
            .setMode(CompilationMode.RELEASE)
            .build();
        R8.run(command);
    }
}
