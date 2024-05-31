import {createApp} from 'vue/dist/vue.esm-bundler'
import App from './App.vue'
import router from './router'
import i18n from "./i18n"
import vuetify from "./vuetify"
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

createApp(App)
  .use(router)
  .use(i18n)
  .use(vuetify)
  .use(toast)
  .mount('#app')