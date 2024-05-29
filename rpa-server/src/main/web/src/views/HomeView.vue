<template>
  <v-app>
    <v-app-bar flat>
      <v-container class="mx-auto d-flex align-center justify-center">
        <v-avatar
            class="me-4 "
            color="grey-darken-1"
            size="32"
        ></v-avatar>

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
              <v-list rounded="lg">
                <v-list-item
                    v-for="app in app.list"
                    :key="app.id"
                    :title="app.name"
                    class="text-center"
                    link
                ></v-list-item>
              </v-list>
            </v-sheet>
          </v-col>

          <v-col cols="10">
            <v-sheet
                min-height="70vh"
                rounded="lg"
            >
              <v-data-table
                  :items="user.list"
                  :server-items-length="user.total"
                  :loading="user.loading" loading-text="Loading..."
              >
              </v-data-table>
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
import LanguageSwitcher from "@/components/LanguageSwitcher.vue";
import {useI18n} from 'vue-i18n'
import api from "@/api/api"

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
      query: {},
      headers: [
        {title: 'User Id', key: 'id', sortable: true, queryable: false, align: 'start'},
        {title: 'Avatar', key: 'avatar', sortable: false, queryable: false},
        {title: 'Account', key: 'account', sortable: true, queryable: true},
        {title: 'Nickname', key: 'nickname', sortable: false, queryable: true},
        {title: 'Realname', key: 'realname', sortable: false, queryable: true},
        {title: 'Company', key: 'company', sortable: false, queryable: true},
        {title: 'Phone', key: 'phone', sortable: false, queryable: true},
        {title: 'Status', key: 'status', sortable: false, queryable: false},
        {title: 'Created Time', key: 'createdTime', sortable: true, queryable: true},
        {title: 'Updated Time', key: 'updatedTime', sortable: true, queryable: true},
        {title: 'Actions', key: 'actions', sortable: false, queryable: false, align: 'center'},
      ],
      options: {
        itemsPerPage: 10,
        multiSort: false,
        mustSort: false,
        page: 1,
        sortBy: ['createdTime'],
        sortDesc: [true],
      },
    },
  }),
  created() {
    this.listApps()
  },
  methods: {
    listApps() {
      let params = {
        page: 1,
        size: 100,
        sort: 'name:ASC',
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