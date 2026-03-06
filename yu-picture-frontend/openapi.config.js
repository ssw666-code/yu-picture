import { generateService } from '@umijs/openapi'

generateService({
  // 接口代码使用的请求库
  requestLibPath: "import request from '@/request'",
  // 指定参考的接口文档地址
  schemaPath: 'http://localhost:9096/api/v2/api-docs',
  // 生成文件存放的地址
  serversPath: './src',
})
