package techcourse.fakebook.utils.uploader;

import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
    String upload(MultipartFile multipartFile, String dirName, String fileName);
    String getArticlePath();
    String getUserProfilePath();
    String getUserProfileDefaultPath();
    String getUserProfileDefaultName();
}
