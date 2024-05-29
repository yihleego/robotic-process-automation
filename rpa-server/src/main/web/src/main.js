import {createApp} from 'vue/dist/vue.esm-bundler'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

import App from './App.vue'
import router from './router'
import i18n from "./i18n"
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

const vuetify = createVuetify({
  components,
  directives,
})

createApp(App)
  .use(router)
  .use(i18n)
  .use(vuetify)
  .mount('#app')