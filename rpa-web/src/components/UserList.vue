<template>

  <v-card flat>
    <v-container>
      <v-row>
        <v-col v-for="header in user.headers.filter(o => o.queryable)">
          <v-text-field
              v-model="user.query[header.key]"
              :label="header.title"
              variant="outlined"
              density="compact"
              clearable>
          </v-text-field>
        </v-col>
        <v-col class="align-center text-center">
          <v-btn
              @click="listUsers">
            {{ $t('common.search') }}
          </v-btn>
        </v-col>
        <v-col class="align-center text-center">
          <v-btn
              @click="popupUserDialog">
            {{ $t('common.add') }}
          </v-btn>
        </v-col>
      </v-row>
    </v-container>

    <v-data-table-server
        :headers="user.headers.filter(o => o.visible)"
        :items="user.list"
        :items-length="user.total"
        :loading="user.loading"
        loading-text="Loading..."
        @update:options="listUsers"
        :page.sync="user.options.page"
        :items-per-page.sync="user.options.itemsPerPage"
        :items-per-page-options.sync="user.options.itemsPerPageOptions"
        :sort-by.sync="user.options.sortBy"
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
    </v-data-table-server>

  </v-card>

  <TaskDialog v-model="task.visible" :app="task.app" :user="task.user"></TaskDialog>

  <v-dialog v-model="user.dialog" width="60vw" @close="closeAppDialog">
    <v-card class="pa-4">
      <v-card-title>
        <span class="text-h5">{{ $t('common.add') }}</span>
      </v-card-title>
      <v-card-text>
        <v-form>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="user.form.account"
                  :label="$t(`user.account`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="user.form.nickname"
                  :label="$t(`user.nickname`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="user.form.realname"
                  :label="$t(`user.realname`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="user.form.company"
                  :label="$t(`user.company`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="user.form.phone"
                  :label="$t(`user.phone`)"
                  required="true"
                  variant="outlined"
                  density="compact">
              </v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="12">
              <v-text-field
                  v-model="user.form.avatar"
                  :label="$t(`user.avatar`)"
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
import {onMounted, reactive, ref, watch} from "vue";
import {useI18n} from "vue-i18n";
import {toast} from 'vue3-toastify'
import TaskDialog from "@/components/TaskDialog.vue";
import api from "@/api"

const {t, locale} = useI18n()

const props = defineProps({
  app: {
    required: true,
    default: () => ({id: null})
  }
})

const user = reactive({
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
    pages: 0,
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
  dialog: false,
  form: {
    account: '',
    nickname: '',
    realname: '',
    company: '',
    phone: '',
    avatar: '',
  },
})

const task = ref({
  app: null,
  user: null,
  visible: false,
})

onMounted(() => {
  user.headers.forEach(o => o.title = t(`user.${o.key}`))
})

watch(locale, () => {
  user.headers.forEach(o => o.title = t(`user.${o.key}`))
})

watch(() => props.app, () => {
  listUsers({})
})

const listUsers = ({page, itemsPerPage, sortBy}) => {
  const app = props.app
  if (!app) {
    return
  }
  let params = {
    appIds: app.id,
    page: page || user.options.page,
    size: itemsPerPage || user.options.itemsPerPage,
    sort: (sortBy || user.options.sortBy).map(o => `${o.key}:${o.order}`).join(','),
    ...user.query,
  };
  user.loading = true
  api.listUsers(params)
      .then((res) => {
        user.list = res.data.list
        user.total = res.data.total
      })
      .catch((err) => {
        toast.error(err)
      })
      .finally(() => {
        user.loading = false
      })
}

const popupTaskDialog = (user) => {
  task.value = {
    app: props.app,
    user: user,
    visible: true,
  }
}

const popupUserDialog = () => {
  user.dialog = true
}
</script>