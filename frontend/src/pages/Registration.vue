<script setup>
import {reactive, ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const router = useRouter()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const typeUser = ref(1);

const errors = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
});

const registrationForm = async () => {
  errors.username = username.value ? '' : 'Введите имя пользователя';
  errors.email = email.value ? '' : 'Введите email';
  errors.password = password.value ? '' : 'Введите пароль';
  errors.confirmPassword = confirmPassword.value ? '' : 'Введите пароль повторно';

  if (errors.username || errors.email || errors.password || errors.confirmPassword) return;

  try {
    if (password.value !== confirmPassword.value) {
      alert('Пароли не совпадают!');
      return;
    }
    let response;

    if (typeUser.value === 1) {
      response = await axios.post('http://localhost:8080/auth/registration', {
        username: username.value,
        email: email.value,
        password: password.value
      });
    } else if (typeUser.value === 2) {
      response = await axios.post('http://localhost:8080/auth/registrationTeacher', {
        username: username.value,
        email: email.value,
        password: password.value
      });
    }

    const token = response.data['jwt-token'];
    if (!token) {
      console.error('Токен отсутствует в ответе сервера');
      return;
    }

    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirationTime = payload.exp * 1000;
    localStorage.setItem('authToken', token);
    localStorage.setItem('tokenExpiration', expirationTime);
    await router.push({name: 'Home'})
  } catch (err) {
    console.error(err)
    alert('Ошибка регистрации')
  }
}
</script>

<template>
  <section class="mb-16">
    <div class="flex flex-col items-center justify-center px-6 py-8 mx-auto lg:py-0">
      <div class="w-full bg-white rounded-lg shadow md:mt-0 sm:max-w-md xl:p-0 ">
        <div class="p-6 space-y-4 md:space-y-6 sm:p-8">
          <h1 class="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl ">
            Регистрация
          </h1>
          <div>
            <label for="username" class="block mb-2 text-sm font-medium text-gray-900 ">Кто вы?</label>
            <select v-model="typeUser" class="w-full bg-gray-50 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
              <option :value="1">Студент</option>
              <option :value="2">Преподаватель</option>
            </select>
          </div>
          <form novalidate class="space-y-4 md:space-y-6" @submit.prevent="registrationForm">
            <div>
              <label for="username" class="block mb-2 text-sm font-medium text-gray-900 ">Имя пользователя</label>
              <input type="text" name="username" id="username" v-model="username"
                     :class="errors.username ? 'border-red-500' : 'border-gray-300'"
                     class="bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder=". . ." required="">
              <p v-if="errors.username" class="text-red-500 text-sm mt-1">
                {{ errors.username }}
              </p>
            </div>
            <div class="mb-4">
              <label for="email" class="block mb-2 text-sm font-medium text-gray-900 ">Email</label>
              <input type="email" id="email" v-model="email"
                     :class="errors.email ? 'border-red-500' : 'border-gray-300'"
                     class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 " placeholder="name@company.com" required />
              <p v-if="errors.email" class="text-red-500 text-sm mt-1">
                {{ errors.email }}
              </p>
            </div>
            <div class="mb-4">
              <label for="password" class="block mb-2 text-sm font-medium text-gray-900 ">Пароль</label>
              <input type="password" id="password" v-model="password"
                     :class="errors.password ? 'border-red-500' : 'border-gray-300'"
                     class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 " placeholder="•••••••••" required />
              <p v-if="errors.password" class="text-red-500 text-sm mt-1">
                {{ errors.password }}
              </p>
            </div>
            <div class="mb-4">
              <label for="confirm_password" class="block mb-2 text-sm font-medium text-gray-900 ">Повторите пароль</label>
              <input type="password" id="confirm_password" v-model="confirmPassword"
                     :class="errors.confirmPassword ? 'border-red-500' : 'border-gray-300'"
                     class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 " placeholder="•••••••••" required />
              <p v-if="errors.confirmPassword" class="text-red-500 text-sm mt-1">
                {{ errors.confirmPassword }}
              </p>
            </div>
            <button type="submit" class="w-full text-gray-50 border-gray-700 bg-gray-700 hover:bg-gray-800 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">Зарегистрироваться</button>
            <p class="text-sm font-light text-gray-500">
              У вас уже есть аккаунт? <a href="/login" class="font-medium text-primary-600 hover:underline">Войти</a>
            </p>
          </form>
        </div>
      </div>
    </div>
  </section>
</template>
