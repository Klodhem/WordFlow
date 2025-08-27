//package git.klodhem.backend.services.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.ResponseBytes;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//@Service
//@Log4j2
//@RequiredArgsConstructor
//public class LocalStorageService {
//
//    @Qualifier("minio") private final S3Client s3Client;
//
//    @Value("${bucketMinio}")
//    private String bucket;
//
//    public void uploadToStorage(MultipartFile file, String fileName) {
//        try {
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(fileName)
//                    .contentType(file.getContentType())
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//            log.debug("Файл успешно загружен: {}", fileName);
//        }
//        catch (Exception e){
//            log.error("Ошибка при отправке файла на Storage: {}", fileName, e);
//            throw new RuntimeException("Ошибка загрузки: " + fileName, e);
//        }
//    }
//
//    public byte[] getFromStorage(MultipartFile file, String fileName) {
//        try {
//            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(fileName)
//                    .build();
//
//            ResponseBytes<?> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
//            return objectBytes.asByteArray();
//
////            ResponseInputStream<GetObjectResponse> object= s3Client.getObject(getObjectRequest);
////            GetObjectResponse response = object.response();
////            response.
//        }
//        catch (Exception e){
//            log.error("Ошибка при получении файла из Storage: {}", fileName, e);
//            throw new RuntimeException("Ошибка получения файла из Storage: " + fileName, e);
//        }
//    }
//}
