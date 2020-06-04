package com.yidao.court.prelitigation.utils;

import com.yidao.court.core.exception.ServiceException;
import com.yidao.court.prelitigation.enums.PreLitiErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author duwei
 * @date 2019/11/7
 */
public class FileOperateUtils {
    public static final Logger logger = LoggerFactory.getLogger(FileOperateUtils.class);

    public static void download(HttpServletRequest request, HttpServletResponse response, String filePath) {
        File file = new File(filePath);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServiceException(PreLitiErrorCodeEnum.DOWNLOAD_ERROR);
        }
        download(request, response, file.getName(), inputStream);
    }

    public static void download(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream inputStream) {
        try {
            //针对IE和非IE浏览器进行编码区分
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0 || request.getHeader("User-Agent").contains("Trident")) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
                logger.info("IE浏览器进行附件下载");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                logger.info("非IE浏览器进行附件下载下载");
            }

            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            // 设置要下载的文件的名称
            response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            // 通知客服文件的MIME类型
            response.setContentType("application/octet-stream;charset=utf-8");

            int b = 0;
            byte[] buffer = new byte[4096];
            while ((b = inputStream.read(buffer)) > 0) {
//                b = inputStream.read(buffer);
                //4.写到输出流(out)中
                outputStream.write(buffer, 0, b);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logger.error("异常", e);
            throw new ServiceException(PreLitiErrorCodeEnum.DOWNLOAD_ERROR);
        }
    }
}
