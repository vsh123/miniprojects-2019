package techcourse.fakebook.utils.uploader.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import techcourse.fakebook.exception.FileDeleteException;
import techcourse.fakebook.exception.FileSaveException;
import techcourse.fakebook.utils.uploader.Uploader;
import techcourse.fakebook.utils.uploader.UploaderConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Component
public class S3Uploader implements Uploader {
    private static final Logger log = LoggerFactory.getLogger(S3Uploader.class);

    private final UploaderConfig uploaderConfig;
    private final AmazonS3 amazonS3Client;
    private final String bucket;

    public S3Uploader(UploaderConfig uploaderConfig, AmazonS3 amazonS3Client, String bucket) {
        this.uploaderConfig = uploaderConfig;
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
    }

    @Override
    public String upload(MultipartFile multipartFile, String dirName, String fileName) {
        try {
            File uploadFile = convert(multipartFile, fileName)
                    .orElseThrow(FileSaveException::new);

            return uploadToBucket(uploadFile, dirName, fileName);
        } catch (IOException e) {
            log.error("FileSaveError : file write 실패");
            log.error(e.getMessage());
            throw new FileSaveException();
        }
    }

    @Override
    public String getArticlePath() {
        return uploaderConfig.getArticlePath();
    }

    @Override
    public String getUserProfilePath() {
        return uploaderConfig.getUserProfilePath();
    }

    @Override
    public String getUserProfileDefaultPath() {
        return uploaderConfig.getUserProfileDefaultPath();
    }

    @Override
    public String getUserProfileDefaultName() {
        return uploaderConfig.getUserProfileDefaultName();
    }

    private String uploadToBucket(File uploadFile, String dirName, String fileName) {
        String savePath = dirName + fileName;
        String uploadImageUrl = putS3(uploadFile, savePath);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if(!targetFile.delete()) {
            throw new FileDeleteException();
        }
    }

    private Optional<File> convert(MultipartFile file, String fileName) throws IOException {
        File convertFile = new File(fileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        log.error("FileSaveError : convert 실패");
        return Optional.empty();
    }
}
