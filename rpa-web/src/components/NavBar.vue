<template>
  <v-app-bar flat>
    <v-container class="mx-auto d-flex align-center justify-center">
      <v-avatar class="me-4 " size="32">
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

      <LanguageSwitcher></LanguageSwitcher>

    </v-container>
  </v-app-bar>
</template>

<script>
import {useI18n} from 'vue-i18n'
import LanguageSwitcher from "@/components/LanguageSwitcher.vue"

export default {
  name: "NavBar",
  components: {LanguageSwitcher},
  setup() {
    const {t, locale} = useI18n()
    return {t, locale}
  },
  data() {
    return {
      links: [
        {title: "nav.title", url: "https://github.com/yihleego/robotic-process-automation"},
        {title: "nav.doc", url: "https://github.com/yihleego/robotic-process-automation/README.md"},
        {title: "nav.issues", url: "https://github.com/yihleego/robotic-process-automation/issues"},
      ],
    }
  }
}
</script>