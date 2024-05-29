<template>
  <v-app>
    <v-app-bar flat>
      <v-container class="mx-auto d-flex align-center justify-center">
        <v-avatar
            class="me-4 "
            size="32"
        >
          <v-img src="/logo.png" alt="RPA"></v-img>
        </v-avatar>

        <v-btn
            v-for="link in links"
            :key="link.title"
            :text="$t(link.title)"
            :href="link.url"
            variant="text"
        ></v-btn>

        <v-spacer></v-spacer>

        <LanguageSwitcher/>

      </v-container>
    </v-app-bar>

    <v-main class="bg-grey-lighten-3">
      <v-container>
        <v-row>
          <v-col cols="2">
            <v-sheet rounded="lg">
              <v-list density="compact">
                <v-list-subheader>{{ $t('app.list') }}</v-list-subheader>
                <v-list-item
                    v-for="app in app.list"
                    :key="app.id"
                    :value="app.name"
                    @click="selectApp(app)"
                >
                  <template v-slot:prepend>
                    <v-icon :class="'icon-'+app.id"></v-icon>
                  </template>

                  <v-list-item-title v-text="$t(`app.${app.id}`)"></v-list-item-title>
                </v-list-item>
              </v-list>
            </v-sheet>
          </v-col>

          <v-col cols="10">
            <v-sheet
                min-height="70vh"
                rounded="lg"
            >
              <v-card
                  flat
              >
                <template v-slot:text>
                  <v-container>
                    <v-row>
                      <v-col
                          v-for="header in user.headers.filter(o => o.queryable)"
                      >
                        <v-text-field
                            v-model="user.query[header.key]"
                            :label="header.title"
                            variant="outlined"
                            density="compact"
                            clearable>
                        </v-text-field>
                      </v-col>
                      <v-col>
                        <v-btn @click="listUsers">{{ $t('common.search') }}</v-btn>
                      </v-col>
                    </v-row>
                  </v-container>
                </template>

                <v-data-table
                    :headers="user.headers.filter(o => o.display)"
                    :items="user.list"
                    :server-items-length="user.total"
                    :loading="user.loading"
                    loading-text="Loading..."
                    @update:options="listUsers"
                    :options.sync="user.options"
                    :items-per-page-options="user.options.itemsPerPageOptions"
                    locale
                >
                  <template v-slot:item.avatar="{ item }">
                    <v-avatar size="25">
                      <v-img :src="item.avatar" :alt="item.nickname"></v-img>
                    </v-avatar>
                  </template>
                  <template v-slot:item.actions="{ item }">
                    <v-icon @click.stop="task.dialog=true; task.user=item; task.app=app">
                      mdi-robot
                    </v-icon>
                  </template>
                </v-data-table>
              </v-card>
            </v-sheet>
          </v-col>
        </v-row>

        <v-snackbar v-model="snackbar.display">
          {{ snackbar.content }}
          <template v-slot:action="{ attrs }">
            <v-btn color="pink" text v-bind="attrs" @click="snackbar.display = false">
              {{ t('toast.close') }}
            </v-btn>
          </template>
        </v-snackbar>

      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import {useI18n} from 'vue-i18n'
import LanguageSwitcher from "@/components/LanguageSwitcher.vue";
import api from "@/api/api"
import '../style.css'

export default {
  components: {LanguageSwitcher},
  setup() {
    const {t, locale} = useI18n()
    return {t, locale}
  },
  data: () => ({
    links: [
      {title: "nav.title", url: "https://github.com/yihleego/robotic-process-automation"},
      {title: "nav.doc", url: "https://github.com/yihleego/robotic-process-automation/README.md"},
      {title: "nav.issues", url: "https://github.com/yihleego/robotic-process-automation/issues"},
    ],
    snackbar: {
      content: "",
      display: false
    },
    curApp: null,
    app: {
      list: [],
      total: 0,
    },
    user: {
      list: [],
      total: 0,
      query: {
        account: null,
        nickname: null,
        realname: null,
        company: null,
      },
      headers: [
        {title: 'user.id', key: 'id', display: true, sortable: true, queryable: false, align: 'start'},
        {title: 'user.avatar', key: 'avatar', display: true, sortable: false, queryable: false},
        {title: 'user.account', key: 'account', display: true, sortable: true, queryable: true},
        {title: 'user.nickname', key: 'nickname', display: true, sortable: false, queryable: true},
        {title: 'user.realname', key: 'realname', display: true, sortable: false, queryable: true},
        {title: 'user.company', key: 'company', display: true, sortable: false, queryable: true},
        {title: 'user.phone', key: 'phone', display: true, sortable: false, queryable: false},
        {title: 'user.status', key: 'status', display: false, sortable: false, queryable: false},
        {title: 'user.createdTime', key: 'createdTime', display: true, sortable: true, queryable: false},
        {title: 'user.updatedTime', key: 'updatedTime', display: true, sortable: true, queryable: false},
        {title: 'user.actions', key: 'actions', display: true, sortable: false, queryable: false, align: 'center'},
      ],
      options: {
        page: 1,
        itemsPerPage: 10,
        itemsPerPageOptions: [
          {value: 10, title: '10'},
          {value: 25, title: '25'},
          {value: 50, title: '50'},
          {value: 100, title: '100'},
        ],
        multiSort: false,
        sortBy: ['createdTime'],
        sortDesc: [true],
      },
    },
  }),
  created() {
    this.listApps()
    this.user.headers.forEach(o => o.title = this.$t(`user.${o.key}`))
  },
  watch: {
    locale() {
      this.user.headers.forEach(o => o.title = this.$t(`user.${o.key}`))
    },
  },
  methods: {
    selectApp(app) {
      this.curApp = app
      this.listUsers()
    },
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
            this.curApp = this.app.list[0]
            this.listUsers()
          })
          .catch((error) => {
            this.toast(error)
          })
    },
    listUsers() {
      if (!this.curApp) {
        return
      }
      this.user.loading = true
      let params = {
        appIds: this.curApp.id,
        page: this.user.options.page,
        size: this.user.options.itemsPerPage
      }
      api.listUsers(params)
          .then((res) => {
            this.user.list = res.data.list
            this.user.total = res.data.total
          })
          .catch((error) => {
            this.toast(error)
          })
          .finally(() => {
            this.user.loading = false
          })
    },
    listEnums() {
      /*axios.get(`/enums`)
          .then((response) => {
              let result = response.data
              if (!result.success) {
                  throw result.message
              }
              this.constants = result.data
              for (let key in this.constants) {
                  let values = this.constants[key]
                  let map = {}
                  for (let i in values) {
                      map[values[i].key] = values[i].value
                  }
                  this.enums[key] = map
              }
          })
          .catch((error) => {
              this.toast(error)
          })*/
    },
    listTaskTypes() {
      /*axios.get(`/tasks/types`)
          .then((response) => {
              let result = response.data
              if (!result.success) {
                  throw result.message
              }
              this.taskTypes = result.data
              let map = {}
              for (let appId in this.taskTypes) {
                  let values = this.taskTypes[appId]
                  for (let i in values) {
                      if (!map[appId]) {
                          map[appId] = {}
                      }
                      map[appId][values[i].key] = values[i].value
                  }
                  this.enums['TaskType'] = map
              }
          })
          .catch((error) => {
              this.toast(error)
          })*/
    },
    toast(message) {
      this.toast.content = message
      this.toast.display = true
    },
  }
}
</script>