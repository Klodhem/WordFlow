<script setup>
import {getCurrentInstance, onMounted, ref} from 'vue';
const { proxy } = getCurrentInstance();

  const emit = defineEmits(['refresh-videos']);

  const selectedFileName = ref("");
  const selectedFile = ref(null);
  const uploadStatus = ref('');

  const selectedLanguage = ref(null);
  const selectedTranslateLanguage = ref(null);
  const language = ref([]);
  const languageTranslate = ref([]);



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
      formData.append('language', selectedLanguage.value);
      formData.append('languageTranslate', selectedTranslateLanguage.value);

      try {
        const response = await proxy.$axios.post('http://localhost:8080/video/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: progressEvent => {
            console.log(`Загружено ${Math.round((progressEvent.loaded / progressEvent.total) * 100)}%`);
          }
        });
        uploadStatus.value = 'Файл успешно загружен!';
        console.log(response.data);
        emit('refresh-videos');
      } catch (error) {
        uploadStatus.value = 'Ошибка загрузки: ' + error.message;
        console.error(error);
      }
    };


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

  languageTranslate.value = [
    { code: "RU", name: "Русский (по умолчанию)" },
    { code: "AF", name: "Африкаанс" },
    { code: "AM", name: "Амхарский" },
    { code: "AR", name: "Арабский" },
    { code: "AZ", name: "Азербайджанский" },
    { code: "BA", name: "Башкирский" },
    { code: "BE", name: "Белорусский" },
    { code: "BG", name: "Болгарский" },
    { code: "BN", name: "Бенгальский" },
    { code: "BS", name: "Боснийский" },
    { code: "CA", name: "Каталанский" },
    { code: "CEB", name: "Себуанский" },
    { code: "CS", name: "Чешский" },
    { code: "CV", name: "Чувашский" },
    { code: "CY", name: "Валлийский" },
    { code: "DA", name: "Датский" },
    { code: "DE", name: "Немецкий" },
    { code: "EL", name: "Греческий" },
    { code: "EN", name: "Английский" },
    { code: "EO", name: "Эсперанто" },
    { code: "ES", name: "Испанский" },
    { code: "ET", name: "Эстонский" },
    { code: "EU", name: "Баскский" },
    { code: "FA", name: "Персидский" },
    { code: "FI", name: "Финский" },
    { code: "FR", name: "Французский" },
    { code: "GA", name: "Ирландский" },
    { code: "GD", name: "Шотландский (гэльский)" },
    { code: "GL", name: "Галисийский" },
    { code: "GU", name: "Гуджарати" },
    { code: "HE", name: "Иврит" },
    { code: "HI", name: "Хинди" },
    { code: "HR", name: "Хорватский" },
    { code: "HT", name: "Гаитянский" },
    { code: "HU", name: "Венгерский" },
    { code: "HY", name: "Армянский" },
    { code: "ID", name: "Индонезийский" },
    { code: "IS", name: "Исландский" },
    { code: "IT", name: "Итальянский" },
    { code: "JA", name: "Японский" },
    { code: "JV", name: "Яванский" },
    { code: "KA", name: "Грузинский" },
    { code: "KAZLAT", name: "Казахский (латиница)" },
    { code: "KK", name: "Казахский" },
    { code: "KM", name: "Кхмерский" },
    { code: "KN", name: "Каннада" },
    { code: "KO", name: "Корейский" },
    { code: "KY", name: "Киргизский" },
    { code: "LA", name: "Латынь" },
    { code: "LB", name: "Люксембургский" },
    { code: "LO", name: "Лаосский" },
    { code: "LT", name: "Литовский" },
    { code: "LV", name: "Латышский" },
    { code: "MG", name: "Малагасийский" },
    { code: "MHR", name: "Марийский" },
    { code: "MI", name: "Маори" },
    { code: "MK", name: "Македонский" },
    { code: "ML", name: "Малаялам" },
    { code: "MN", name: "Монгольский" },
    { code: "MR", name: "Маратхи" },
    { code: "MRJ", name: "Горномарийский" },
    { code: "MS", name: "Малайский" },
    { code: "MT", name: "Мальтийский" },
    { code: "MY", name: "Бирманский" },
    { code: "NE", name: "Непальский" },
    { code: "NL", name: "Нидерландский" },
    { code: "NO", name: "Норвежский" },
    { code: "OS", name: "Осетинский" },
    { code: "PA", name: "Панджаби" },
    { code: "PAP", name: "Папьяменто" },
    { code: "PL", name: "Польский" },
    { code: "PT", name: "Португальский" },
    { code: "PT_BR", name: "Португальский (бразильский)" },
    { code: "RO", name: "Румынский" },
    { code: "SAH", name: "Якутский" },
    { code: "SI", name: "Сингальский" },
    { code: "SK", name: "Словацкий" },
    { code: "SL", name: "Словенский" },
    { code: "SQ", name: "Албанский" },
    { code: "SR", name: "Сербский" },
    { code: "SR_LATN", name: "Сербский (латиница)" },
    { code: "SU", name: "Сунданский" },
    { code: "SV", name: "Шведский" },
    { code: "SW", name: "Суахили" },
    { code: "TA", name: "Тамильский" },
    { code: "TE", name: "Телугу" },
    { code: "TG", name: "Таджикский" },
    { code: "TH", name: "Тайский" },
    { code: "TL", name: "Тагальский" },
    { code: "TR", name: "Турецкий" },
    { code: "TT", name: "Татарский" },
    { code: "UDM", name: "Удмуртский" },
    { code: "UK", name: "Украинский" },
    { code: "UR", name: "Урду" },
    { code: "UZ", name: "Узбекский" },
    { code: "UZBCYR", name: "Узбекский (кириллица)" },
    { code: "VI", name: "Вьетнамский" },
    { code: "XH", name: "Коса" },
    { code: "YI", name: "Идиш" },
    { code: "ZH", name: "Китайский" },
    { code: "ZU", name: "Зулу" }
  ];

  selectedLanguage.value = language.value[0].code || null;
  selectedTranslateLanguage.value = languageTranslate.value[0].code || null;
});

</script>

<template>
  <h3 class="block text-lg font-medium text-gray-900 ">Загрузка видео</h3>
  <div class="flex gap-4">
    <div class="flex-1">
      <label for="language" class="block mb-2 text-sm font-medium text-gray-900">Какой язык распознать?</label>
      <select id="language" v-model="selectedLanguage" class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
        <option v-for="lang in language" :key="lang.code" :value="lang.code">
          {{ lang.name }}
        </option>
      </select>
    </div>
    <div class="flex-1">
      <label for="languageTranslate" class="block mb-2 text-sm font-medium text-gray-900">Перевод на</label>
      <select id="languageTranslate" v-model="selectedTranslateLanguage" class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
        <option v-for="lang in languageTranslate" :key="lang.code" :value="lang.code">
          {{ lang.name }}
        </option>
      </select>
    </div>

  </div>

  <div>

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

