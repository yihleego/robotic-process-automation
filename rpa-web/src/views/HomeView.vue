<template>
  <v-app>
    <NavBar></NavBar>

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
                    :active="app.id === curApp.id"
                    @click="selectApp(app)"
                >
                  <template v-slot:prepend>
                    <v-avatar :image="app.logo" size="25" rounded="0"></v-avatar>
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
                      <v-col
                          class="align-center text-center"
                      >
                        <v-btn
                            @click="listUsers">
                          {{ $t('common.search') }}
                        </v-btn>
                      </v-col>
                    </v-row>
                  </v-container>
                </template>

                <v-data-table
                    :headers="user.headers.filter(o => o.visible)"
                    :items="user.list"
                    :server-items-length="user.total"
                    :loading="user.loading"
                    loading-text="Loading..."
                    @update:options="listUsers"
                    :page.sync="user.options.page"
                    :items-per-page.sync="user.options.itemsPerPage"
                    :items-per-page-options.sync="user.options.itemsPerPageOptions"
                    :sort-by.sync="user.options.sortBy"
                    locale
                >
                  <template v-slot:item.account="{ item }">
                    <v-avatar size="25">
                      <v-img :src="item.avatar" :alt="item.nickname"></v-img>
                    </v-avatar>
                    {{ item.account }}
                  </template>
                  <template v-slot:item.actions="{ item }">
                    <v-icon @click.stop="popupTaskDialog(item)">
                      mdi-robot
                    </v-icon>
                  </template>
                </v-data-table>
              </v-card>
            </v-sheet>
          </v-col>
        </v-row>

        <TaskExecutor v-bind="task" @update:visible="task.visible = $event"/>

        <Toast v-bind="toast" @update:visible="toast.visible = $event"/>

      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import {useI18n} from 'vue-i18n'
import NavBar from "@/components/NavBar/index.vue";
import TaskExecutor from "@/components/TaskExecutor/index.vue";
import Toast from "@/components/Toast/index.vue";
import api from "@/api"

export default {
  components: {NavBar, TaskExecutor, Toast},
  setup() {
    const {t, locale} = useI18n()
    return {t, locale}
  },
  data: () => ({
    toast: {
      visible: false,
      message: "123",
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
        {title: 'Id', key: 'id', sortable: true, queryable: false, visible: true},
        {title: 'Account', key: 'account', sortable: true, queryable: true, visible: true},
        {title: 'Nickname', key: 'nickname', sortable: false, queryable: true, visible: true},
        {title: 'Realname', key: 'realname', sortable: false, queryable: true, visible: true},
        {title: 'Company', key: 'company', sortable: false, queryable: true, visible: true},
        {title: 'Phone', key: 'phone', sortable: false, queryable: true, visible: true},
        {title: 'Status', key: 'status', sortable: false, queryable: false, visible: false},
        {title: 'Created Time', key: 'createdTime', sortable: true, queryable: false, visible: true},
        {title: 'Updated Time', key: 'updatedTime', sortable: true, queryable: false, visible: true},
        {title: 'Actions', key: 'actions', sortable: false, queryable: false, visible: true},
      ],
      options: {
        page: 1,
        itemsPerPage: 10,
        itemsPerPageOptions: [
          {value: 10, title: '10'},
          {value: 20, title: '20'},
          {value: 50, title: '50'},
          {value: 100, title: '100'},
        ],
        sortBy: [
          {key: "createdTime", order: "desc"},
        ],
      },
    },
    task: {
      app: null,
      user: null,
      visible: false,
      configs: [],
      definitions: []
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
      this.$toast.()
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
            this.selectApp(this.curApp)
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
        size: this.user.options.itemsPerPage,
        sort: this.user.options.sortBy.map(o => `${o.key}:${o.order}`).join(','),
        ...this.user.query
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
    popupTaskDialog(user) {
      this.task.visible = true
      this.task.user = user
      this.task.app = this.curApp
      console.log('configs', this.task)
    },
    toast(message) {
      this.toast.content = message
      this.toast.visible = true
    },
  }
}
</script>