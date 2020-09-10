<template>
    <div>
      <van-nav-bar
        title="清洗"
        left-text="回到首页"
        right-text="切换删除模式"
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
      />
      <van-form @submit="onSubmit" :submit-on-enter="false">
        <van-field
          v-model="currentWaferSource"
          name="扫描wafer"
          label="扫描wafer" ref="field"
          placeholder="wafer" @keyup.enter="onEnter" autocomplete="off"
        />
        <div style="margin: 16px;">
          <van-button round block type="info" native-type="submit">
            提交
          </van-button>
        </div>
      </van-form>
      <van-cell style="font-size: 12px;font-weight: bold">
        <van-row>
          <van-col span="12">plasma程序:</van-col>
          <van-col span="12" v-if="currentPlasma===''">???</van-col>
          <van-col span="12" v-if="currentPlasma!==''">{{currentPlasma}}</van-col>
        </van-row>
      </van-cell>
      <van-cell style="font-size: 15px;font-weight: bold">
        <van-row>
          <van-col span="8">device型号</van-col>
          <van-col span="8">waferSource</van-col>
          <van-col span="8">waferLot</van-col>
        </van-row>
      </van-cell>
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
      >
        <van-cell v-for="item in waferList" :key="item.ws"  >
          <van-row style="text-align: left;color: red" v-if="item.isOverCount===true">
            <van-col span="8">{{item.device}}</van-col>
            <van-col span="8">{{item.waferSource}}</van-col>
            <van-col span="8">{{item.waferLot}}</van-col>
          </van-row>
          <van-row style="text-align: left" v-if="item.isOverCount!==true">
            <van-col span="8">{{item.device}}</van-col>
            <van-col span="8">{{item.waferSource}}</van-col>
            <van-col span="8">{{item.waferLot}}</van-col>
          </van-row>
        </van-cell>
      </van-list>
      <!-- 输入密码对话框  -->
      <van-dialog v-model="show" title="芯片清洗次数超过限定!输入密码放行" show-cancel-button @confirm="onConfirm" @close="onClose">
        <van-field
          v-model="password"
          name="密码" type="password"
          label="密码"
          placeholder="密码"  autocomplete="off"
        />
      </van-dialog>
    </div>
</template>

<script>
export default {
  name: 'Wash',
  mounted () {
    this.$refs.field.focus()
  },
  data () {
    return {
      currentWaferSource: '',
      currentPlasma: '',
      waferList: [],
      loading: true,
      finished: true,
      show: false,
      password: '',
      startTime: undefined,
      endTime: undefined,
      needConfirmAuth: false,
      deleteMode: false
    }
  },
  watch: {
    currentWaferSource () {
      if (this.currentWaferSource.length === 1) {
        this.startTime = new Date()
      }
    }
  },
  methods: {
    onClickLeft () {
      this.$router.push('/')
    },
    onClickRight () {
      if (this.deleteMode) {
        this.deleteMode = false
        this.$toast.success('已关闭删除模式!请照常使用!')
      } else {
        this.deleteMode = true
        if (this.waferList.length === 0) {
          return this.$toast.fail('当前列表无任何内容!不能开启删除模式!')
        }
        this.$toast.success('已开启删除模式!再次扫描之前扫描过的芯片即可从下方列表删除!')
      }
    },
    onToastClose () {
      this.$refs.field.focus()
    },
    async onSubmit () {
      if (this.waferList.length === 0) {
        return this.$toast({
          type: 'fail', message: '当前没有扫描任何waferSource!', onClose: this.onToastClose
        })
      }
      if (this.needConfirmAuth === true) {
        this.show = true
        return
      }
      const res = await this.$http.post('rec/add', this.waferList)
      if (res.status === 200) {
        this.$toast.success('提交清洗记录成功!')
        await this.$router.push('/')
      }
    },
    async onEnter () {
      if (this.currentWaferSource === '') {
        return this.$toast.fail('未键入任何内容')
      }
      // this.endTime = new Date()
      // const timeDiff = this.endTime - this.startTime
      // if (timeDiff > 50) {
      //   this.currentWaferSource = ''
      //   return this.$toast.fail('不允许手输:' + timeDiff + 'ms')
      // }
      const array = this.currentWaferSource.split('^')
      const device = array[0]
      const waferSource = array[1]
      const waferLot = array[3]
      const wafer = {
        device,
        waferSource,
        waferLot
      }
      if (this.deleteMode && this.waferList.length >= 1) {
        for (let i = 0; i < this.waferList.length; i++) {
          if (waferLot === this.waferList[i].waferLot) {
            this.waferList.splice(i, 1)
            this.deleteMode = false
            return this.$toast.success('已成功删除:' + waferLot)
          }
        }
      }
      // 当前扫描数已达8个
      if (this.waferList.length === 8) {
        this.currentWaferSource = ''
        return this.$toast.fail('当前已扫描数量:' + this.waferList.length + '已达到一筐8个的上限')
      }
      // 每个芯片不能重复扫描，判断标准waferLot
      if (this.waferList.length >= 1) {
        for (let i = 0; i < this.waferList.length; i++) {
          if (this.waferList[i].waferLot === waferLot) {
            this.currentWaferSource = ''
            return this.$toast.fail('当前芯片已重复扫描!')
          }
        }
      }
      // 判断当前芯片是否还允许清洗
      const countRes = await this.$http.get('rec/isOverCount', { params: { waferLot } })
      // 如果已达上限
      if (countRes.status === 200) {
        // 判断是否有权限
        const authRes = await this.$http.get('user/auth')
        // 没有权限
        if (authRes.status !== 200) {
          this.currentWaferSource = ''
          return this.$toast.fail('当前扫描芯片已达上限!已经扫描次数:' + countRes.data + ' 而你没有权限操作!')
        }
        // 有权限标注本次操作需要权限，然后不做多余操作，往下继续执行
        wafer.isOverCount = true
        this.needConfirmAuth = true
      }
      const res = await this.$http.get(`recipe/${waferSource}`)
      if (res.status !== 200) {
        this.currentWaferSource = ''
        return this.$toast.fail('获取waferSource与plasma的对应关系失败')
      }
      // 和第一个扫描的做对比recipeName必须一致
      if (this.waferList.length >= 1) {
        if (res.data.recipeName === this.waferList[0].recipeName) {
          wafer.recipeName = res.data.recipeName
          this.currentPlasma = wafer.recipeName
          this.waferList.push(wafer)
        } else {
          this.currentWaferSource = ''
          return this.$toast.fail('当前扫描waferSource获取plasma程序与第一个不一致!')
        }
        this.currentWaferSource = ''
      } else {
        wafer.recipeName = res.data.recipeName
        this.currentPlasma = wafer.recipeName
        this.waferList.push(wafer)
        this.currentWaferSource = ''
      }
    },
    async onConfirm () {
      const confirmRes = await this.$http.post('user/loginAuth', { pw: this.password })
      if (confirmRes.status === 200) {
        // 输入密码成功允许超限次清洗芯片
        const res = await this.$http.post('rec/add', this.waferList)
        if (res.status === 200) {
          this.$toast.success('输入密码成功,已经完成提交!')
          await this.$router.push('/')
        } else { this.$toast.fail('输入密码成功,但提交清洗记录失败!') }
      } else {
        // 不成功再次打开对话框
        this.$toast.fail('密码输入错误')
        this.show = true
      }
    },
    onClose () {
      this.currentWaferSource = ''
      this.password = ''
      this.$refs.field.focus()
    }
  }
}
</script>

<style scoped>

</style>
