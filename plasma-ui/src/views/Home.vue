<template>
  <div class="home">
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
      }
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
    }
  }
}
</script>
<style scoped>
  .home{
    margin-top: 50%;
  }
</style>
