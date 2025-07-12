package api.domain.image.utils;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FileUtils {

    @Value("${url.host}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Value("${url.scheme}")
    private String scheme;

    @Value("${file.context-path}")
    private String contextPath;

    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 확장자를 제외한 순수 파일 이름
    public static String getFileOfName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    // 확장자를 제외한 순수 파일 이름
    public static String getFileOfName(MultipartFile multipartFile) {
        String originalFileName = FileUtils.getOriginalFileName(multipartFile);
        return originalFileName.substring(0, originalFileName.lastIndexOf("."));
    }

    public static String getOriginalFileName(MultipartFile multipartFile) {
        return StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    }

    public String createImageUrl(Path filePath) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)
            .path(contextPath + filePath.getFileName());

        // localhost 일 때만 포트 추가
        if ("localhost".equalsIgnoreCase(host)) {
            builder.port(port);
        }

        return builder.toUriString();
    }

}
