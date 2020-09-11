<template>
<div>
    <!-- 面包屑导航 -->
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    <el-breadcrumb-item>recipe关系维护</el-breadcrumb-item>
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
      <el-col :span="3">
        <el-button type="warning" icon="el-icon-search" @click="search">搜索</el-button>
      </el-col>
      <el-col :span="3">
        <el-button type="primary" @click="dialogVisible=true">添加</el-button>
      </el-col>
      <el-col :span="3">
        <el-button type="danger" @click="exportExcel">导出数据</el-button>
      </el-col>
      <el-col :span="3">
        <el-button type="success" @click="showUploadDialog">导入数据</el-button>
      </el-col>
      <el-col :span="3">
        <el-link type="info" :href="BASE_REQUEST_PATH+'plasma/recipe/download'">下载模板</el-link>
      </el-col>
    </el-row>
    <!-- 用户列表区域 -->
    <el-table :data="recipeList" border stripe :height="height">
      <el-table-column type="index"></el-table-column>
      <el-table-column label="waferSource" prop="ws"></el-table-column>
      <el-table-column label="plasma程序" prop="recipeName"></el-table-column>
      <el-table-column label="创建时间" prop="inTime"></el-table-column>
      <el-table-column label="操作" width="200px">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.ws)">编辑</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteRecipe(scope.row.ws)">删除</el-button>
        </template>
      </el-table-column>
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
  <!--    添加recipe的对话框-->
  <el-dialog
    title="添加"
    :visible.sync="dialogVisible"
    width="50%" @close="addDialogClosed"
  >
    <!--      内容主体区域-->
    <el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="120px" >
      <el-form-item label="waferSource" prop="ws">
        <el-input v-model="addForm.ws"></el-input>
      </el-form-item>
      <el-form-item label="plasma程序" prop="recipeName">
        <el-input v-model="addForm.recipeName"></el-input>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary"  @click="addRecipe">确 定</el-button>
      </span>
  </el-dialog>
  <!--    修改recipe的对话框-->
  <el-dialog
    title="修改"
    :visible.sync="editDialogVisible"
    width="50%" @close="editDialogClosed"
  >
    <!--      内容主体区域-->
    <el-form :model="editForm" :rules="addFormRules" ref="editFormRef" label-width="120px" >
      <el-form-item label="waferSource" prop="ws">
        <el-input v-model="editForm.ws" disabled></el-input>
      </el-form-item>
      <el-form-item label="plasma程序" prop="recipeName">
        <el-input v-model="editForm.recipeName"></el-input>
      </el-form-item>
    </el-form>
    <!--      底部区域-->
    <span slot="footer" class="dialog-footer">
    <el-button @click="editDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="editRecipe" >确 定</el-button>
      </span>
  </el-dialog>
  <!-- 上传excel -->
  <el-dialog
    title="上传数据"
    :visible.sync="uploadDialogVisible"
    width="30%" @close="uploadDialogClosed"
  >
    <el-upload
      class="upload-demo"
      drag with-credentials
      :action="BASE_REQUEST_PATH+'plasma/recipe/upload'"
      multiple>
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传xlsx文件，且不超过500kb</div>
    </el-upload>
  </el-dialog>
</div>
</template>

<script>
import config from '../../util/config'
export default {
  data () {
    return {
      height: 500,
      BASE_REQUEST_PATH: config.BASE_REQUEST_PATH,
      // 获取用户列表的参数对象
      queryInfo: {
        waferSource: '',
        recipeName: '',
        // 当前的页数
        pageNum: 1,
        // 当前每页显示多少条数据
        pageSize: 5
      },
      recipeList: [],
      total: 0,
      // 用户对话框的可见性
      dialogVisible: false,
      editDialogVisible: false,
      uploadDialogVisible: false,
      addForm: {
        ws: '',
        recipeName: ''
      },
      editForm: {},
      addFormRules: {
        ws: [
          { required: true, message: '请输入', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        recipeName: [
          { required: true, message: '请输入', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    if (window.screen.width < 1400) {
      this.height -= 120
    }
    this.getList()
  },
  methods: {
    async getList () {
      const { data: res } = await this.$http.get('recipe/list', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('获取关系列表失败！')
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
    addDialogClosed () {
      // 不需要自己清空数据 以下不仅清空了addForm的数据并且清空了验证遗留信息
      this.$refs.addFormRef.resetFields()
    },
    addRecipe () {
      this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) {
          return false
        }
        // 可以发起添加用户的网络请求
        const { data: res } = await this.$http.post('recipe/add', this.addForm)
        if (res.status !== 200) {
          return this.$message.error('添加失败！')
        }
        this.$message.success('添加成功！')
        // 隐藏添加用户的对话框
        this.dialogVisible = false
        // 重新获取用户列表数据
        this.getList()
      })
    },
    async showEditDialog (ws) {
      this.editDialogVisible = true
      const { data: res } = await this.$http.get(`recipe/${ws}`)
      if (res.status !== 200) {
        this.$message.error('获取信息失败')
        return false
      }
      this.editForm = res.data
    },
    async showUploadDialog () {
      const { data: res } = await this.$http.get('user/isLogin')
      if (res.status !== 200) {
        return this.$message.error('您还未登录')
      }
      this.uploadDialogVisible = true
    },
    editDialogClosed () {
      this.$refs.editFormRef.resetFields()
    },
    uploadDialogClosed () {
      this.getList()
    },
    editRecipe () {
      this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return false
        }
        // 可以发起修改用户的网络请求
        const { data: res } = await this.$http.put('recipe/update', this.editForm)
        if (res.status !== 200) {
          return this.$message.error('修改失败！')
        }
        this.$message.success('修改成功！')
        // 隐藏修改的对话框
        this.editDialogVisible = false
        // 重新获取列表数据
        await this.getList()
      })
    },
    deleteRecipe (ws) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteRecipeByWs(ws)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    async deleteRecipeByWs (ws) {
      // 可以发起删除的网络请求
      const { data: res } = await this.$http.delete(`recipe/${ws}`)
      if (res.status !== 200) {
        return this.$message.error('删除失败')
      }
      this.$message({
        type: 'success',
        message: '删除成功!'
      })
      this.getList()
    },
    search () {
      this.queryInfo.pageNum = 1
      this.getList()
    },
    async exportExcel () {
      // 可以发起存储excel的网络请求
      const { data: res } = await this.$http.get('recipe/store', { params: this.queryInfo })
      if (res.status !== 200) {
        return this.$message.error('导出失败')
      }
      window.location.href = this.BASE_REQUEST_PATH + 'plasma/recipe/exportExcel'
    }
  }
}
</script>

<style scoped>

</style>
