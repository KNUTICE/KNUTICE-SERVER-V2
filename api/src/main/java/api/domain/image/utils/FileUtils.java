package api.domain.image.utils;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FileUtils {

    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getFileOfName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
