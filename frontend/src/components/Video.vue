<script setup>
// import { ref, reactive } from 'vue';
//
// const videoPlayer = ref(null);
// const isPlaying = ref(false);
//
// const videoSource = new URL('@/assets/my-video.mp4', import.meta.url).href;
//
// const subtitles = reactive([
//   {
//     src: new URL('@/assets/subtitles-en.vtt', import.meta.url).href,
//     label: 'English 🇺🇸',
//     srclang: 'en',
//     default: true,
//   },
//   {
//     src: new URL('@/assets/subtitles-ru.vtt', import.meta.url).href,
//     label: 'Русский 🇷🇺',
//     srclang: 'ru',
//     default: false,
//   },
// ]);
//
// const togglePlay = () => {
//   if (!videoPlayer.value) return;
//   videoPlayer.value.paused ? videoPlayer.value.play() : videoPlayer.value.pause();
// };
//
// const changeSubtitle = (event) => {
//   if (!videoPlayer.value) return;
//   const selectedIndex = event.target.value;
//
//   Array.from(videoPlayer.value.textTracks).forEach((track, index) => {
//     track.mode = selectedIndex === 'off'
//       ? 'hidden'
//       : (index === Number(selectedIndex) ? 'showing' : 'hidden');
//   });
// };
</script>

<template>
  <div class="mx-auto">
    <video class="w-full aspect-video bg-gray-900" controls>
      <source :src="videoSource" type="video/mp4" />
    </video>
  </div>
</template>

<style scoped>

</style>
