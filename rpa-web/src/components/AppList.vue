<template>
  <v-list density="compact">
    <v-list-subheader>{{ $t('app.list') }}</v-list-subheader>
    <v-list-item
        v-for="app in app.list"
        :key="app.id"
        :value="app.name"
        :active="app.id === cur.id"
        @click="selectApp(app)"
    >
      <template v-slot:prepend>
        <v-avatar :image="app.logo" size="25" rounded="0"></v-avatar>
      </template>

      <v-list-item-title v-text="$t(`app.${app.id}`)"></v-list-item-title>
    </v-list-item>
  </v-list>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {toast} from 'vue3-toastify'
import api from "@/api"

const emit = defineEmits(['update'])

const cur = ref({
  id: null,
})

const app = ref({
  list: [],
  total: 0,
})

onMounted(() => {
  listApps()
})

const selectApp = (app) => {
  cur.value = app
  emit('update', app)
}

const listApps = () => {
  let params = {
    page: 1,
    size: 100,
    sort: 'createdTime:ASC',
  }
  api.listApps(params)
      .then((res) => {
        app.value = {
          list: res.data.list,
          total: res.data.total,
        }
        selectApp(app.value.list[0])
      })
      .catch((err) => {
        console.error(err)
        toast.error(err)
      })
}
</script>