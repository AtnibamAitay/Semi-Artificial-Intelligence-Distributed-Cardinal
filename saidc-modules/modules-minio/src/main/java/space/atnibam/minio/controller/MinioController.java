package space.atnibam.minio.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.atnibam.common.core.domain.R;
import space.atnibam.common.core.exception.MinioException;
import space.atnibam.minio.model.dto.UploadFileParamsDTO;
import space.atnibam.minio.service.FileService;
import space.atnibam.minio.utils.FileServiceUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

import static space.atnibam.common.core.enums.ResultCode.MINIO_UPLOAD_ERROR;

/**
 * @ClassName: MinioController
 * @Description: 对象存储服务控制器
 * @Author: AtnibamAitay
 * @CreateTime: 2023/10/16 16:04
 **/
@Slf4j
@Api(value = "对象存储服务", tags = "对象存储服务")
@RestController
@RequestMapping("/api")
public class MinioController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param files  文件
     * @param bucket 存储桶
     * @param userId 用户id
     * @param folder 文件存储的文件夹，可以为空
     * @return 返回文件上传结果信息
     */
    @ApiOperation("上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R upload(@RequestPart("files") MultipartFile files,
                    @RequestParam(value = "bucket") String bucket,
                    @RequestParam(value = "userId") Integer userId,
                    @RequestParam(value = "folder", required = false) String folder) {

        log.info("文件上传开始，文件名：{}", files.getOriginalFilename());

        // 创建一个新的文件上传参数对象
        UploadFileParamsDTO uploadFileParamsDTO = UploadFileParamsDTO.builder()
                // 设置文件名
                .fileName(files.getOriginalFilename())
                // 设置文件大小
                .fileSize(files.getSize())
                // 设置桶
                .bucket(bucket)
                // 设置用户id
                .userId(userId)
                // 设置文件类型
                .contentType(FileServiceUtil.getContentType(Objects.requireNonNull(files.getOriginalFilename())))
                .build();

        try {
            // 调用服务层方法进行文件上传，并返回上传结果
            String url = fileService.uploadFile(uploadFileParamsDTO, files.getBytes(), folder);

            log.info("文件上传成功，文件url：{}", url);

            return R.ok(url);
        } catch (IOException e) {
            log.error("文件上传失败，错误信息：{}", e.getMessage());
            // 如果在上传过程中发生错误，那么抛出自定义异常
            throw new MinioException(MINIO_UPLOAD_ERROR);
        }
    }

}
