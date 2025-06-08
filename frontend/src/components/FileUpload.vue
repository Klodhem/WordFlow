<script setup>
import {onMounted, ref} from 'vue';
import apiClient from '@/axios.js';

const emit = defineEmits(['refresh-videos']);

const selectedFileName = ref("");
const selectedFile = ref(null);

const selectedLanguage = ref(null);
const selectedTranslateLanguage = ref(null);
const language = ref([]);
const languageTranslate = ref([]);
const videoName = ref('')


const handleFileUpload = (event) => {
  selectedFile.value = event.target.files[0];
  selectedFileName.value = event.target.files[0].name;
};

const uploadFile = async () => {
  if (!selectedFile.value) {
    return;
  }

  const formData = new FormData();
  formData.append('file', selectedFile.value);
  formData.append('language', selectedLanguage.value);
  formData.append('languageTranslate', selectedTranslateLanguage.value);
  formData.append('videoName', videoName.value);

  try {
    await apiClient.post('/videos', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: progressEvent => {
        console.log(`Загружено ${Math.round((progressEvent.loaded / progressEvent.total) * 100)}%`);
      }
    });
    emit('refresh-videos');
  } catch (error) {
    console.error(error);
  }
};


onMounted(async () => {
  try {
    const [lang, langTranslate] = await Promise.all([
      fetch('/data/language.json').then(r => r.json()),
      fetch('/data/language_translate.json').then(r => r.json())
    ])

    language.value = lang;
    languageTranslate.value = langTranslate;
  } catch (e) {
    console.error('Ошибка загрузки языков:', e)
  }

  selectedLanguage.value = language.value[0].code || null;
  selectedTranslateLanguage.value = languageTranslate.value[0].code || null;
});

</script>

<template>
  <h3 class="block text-lg font-medium text-gray-900 ">Загрузка видео</h3>
  <div class="flex gap-4">
    <div class="flex-1">
      <label for="language" class="block mb-2 text-sm font-medium text-gray-900">Какой язык
        распознать?</label>
      <select id="language" v-model="selectedLanguage"
              class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
        <option v-for="lang in language" :key="lang.code" :value="lang.code">
          {{ lang.name }}
        </option>
      </select>
    </div>
    <div class="flex-1">
      <label for="languageTranslate" class="block mb-2 text-sm font-medium text-gray-900">Перевод
        субтитров на</label>
      <select id="languageTranslate" v-model="selectedTranslateLanguage"
              class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
        <option v-for="lang in languageTranslate" :key="lang.code" :value="lang.code">
          {{ lang.name }}
        </option>
      </select>
    </div>

  </div>

  <div>

    <div class="relative">
      <div class="relative w-full">
        <div
          class="grid grid-cols-1 md:grid-cols-[1fr_auto_auto] gap-2 items-center justify-between border border-gray-300 rounded-lg p-2 bg-gray-50 dark:bg-gray-500">
          <label for="file_input" class="flex-1 cursor-pointer overflow-hidden">
            <span
              class="block text-gray-500 dark:text-white text-sm whitespace-nowrap text-ellipsis overflow-hidden min-w-32">
              {{ selectedFileName || 'Выберите файл...' }}
            </span>
          </label>

          <input
            type="file"
            id="file_input"
            class="hidden"
            @change="handleFileUpload"
            accept="video/*"
          />

          <input
            type="text"
            name="videoName"
            id="videoName"
            v-model="videoName"
            class="max-w-80 w-80 bg-gray-50 border text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 p-1"
            placeholder="Название видео"
            required
          />

          <button
            :disabled="!selectedFile || !videoName.trim()"
            @click="uploadFile"
            class="px-3 py-1 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm"
          >
            Загрузить
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

