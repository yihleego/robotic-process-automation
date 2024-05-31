// src/plugins/toast.js
import { createApp } from 'vue';
import ToastComponent from '../components/Toast.vue';
import toast from '@/components/Toast/index.vue'

let toastInstance = null;

const createToast = (message) => {
  if (!toastInstance) {
    const toastApp = createApp(ToastComponent);
    const mountNode = document.createElement('div');
    document.body.appendChild(mountNode);
    toastInstance = toastApp.mount(mountNode);
  }
  toastInstance.showToast(message);
};

export default {
  install(app) {
    app.config.globalProperties.$toast = createToast;
  }
};
