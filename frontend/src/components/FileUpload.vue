<script setup>
import { ref } from 'vue';
import axios from 'axios';

  const selectedFileName = ref("");
  const selectedFile = ref(null);
  const uploadStatus = ref('');


    const handleFileUpload = (event) => {
      selectedFile.value = event.target.files[0];
      selectedFileName.value = event.target.files[0].name;
    };

    const uploadFile = async () => {
      if (!selectedFile.value) {
        uploadStatus.value = 'Файл не выбран!';
        return;
      }

      const formData = new FormData();
      formData.append('file', selectedFile.value);
      formData.append('language', 'RU_RU');
      formData.append('languageTranslate', 'EN');

      try {
        const response = await axios.post('http://localhost:8080/video/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: progressEvent => {
            console.log(`Uploaded ${Math.round((progressEvent.loaded / progressEvent.total) * 100)}%`);
          }
        });
        uploadStatus.value = 'Файл успешно загружен!';
        console.log(response.data);
      } catch (error) {
        uploadStatus.value = 'Ошибка загрузки: ' + error.message;
        console.error(error);
      }
    };


</script>

<template>
  <div>
    <h3 class="block mb-2 text-lg font-medium text-gray-900 ">Загрузка видео</h3>
    <div class="relative">
      <div class="relative w-full">
        <input
          type="file"
          id="file_input"
          class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
          @change="handleFileUpload"
          accept="video/*"
        />

        <div class="flex items-center justify-between border border-gray-300 rounded-lg p-2 bg-gray-50 dark:bg-gray-500 cursor-pointer">
          <span class="text-gray-500 dark:text-white text-sm cursor-pointer overflow-hidden whitespace-nowrap text-ellipsis">{{ selectedFileName || 'Выберите файл...' }}</span>
          <button
            :disabled="!selectedFile"
            @click="uploadFile"
            class="px-3 py-1 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10"
          >
            Загрузить
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

