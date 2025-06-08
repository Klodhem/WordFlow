<script setup>
import FileUpload from "@/components/FileUpload.vue";
import {onMounted, onUnmounted, ref} from "vue";
import apiClient from '@/axios.js';
import SpeechSynthesis from "@/components/SpeechSynthesis.vue";
import SelectVideo from "@/components/SelectVideo.vue";
import store from "@/store/store.js";
import Video from "@/components/Video.vue";
import Test from "@/components/Test.vue";
import Dictionary from "@/components/Dictionary.vue";

const userText = ref('')
const selectedLanguage = ref(null);
const language = ref([]);
const foundTime = ref(null);
const errorMessage = ref(null);
const intervalId = ref(null);
const videos = ref([])


const getVideos = async () => {
  try {
    const response = await apiClient.get('/videos')
    videos.value = response.data
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const findPhrase = async () => {
  errorMessage.value = null;
  foundTime.value = null;

  try {
    const response = await apiClient.get(`/videos/${store.state.selectedVideo.videoId}/phrase`, {
      params: {
        phrase: userText.value
      },
    });
    foundTime.value = response.data;
    seekToTime(foundTime.value * 0.001)
  } catch (error) {
    if (error.response && error.response.status === 404) {
      errorMessage.value = error.response.data;
    } else {
      errorMessage.value = 'Произошла ошибка при поиске фразы.';
    }
  }
};

function seekToTime(seconds) {
  if (store.state.videoPlayer.value) {
    store.state.videoPlayer.value.currentTime = seconds;
  }
}

onMounted(async () => {
  try {
    const [lang] = await Promise.all([
      fetch('/data/language.json').then(r => r.json())
    ]);
    language.value = lang;
    selectedLanguage.value = language.value[0]?.code || null;
  } catch (e) {
    console.error('Ошибка загрузки языков:', e);
  }
});

onMounted(() => {
  getVideos()
  intervalId.value = setInterval(getVideos, 120000)
})

onUnmounted(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value)
    intervalId.value = null
  }
})
</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4 auto-rows-min items-start">
    <div class="bg-gray-100 rounded shadow flex flex-col p-2 gap-4 min-h-0 h-[27.2rem]">
      <FileUpload @refresh-videos="getVideos"/>
      <SelectVideo v-model:videos="videos"></SelectVideo>
    </div>

    <div class="bg-gray-100 rounded shadow p-2 flex flex-col justify-center gap-4 h-[27.2rem]">
      <h3 class="block text-lg font-medium text-gray-900 ">Работа с речью</h3>
      <SpeechSynthesis :userText="userText"></SpeechSynthesis>
      <div class="w-full">
        <button @click="findPhrase"
                :disabled="!userText.trim() || !store.state.selectedVideo?.videoId"
                class="px-3 py-1 w-full text-white bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10 disabled:bg-slate-600">
          Найти фрагмент
        </button>
        <textarea
          v-model="userText"
          id="message"
          rows="4"
          class="resize-none w-full block p-2.5 text-gray-100 bg-gray-600 rounded-lg border
              border-gray-300 text-sm md:text-base h-44"
          placeholder="Введите текст..."></textarea>
      </div>
    </div>
  </div>

  <div class="p-8 md:p-16">
    <Video></Video>
  </div>

  <div v-if="store.state.selectedVideo" class="m-4 md:m-8 overflow-x-auto">
    <Test></Test>
    <Dictionary></Dictionary>
  </div>
</template>


