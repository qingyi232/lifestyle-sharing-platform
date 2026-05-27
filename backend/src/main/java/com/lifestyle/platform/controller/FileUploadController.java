package com.lifestyle.platform.controller;

import com.lifestyle.platform.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    private static File uploadRootDir() {
        return Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize().toFile();
    }

    @PostMapping
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return Result.error(400, "文件名不能为空");
        }

        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot < 1 || lastDot >= originalFilename.length() - 1) {
            return Result.error(400, "文件名须包含有效的扩展名（如 .jpg）");
        }
        String extension = originalFilename.substring(lastDot + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return Result.error(400, "不支持的文件格式，仅支持 jpg, jpeg, png, gif, webp");
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        File uploadDir = uploadRootDir();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            File dest = new File(uploadDir, newFilename);
            file.transferTo(dest);
            String url = "/uploads/" + newFilename;
            return Result.success("上传成功", url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
