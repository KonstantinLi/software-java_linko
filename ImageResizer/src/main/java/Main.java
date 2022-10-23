import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String SRC_FOLDER = "src/main/resources/original";
    private static final String DST_FOLDER = "src/main/resources/shrinked";

    public static void main(String[] args) {
        File srcDir = new File(SRC_FOLDER);
        File[] files = srcDir.listFiles();

        List<ImageResizer> threads = new ArrayList<>();
        int processors = Runtime.getRuntime().availableProcessors();
        int remainFiles = files.length;

        for (int i = 1; i <= processors; i++) {
            int countFiles = remainFiles / (processors - i + 1);

            File[] newFiles = new File[countFiles];
            System.arraycopy(files, files.length - remainFiles, newFiles, 0, countFiles);

            ImageResizer thread = new ImageResizer(newFiles, DST_FOLDER, true);
            threads.add(thread);

            remainFiles -= countFiles;
        }

        for (ImageResizer thread : threads) {
            long start = System.currentTimeMillis();
            thread.setStart(start);
            thread.start();
        }
    }
}
