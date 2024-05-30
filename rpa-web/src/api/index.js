import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL.replace(/\/$/, '')

export default {
  listApps: (params) => {
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
  createUser: (params) => {

  },
  updateUser: (params) => {

  },
  listUsers: (params) => {
    return axios.get(`${API_BASE_URL}/users`, {params})
      .then((resp) => {
        let res = resp.data
        if (res.success) {
          return res
        } else {
          throw res.message
        }
      })
  },
  getTask: () => {

  },
}
