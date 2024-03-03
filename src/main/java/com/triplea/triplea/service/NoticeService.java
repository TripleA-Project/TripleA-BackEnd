package com.triplea.triplea.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.notice.NoticeRequest;
import com.triplea.triplea.model.image.UploadImage;
import com.triplea.triplea.model.image.UploadImageRepository;
import com.triplea.triplea.model.notice.Notice;
import com.triplea.triplea.model.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AmazonS3Client amazonS3Client;
    private final UploadImageRepository uploadImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void noticeSave(NoticeRequest.Save save){
        try{
            noticeRepository.save(save.toEntity());
        }catch (Exception e){
            throw new Exception400("notice", "공지사항 작성 실패");
        }

    }

    @Transactional
    public void noticeUpdate(NoticeRequest.Update update){
        try {
            Notice noticePS = getNotice(update.getId());
            noticePS.modifyNotice(update.getTitle(), update.getContent());
        }catch (Exception e) {
            throw new Exception400("notice", "수정실패");
        }
    }

    @Transactional
    public void noticeDelete(Long id){
        try{
            noticeRepository.deleteById(id);
        }catch (Exception e){
            throw new Exception500("공지사항 삭제 실패");
        }
    }

    public List<Notice> getNoticeList(){

        return noticeRepository.findAll();
    }

    public Notice getNotice(Long id){

        return noticeRepository.findById(id).orElseThrow(
                () -> new Exception400("notice", "해당 ID가 없습니다.")
        );
    }

    public String fileUpload(MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        if (file != null && !file.isEmpty()) {

            try {
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                String originalFileName = file.getOriginalFilename();
                int index = originalFileName.lastIndexOf(".");
                String ext = originalFileName.substring(index + 1);

                String storeFileName = UUID.randomUUID() + "." + ext;
                String key = "test/" + storeFileName;
                try (InputStream inputStream = file.getInputStream()){
                    amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                }
                String storeFileUrl = amazonS3Client.getUrl(bucket, key).toString();
                UploadImage uploadImage = new UploadImage(storeFileUrl, originalFileName);

                uploadImageRepository.save(uploadImage);

                return storeFileUrl;
            } catch (IOException e) {
                // 파일 저장 실패 시 예외 처리
                e.printStackTrace();
                throw new Exception400("imageUpload", "업로드 실패");
            }
        }else {
            return "파일 없음";
        }

    }
}
