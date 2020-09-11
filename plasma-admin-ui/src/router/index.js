import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../components/Login.vue'
import Home from '../components/Home.vue'
import Welcome from '../components/Welcome.vue'
import Recipes from '../components/user/Recipes.vue'
import Logins from '../components/user/Logins'
import Records from '../components/user/Records'
import Topics from '../components/topic/Topics'
import Configs from '../components/topic/Configs'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  {
    path: '/home',
    component: Home,
    redirect: '/welcome',
    children: [
      { path: '/welcome', component: Welcome },
      { path: '/recipes', component: Recipes },
      { path: '/logins', component: Logins },
      { path: '/records', component: Records },
      { path: '/topics', component: Topics },
      { path: '/configs', component: Configs }
    ]
  }
]

const router = new VueRouter({
  routes
})
// 挂载路由导航守卫
router.beforeEach((to, from, next) => {
  // to 将要访问的路径
  // from 代表从哪个路径跳转而来
  // next 是一个函数，表示放行
  //     next()  放行    next('/login')  强制跳转

  if (to.path === '/login') return next()
  // 获取cookie
  const myCookie = document.cookie
  // eslint-disable-next-line no-useless-escape
    .replace(/(?:(?:^|.*;\s*)JSESSIONID\s*\=\s*([^;]*).*$)|^.*$/, '$1')
  if (!myCookie) return next('/login')
  next()
})

export default router
