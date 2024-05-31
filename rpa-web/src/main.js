import {createApp} from 'vue/dist/vue.esm-bundler'
import App from './App.vue'
import router from './router'
import i18n from "./i18n"
import vuetify from "./vuetify"
import {toast} from 'vue3-toastify'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import 'vue3-toastify/dist/index.css'

createApp(App)
  .use(router)
  .use(i18n)
  .use(vuetify)
  .use({
    install(app) {
      app.config.globalProperties.$toast = toast
    }
  })
  .mount('#app')