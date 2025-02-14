<script setup>
import FileUpload from "@/components/FileUpload.vue";
import Video from "@/components/Video.vue";
import {ref} from "vue";
import axios from "axios";

const userText = ref('')

const videos = ref([
  { id: 1, title: "Видео 1" },
  { id: 2, title: "Видео 2" },
  { id: 3, title: "Видео 3" },
  { id: 4, title: "Видео 4" },
  { id: 5, title: "Видео 5" },
  { id: 6, title: "Видео 6" },
  { id: 7, title: "Видео 7" }
])

const selectedVideo = ref(null)

const selectVideo = async video => {
  selectedVideo.value = video
  try {
    const response = await axios.post('http://your-backend-url/api/video/select', {
      videoId: video.id
    })
    console.log("Ответ от бекенда:", response.data)
  } catch (error) {
    console.error("Ошибка запроса:", error)
  }
}

const testPairs = ref([
  {
    en: "Hello world!",
    ru: "Привет, мир!"
  },
  {
    en: "The weather is beautiful today",
    ru: "Сегодня прекрасная погода"
  },
  {
    en: "How are you?",
    ru: "Как дела?"
  }
]);

async function playSynthesizedSpeech(text) {
  try {
    const response = await axios.get('http://localhost:8080/speech/synthesize', {
      params: { text },
      responseType: 'blob'
    })
    const audioBlob = new Blob([response.data], { type: 'audio/wav' })
    const audioUrl = URL.createObjectURL(audioBlob)
    // Используем объект Audio, т.к. на странице нет аудиоплеера
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

</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
  <div class="bg-gray-100 rounded shadow p-2 flex flex-col gap-4">
    <FileUpload />
    <h3 class="text-base md:text-lg font-medium">Выберите видео</h3>
    <div class="bg-gray-100 rounded shadow h-56 overflow-y-auto">
      <ul>
        <li
          v-for="(video, index) in videos"
          :key="index"
          @click="selectVideo(video)"
          class="p-2 hover:bg-gray-200 cursor-pointer rounded text-sm md:text-base"
          :class="{'bg-gray-300': selectedVideo && selectedVideo.id === video.id}"
        >
          {{ video.title }}
        </li>
      </ul>
    </div>
  </div>

  <div class="bg-gray-100 rounded shadow p-2 flex flex-col justify-center">
    <div class="h-full">
      <h3 class="block mb-2 text-lg font-medium text-gray-900 ">Поиск фрагмента</h3>

      <div class="bg-gray-100 p-2 flex flex-row">
        <form class="">
          <select id="countries" class="bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block w-full p-2.5">
            <option selected>Голос</option>
            <option value="US">Woman</option>
            <option value="CA">Men</option>
          </select>
        </form>

        <button @click="handlePlayUserText"
                class="px-3 py-1 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">
          Озвучить</button>
      </div>
    </div>
    <div class="w-full">
            <textarea
              v-model="userText"
              id="message"
              rows="4"
              class="resize-none w-full block p-2.5 text-gray-100 bg-gray-600 rounded-lg border
            border-gray-300 text-sm md:text-base h-56"
              placeholder="Введите текст..."></textarea>
    </div>
  </div>
  </div>



  <div class="p-8 md:p-16">
    <Video></Video>
  </div>

  <div class="m-4 md:m-8 overflow-x-auto">
    <table class="w-full table-fixed divide-y divide-gray-200 bg-gray-500">
      <thead>
      <tr>
        <th class="w-1/2 px-4 md:px-6 py-3 text-left text-xl font-medium uppercase tracking-wider">Оригинал</th>
        <th class="w-1/2 px-4 md:px-6 py-3-left text-xl font-medium uppercase tracking-wider">Перевод</th>
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
            <span class="flex-1">{{ pair.en }}</span>
            <button @click="playSynthesizedSpeech(pair.en)">
              {{ '▶' }}
            </button>
          </div>
        </td>
        <td class="px-2 md:px-4 py-3 align-top break-words">
          <div class="flex items-center w-full gap-2">
            <span class="flex-1">{{ pair.ru }}</span>
            <button
              @click="playSynthesizedSpeech(pair.ru)"
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

