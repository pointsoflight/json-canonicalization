import java.io.File;

import org.webpki.json.JSONParser;
import org.webpki.json.JSONOutputFormats;

import org.webpki.util.ArrayUtil;
import org.webpki.util.DebugFormatter;

public class CanonicalizerTest {

    static String inputDirectory;
    static String outputDirectory;

    static void performOneTest(String fileName) throws Exception {
        byte[] rawInput = ArrayUtil.readFile(inputDirectory + File.separator + fileName);
        byte[] expected = ArrayUtil.readFile(outputDirectory + File.separator + fileName);
        byte[] actual = JSONParser.parse(rawInput).serializeToBytes(JSONOutputFormats.CANONICALIZED);
        if (ArrayUtil.compare(expected, actual)) {
            StringBuilder utf8 = new StringBuilder("\nFile: ");
            utf8.append(fileName).append("\n");
            int byteCount = 0;
			boolean next = false;
            for (byte b : actual) {
                if (byteCount++ % 32 == 0) {
                    utf8.append('\n');
                    next = false;
                }
                if (next) {
                    utf8.append(" ");
                }
                next = true;
                utf8.append(DebugFormatter.getHexString(new byte[]{b}));
            }
			System.out.println(utf8.append("\n").toString());
        } else {
            throw new RuntimeException("Failed for file: " + fileName);
        }
    }

    public static void main(String[] args) throws Exception {
        inputDirectory = args[0] + File.separator + "input";
        outputDirectory = args[0] + File.separator + "output";
        JSONParser.setStrictNumericMode(false);
        if (args.length == 1) {
            File[] files = new File(inputDirectory).listFiles();
            for (File f : files) {
                performOneTest(f.getName());
            }
        } else {
            performOneTest(args[1]);
        }
    }
}
