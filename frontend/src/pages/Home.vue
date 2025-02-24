<script setup>
import FileUpload from "@/components/FileUpload.vue";
import {getCurrentInstance, onMounted, ref} from "vue";
const { proxy } = getCurrentInstance();

const userText = ref('')

const videos = ref([])

const videoPlayer = ref(null);
const selectedVideo = ref(null);
const videoUrl = ref(null);
const originalSubtitleUrl = ref(null);
const translatedSubtitleUrl = ref(null);

const selectedLanguage = ref(null);
const language = ref([]);

const speechRate = ref(1.0);


const foundTime = ref(null);
const errorMessage = ref(null);

const intervalId = ref(null);

const selectVideo = async video => {
  if (video.status === 'OK') {
    try {
      const response = await proxy.$axios.get('http://localhost:8080/video/dictionary/'+video.title);
      testPairs.value = response.data;
    } catch (error) {
      console.log('Ошибка запроса словаря:', error.message)
    }
    videoUrl.value = await fetchWithAuth(`http://localhost:8080/video/watch/${video.title}`)
    originalSubtitleUrl.value = await fetchWithAuth(`http://localhost:8080/video/originalSubtitle/${video.title}`)
    translatedSubtitleUrl.value = await fetchWithAuth(`http://localhost:8080/video/translateSubtitle/${video.title}`)
    selectedVideo.value = video
    if (videoPlayer.value) {
      const videoElement = videoPlayer.value;
      const originalTrack = videoElement.querySelector('track[srclang="or"]');
      if (originalTrack) {
        originalTrack.src = originalSubtitleUrl.value;
      }
      const translatedTrack = videoElement.querySelector('track[srclang="tr"]');
      if (translatedTrack) {
        translatedTrack.src = translatedSubtitleUrl.value;
      }
      videoPlayer.value.load();
    }
  }
}

const findPhrase = async () => {
  errorMessage.value = null;
  foundTime.value = null;

  try {
    const response = await proxy.$axios.get('http://localhost:8080/video/searchPhrase', {
      params: {
        videoId: selectedVideo.value.videoId,
        phrase: userText.value
      },
    });
    foundTime.value = response.data;
    seekToTime(foundTime.value*0.001)
  } catch (error) {
    if (error.response && error.response.status === 404) {
      errorMessage.value = error.response.data;
    } else {
      errorMessage.value = 'Произошла ошибка при поиске фразы.';
    }
  }
};

function seekToTime(seconds) {
  if (videoPlayer.value) {
    videoPlayer.value.currentTime = seconds;
  }
}

