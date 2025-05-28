<script setup>
import apiClient from "@/axios.js";
import {onUnmounted, reactive, ref} from "vue";
import {useStore} from "vuex";
import {useRoute} from "vue-router";

let emit = defineProps(['videos']);
const groupId = Number(useRoute().params.groupId)

const testPairs = ref([]);
const videoUrl = ref(null);
const originalSubtitleUrl = ref(null);
const translatedSubtitleUrl = ref(null);
const selectedVideo = ref(null);
const test = ref([])
const userAnswers = reactive({});
const store = useStore();

const selectVideo = async video => {
  if (video.status === 'OK') {
    try {
      let response;
      if(!Number.isNaN(groupId)){
        response = await apiClient.get('/video/dictionary/'+video.videoId,{
          params: {
            groupId: groupId
          }
        });
      }else {
        response = await apiClient.get('/video/dictionary/'+video.videoId);
      }
      testPairs.value = response.data;
      store.commit('SET_TEST_PAIRS', testPairs)
    } catch (error) {
      console.log('Ошибка запроса словаря:', error.message)
    }
    let videoResponse, originalSubtitleResponse, translatedSubtitleResponse;
    if (!Number.isNaN(groupId)){
      [videoResponse, originalSubtitleResponse, translatedSubtitleResponse] = await Promise.all([
        fetchWithAuth(`http://localhost:8080/video/group/watch/${groupId}/${video.videoId}`),
        fetchWithAuth(`http://localhost:8080/video/group/originalSubtitle/${groupId}/${video.videoId}`),
        fetchWithAuth(`http://localhost:8080/video/group/translateSubtitle/${groupId}/${video.videoId}`)
      ]);
    }
    else {
      [videoResponse, originalSubtitleResponse, translatedSubtitleResponse] = await Promise.all([
        fetchWithAuth(`http://localhost:8080/video/watch/${video.videoId}`),
        fetchWithAuth(`http://localhost:8080/video/originalSubtitle/${video.videoId}`),
        fetchWithAuth(`http://localhost:8080/video/translateSubtitle/${video.videoId}`)
      ]);
    }
    videoUrl.value = videoResponse;
    originalSubtitleUrl.value = originalSubtitleResponse;
    translatedSubtitleUrl.value = translatedSubtitleResponse;
    store.commit('SET_VIDEO_URL', videoUrl)
    store.commit('SET_ORIGINAL_SUBTITLE_URL', originalSubtitleUrl)
    store.commit('SET_TRANSLATE_SUBTITLE_URL', translatedSubtitleUrl)


    selectedVideo.value = video
    store.commit('SET_SELECTED_VIDEO', selectedVideo)
    if (store.state.videoPlayer) {
      const videoElement = store.state.videoPlayer;
      let activeLang = null
      for (let tt of videoElement.textTracks) {
        if (tt.mode === 'showing') {
          activeLang = tt.language || tt.srclang
          break
        }
      }

      const originalTrack = videoElement.querySelector('track[srclang="or"]');
      if (originalTrack) {
        originalTrack.src = originalSubtitleUrl.value;
      }
      const translatedTrack = videoElement.querySelector('track[srclang="tr"]');
      if (translatedTrack) {
        translatedTrack.src = translatedSubtitleUrl.value;
      }
      for (let tt of videoElement.textTracks) {
        tt.mode = 'disabled';
      }
      videoElement.load();
      videoElement.addEventListener('loadedmetadata', () => {
        Array.from(videoElement.textTracks).forEach(tt => {
          if ((tt.language || tt.srclang) === activeLang) {
            tt.mode = 'showing'
          }
        })
      }, { once: true })
    }
    await getTest(video)
  }
}

const getTest = async video => {
  try {
    const response = await apiClient.get('/test/'+video.videoId, {
      params: {
        groupId: groupId
      }
    })
    test.value = response.data
    test.value.forEach(q => {
      userAnswers[q.questionId] = q.type === 'SINGLE' ? null : []
    })
    store.commit('SET_TEST', test)
    store.commit('SET_USER_ANSWERS', userAnswers)

  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const fetchWithAuth = async (url) => {
  const token = localStorage.getItem('authToken')
  if (!token) {
    console.error('Нет токена, пользователь не авторизован')
    return null
  }

  const response = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` }
  })

  if (!response.ok) {
    console.error(`Ошибка загрузки: ${url}`)
    return null
  }

  return URL.createObjectURL(await response.blob())
}

onUnmounted(() => {
  selectedVideo.value = null;
})

</script>

<template>
  <div class="flex flex-col flex-1 min-h-0">
    <h3 class="text-base md:text-lg font-medium">Выберите видео</h3>
    <div class="bg-gray-100 rounded shadow overflow-y-auto flex-1 min-h-0">
      <ul>
        <li
          v-for="(video, index) in emit.videos"
          :key="index"
          @click="selectVideo(video)"
          class="p-2 hover:bg-gray-200 rounded text-sm md:text-base flex justify-between items-center"
          :class="[
            {'cursor-not-allowed opacity-60': video.status !== 'OK'},
            {'hover:bg-gray-200 cursor-pointer': video.status === 'OK'},
            {'bg-gray-300': selectedVideo && selectedVideo.videoId === video.videoId}
          ]"
        >
          <span class="flex-1">{{ video.title }}</span>

          <span
            class="text-xs md:text-sm font-semibold"
            :class="{
              'text-green-600': video.status === 'OK',
              'text-blue-600': video.status === 'PROCESSING',
              'text-red-600': video.status === 'ERROR'
            }"
          >
            {{ video.status }}
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>

</style>
