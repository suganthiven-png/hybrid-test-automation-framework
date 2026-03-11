package method;

import org.testng.annotations.DataProvider;
import testNgframeworkwithAIV1.util.ConfigReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DataProviders {

    @DataProvider(name = "loginUsers", parallel = false)
    public static Object[][] loginUsers() {
        ConfigReader cfg = new ConfigReader();
        String[][] base = cfg.getUsers();
        if (base == null || base.length == 0) return new Object[0][0];

        Object[][] out = new Object[base.length][2];

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String outDir = "test-output";
        File dir = new File(outDir);
        if (!dir.exists()) dir.mkdirs();
        String outFileName = outDir + File.separator + "generated-users-" + timestamp + ".txt";

        try (BufferedWriter w = new BufferedWriter(new FileWriter(outFileName, true))) {
            for (int i = 0; i < base.length; i++) {
                String baseName = base[i][0];
                String pwd = base[i][1];
                // short random suffix to avoid collisions across parallel CI runs
                String shortRand = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
                String unique = baseName + "_" + timestamp + "_" + shortRand;
                out[i][0] = unique;
                out[i][1] = pwd;
                // persist username:password
                try {
                    w.write(unique + ":" + pwd);
                    w.newLine();
                } catch (IOException ioe) {
                    // ignore write error but continue
                }
            }
            w.flush();
        } catch (IOException e) {
            // failing to write file should not stop tests; continue returning users
        }

        return out;
    }
}