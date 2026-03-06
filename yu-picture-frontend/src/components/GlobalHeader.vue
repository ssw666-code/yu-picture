<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <!-- 左侧网站信息 -->
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">鱼皮云图库</div>
          </div>
        </router-link>
      </a-col>`

      <!-- 中间菜单栏 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          @click="doMenuClick"
        >
          <a-menu-item
            v-for="item in visibleRoutes"
            :key="item.path"
            :key-path="item.path"
          >
            {{ item.name }}
          </a-menu-item>
        </a-menu>
      </a-col>

      <!-- 用户信息展示 -->
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '未登录' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                  <a-menu-item @click="toUserMessage">
                    个人信息
                  </a-menu-item>
                  <a-menu-item>
                    <router-link to="/my_space">
                      <UserOutlined/>
                      我的空间
                    </router-link>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { computed, ref } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import checkAccess from '@/access/checkAccess.ts'
import router from '@/router'
import { userLogoutUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import routers from '@/router/routes.ts'
import UserMessagePage from '@/views/user/PersonalInformationPage.vue'

const loginUserStore = useLoginUserStore()

// 展示在菜单栏的路由数组
const visibleRoutes = computed(() => {
  return routers.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false
    }
    // 根据权限过滤菜单
    if (!checkAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false
    }
    return true
  })
})

// 点击菜单跳转到对应页面
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  });
};

// 当前高亮的菜单项
const current = ref<string[]>(['/'])
// 监听路由变化，更新高亮菜单项
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

// 用户退出登录
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error("退出登录失败" + res.data.message)
  }
}

const toUserMessage = () => {
  router.push({
    path: '/user/message',
  })
}
</script>

<style scoped>
#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  width: 48px;
}
</style>
