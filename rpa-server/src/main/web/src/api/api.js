import axios from "axios";

const URL_PREFIX = 'http://localhost:8080'

export default {
  listApps: function (params) {
    return axios.get(`${URL_PREFIX}/apps`, {params})
      .then((resp) => {
        const res = resp.data
        if (res.success) {
          return res
        } else {
          throw res.message
        }
      })
  },
  listUsers: function (params) {
    return axios.get(`${URL_PREFIX}/users`, {params})
      .then((resp) => {
        let res = resp.data
        if (res.success) {
          return res
        } else {
          throw res.message
        }
      })
  }
}
