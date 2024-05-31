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

<script>
import {useI18n} from 'vue-i18n'
import api from "@/api"

export default {
  name: "AppList",
  setup() {
    const {t, locale} = useI18n()
    return {t, locale}
  },
  created() {
    this.listApps()
  },
  data() {
    return {
      cur: null,
      app: {
        list: [],
        total: 0,
      },
    }
  },
  methods: {
    listApps() {
      let params = {
        page: 1,
        size: 100,
        sort: 'createdTime:ASC',
      }
      api.listApps(params)
          .then((res) => {
            this.app.list = res.data.list
            this.app.total = res.data.total
            this.selectApp(this.app.list[0])
          })
          .catch((err) => {
            this.$toast.error(err)
          })
    },
    selectApp(app) {
      this.cur = app
      this.$emit('update', app)
    },
  }
}
</script>