package com.leyou.upload.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;


@Service
public class UploadService {

    private static final List<String> content_type = Arrays.asList("image/gif", "image/jpg");
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String uploadImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            //校验文件类型
            if (!content_type.contains(contentType)) {
                LOGGER.info("文件类型不合法：{}", originalFilename);
                return null;
            }
            //校验文件的内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null) {
                LOGGER.info("文件内容不合法：{}", originalFilename);
                return null;
            }
            //保存到服务器
            file.transferTo(new File("C"));
            //返回Url
            return "路径" + originalFilename;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
