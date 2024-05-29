<template>
  <v-row v-for="(item, idx) in form">
    <v-col cols="12">
      <TaskFormItem v-model="form[idx]"></TaskFormItem>
    </v-col>
  </v-row>
</template>

<script setup>
import {ref, watch} from "vue"
import {useI18n} from "vue-i18n";
import TaskFormItem from "@/components/TaskFormItem.vue";

const {t, locale} = useI18n()

const model = defineModel({
  type: Object,
  required: true,
  default: () => ({})
})

const props = defineProps({
  definition: {
    type: Object,
    required: false,
  },
})

const form = ref([])

watch(() => props.definition, (newValue, oldValue) => {
  console.log('definition changed', newValue, oldValue)
  form.value = props.definition ? JSON.parse(props.definition) : []
})

watch(() => form, (newValue, oldValue) => {
  const convert = (values) => {
    const data = {}
    for (let i in values) {
      const v = values[i]
      if (v.type !== 'array') {
        // console.log('simple', JSON.stringify(v))
        data[v.key] = v.value
      } else if (v.value) {
        // console.log('array', JSON.stringify(v))
        data[v.key] = v.value.map(o => convert(o))
      }
    }
    return data
  }
  model.value = convert(form.value)
  console.log('form changed', newValue, oldValue, model.value)
}, {deep: true})
</script>