const getVideos = async () => {
  try {
    const response = await proxy.$axios.get('http://localhost:8080/video/getVideos')
    videos.value = response.data
    console.log(videos.value[1])
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}
onMounted(() => {
  getVideos()
  intervalId.value = setInterval(getVideos, 120000)
})

onMounted(() => {
  language.value = [
    { code: 'EN_US', name: 'Английский (по умолчанию)' },
    { code: 'DE_DE', name: 'Немецкий' },
    { code: 'ES_ES', name: 'Испанский' },
    { code: 'FI_FI', name: 'Финский' },
    { code: 'FR_FR', name: 'Французский' },
    // { code: 'HE_HE', name: 'Иврит' },
    { code: 'IT_IT', name: 'Итальянский' },
    { code: 'KK_KZ', name: 'Казахский' },
    { code: 'NL_NL', name: 'Голландский' },
    { code: 'PL_PL', name: 'Польский' },
    { code: 'PT_PT', name: 'Португальский' },
    { code: 'PT_BR', name: 'Бразильский португальский' },
    { code: 'RU_RU', name: 'Русский' },
    { code: 'SV_SE', name: 'Шведский' },
    { code: 'TR_TR', name: 'Турецкий' },
    { code: 'UZ_UZ', name: 'Узбекский (латиница)' }
  ]


  selectedLanguage.value = language.value[0].code || null;
});

const testPairs = ref([]);

async function playSynthesizedSpeech(text) {
  try {
    const response = await proxy.$axios.get('http://localhost:8080/speech/synthesize', {
      params: {
        text: text,
        language: selectedLanguage.value,
        speechRate: speechRate.value
      },
      responseType: 'blob'
    })
    const audioBlob = new Blob([response.data], { type: 'audio/wav' })
    const audioUrl = URL.createObjectURL(audioBlob)
    const audio = new Audio(audioUrl)
    await audio.play()
  } catch (error) {
    console.error('Ошибка синтеза речи:', error)
  }
}

const handlePlayUserText = () => {
  if (!userText.value.trim()) {
    console.error('Введите текст!')
    return
  }
  playSynthesizedSpeech(userText.value)
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


</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
  <div class="bg-gray-100 rounded shadow flex flex-col p-2 gap-4">
    <FileUpload @refresh-videos="getVideos"/>
    <h3 class="text-base md:text-lg font-medium">Выберите видео</h3>
    <div class="bg-gray-100 rounded shadow h-44 overflow-y-auto">
      <ul>
        <li
          v-for="(video, index) in videos"
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

  <div class="bg-gray-100 rounded shadow p-2 flex flex-col justify-center gap-4">
      <h3 class="block text-lg font-medium text-gray-900 ">Работа с речью</h3>
    <div class="h-full">
      <div class="flex gap-4">
        <div class="flex-1">
          <label for="language" class="block mb-2 text-sm font-medium text-gray-900">На каком языке синтезировать речь?</label>
          <select id="language" v-model="selectedLanguage" class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
            <option v-for="lang in language" :key="lang.code" :value="lang.code">
              {{ lang.name }}
            </option>
          </select>
          <div class="flex flex-1 gap-4 items-center mb-4 mt-4">
            <label for="speechRate" class="text-sm font-medium text-gray-900 mb-1">Скорость</label>
            <input
              id="speechRate"
              type="range"
              min="0.1"
              max="3.0"
              step="0.1"
              v-model="speechRate"
              class="w-32 h-2 rounded-lg appearance-none cursor-pointer bg-gray-300"
            />
            <span class="text-sm text-gray-900">{{ speechRate }}</span>
          </div>
          <button @click="handlePlayUserText"
                  class="px-3 py-1 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">
            Озвучить</button>
        </div>

      </div>

    </div>

    <div class="w-full">
      <button @click="findPhrase"
              class="px-3 py-1 w-full text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">
        Найти фрагмент</button>
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
    <div class="mx-auto" v-if="selectedVideo">
      <video ref="videoPlayer" class="w-full aspect-video bg-gray-900 rounded-lg" controls crossOrigin="anonymous">
        <source :src="videoUrl" type="video/mp4" />
        <track
          label="original"
          kind="subtitles"
          srclang="or"
          :src="originalSubtitleUrl"
          default />
        <track
          label="translate"
          kind="subtitles"
          srclang="tr"
          :src="translatedSubtitleUrl"
          />
      </video>
    </div>
  </div>

  <div v-if="selectedVideo" class="m-4 md:m-8 overflow-x-auto">
    <table class="rounded-lg w-full table-fixed divide-y divide-gray-200 bg-gray-500">
      <thead>
      <tr>
        <th class="w-1/2 px-4 md:px-6 py-3  text-xl font-medium uppercase tracking-wider text-gray-50">Оригинал</th>
        <th class="w-1/2 px-4 md:px-6 py-3 text-xl font-medium uppercase tracking-wider text-gray-50">Перевод</th>
      </tr>
      </thead>
      <tbody class="bg-gray-200 divide-y divide-gray-200">
      <tr
        v-for="(pair, index) in testPairs"
        :key="index"
        class="group hover:bg-gray-300 transition-colors"
      >
        <td class="px-2 md:px-4 py-3 align-top border-r-2 border-gray-200 break-words">
          <div class="flex items-center w-full gap-2">
            <span class="flex-1">{{ pair.original }}</span>
            <button @click="playSynthesizedSpeech(pair.original)">
              {{ '▶' }}
            </button>
          </div>
        </td>
        <td class="px-2 md:px-4 py-3 align-top break-words">
          <div class="flex items-center w-full gap-2">
            <span class="flex-1">{{ pair.translated }}</span>
            <button
              @click="playSynthesizedSpeech(pair.translated)"
            >
              {{  '▶' }}
            </button>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>
