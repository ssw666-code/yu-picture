<template>
  <div id="userMessagePage">
    <!-- 搜索表单 -->
    <div class="search-form">
      <a-form layout="inline" :model="searchParams" @finish="doSearch" bordered>
        <a-form-item label="账号">
          <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="dataList"
        :pagination="pagination"
        @change="doTableChange"
      >
        <!-- 定义表格中的数据 -->
        <template #bodyCell="{ column, text, record }">
          <template v-if="['userName', 'userProfile'].includes(column.dataIndex)">
            <div>
              <a-input
                v-if="editableData[record.id]"
                v-model:value="editableData[record.id][column.dataIndex]"
                style="margin: -5px 0"
              />
              <template v-else>
                {{ text }}
              </template>
            </div>
          </template>
          <template v-if="column.dataIndex === 'userAvatar'">
            <a-image :src="record.userAvatar" width="120px" />
          </template>
          <template v-else-if="column.dataIndex === 'userRole'">
            <div v-if="record.userRole === 'admin'">
              <a-tag color="green">管理员</a-tag>
            </div>
            <div v-else>
              <a-tag color="blue">普通用户</a-tag>
            </div>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
          </template>
          <!-- 按钮部分 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <span>
                <a-button danger @click="daDelete(record.id)">删除</a-button>
              </span>
              <div class="editable-row-operations">
                <span v-if="editableData[record.id]">
                  <a-typography-link @click="save(record.id)">保存</a-typography-link>
                  <a-popconfirm title="确认取消?" @confirm="cancel(record.id)">
                    <a>取消</a>
                  </a-popconfirm>
                </span>
                <span v-else>
                  <a-button type="primary" @click="edit(record.id)">修改</a-button>
                </span>
              </div>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref, type UnwrapRef } from 'vue'
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { cloneDeep } from 'lodash-es'

// 定义表格列
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: 50,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 200,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 80,
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: 600,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 50,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 定义数据
const dataList = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 分页对象
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条数据`,
  }
})

const editableData: Record<number, Partial<API.UserVO>> = reactive({})
const edit = (key: number) => {
  console.log('触发edit方法')
  const target = dataList.value.find((item) => item.id === key)
  if (target) {
    editableData[key] = cloneDeep(target)
  }
}

const save = (key: number) => {
  const target = dataList.value.find((item) => item.id === key)
  if (target && editableData[key]) {
    Object.assign(target, editableData[key])
    delete editableData[key]
  }
}

const cancel = (key: number) => {
  delete editableData[key]
}

// 表格变化之后重新获取数据
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 删除数据
const daDelete = async (id: number) => {
  if (!id) {
    return
  }
  const res = await deleteUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    await fetchData()
  } else {
    message.error('删除失败' + res.data.message)
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}

.editable-row-operations a {
  margin-right: 8px;
}
</style>
