<template>
  <div id="userLoginPage">
    <h2 class="title">鱼皮云图库-用户登录</h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item
        label="用户账号"
        name="userAccount"
        :rules="[{ required: true, message: '请输入账号!' }]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <a-form-item
        label="用户密码"
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码!' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">立即注册</RouterLink>
      </div>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userLoginUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用于接收表单输入的值
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await userLoginUsingPost(values)
  // 登录成功，保存全局用户状态
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    await router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error("登录失败" + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  max-width: 360px;
  margin: auto;
}
.title {
  text-align: center;
  margin-bottom: 16px;
}
.desc {
  color: #bbb;
  text-align: center;
  margin-bottom: 16px;
}
.tips {
  color: #bbb;
  text-align: right;
  font-size: 16px;
}
</style>
