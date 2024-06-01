<template>
  <v-row>
    <v-col cols="12">
      <v-text-field
          v-model="model.target"
          :label="$t('task.message.target')"
          variant="outlined"
          density="compact"
          required>
      </v-text-field>
    </v-col>
  </v-row>
  <v-row v-for="(msg,idx) in model.messages">
    <v-col cols="2">
      <v-select
          v-model="msg.type"
          :items="types"
          item-title="name"
          item-value="value"
          :label="$t('task.message.type')"
          @update:focused="popupMessageType"
          variant="outlined"
          density="compact"
          required>
      </v-select>
    </v-col>
    <v-col cols="9">
      <v-text-field
          v-model="msg.content"
          :label="$t('task.message.content')"
          variant="outlined"
          density="compact"
          required>
      </v-text-field>
    </v-col>
    <v-col cols="1">
      <v-btn
          icon="mdi-close-circle-outline"
          color="red"
          @click="delLine(idx)"
      ></v-btn>
    </v-col>
  </v-row>
  <v-btn
      icon="mdi-plus-circle-outline"
      @click="addLine"
  ></v-btn>
  <v-row>
  </v-row>
</template>

<script setup>
import {onMounted, watch} from "vue"
import {useI18n} from "vue-i18n";

const {t, locale} = useI18n()

const model = defineModel({
  type: Object,
  required: true,
  default: () => ({
    target: '',
    messages: [],
  })
})

const props = defineProps({
  definition: {
    type: Object,
    required: false,
  },
})

console.log('TaskForm', model, props)

/*const form = ref({
  target: '',
  messages: [],
})*/

onMounted(() => {
  // form.value = props.definition
})

watch(() => props.definition, (newValue, oldValue) => {
  console.log('TaskForm', newValue)
  model.value = {
    target: '',
    messages: [{type: 'text', content: ''}],
  }
})

watch(model, (newValue, oldValue) => {
  console.log('TaskForm', newValue)
})

/*watch(form, (newValue, oldValue) => {
  emit('update:modelValue', newValue)
})*/

const popupMessageType = () => {
  // types.forEach(o => o.name = t(`task.message.types.${o.value}`))
}

const addLine = () => {
  console.log(model)
  if (!model.value.messages) {
    model.value.messages = []
  }
  model.value.messages.push({type: 'text', content: ''});
}

const delLine = (idx) => {
  if (model.messages.length <= 1) {
    return
  }
  console.log(idx)
  model.messages.splice(idx, 1)
}
</script>

