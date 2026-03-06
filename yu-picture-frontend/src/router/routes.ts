import type { RouteRecordRaw } from 'vue-router'
import HomePage from '@/views/HomePage.vue'
import UserLoginPage from '@/views/user/UserLoginPage.vue'
import UserRegisterPage from '@/views/user/UserRegisterPage.vue'
import UserManagePage from '@/views/admin/UserManagePage.vue'
import ACCESS_ENUM from '@/access/accessEnum.ts'
import AddPicturePage from '@/views/AddPicturePage.vue'
import PictureManagePage from '@/views/admin/PictureManagePage.vue'
import PictureDetailPage from '@/views/PictureDetailPage.vue'
import AddPictureBatchPage from '@/views/AddPictureBatchPage.vue'
import SpaceManagePage from '@/views/admin/SpaceManagePage.vue'
import AddSpacePage from '@/views/AddSpacePage.vue'
import MySpacePage from '@/views/MySpacePage.vue'
import SpaceDetailPage from '@/views/SpaceDetailPage.vue'

const routers: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: '主页',
    component: HomePage,
    meta: {
      icon: 'HomeOutlined',
    }
  },
  {
    path: '/user/login',
    name: '用户登录',
    component: UserLoginPage,
    meta: {
      hideInMenu: true,
    }
  },
  {
    path: '/user/register',
    name: '用户注册',
    component: UserRegisterPage,
    meta: {
      hideInMenu: true,
    }
  },
  {
    path: '/user/message',
    name: '个人信息',
    component: () => import('@/views/user/PersonalInformationPage.vue'),
    meta: {
      hideInMenu: true,
    }
  },
  {
    path: '/admin/userManage',
    name: '用户管理',
    component: UserManagePage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/admin/pictureManage',
    name: '图片管理',
    component: PictureManagePage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/admin/spaceManage',
    name: '空间管理',
    component: SpaceManagePage,
  },
  {
    path: '/add_picture',
    name: '创建图片',
    component: AddPicturePage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/add_space',
    name: '创建空间',
    component: AddSpacePage,
  },
  {
    path: '/my_space',
    name: '我的空间',
    component: MySpacePage,
    meta: {
      hideInMenu: true,
    }
  },
  {
    path: '/space/:id',
    name: '空间详情',
    component: SpaceDetailPage,
    props: true,
    meta: {
      hideInMenu: true,
    }
  },
  {
    path: '/add_picture/batch',
    name: '批量创建图片',
    component: AddPictureBatchPage,
    meta: {
      access: ACCESS_ENUM.ADMIN,
      hideInMenu: true,
    }
  },
  {
    path: '/picture/:id',
    name: '图片详情',
    component: PictureDetailPage,
    props: true,
    meta: {
      hideInMenu: true,
    }
  },
];

export default routers
