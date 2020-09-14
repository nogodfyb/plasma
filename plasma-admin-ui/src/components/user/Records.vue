<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    <el-breadcrumb-item>plasma记录</el-breadcrumb-item>
  </el-breadcrumb>
    <!-- 卡片视图 -->
  <el-card>
<!-- 搜索与添加区域  -->
    <el-row :gutter="20">
      <el-col :span="4">
        <el-input placeholder="waferSource" v-model="queryInfo.waferSource" clearable @clear="getList">
      </el-input>
      </el-col>
      <el-col :span="4">
        <el-input placeholder="plasma程序" v-model="queryInfo.recipeName" clearable @clear="getList">
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-input placeholder="waferLot" v-model="queryInfo.waferLot" clearable @clear="getList">
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-input placeholder="芯片型号" v-model="queryInfo.device" clearable @clear="getList">
        </el-input>
      </el-col>
      <el-col :span="3">
        <el-button type="warning" icon="el-icon-search" @click="search">搜索</el-button>
      </el-col>
      <el-col :span="3">
        <el-button type="danger" @click="exportExcel">导出数据</el-button>
      </el-col>
    </el-row>
    <el-row style="margin-top: 10px">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至" unlink-panels
        start-placeholder="开始日期" @change="dateChange"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd">
      </el-date-picker>
    </el-row>
    <!-- plasma记录列表区域 -->
    <el-table :data="recipeList" border  :height="height" :row-class-name="tableRowClassName">
      <el-table-column type="index"></el-table-column>
      <el-table-column label="机器号" prop="mc"></el-table-column>
      <el-table-column label="芯片型号" prop="tt"></el-table-column>
      <el-table-column label="waferSource" prop="ws"></el-table-column>
      <el-table-column label="waferLot" prop="wl" width="150px"></el-table-column>
      <el-table-column label="plasma程序" prop="recipeName"></el-table-column>
      <el-table-column label="工号" prop="bn"></el-table-column>
      <el-table-column label="班次" prop="sf"></el-table-column>
      <el-table-column label="第几次" prop="plasmaCount"></el-table-column>
      <el-table-column label="清洗时间" prop="inTime" width="200px"></el-table-column>
    </el-table>
<!--    分页区域-->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="queryInfo.pageNum"
      :page-sizes="[3, 5, 10, 15]"
      :page-size="queryInfo.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </el-card>
</div>
</template>

<script>
import config from '../../util/config'
export default {
  data () {
    return {
      dateRange: [],
      height: 450,
      BASE_REQUEST_PATH: config.BASE_REQUEST_PATH,
      // 获取用户列表的参数对象
      queryInfo: {
        waferSource: '',
        recipeName: '',
        waferLot: '',
        device: '',
        startTime: '',
        endTime: '',
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      recipeList: [],
      total: 0
    }
  },
  watch: {
    dateRange () {
      if (this.dateRange !== null) {
        this.queryInfo.startTime = this.dateRange[0]
        this.queryInfo.endTime = this.dateRange[1]
      } else {
        this.queryInfo.startTime = ''
        this.queryInfo.endTime = ''
      }
    }
  },
  created () {
    if (window.screen.width < 1400) {
      this.height -= 120
    }
    // 初始化时间
    const today = new Date().toISOString().slice(0, 10)
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 35)
    const startString = start.toISOString().slice(0, 10)
    this.dateRange[0] = startString
    this.dateRange[1] = today
    this.queryInfo.startTime = startString
    this.queryInfo.endTime = today
    this.getList()
  },
  methods: {
    dateChange () {
      const end = new Date(this.queryInfo.endTime)
      const start = new Date(this.queryInfo.startTime)
      if ((end - start) > (1000 * 3600 * 24 * 100)) {
        this.$message.error('当前选中时间范围已超过100天!')
        // 初始化时间
        const today = new Date().toISOString().slice(0, 10)
        const start = new Date()
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 35)
        const startString = start.toISOString().slice(0, 10)
        this.dateRange[0] = startString
        this.dateRange[1] = today
        this.queryInfo.startTime = startString
        this.queryInfo.endTime = today
      }
    },
    tableRowClassName ({ row, rowIndex }) {
      if (row.isOverCount === true) {
        return 'warning-row'
      }
      return ''
    },
    async getList () {
      const { data: res } = await this.$http.get('rec/list', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取plasma记录列表失败！')
      }
      this.recipeList = res.data.list
      this.total = res.data.total
      console.log(res)
    },
    handleSizeChange (newSize) {
      this.queryInfo.pageSize = newSize
      this.getList()
    },
    handleCurrentChange (newPage) {
      this.queryInfo.pageNum = newPage
      this.getList()
    },
    search () {
      this.queryInfo.pageNum = 1
      this.getList()
    },
    async exportExcel () {
      window.location.href = this.BASE_REQUEST_PATH + 'plasma/rec/exportExcel'
    }
  }
}
</script>

<style>
  .el-table .warning-row {
    background: red;
    color: #000000 ;
  }
</style>
