<template>
  <div id="userRegisterPage">
    <h2 class="title">鱼皮云图库-用户注册</h2>
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
      <a-form-item
        label="确认密码"
        name="checkPassword"
        :rules="[
          { required: true, message: '请再次输入密码!' },
          { min: 8, message: '确认密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请再次输入密码" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
      <div class="tips">
        已有账号？
        <RouterLink to="/user/login">立即登录</RouterLink>
      </div>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { userRegisterUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用于接收表单输入的值
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  // 校验两次密码是否一致
  if (values.userPassword !== values.checkPassword) {
    message.error('两次密码不一致')
    return
  }
  // 注册成功，跳转到登录页
  const res = await userRegisterUsingPost(values)
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功')
    await router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
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
