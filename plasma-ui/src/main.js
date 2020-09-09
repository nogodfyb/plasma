import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './plugins/vant'
import axios from 'axios'
axios.defaults.baseURL = 'http://192.168.191.1:8084/plasma/'
// axios.defaults.baseURL = '/plasma/'
// 允许携带cookie
axios.defaults.withCredentials = true
// 响应拦截器
axios.interceptors.response.use(res => {
  return res.data
}, error => {
  Vue.prototype.$toast.fail(error.message)
})
Vue.prototype.$http = axios
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
