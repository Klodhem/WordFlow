package git.klodhem.videoservice.gRPC;

import com.google.protobuf.Empty;
import git.klodhem.common.dto.TranslateProposalDTO;
import git.klodhem.common.dto.model.VideoDTO;
import git.klodhem.grpc.video.VideoServiceGrpc;
import git.klodhem.grpc.video.VideoServiceProto;
import git.klodhem.videoservice.services.VideoService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class VideoGrpcServerService extends VideoServiceGrpc.VideoServiceImplBase {

    private final VideoService videoService;

    @Override
    public void getVideos(VideoServiceProto.VideoListRequest request,
                          StreamObserver<VideoServiceProto.VideoListResponse> responseObserver) {
        List<VideoDTO> videoDTOS = videoService.getVideos(request.getUserId());
        List<VideoServiceProto.VideoDTO> protoVideos = videoDTOS.stream()
                .map(dto -> VideoServiceProto.VideoDTO.newBuilder()
                        .setVideoId(dto.getVideoId())
                        .setTitle(dto.getTitle())
                        .setStatus(dto.getStatus())
                        .build()
                )
                .toList();

        VideoServiceProto.VideoListResponse response = VideoServiceProto.VideoListResponse.newBuilder()
                .addAllVideos(protoVideos)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteVideo(VideoServiceProto.DeleteVideoRequest request, StreamObserver<Empty> responseObserver) {
        videoService.deleteVideo(request.getVideoId(), request.getUserId());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getDictionary(VideoServiceProto.DictionaryRequest request, StreamObserver<VideoServiceProto.TranslateProposalListResponse> responseObserver) {
        List<TranslateProposalDTO> translateProposalDTOS = videoService.getDictionary(request.getVideoId(), false, request.getUserId());
        List<VideoServiceProto.TranslateProposalDTO> protoProposals = translateProposalDTOS.stream()
                .map(dto -> VideoServiceProto.TranslateProposalDTO.newBuilder()
                        .setTranslated(dto.getTranslated())
                        .setOriginal(dto.getOriginal())
                        .build()
                )
                .toList();

        VideoServiceProto.TranslateProposalListResponse response = VideoServiceProto.TranslateProposalListResponse.newBuilder()
                .addAllProposals(protoProposals)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchPhrase(VideoServiceProto.PhraseSearchRequest request, StreamObserver<VideoServiceProto.PhraseSearchResponse> responseObserver) {
        Long time = videoService.searchPhrase(request.getVideoId(), request.getPhrase());
        VideoServiceProto.PhraseSearchResponse response;
        if (time == null) {
            response = VideoServiceProto.PhraseSearchResponse.newBuilder()
                    .build();
        } else {
            response = VideoServiceProto.PhraseSearchResponse.newBuilder()
                    .setTime(time)
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getVttFilePath(VideoServiceProto.VttFilePathRequest request, StreamObserver<VideoServiceProto.VttFilePathResponse> responseObserver) {
        String fileVttPath = videoService.getVttFile(request.getVideoId(), false, request.getType(), request.getUserId());
        VideoServiceProto.VttFilePathResponse response = VideoServiceProto.VttFilePathResponse.newBuilder()
                .setVttFilePath(fileVttPath)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getVideoFilePath(VideoServiceProto.VideoFilePathRequest request, StreamObserver<VideoServiceProto.VideoFilePathResponse> responseObserver) {
        String videoFilePath = videoService.getVideoFilePath(request.getVideoId(), false, request.getUserId());
        VideoServiceProto.VideoFilePathResponse response = VideoServiceProto.VideoFilePathResponse.newBuilder()
                .setVideoFilePath(videoFilePath)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getVideosGroup(VideoServiceProto.VideoGroupRequest request, StreamObserver<VideoServiceProto.VideoGroupResponse> responseObserver) {
        List<VideoDTO> videoDTOS = videoService.getVideosGroupDTO(request.getVideosIdList());
        List<VideoServiceProto.VideoDTO> protoVideos = videoDTOS.stream()
                .map(dto -> VideoServiceProto.VideoDTO.newBuilder()
                        .setVideoId(dto.getVideoId())
                        .setTitle(dto.getTitle())
                        .setStatus(dto.getStatus())
                        .build()
                )
                .toList();
        VideoServiceProto.VideoGroupResponse videoGroupResponse = VideoServiceProto.VideoGroupResponse.newBuilder()
                .addAllVideos(protoVideos)
                .build();
        responseObserver.onNext(videoGroupResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getDictionaryGroup(VideoServiceProto.DictionaryRequestGroup request, StreamObserver<VideoServiceProto.TranslateProposalListResponse> responseObserver) {
        List<TranslateProposalDTO> translateProposalDTOS = videoService.getDictionary(request.getVideoId(), true, null);
        List<VideoServiceProto.TranslateProposalDTO> protoProposals = translateProposalDTOS.stream()
                .map(dto -> VideoServiceProto.TranslateProposalDTO.newBuilder()
                        .setTranslated(dto.getTranslated())
                        .setOriginal(dto.getOriginal())
                        .build()
                )
                .toList();

        VideoServiceProto.TranslateProposalListResponse response = VideoServiceProto.TranslateProposalListResponse.newBuilder()
                .addAllProposals(protoProposals)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getVttFilePathGroup(VideoServiceProto.VttFilePathRequestGroup request, StreamObserver<VideoServiceProto.VttFilePathResponse> responseObserver) {
        String fileVttPath = videoService.getVttFile(request.getVideoId(), true, request.getType(), null);
        VideoServiceProto.VttFilePathResponse response = VideoServiceProto.VttFilePathResponse.newBuilder()
                .setVttFilePath(fileVttPath)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getVideoFilePathGroup(VideoServiceProto.VideoFilePathRequestGroup request, StreamObserver<VideoServiceProto.VideoFilePathResponse> responseObserver) {
        String videoFilePath = videoService.getVideoFilePath(request.getVideoId(), true, null);
        VideoServiceProto.VideoFilePathResponse response = VideoServiceProto.VideoFilePathResponse.newBuilder()
                .setVideoFilePath(videoFilePath)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkAccessFromVideoByVideoIdAndUserId(VideoServiceProto.CheckAccessFromVideoByVideoIdAndUserIdRequest request, StreamObserver<VideoServiceProto.CheckAccessFromVideoByVideoIdAndUserIdResponse> responseObserver) {
        boolean access = videoService.checkAccessFromVideoByVideoIdAndUserId(request.getVideoId(), request.getUserId());
        VideoServiceProto.CheckAccessFromVideoByVideoIdAndUserIdResponse response = VideoServiceProto.CheckAccessFromVideoByVideoIdAndUserIdResponse.newBuilder()
                .setAccess(access)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
