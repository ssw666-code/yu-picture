<template>
  <div id="mySpacePage">
    正在加载
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { onMounted } from 'vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 检查用户是否有个人空间
const checkUserStore = async () => {

// 用户是否登录
const loginUser = loginUserStore.loginUser;
if (!loginUser?.id) {
  router.replace("/user/login")
  return;
}

// 用户是否创建个人空间
const res = await listSpaceVoByPageUsingPost({
  userId: loginUser.id,
  current: 1,
  pageSize: 1
})
  if (res.data.code === 0) {
    if (res.data.data?.records?.length > 0) {
      const space = res.data.data.records[0]
      router.replace(`/space/${space.id}`)
    } else {
      router.replace("/add_space")
      message.warning('请先创建个人空间')
    }
  } else {
    message.error('加载我的空间失败' + res.data.message);
  }
}

onMounted(() => {
  checkUserStore();
})

</script>

<style>

</style>




