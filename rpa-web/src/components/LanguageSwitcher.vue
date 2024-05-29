<template>
  <v-menu>
    <template v-slot:activator="{ props }">
      <v-btn
          class="align-self-center mr-4"
          v-bind="props"
          variant="text">
        <v-icon>mdi-translate</v-icon>
      </v-btn>
    </template>
    <v-list>
      <v-list-item
          v-for="locale in supportedLocales"
          @click="switchLanguage(locale)"
      >
        {{ $t('locale.' + locale) }}
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script setup>
import {useRouter} from "vue-router"
import Tr from "@/i18n/translation"

const router = useRouter()
const supportedLocales = Tr.supportedLocales

const switchLanguage = async (locale) => {
  const newLocale = locale
  await Tr.switchLanguage(newLocale)
  try {
    await router.replace({params: {locale: newLocale}})
  } catch (err) {
    await router.push("/")
  }
}
</script>