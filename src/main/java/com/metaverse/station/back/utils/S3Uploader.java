package com.metaverse.station.back.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("{cloud.aws.cloudfront.s3.url}")
    private String cdn;

    public List<String> upload(List<MultipartFile> multipartFiles, String dirName) throws IOException {

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
//            System.out.println(multipartFile.getOriginalFilename());
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환이 실패했습니다."));
            imageUrls.add(upload(uploadFile, dirName));
        }


        return imageUrls;
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = UUID.randomUUID() + uploadFile.getName();
        String fileDir = dirName + "/" + fileName;
        String uploadImageUrl = putS3(uploadFile, fileDir);
        removeNewFile(uploadFile);
        return fileName;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}
