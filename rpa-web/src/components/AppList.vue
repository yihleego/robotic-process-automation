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

    <v-divider class="my-2"></v-divider>

    <v-list-item
        :title="$t('common.add')"
        link
        @click="popupAppDialog"
    >
      <template v-slot:prepend>
        <v-avatar size="25" rounded="0">
          <v-icon>mdi-plus</v-icon>
        </v-avatar>
      </template>
    </v-list-item>
  </v-list>

  <v-dialog v-model="app.dialog" width="60vw" @close="closeAppDialog">
    <v-card class="pa-4">
      <v-card-title>
        <span class="text-h5">{{ $t('common.add') }}</span>
      </v-card-title>
      <v-card-text>
        <v-form>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="app.form.name"
                  :label="$t(`app.name`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="app.form.logo"
                  :label="$t(`app.logo`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn variant="outlined" @click="closeAppDialog">
          {{ $t('common.close') }}
        </v-btn>
        <v-btn variant="outlined" @click="">
          {{ $t('common.add') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {toast} from 'vue3-toastify'
import api from "@/api"

const emit = defineEmits(['update'])

const cur = ref({
  id: null,
})

const app = reactive({
  list: [],
  total: 0,
  form: {
    name: '',
    logo: '',
  },
  dialog: false,
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
        app.list = res.data.list
        app.total = res.data.total
        selectApp(app.list[0])
      })
      .catch((err) => {
        console.error(err)
        toast.error(err)
      })
}

const popupAppDialog = () => {
  app.dialog = true
}

const closeAppDialog = () => {
  app.dialog = false
}

</script>