<template>
    <div>
      <h3>清洗</h3>
      <van-form @submit="onSubmit" :submit-on-enter="false">
        <van-field
          v-model="currentWaferSource"
          name="扫描wafer"
          label="扫描wafer"
          placeholder="wafer" @keyup.enter="onEnter" autocomplete="off"
        />
        <div style="margin: 16px;">
          <van-button round block type="info" native-type="submit">
            提交
          </van-button>
        </div>
      </van-form>
      <van-cell style="font-size: 18px;font-weight: bold">
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
          <van-row style="text-align: left">
            <van-col span="8">{{item.device}}</van-col>
            <van-col span="8">{{item.waferSource}}</van-col>
            <van-col span="8">{{item.waferLot}}</van-col>
          </van-row>
        </van-cell>
      </van-list>
    </div>
</template>

<script>
export default {
  name: 'Wash',
  data () {
    return {
      currentWaferSource: '',
      waferList: [],
      loading: true,
      finished: true
    }
  },
  methods: {
    async onSubmit () {
      if (this.waferList.length === 0) {
        return this.$toast.fail('当前没有扫描任何waferSource!')
      }
      const res = await this.$http.post('rec/add', this.waferList)
      console.log(res)
    },
    async onEnter () {
      const array = this.currentWaferSource.split('^')
      const device = array[0]
      const waferSource = array[1]
      const waferLot = array[3]
      const wafer = {
        device,
        waferSource,
        waferLot
      }
      // 当前扫描数已达8个
      if (this.waferList.length === 8) {
        return this.$toast.fail('当前已扫描数量:' + this.waferList.length + '已达到一筐8个的上限')
      }
      // 每个芯片不能重复扫描，判断标准waferLot
      if (this.waferList.length >= 1) {
        for (let i = 0; i < this.waferList.length; i++) {
          if (this.waferList[i].waferLot === waferLot) {
            return this.$toast.fail('当前芯片已重复扫描!')
          }
        }
      }
      const res = await this.$http.get(`recipe/${waferSource}`)
      if (res.status !== 200) {
        return this.$toast.fail('获取waferSource与plasma的对应关系失败')
      }
      // 和第一个扫描的做对比recipeName必须一致
      if (this.waferList.length >= 1) {
        if (res.data.recipeName === this.waferList[0].recipeName) {
          wafer.recipeName = res.data.recipeName
          this.waferList.push(wafer)
        } else return this.$toast.fail('当前扫描waferSource获取plasma程序与第一个不一致!')
      } else {
        wafer.recipeName = res.data.recipeName
        this.waferList.push(wafer)
      }
    }
  }
}
</script>

<style scoped>

</style>
