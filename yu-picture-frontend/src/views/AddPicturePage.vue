<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? '修改图片' : '创建图片' }}
    </h2>
    <a-typography-paragraph v-if="spaceId" type="secondary">
      保存空间至：<a :href="`/space/${spaceId}`" target="_blank">{{ spaceId }}</a>
    </a-typography-paragraph>
    <!-- 选择上传方式 -->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="文件上传">
        <!-- 图片上传组件 -->
        <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL 上传">
        <!-- URL 图片上传组件 -->
        <UrlPictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>
    <!-- 图片信息表单 -->
    <div class="search-form">
      <a-form
        v-if="picture"
        name="pictureForm"
        layout="vertical"
        :model="pictureForm"
        @finish="handleSubmit"
        bordered
      >
        <a-form-item name="name" label="名称">
          <a-input v-model:value="pictureForm.name" placeholder="请输入名称" allow-clear />
        </a-form-item>
        <a-form-item name="introduction" label="简介">
          <a-textarea
            v-model:value="pictureForm.introduction"
            placeholder="请输入简介"
            autoSize
            :auto-size="{ minRows: 2, maxRow: 3 }"
            allow-clear
          />
        </a-form-item>
        <a-form-item name="category" label="分类">
          <a-auto-complete
            v-model:value="pictureForm.category"
            placeholder="请输入分类"
            :options="categoryOptions"
            allow-clear
          />
        </a-form-item>
        <a-form-item name="tags" label="标签">
          <a-select
            v-model:value="pictureForm.tags"
            mode="tags"
            placeholder="请输入标签"
            :options="tagOptions"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">
            {{ route.query?.id ? '修改' : '创建' }}
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import PictureUpload from '@/components/PictureUpload.vue'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController.ts'
import router from '@/router'
import { useRoute } from 'vue-router'

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})
const uploadType = ref<'file' | 'url'>('file');
const route = useRoute()

// 空间id
const spaceId = computed(() => {
  return route.query?.spaceId
})


/**
 * 图片上传成功
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const pictureId = picture.value?.id
  if (!pictureId) {
    message.error('请上传图片')
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('创建成功')
    // 跳转图片详情页
    router.push({
      path: `/picture/${pictureId}`,
      replace: true,
    })
  } else {
    message.error('创建失败' + res.data.message)
  }
}

const categoryOptions = ref<{ value: string; label: string }[]>([])
const tagOptions = ref<{ value: string; label: string }[]>([])

/**
 * 获取标签和分类选项
 * @param values
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('获取标签分类列表失败失败' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})

// 获取图片数据
const getOldPicture = async () => {
  // 获取到id
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})
</script>

<style>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
