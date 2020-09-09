import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Wash from '../views/Wash'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/wash',
    name: 'Wash',
    component: Wash
  }
]

const router = new VueRouter({
  routes
})

export default router
