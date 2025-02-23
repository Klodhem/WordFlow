import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    const expirationTime = localStorage.getItem('tokenExpiration');

    if (!token || !expirationTime || Date.now() >= parseInt(expirationTime)) {
      console.log('Токен истек, выполняем выход...');
      removeToken();
      window.location.href = '/Login';
      return Promise.reject(new Error('Токен истек'));
    }

    config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      console.log('Ошибка авторизации, выполняем выход...');
      removeToken();
      window.location.href = '/Login';
    }
    return Promise.reject(error);
  }
);

function removeToken() {
  localStorage.removeItem('authToken');
  localStorage.removeItem('tokenExpiration');
  console.log('Токен удален.');
}

export default apiClient;
