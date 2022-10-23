import lombok.Data;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Data
public class ImageResizer extends Thread {
    private final File[] files;
    private final String dstFolder;
    private final boolean useImgscalr;

    private long start;

    @Override
    public void run() {
        try {
            if (start == 0L) {
                throw new IllegalArgumentException("Variable start hasn't been initialized");
            }

            for (File file : files) {
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    continue;
                }

                int newWidth = 300;
                int newHeight = (int) Math.round(
                        image.getHeight() / (image.getWidth() / (double) newWidth)
                );

                BufferedImage newImage;
                if (useImgscalr) {
                    newImage = resizeWithImgscalr(image, newWidth, newHeight);
                } else {
                    newImage = resize(image, newWidth, newHeight);
                }

                File newFile = new File(dstFolder + "/" + file.getName());
                ImageIO.write(newImage, "jpg", newFile);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("'" + this.getName() + "' worked: " + (System.currentTimeMillis() - start) + " ms.");
    }

    private BufferedImage resize(BufferedImage image, int newWidth, int newHeight) throws IOException {

        BufferedImage newImage = new BufferedImage(
                newWidth, newHeight, BufferedImage.TYPE_INT_RGB
        );

        int widthStep = image.getWidth() / newWidth;
        int heightStep = image.getHeight() / newHeight;

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                int rgb = image.getRGB(x * widthStep, y * heightStep);
                newImage.setRGB(x, y, rgb);
            }
        }

        return newImage;
    }

    private BufferedImage resizeWithImgscalr(BufferedImage image, int newWidth, int newHeight) {
        return Scalr.resize(image, newWidth, newHeight);
    }
}
