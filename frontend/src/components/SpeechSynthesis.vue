<script setup>
import apiClient from '@/axios.js';
import {onMounted, ref} from "vue";

const speechRate = ref(1.0);
const selectedLanguage = ref(null);
const language = ref([]);

let emit = defineProps(['userText']);

const handlePlayUserText = () => {
  if (!emit.userText.trim()) {
    console.error('Введите текст!')
    return
  }
  playSynthesizedSpeech(emit.userText)
}

async function playSynthesizedSpeech(text) {
  try {
    const response = await apiClient.get('/speech/synthesize', {
      params: {
        text: text,
        language: selectedLanguage.value,
        speechRate: speechRate.value
      },
      responseType: 'blob'
    })
    const audioBlob = new Blob([response.data], {type: 'audio/wav'})
    const audioUrl = URL.createObjectURL(audioBlob)
    const audio = new Audio(audioUrl)
    await audio.play()
  } catch (error) {
    console.error('Ошибка синтеза речи:', error)
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
</script>

<template>
  <div class="h-full">
    <div class="flex gap-4">
      <div class="flex-1">
        <label for="language" class="block mb-2 text-sm font-medium text-gray-900">На каком языке
          синтезировать речь?</label>
        <select id="language" v-model="selectedLanguage"
                class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
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
                :disabled="!emit.userText"
                class="px-3 py-1 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">
          Озвучить
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
