<template>
  <v-dialog v-model="internalVisible" width="70vw">
    <v-card>
      <v-card-title>
        <span class="text-h5">{{ $t('task.form.title') }}</span>
      </v-card-title>
      <v-card-text>
        <v-form v-model="valid">
          <v-container>
            <v-row>
              <v-col cols="12" md="12">
                <v-select
                    v-model="params.type"
                    :items="configs"
                    item-title="name"
                    item-value="code"
                    :label="$t('task.form.type')"
                    @update:model-value="selectTaskType"
                    @update:focused="popupTaskType"
                    required>
                </v-select>
              </v-col>
              <v-col cols="12" md="12">
                <PrivateMessageBox v-if="params.type === 'send_private_messages'">

                </PrivateMessageBox>
                <GroupMessageBox v-else-if="params.type === 'send_group_messages'">

                </GroupMessageBox>
              </v-col>
            </v-row>
          </v-container>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="internalVisible = false">
          Close
        </v-btn>
        <v-btn :disabled="!valid" @click="internalVisible = false">
          Execute
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {ref, watch} from 'vue';
import {useI18n} from "vue-i18n";
import PrivateMessageBox from "@/components/TaskExecutor/PrivateMessageBox.vue";
import GroupMessageBox from "@/components/TaskExecutor/GroupMessageBox.vue";

export default {
  name: "TaskExecutor",
  components: {PrivateMessageBox, GroupMessageBox},
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    app: {
      type: Object,
      default: {}
    },
    user: {
      type: Object,
      default: {}
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
  data() {
    return {
      valid: false,
      params: {
        userId: null,
        type: null,
        priority: null,
        data: null,
        scheduleTime: null,
      },
      configs: [
        {
          key: "login",
          code: "login",
          name: "Login",
          priority: 0,
        },
        {
          key: "logout",
          code: "logout",
          name: "Logout",
          priority: 1,
        },
        {
          key: "sendPrivateMessages",
          code: "send_private_messages",
          name: "Send Private Messages",
          priority: 100,
        }
      ],
    }
  },
  methods: {
    popupTaskType() {
      this.configs.forEach(o => o.name = this.$t(`task.${this.app.id}.${o.key}`))
    },
    selectTaskType(type) {
      console.log('selectTaskType', type)
      // const t = this.configs.filter(o => o.code === type)[0]
      // this.definitions = [...t.definitions]
    },
  }
};
</script>
