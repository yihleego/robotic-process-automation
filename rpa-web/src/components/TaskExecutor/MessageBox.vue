<template>

  <v-row>
    <v-col cols="12">
      <v-text-field
          v-model="data.target"
          :label="$t('task.message.target')"
          variant="outlined"
          density="compact"
          required>
      </v-text-field>
    </v-col>
  </v-row>
  <v-row v-for="(msg,idx) in data.messages">
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

<script>
import {watch} from "vue";

export default {
  props: {
    mode: {
      type: String,
      default: "private"
    }
  },
  data() {
    return {
      data: {
        target: '',
        messages: [],
      },
      types: [
        {value: "text", name: "Text"},
        {value: "image", name: "Image"},
        {value: "video", name: "Video"},
        {value: "file", name: "File"},
      ]
    }
  },
  created() {
    if (this.$props.mode === 'group') {
      this.types.push({value: "mention", name: "Mention"})
    }
    watch(this.data, (newValue) => {
      this.$emit('update:data', newValue);
    });
    this.addLine()
    this.popupMessageType()
  },
  methods: {
    addLine() {
      this.data.messages.push({type: 'text', content: ''})
    },
    delLine(idx) {
      if (this.data.messages.length <= 1) {
        return
      }
      console.log(idx)
      this.data.messages.splice(idx, 1)
    },
    popupMessageType() {
      this.types.forEach(o => o.name = this.$t(`task.message.types.${o.value}`))
    },
  }
};
</script>

