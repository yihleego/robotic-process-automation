import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

console.log('API_BASE_URL', API_BASE_URL)

export default {
  listApps: function (params) {
    return axios.get(`${API_BASE_URL}/apps`, {params})
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
    return axios.get(`${API_BASE_URL}/users`, {params})
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
