<template>
  <v-dialog v-model="model" width="60vw">
    <v-card v-if="!task.result.created">
      <v-card-title>
        <span class="text-h5">{{ $t('task.form.title') }}</span>
      </v-card-title>
      <v-card-text>
        <v-form v-model="task.valid">
          <v-container>
            <v-row>
              <v-col cols="12" md="12">
                <v-select
                    v-model="task.params.type"
                    :items="funcs.filter(o => o.appId === app.id)"
                    item-title="title"
                    item-value="name"
                    :label="$t('task.form.type')"
                    @update:model-value="selectTaskType"
                    @update:focused="popupTaskType"
                    variant="outlined"
                    density="compact"
                    required>
                </v-select>
              </v-col>
            </v-row>
            <MessageBox v-if="task.params.type === 'send_private_messages'" mode="private" @update:data="task.params.data = $event">
            </MessageBox>
            <MessageBox v-else-if="task.params.type === 'send_group_messages'" mode="group" @update:data="task.params.data = $event">
            </MessageBox>
          </v-container>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn variant="outlined" @click="model=false">
          {{ $t('common.close') }}
        </v-btn>
        <v-btn variant="outlined" :disabled="!task.valid" @click="runTask">
          {{ $t('common.execute') }}
        </v-btn>
      </v-card-actions>
    </v-card>
    <v-card v-else :loading="task.result.running" class="text-center align-center">
      <v-card-title>
        <span class="text-h5">{{ task.result.running ? 'Running' : 'Result' }}</span>
      </v-card-title>
      <v-card-text>
        <v-skeleton-loader
            v-if="task.result.running"
            width="280px"
            height="280px"
            type="card">
        </v-skeleton-loader>
        <div v-if="!task.result.running">
          <v-img
              v-if="task.result.qrcode"
              :src="task.result.qrcode"
              max-width="280px"
              max-height="280px">
          </v-img>
          <p class="text-h5 text--primary">
            {{ task.result.status }}
          </p>
          <p class="text--primary" v-if="task.result.message">
            {{ task.result.message }}
          </p>
          <p class="text--primary">
            {{ task.result.result }}
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

<script setup>
import {onMounted, ref} from "vue";
import {useI18n} from "vue-i18n";
import {toast} from 'vue3-toastify'
import MessageBox from "@/components/MessageBox.vue"
import api from "@/api"

const {t, locale} = useI18n()

const model = defineModel()

const props = defineProps({
  app: {
    type: Object,
    default: () => ({id: null}),
  },
  user: {
    type: Object,
    default: () => ({id: null}),
  },
})

const task = ref({
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
  }
})

const configs = ref([
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
])

const funcs = ref([])

const popupTaskType = () => {
  // configs.value.forEach(o => o.name = t(`task.type.${o.key}`))
  funcs.value.forEach(o => o.title = t(`task.type.${o.name}`))
}

const selectTaskType = (type) => {
  console.log('selectTaskType', type)
}

const runTask = () => {
  task.value.params.appId = props.app.id
  task.value.params.userId = props.user.id
  task.value.params.data = JSON.stringify(task.value.params.data)
  console.log('runTask', task.value.params)
  api.createTasks({tasks: [task.value.params]})
      .then((res) => {
        let task = res.data[0]
        task.value.result = task
        showTaskResult(task.id)
      })
      .catch((err) => {
        toast.error(err)
      })
}

const showTaskResult = (taskId) => {
  task.value.result.created = true
  task.value.result.running = true
  task.value.result.qrcode = null
  task.value.result.retries = 0
  task.value.result.job = setInterval(() => {
    api.getTask(taskId)
        .then((res) => {
          task.value.result.retries++
          let task = res.data
          let taskType = task.type
          let taskStatus = task.status
          let taskMessage = task.message
          let taskResult = task.result
          if (taskStatus === 0 || taskStatus === 1) {
            // Created, Running
            console.log('Waiting', task)
            if (task.value.result.retries > 60) {
              task.value.result.running = false
              task.value.result.message = 'Timeout'
              clearInterval(task.value.result.job)
            }
            return
          }
          if (taskStatus === 2 || taskStatus === 3) {
            // Deleted, Cancelled
            toast.warning('Task has been cancelled')
          } else if (taskStatus === 10) {
            task.value.result.success = true
          } else {
            task.value.result.success = false
            toast.error('Failed to obtain QR code ' + taskMessage)
          }
          task.value.result.running = false
          task.value.result.status = taskStatus
          task.value.result.message = taskMessage
          task.value.result.result = null
          if (taskResult && this.isJSON(taskResult)) {
            let r = JSON.parse(taskResult)
            task.value.result.result = r
            if (taskType === 'login' && r.qrcode) {
              task.value.result.qrcode = createQRCode(r.qrcode, {size: 256})
            }
          }
          clearInterval(task.value.result.job)
        })
        .catch((err) => {
          toast.error(err)
          clearInterval(task.value.result.job)
        })
  }, 1000)
}

const closeTaskResult = () => {
  if (task.value.result.job) {
    clearInterval(task.value.result.job)
  }
  task.value.result = {
    created: false,
    running: false,
    qrcode: null,
    message: null,
    job: null,
  }
}

const isJSON = (v) => {
  if (!v) {
    return true
  }
  try {
    JSON.parse(v)
    return true
  } catch (e) {
    return false
  }
}

const createQRCode = (text, options) => {
  options = options || {}
  let typeNumber = options.typeNumber || 0
  let errorCorrectionLevel = options.errorCorrectionLevel || 'M'
  let size = options.size || 500
  let mode = options.mode || 'Byte'
  let mb = options.mb || 'UTF-8'
  qrcode.stringToBytes = qrcode.stringToBytesFuncs[mb]
  let qr = qrcode(typeNumber, errorCorrectionLevel)
  qr.addData(text, mode)
  qr.make()
  let moduleCount = qr.getModuleCount()
  let cellSize = size / moduleCount
  let margin = (size - moduleCount * cellSize) / 2
  return qr.createDataURL(cellSize, margin, size)
}

onMounted(() => {
  api.listFuncs()
      .then((res) => {
        funcs.value = res.data
      })
      .catch((err) => {
        toast.error(err)
      })
})
</script>
