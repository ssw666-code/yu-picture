<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? '修改空间' : '创建空间' }}
    </h2>
    <!-- 空间信息表单 -->
    <div class="search-form">
      <a-form
        name="spaceForm"
        layout="vertical"
        :model="spaceForm"
        @finish="handleSubmit"
        bordered
      >
        <a-form-item name="name" label="空间名称">
          <a-input v-model:value="spaceForm.spaceName" placeholder="请输入空间名称" allow-clear />
        </a-form-item>
        <a-form-item name="spaceLevel" label="空间级别">
          <a-select
            v-model:value="spaceForm.spaceLevel"
            placeholder="请选择空间级别"
            :options="SPACE_LEVEL_OPTIONS"
            style="min-width: 180px"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
            提交
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    <a-card title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版，如需升级空间，请联系
        <a href="http://codefather.cn" target="_blank">程序员鱼皮</a>
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList">
        {{ spaceLevel.text }}：大小 {{ formatSize(spaceLevel.maxSize) }}，数量 {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  addSpaceUsingPost,
  editSpaceUsingPost,
  getSpaceVoByIdUsingGet,
  listSpaceLevelUsingGet, updateSpaceUsingPost
} from '@/api/spaceController.ts'
import router from '@/router'
import { useRoute } from 'vue-router'
import { SPACE_LEVEL_OPTIONS } from '@/constants/space.ts'
import { formatSize } from '../utils'

const space = ref<API.SpaceVO>()
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceEditRequest>({})
const loading = ref(false)
const spaceLevelList = ref<API.SpaceLevel[]>([])

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('获取空间级别失败' + res.data.message)
  }
}

onMounted(() => {
  fetchSpaceLevelList()
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const spaceId = space.value?.id;
  loading.value = true
  let res;
  if (spaceId) {
    // 更新
    res = await updateSpaceUsingPost({
      id: spaceId,
      ...values,
    })
  } else  {
    // 创建
    res = await addSpaceUsingPost({
      ...values,
    })
  }
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    // 跳转空间详情页
    router.push({
      path: `/space/${res.data.data}`,
      replace: true,
    })
  } else {
    message.error('操作失败' + res.data.message)
  }
  loading.value = false
}

const route = useRoute()

// 获取空间数据
const getOldSpace = async () => {
  // 获取到id
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      // 填充表单
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}

onMounted(() => {
  getOldSpace()
})
</script>

<style>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>




