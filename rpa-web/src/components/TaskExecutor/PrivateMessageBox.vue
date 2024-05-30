<template>
  <v-textarea
      v-model="params.data"
      :label="def.name"
      auto-grow
      optional>
  </v-textarea>
  <v-select
      v-model="task.params.type"
      :items="child.options"
      item-title="name"
      item-value="value"
      :label="child.name"
      required>
  </v-select>
  <v-textarea
      v-model="params.data"
      :label="def.name"
      auto-grow
      optional>
  </v-textarea>
</template>

<script>
export default {};
</script>
