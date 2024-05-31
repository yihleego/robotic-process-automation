<template>
  <v-snackbar
      :timeout="timeout"
      :color="color"
      v-model="active"
      class="application"
      @click="closeable">
    <v-icon
        dark
        left
        v-if="icon.length > 0">
      {{ icon }}
    </v-icon>
    {{ text }}
  </v-snackbar>
</template>

<script>
export default {
  data() {
    return {
      active: false,
      text: '',
      icon: '',
      color: 'info',
      timeout: 3000,
      closeable: true,
    }
  },
  methods: {
    show(options = {}) {
      if (this.active) {
        this.close()
        this.$nextTick(() => this.show(options))
        return
      }
      Object.keys(options).forEach(field => (this[field] = options[field]))
      this.active = true
    },
    close() {
      this.active = false
    },
  },
}
</script>