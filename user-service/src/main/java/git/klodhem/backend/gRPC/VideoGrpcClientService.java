package git.klodhem.backend.gRPC;

import git.klodhem.common.dto.TranslateProposalDTO;
import git.klodhem.common.dto.model.VideoDTO;
import git.klodhem.common.exception.VideoFileException;
import git.klodhem.grpc.video.VideoServiceGrpc;
import git.klodhem.grpc.video.VideoServiceProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Log4j2
public class VideoGrpcClientService {

    private final VideoServiceGrpc.VideoServiceBlockingStub videoStub;

    private static final long CHUNK_SIZE = 2 * 1024 * 1024;

    public List<VideoDTO> getVideos() {
        VideoServiceProto.VideoListRequest request = VideoServiceProto.VideoListRequest.newBuilder()
                .setUserId(getCurrentUser().getUserId())
                .build();

        VideoServiceProto.VideoListResponse response = videoStub.getVideos(request);
        return response.getVideosList().stream()
                .map(this::fromProto)
                .toList();
    }

    public void deleteVideo(String videoId) {
        VideoServiceProto.DeleteVideoRequest request = VideoServiceProto.DeleteVideoRequest.newBuilder()
                .setVideoId(videoId)
                .setUserId(getCurrentUser().getUserId())
                .build();

        videoStub.deleteVideo(request);
    }

    public Long searchPhrase(String videoId, String phrase) {
        VideoServiceProto.PhraseSearchRequest request = VideoServiceProto.PhraseSearchRequest.newBuilder()
                .setVideoId(videoId)
                .setPhrase(phrase)
                .build();

        VideoServiceProto.PhraseSearchResponse response = videoStub.searchPhrase(request);
        if (response.hasTime()){
            return response.getTime();
        } else return null;
    }

    public List<TranslateProposalDTO> getDictionary(String videoId, boolean fromGroup) {
        VideoServiceProto.TranslateProposalListResponse response;
        if (!fromGroup) {
            VideoServiceProto.DictionaryRequest request = VideoServiceProto.DictionaryRequest.newBuilder()
                    .setVideoId(videoId)
                    .setUserId(getCurrentUser().getUserId())
                    .build();
            response = videoStub.getDictionary(request);
        } else {
            VideoServiceProto.DictionaryRequestGroup request = VideoServiceProto.DictionaryRequestGroup.newBuilder()
                    .setVideoId(videoId)
                    .build();
            response = videoStub.getDictionaryGroup(request);
        }

        return response.getProposalsList().stream()
                .map(this::fromProto)
                .toList();
    }

    public File getVttFile(String videoId, boolean fromGroup, String type) {
        VideoServiceProto.VttFilePathResponse response;
        if (!fromGroup) {
            VideoServiceProto.VttFilePathRequest request = VideoServiceProto.VttFilePathRequest.newBuilder()
                    .setVideoId(videoId)
                    .setType(type)
                    .setUserId(getCurrentUser().getUserId())
                    .build();
            response = videoStub.getVttFilePath(request);
        } else {
            VideoServiceProto.VttFilePathRequestGroup request = VideoServiceProto.VttFilePathRequestGroup.newBuilder()
                    .setVideoId(videoId)
                    .setType(type)
                    .build();
            response = videoStub.getVttFilePathGroup(request);
        }

        String VttFilePath = response.getVttFilePath();
        File videoFileVtt = new File(VttFilePath);

        if (!videoFileVtt.exists()) {
            log.error("Файл не найден на диске!");
            throw new VideoFileException("Файл не найден на диске!");
        }
        return videoFileVtt;
    }

    public ResourceRegion getVideoRegion(String videoId, boolean fromGroup, HttpHeaders headers) {
        VideoServiceProto.VideoFilePathResponse response;
        if (!fromGroup) {
            VideoServiceProto.VideoFilePathRequest request = VideoServiceProto.VideoFilePathRequest.newBuilder()
                    .setVideoId(videoId)
                    .setUserId(getCurrentUser().getUserId())
                    .build();
            response = videoStub.getVideoFilePath(request);
        } else {
            VideoServiceProto.VideoFilePathRequestGroup request = VideoServiceProto.VideoFilePathRequestGroup.newBuilder()
                    .setVideoId(videoId)
                    .build();
            response = videoStub.getVideoFilePathGroup(request);
        }

        String videoFilePath = response.getVideoFilePath();
        File videoFile = new File(videoFilePath);
        long contentLength = videoFile.length();
        Resource videoResource = new FileSystemResource(videoFile);
        HttpRange range = headers.getRange().stream().findFirst().orElse(null);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            if (end >= contentLength) {
                end = start + CHUNK_SIZE - 1;
            }
            long requestedSize = end - start + 1;
            long regionLength = Math.max(requestedSize, CHUNK_SIZE);
            regionLength = Math.min(regionLength, contentLength - start);
            return new ResourceRegion(videoResource, start, regionLength);
        } else {
            return new ResourceRegion(videoResource, 0, contentLength);
        }
    }

    private VideoDTO fromProto(VideoServiceProto.VideoDTO proto) {
        VideoDTO dto = new VideoDTO();
        dto.setVideoId(proto.getVideoId());
        dto.setTitle(proto.getTitle());
        dto.setStatus(proto.getStatus());
        return dto;
    }

    private TranslateProposalDTO fromProto(VideoServiceProto.TranslateProposalDTO proto) {
        TranslateProposalDTO dto = new TranslateProposalDTO();
        dto.setOriginal(proto.getOriginal());
        dto.setTranslated(proto.getTranslated());
        return dto;
    }

    public List<VideoDTO> getGroupVideos(List<String> videosId) {
        VideoServiceProto.VideoGroupRequest request = VideoServiceProto.VideoGroupRequest.newBuilder()
                .addAllVideosId(videosId)
                .build();

        VideoServiceProto.VideoGroupResponse response = videoStub.getVideosGroup(request);
        return response.getVideosList().stream()
                .map(this::fromProto)
                .toList();
    }

    public boolean checkAccessFromVideoById(String videoId, long userId) {
        VideoServiceProto.CheckAccessFromVideoByVideoIdAndUserIdRequest request = VideoServiceProto
                .CheckAccessFromVideoByVideoIdAndUserIdRequest.newBuilder()
                .setVideoId(videoId)
                .setUserId(userId)
                .build();

        VideoServiceProto.CheckAccessFromVideoByVideoIdAndUserIdResponse response = videoStub
                .checkAccessFromVideoByVideoIdAndUserId(request);
        return response.getAccess();
    }
}
