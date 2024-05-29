import {createVuetify} from 'vuetify'
import {en, zhHans} from 'vuetify/locale'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

export default createVuetify({
  components,
  directives,
  locale: {
    locale: import.meta.env.VITE_DEFAULT_LOCALE,
    fallback: import.meta.env.VITE_DEFAULT_LOCALE,
    messages: {zhHans, en},
  },
})