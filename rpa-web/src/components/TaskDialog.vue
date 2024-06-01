<template>
  <v-dialog v-model="model" width="60vw">
    <v-card v-if="!task.result.created">
      <v-card-title>
        <span class="text-h5">{{ $t('task.dialog.title') }}</span>
      </v-card-title>
      <v-card-text>
        <v-form v-model="task.valid">
          <v-container>
            <v-row>
              <v-col cols="12" md="12">
                <v-select
                    v-model="task.params.type"
                    :items="func.list"
                    item-title="title"
                    item-value="name"
                    :label="$t('task.dialog.type')"
                    @update:model-value="selectTaskType"
                    @update:focused="popupTaskType"
                    variant="outlined"
                    density="compact"
                    required>
                </v-select>
              </v-col>
            </v-row>
            <TaskForm v-model="task.params.data" :definition="func.cur.param"></TaskForm>
          </v-container>
        </v-form>
      </v-card-text>
      <v-card-actions>
        {{task.params}}
        <v-spacer></v-spacer>
        <v-btn variant="outlined" @click="model=false">
          {{ $t('common.close') }}
        </v-btn>
        <v-btn variant="outlined" :disabled="!task.valid" @click="runTask">
          {{ $t('common.run') }}
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
        <v-btn @click="closeTaskResult">
          {{ $t('common.close') }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import {onMounted, reactive} from "vue";
import {useI18n} from "vue-i18n";
import {toast} from 'vue3-toastify'
import TaskForm from "@/components/TaskForm.vue";
import api from "@/api"

const {t, locale} = useI18n()

const model = defineModel({
  type: Boolean,
  default: false,
})

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

const task = reactive({
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
    retries: 0,
    job: null,
  }
})

const func = reactive({
  cur: {},
  all: [],
  list: [],
})

onMounted(() => {
  api.listFuncs()
      .then((res) => {
        func.all = res.data
      })
      .catch((err) => {
        toast.error(err)
      })
})

const popupTaskType = () => {
  func.list = func.all.filter(o => o.appId === props.app.id)
  func.list.forEach(o => o.title = t(`task.type.${o.name}`));
}

const selectTaskType = (type) => {
  console.log('selectTaskType', type)
  func.cur = func.list.filter(o => o.appId === props.app.id && o.name === type)[0]
  task.params.data = {}
}

const runTask = () => {
  task.params.appId = props.app.id
  task.params.userId = props.user.id
  task.params.data = JSON.stringify(task.params.data)
  console.log('runTask', task.params)
  api.createTasks({tasks: [task.params]})
      .then((res) => {
        let task = res.data[0]
        task.result = task
        showTaskResult(task.id)
      })
      .catch((err) => {
        toast.error(err)
      })
}

const showTaskResult = (taskId) => {
  task.result.created = true
  task.result.running = true
  task.result.qrcode = null
  task.result.retries = 0
  task.result.job = setInterval(() => {
    api.getTask(taskId)
        .then((res) => {
          task.result.retries = task.result.retries + 1
          /*let task = res.data
          let taskType = task.type
          let taskStatus = task.status
          let taskMessage = task.message
          let taskResult = task.result*/
          if (task.status === 0 || task.status === 1) {
            // Created, Running
            console.log('Waiting', task)
            if (task.result.retries > 60) {
              task.result.running = false
              task.result.message = 'Timeout'
              clearInterval(task.result.job)
            }
            return
          }
          if (task.status === 2 || task.status === 3) {
            // Deleted, Cancelled
            toast.warning('Task has been cancelled')
          } else if (task.status === 10) {
            task.result.success = true
          } else {
            task.result.success = false
            toast.error('Failed to obtain QR code ' + task.message)
          }
          task.result.running = false
          task.result.status = task.status
          task.result.message = task.message
          task.result.result = null
          if (task.result && this.isJSON(task.result)) {
            let r = JSON.parse(task.result)
            task.result.result = r
            if (task.type === 'login' && r.qrcode) {
              task.result.qrcode = createQRCode(r.qrcode, {size: 256})
            }
          }
          clearInterval(task.result.job)
        })
        .catch((err) => {
          toast.error(err)
          clearInterval(task.result.job)
        })
  }, 1000)
}

const closeTaskResult = () => {
  if (task.result.job) {
    clearInterval(task.result.job)
  }
  task.result = {
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
</script>
