<template>
  <v-app>
    <NavBar></NavBar>
    <v-main class="bg-grey-lighten-3">
      <v-container>
        <v-row>
          <v-col cols="2">
            <v-sheet rounded="lg">
              <AppList @update="selectApp"></AppList>
            </v-sheet>
          </v-col>
          <v-col cols="10">
            <v-sheet rounded="lg" min-height="70vh">
              <UserList :app="curApp"></UserList>
            </v-sheet>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import {useI18n} from 'vue-i18n'
import NavBar from "@/components/NavBar.vue";
import AppList from "@/components/AppList.vue";
import UserList from "@/components/UserList.vue";
import TaskDialog from "@/components/TaskDialog.vue";

export default {
  components: {NavBar, AppList, UserList, TaskDialog},
  setup() {
    const {t, locale} = useI18n()
    return {t, locale}
  },
  data: () => ({
    curApp: null,
  }),
  created() {
  },
  methods: {
    selectApp(app) {
      this.curApp = app
    },
  }
}
</script>