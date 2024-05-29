<template>
  <v-menu bottom>
    <template v-slot:activator="{ props }">
      <v-btn
          class="align-self-center mr-4"
          v-bind="props"

          variant="text">
        <v-icon>mdi-translate</v-icon>
      </v-btn>
    </template>
    <v-list>
      <v-list-item v-for="locale in supportedLocales"
                   @click="switchLanguage(locale)">
        {{ $t('locale.' + locale) }}
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
import {useI18n} from 'vue-i18n'
import {useRouter} from "vue-router"
import Tr from "@/i18n/translation"

export default {
  setup() {
    const {t, locale} = useI18n()

    const supportedLocales = Tr.supportedLocales

    const router = useRouter()

    const switchLanguage = async (locale) => {
      const newLocale = locale

      await Tr.switchLanguage(newLocale)

      try {
        await router.replace({params: {locale: newLocale}})
      } catch (e) {
        console.log(e)
        router.push("/")
      }
    }

    return {t, locale, supportedLocales, switchLanguage}
  }
}
</script>