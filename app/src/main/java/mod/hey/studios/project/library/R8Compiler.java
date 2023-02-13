import com.android.tools.r8.R8;
import com.android.tools.r8.OutputMode;
import com.android.tools.r8.D8Command;

import java.nio.file.Paths;

public class R8Compiler {

  public static void compileJarToDex(String jarPath, String dexPath) throws Exception {
    D8Command.Builder builder = D8Command.builder();
    builder.addProgramFiles(Paths.get(jarPath));
    builder.setOutput(Paths.get(dexPath), OutputMode.DexIndexed);
    R8.run(builder.build());
  }
}