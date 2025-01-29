<script setup>
import { ref } from 'vue';
import axios from 'axios';

    const selectedFile = ref(null);
    const uploadStatus = ref('');

    const handleFileUpload = (event) => {
      selectedFile.value = event.target.files[0];
    };

    const uploadFile = async () => {
      if (!selectedFile.value) {
        uploadStatus.value = 'Файл не выбран!';
        return;
      }

      const formData = new FormData();
      formData.append('file', selectedFile.value);

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
    <input type="file" @change="handleFileUpload" accept="video/*" />
    <button @click="uploadFile">Загрузить видео</button>
    <p v-if="uploadStatus">{{ uploadStaАtus }}</p>
  </div>
</template>

