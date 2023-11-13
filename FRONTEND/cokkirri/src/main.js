import { createApp } from 'vue'
import App from './App.vue'
import router from './routes/index.js'
import store from "./store/index.js"
import axios from 'axios'
import PrimeVue from 'primevue/config';

const app = createApp(App)
app.use(PrimeVue)
app.use(store)
app.use(router)
app.mount('#app')
app.config.globalProperties.$http = axios

