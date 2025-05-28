import { createStore } from 'vuex'

export default createStore({
      state: () => ({
        selectedGroupId: null,
        test: [],
        selectedVideo: null,
        videoUrl: null,
        originalSubtitleUrl: null,
        translatedSubtitleUrl: null,
        videoPlayer: null,
        testPairs: [],
        userAnswers: {},
      }),
      getters: {
        selectedGroupId: state => state.selectedGroupId,
        test: state => state.test,
        selectedVideo: state => state.selectedVideo,
        videoUrl: state => state.videoUrl,
        originalSubtitleUrl: state => state.originalSubtitleUrl,
        translatedSubtitleUrl: state => state.translatedSubtitleUrl,
        videoPlayer: state => state.videoPlayer,
        testPairs: state => state.testPairs,
        userAnswers: state => state.userAnswers,
      },
      mutations: {
        SET_SELECTED_GROUP_ID(state, selectedGroupId) {
          state.selectedGroupId = selectedGroupId
        },
        SET_TEST(state, test) {
          state.test = test
        },
        SET_SELECTED_VIDEO(state, selectedVideo) {
          state.selectedVideo = selectedVideo
        },
        SET_VIDEO_URL(state, videoUrl) {
          state.videoUrl = videoUrl
        },
        SET_ORIGINAL_SUBTITLE_URL(state, originalSubtitleUrl) {
          state.originalSubtitleUrl = originalSubtitleUrl
        },
        SET_TRANSLATE_SUBTITLE_URL(state, translatedSubtitleUrl) {
          state.translatedSubtitleUrl = translatedSubtitleUrl
        },
        SET_VIDEO_PLAYER(state, videoPlayer) {
          state.videoPlayer = videoPlayer
        },
        SET_TEST_PAIRS(state, testPairs) {
          state.testPairs = testPairs
        },
        SET_USER_ANSWERS(state, userAnswers) {
          state.userAnswers = userAnswers
        },
      },
})
