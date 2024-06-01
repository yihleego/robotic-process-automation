<template>
  <v-number-input
      v-if="model.type === 'number'"
      v-model="model.value"
      :label="$t(`task.key.${model.key}`)"
      :required="model.required"
      variant="outlined"
      density="compact"
  ></v-number-input>
  <v-text-field
      v-else-if="model.type === 'string'"
      v-model="model.value"
      :label="$t(`task.key.${model.key}`)"
      :required="model.required"
      variant="outlined"
      density="compact">
  </v-text-field>
  <v-select
      v-else-if="model.type === 'select'"
      v-model="model.value"
      :items="model.options"
      item-title="title"
      item-value="value"
      :label="$t(`task.key.${model.key}`)"
      :required="model.required"
      variant="outlined"
      density="compact">
  </v-select>
  <v-select
      v-else-if="model.type === 'multiple'"
      multiple
      v-model="model.value"
      :items="model.options"
      item-title="title"
      item-value="value"
      :label="$t(`task.key.${model.key}`)"
      :required="model.required"
      variant="outlined"
      density="compact">
  </v-select>
  <div v-else-if="model.type === 'array'">
    <v-row v-for="item in model.value">
      <v-col v-for="sub in item">
        <TaskFormItem :model-value="sub"></TaskFormItem>
      </v-col>
      <v-col cols="1">
        <v-btn
            icon="mdi-close-circle-outline"
            color="red"
            @click="delLine(item)"
        ></v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-btn
          icon="mdi-plus-circle-outline"
          @click="addLine()"
      ></v-btn>
    </v-row>
  </div>
</template>

<script setup>
import {useI18n} from "vue-i18n"

const {t, locale} = useI18n()

const model = defineModel({
  type: Object,
  required: false,
  default: () => ({})
})

const addLine = () => {
  if (!model.value.value) {
    model.value.value = []
  }
  // children are templates, deep copy them to value
  model.value.value.push(JSON.parse(JSON.stringify(model.value.children)))
}

const delLine = (item) => {
  if (model.value.value.length <= 1) {
    return
  }
  const idx = model.value.value.indexOf(item)
  model.value.value.splice(idx, 1)
}

if (model.value.type === 'array') {
  // add one default line if it is an array and empty
  if (!model.value.value) {
    model.value.value = []
  }
  if (model.value.value.length === 0) {
    addLine()
  }
} else if (model.value.type === 'select' || model.value.type === 'multiple') {
  // translate options
  model.value.options.forEach(o => o.title = t(`task.key.${o.value}`))
}
</script>

