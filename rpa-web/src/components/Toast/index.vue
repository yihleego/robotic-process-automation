<template>
  <v-snackbar v-model="internalVisible" :timeout="timeout">
    {{ message }}
    <template v-slot:actions>
      <v-btn color="pink" @click="internalVisible = false">
        {{ $t('common.close') }}
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>
import {ref, watch} from 'vue';
import {useI18n} from "vue-i18n";

export default {
  name: "Toast",
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    message: {
      type: String,
      default: null
    },
    timeout: {
      type: Number,
      default: 2000
    },
  },
  setup(props, {emit}) {
    const {t, locale} = useI18n()
    const internalVisible = ref(props.visible);

    watch(props, (newProps) => {
      internalVisible.value = newProps.visible;
    });

    watch(internalVisible, (newValue) => {
      emit('update:visible', newValue);
    });

    return {t, locale, internalVisible};
  },
};
</script>
