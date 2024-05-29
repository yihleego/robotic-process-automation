<template>
  <v-dialog v-model="model" width="60vw" @close="closeTaskDialog">
    <v-card v-if="!task.result.created" class="pa-4">
      <v-card-title>
        <span class="text-h5">{{ $t('task.dialog.title') }}</span>
      </v-card-title>
      <v-card-text>
        <v-form>
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
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn variant="outlined" @click="closeTaskDialog">
          {{ $t('common.close') }}
        </v-btn>
        <v-btn variant="outlined" @click="runTask">
          {{ $t('common.run') }}
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-card v-else :loading="task.result.running" class="text-center align-center pa-4">
      <v-card-title>
        <span class="text-h5">{{ task.result.running ? $t('common.running') : $t('common.result') }}</span>
      </v-card-title>
      <v-card-text class="d-flex justify-center">
        <v-skeleton-loader
            :loading="task.result.running"
            width="280px"
            height="280px"
            type="card"
        >
          <v-responsive>
          <p class="text-h5 text--primary">
            {{ $t(task.result.status) }}
          </p>
          <p class="text--primary pt-2" v-if="task.result.message">
            {{ task.result.message }}
          </p>
          <img
              v-if="task.result.qrcode"
              :src="task.result.qrcode"
              width="280px"
              height="280px"
              class="block"
          />
          <p class="text--primary pt-2" v-if="task.result.result">
            <p v-for="(v, k) in task.result.result">
              {{ k }}: {{ v }}
            </p>
          </p>
          </v-responsive>
        </v-skeleton-loader>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn variant="outlined" @click="closeTaskDialog">
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
import QRCode from 'qrcode'

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
  console.log('selectTaskType', type, task, func)
  func.cur = {...func.list.filter(o => o.appId === props.app.id && o.name === type)[0]}
  task.params.data = {}
}

const runTask = () => {
  let params = {
    userId: props.user.id,
    type: func.cur.name,
    priority: func.cur.priority || 100,
    data: JSON.stringify(task.params.data),
    scheduleTime: null
  }
  console.log('run task', params)
  // create one task, and wait for the result
  api.createTasks({tasks: [params]})
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
  const Status = {
    CREATED: {code: 0, desc: "task.status.created"},
    RUNNING: {code: 1, desc: "task.status.running"},
    DELETED: {code: 2, desc: "task.status.deleted"},
    CANCELLED: {code: 3, desc: "task.status.cancelled"},
    FINISHED: {code: 10, desc: "task.status.finished"},
    FAILED: {code: 11, desc: "task.status.failed"},
    TIMEOUT: {code: 12, desc: "task.status.timeout"},
    OFFLINE: {code: 13, desc: "task.status.offline"},
    TERMINATED: {code: 14, desc: "task.status.terminated"},
    UNSUPPORTED: {code: 15, desc: "task.status.unsupported"},
  }

  const end = () => {
    if (task.result.job) {
      clearInterval(task.result.job)
    }
    task.result.running = false
  }
  const result = task.result
  result.created = true
  result.running = true
  result.qrcode = null
  result.retries = 0
  result.job = setInterval(() => {
    api.getTask(taskId)
        .then((res) => {
          result.retries++
          result.result = res.data.result
          result.message = res.data.message
          result.status = Object.values(Status).filter(o => o.code === res.data.status).map(o => o.desc)[0]

          const status = res.data.status
          if (status === Status.CREATED.code || status === Status.RUNNING.code) {
            console.log('waiting', task)
            if (result.retries > 60) {
              result.message = 'Timeout'
              end()
            }
            return
          }

          if (status === Status.DELETED.code || status === Status.CANCELLED.code) {
            toast.warning('Task has been cancelled')
          } else if (status === Status.FINISHED.code) {
            result.success = true
          } else {
            result.success = false
            toast.error('Failed: ' + result.message)
          }

          if (result.result && isJSON(result.result)) {
            result.result = JSON.parse(result.result)
            if (result.result.qrcode) {
              QRCode.toDataURL(result.result.qrcode)
                  .then(url => {
                    console.log(url)
                    result.qrcode = url
                  })
                  .catch(err => {
                    console.error(err)
                  })
            }
          }

          end()
        })
        .catch((err) => {
          toast.error(err)
          end()
        })
  }, 1000)
}

const closeTaskDialog = () => {
  task.params = {
    userId: null,
    type: null,
    priority: null,
    data: {},
    scheduleTime: null,
  }
  task.result = {
    created: false,
    running: false,
    qrcode: null,
    message: null,
    job: null,
  }
  func.cur = {}
  if (task.result.job) {
    clearInterval(task.result.job)
  }
  model.value = false
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
