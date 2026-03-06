<template>
  <div id="app">
    <a-config-provider :locale="zhCN">
      <BasicLayout />
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
import { healthUsingGet } from '@/api/mainController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import zhCN from 'ant-design-vue/es/locale/zh_CN'
dayjs.locale('zh-cn');

// 获取登录用户信息
const loginUserStore = useLoginUserStore();
loginUserStore.fetchLoginUser();

healthUsingGet().then((res) => {
  console.log(res);
})
</script>

<style scoped>

</style>
