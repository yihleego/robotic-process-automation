<template>
  <v-dialog v-model="internalVisible" width="60vw">
    <v-card v-if="!result.created">
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
                    variant="outlined"
                    density="compact"
                    required>
                </v-select>
              </v-col>
            </v-row>
            <MessageBox v-if="params.type === 'send_private_messages'" mode="private" @update:data="params.data = $event">
            </MessageBox>
            <MessageBox v-else-if="params.type === 'send_group_messages'" mode="group" @update:data="params.data = $event">
            </MessageBox>
          </v-container>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn variant="outlined" @click="internalVisible = false">
          {{ $t('common.close') }}
        </v-btn>
        <v-btn variant="outlined" :disabled="!valid" @click="runTask">
          {{ $t('common.execute') }}
        </v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-else :loading="result.running" class="text-center align-center">
      <v-card-title>
        <span class="text-h5">{{ result.running ? 'Running' : 'Result' }}</span>
      </v-card-title>
      <v-card-text>
        <v-skeleton-loader
            v-if="result.running"
            width="280px"
            height="280px"
            type="card">
        </v-skeleton-loader>
        <div v-if="!result.running">
          <v-img
              v-if="result.qrcode"
              :src="result.qrcode"
              max-width="280px"
              max-height="280px">
          </v-img>
          <p class="text-h5 text--primary">
            {{ result.status }}
          </p>
          <p class="text--primary" v-if="result.message">
            {{ result.message }}
          </p>
          <p class="text--primary">
            {{ result.result }}
          </p>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="internalVisible = false; closeTaskResult()">
          {{ $t('common.close') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import {ref, watch} from 'vue';
import {useI18n} from "vue-i18n";
import MessageBox from "@/components/TaskExecutor/MessageBox.vue";
import api from "@/api";

export default {
  name: "TaskExecutor",
  components: {MessageBox},
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
  data() {
    return {
      valid: false,
      params: {
        userId: null,
        type: null,
        priority: null,
        data: {},
        scheduleTime: null,
      },
      result: {
        created: false,
        running: false,
        qrcode: null,
        message: null,
        job: null,
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
        },
        {
          key: "sendGroupMessages",
          code: "send_group_messages",
          name: "Send Group Messages",
          priority: 100,
        }
      ],
    }
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
  methods: {
    created() {
      api.listFuncs()
          .then((res) => {
            this.configs = res.data
          })
          .catch((error) => {
            this.toast(error)
          })
    },
    popupTaskType() {
      this.configs.forEach(o => o.name = this.$t(`task.type.${o.key}`))
    },
    selectTaskType(type) {
      console.log('selectTaskType', type)
      // const t = this.configs.filter(o => o.code === type)[0]
      // this.definitions = [...t.definitions]
    },
    runTask() {
      this.params.appId = this.$props.app.id
      this.params.userId = this.$props.user.id
      this.params.data = JSON.stringify(this.params.data)
      console.log('runTask', this.params)
      api.createTasks({tasks: [this.params]})
          .then((res) => {
            let task = res.data[0]
            this.result = task
            this.showTaskResult(task.id)
          })
          .catch((error) => {
            this.toast(error)
          })
    },
    showTaskResult(taskId) {
      this.result.created = true
      this.result.running = true
      this.result.qrcode = null
      this.result.retries = 0
      this.result.job = setInterval(() => {
        api.getTask(taskId)
            .then((res) => {
              this.result.retries++
              let task = res.data
              let taskType = task.type
              let taskStatus = task.status
              let taskMessage = task.message
              let taskResult = task.result
              if (taskStatus === 0 || taskStatus === 1) {
                // Created, Running
                console.log('Waiting', task);
                if (this.result.retries > 60) {
                  this.result.running = false
                  this.result.message = 'Timeout'
                  clearInterval(this.result.job)
                }
                return
              }
              if (taskStatus === 2 || taskStatus === 3) {
                // Deleted, Cancelled
                this.toast('Task has been cancelled')
              } else if (taskStatus === 10) {
                this.result.success = true
              } else {
                this.result.success = false
                this.toast('Failed to obtain QR code ' + taskMessage)
              }
              this.result.running = false
              this.result.status = taskStatus
              this.result.message = taskMessage
              this.result.result = null
              if (taskResult && this.isJSON(taskResult)) {
                let r = JSON.parse(taskResult);
                this.result.result = r
                if (taskType === 'login' && r.qrcode) {
                  this.result.qrcode = this.createQRCode(r.qrcode, {size: 256})
                }
              }
              clearInterval(this.result.job)
            })
            .catch((error) => {
              this.toast(error)
              clearInterval(this.result.job)
            })
      }, 1000)
    },
    closeTaskResult() {
      if (this.result.job) {
        clearInterval(this.result.job)
      }
      this.result = {
        created: false,
        running: false,
        qrcode: null,
        message: null,
        job: null,
      }
    },
    isJSON(v) {
      if (!v) {
        return true
      }
      try {
        JSON.parse(v)
        return true
      } catch (e) {
        return false
      }
    },
    createQRCode(text, options) {
      options = options || {}
      let typeNumber = options.typeNumber || 0;
      let errorCorrectionLevel = options.errorCorrectionLevel || 'M';
      let size = options.size || 500;
      let mode = options.mode || 'Byte';
      let mb = options.mb || 'UTF-8';
      qrcode.stringToBytes = qrcode.stringToBytesFuncs[mb];
      let qr = qrcode(typeNumber, errorCorrectionLevel);
      qr.addData(text, mode);
      qr.make();
      let moduleCount = qr.getModuleCount();
      let cellSize = size / moduleCount
      let margin = (size - moduleCount * cellSize) / 2
      return qr.createDataURL(cellSize, margin, size);
    }
  }
};
</script>
