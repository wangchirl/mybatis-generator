package com.yidao.court.prelitigation.utils;

import com.yidao.court.core.exception.ServiceException;
import com.yidao.court.prelitigation.enums.PreLitiErrorCodeEnum;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 *     zip解压缩工具类
 * </p>
 *
 * @author guojia
 * @since 2019-09-03
 */
public class ZipUtils {

    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 压缩指定目录所有文件
     * @param sourceFilePath 源文件所在目录（全路径）
     * @param targetZipPath zip目标文件目录（全路径）
     * @param zipFileName zip名称（带后缀名）
     * @return
     */
    public static boolean folderToZip(String sourceFilePath, String targetZipPath, String zipFileName) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            // 源文件目录
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                throw new ServiceException(PreLitiErrorCodeEnum.ZIP_FILE_PATH_IS_NOT_EXIST, sourceFilePath);
            }

            // zip文件目录
            File zipFilePath = new File(targetZipPath);
            if (!zipFilePath.exists()) {
                FileUtils.forceMkdir(zipFilePath);
            }

            File zipFile = new File(targetZipPath + File.separator + zipFileName);

            // 待压缩的所有文件
            File[] sourceFileList = sourceFile.listFiles();
            if (sourceFileList == null && sourceFileList.length <= 0) {
                throw new ServiceException(PreLitiErrorCodeEnum.ZIP_FILE_IS_NOT_EXIST, sourceFilePath);
            }
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            int size = 1024 * 10;
            byte[] bufs = new byte[size];
            for (int i=0; i<sourceFileList.length; i++) {
                // 创建ZIP实体，并添加进压缩包
                ZipEntry zipEntry = new ZipEntry(sourceFileList[i].getName());
                zos.putNextEntry(zipEntry);

                // 读取待压缩的文件并写进压缩包里
                FileInputStream fis = new FileInputStream(sourceFileList[i]);
                BufferedInputStream bis = new BufferedInputStream(fis, size);
                int read;
                while ((read = bis.read(bufs, 0, size)) != -1) {
                    zos.write(bufs, 0, read);
                }
                zos.flush();
                fis.close();
                bis.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ServiceException(PreLitiErrorCodeEnum.ZIP_FILE_ERROR);
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


//    public static void main(String[] args) {
//        String sourceFilePath = "/usr/local/temp";
//        String targetZipPath = "/usr/local/temp1";
//        String zipFileName = "download.zip";
//        ZipUtils.folderToZip(sourceFilePath, targetZipPath, zipFileName);
//    }
}
