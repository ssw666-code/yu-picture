package com.yupi.yupicturebackend.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 测试上传文件
     * @param multipartFile
     * @return
     */
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> testUploadFile(@RequestPart("file")MultipartFile  multipartFile) {
        // 当前文件名称
        String filename = multipartFile.getOriginalFilename();
        // 文件上传路径
        String filePath = String.format("/test/%s", filename);

        File file = null;
        try {
            // 上传文件
            // 创建临时文件，并将MultipartFile对象转为File对象(保存路径，后缀)
            file = File.createTempFile(filePath, null);
            // 将multipartFile文件传输给file文件
            multipartFile.transferTo(file);
            // 将路径作为唯一key
            cosManager.putObject(filePath, file);
            // 返回可访问的地址
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("文件上传问题，filePath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 删除临时文件
            boolean delete = file.delete();
            if (!delete) {
                log.error("文件删除失败，filePath = {} ", filePath);
            }
        }
    }

    /**
     * 测试下载文件
     * @param filePath
     * @param response
     * @throws IOException
     */
    @GetMapping("/test/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    // 以流的形式下载
    public void testDownloadFile(String filePath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            // 获取文件
            COSObject cosObject = cosManager.getObject(filePath);
            // 获取存储的内容（输入流）
            cosObjectInput = cosObject.getObjectContent();
            // 将流转为字节数组
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            //设置响应头，下载文件
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
            // 写入响应
            // 获取输入流，写入字节数组
            response.getOutputStream().write(bytes);
            // 刷新，将内容刷新到响应体中
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 关闭流
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }
}
