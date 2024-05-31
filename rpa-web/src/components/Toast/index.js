import {toast} from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

export default {
  install(app) {
    app.config.globalProperties.$toast = toast
  },
}
