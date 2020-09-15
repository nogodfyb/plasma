<template>
  <div class="home">
    <van-icon name="setting-o" @click="showSetIpDiglog" size="20" style="margin-left: 300px"/>
    <h3>请扫工号</h3>
    <van-form @submit="onSubmit">
      <van-field
        v-model="loginForm.bn"
        name="工号"
        label="工号"
        placeholder="工号"
        autocomplete="off"
        ref="field"
      />
    </van-form>
    <!-- 输入密码对话框  -->
    <van-dialog v-model="show" title="设置服务器地址" show-cancel-button @confirm="onConfirm" @close="onClose">
      <van-field
        v-model="password"
        name="密码" type="password"
        label="密码"
        placeholder="密码"  autocomplete="off"
      />
      <van-field
        v-model="ip"
        name="ip"
        label="ip"
        placeholder="ip"  autocomplete="off"
      />
    </van-dialog>
  </div>
</template>

<script>

export default {
  name: 'Home',
  mounted () {
    this.$refs.field.focus()
  },
  data () {
    return {
      loginForm: {
        bn: ''
      },
      show: false,
      password: '',
      ip: ''
    }
  },
  created () {
    const baseURL = window.localStorage.getItem('baseURL')
    if (baseURL !== null) {
      this.$http.defaults.baseURL = baseURL
    }
  },
  methods: {
    // 扫码登录
    async onSubmit () {
      if (!(this.validte(this.loginForm.bn))) {
        this.loginForm.bn = ''
        return this.$toast.fail('登录失败')
      }
      const res = await this.$http.post('user/login', this.loginForm)
      if (res.status !== 200) {
        this.loginForm.bn = ''
        return this.$toast.fail('登录失败')
      }
      await this.$router.push('/wash')
    },
    validte (code) {
      const code1 = code[0]
      const regCode1 = /^[A-Z]$/
      if (!regCode1.test(code1)) {
        return false
      }
      const code2 = code.substr(1)
      const regCode2 = /^[0-9]{4}$/
      if (!regCode2.test(code2)) {
        return false
      }
      return true
    },
    onConfirm () {
      if (this.password === 'admin5566' && this.ip !== '') {
        this.$http.defaults.baseURL = 'http://' + this.ip + ':8084/plasma'
        this.password = ''
        window.localStorage.setItem('baseURL', this.$http.defaults.baseURL)
        return this.$toast.success('设置服务器ip地址成功!')
      } else {
        this.password = ''
        return this.$toast.fail('设置服务器地址失败!')
      }
    },
    onClose () {
      this.password = ''
    },
    showSetIpDiglog () {
      this.show = true
    }
  }
}
</script>
<style scoped>
  .home{
    margin-top: 10%;
  }
</style>
