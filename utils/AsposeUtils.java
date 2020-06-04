package com.yidao.court.prelitigation.utils;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.yidao.court.core.exception.ServiceException;
import com.yidao.court.prelitigation.enums.PreLitiErrorCodeEnum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *     doc转pdf工具类
 * </p>
 */
public class AsposeUtils {
    /**
     * 获取license
     * @return
     */
    public static boolean getLicense() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("license/license.xml");
        License license = new License();
        license.setLicense(is);

        return true;
    }

    /**
     * doc转pdf
     * @param inPath 源文件全路径
     * @param outPath 目标文件全路径
     */
    public static void doc2pdf(String inPath, String outPath) {
        FileOutputStream os = null;

        try {
            getLicense();
            File file = new File(outPath);
            os = new FileOutputStream(file);
            Document doc = new Document(inPath);
            doc.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            throw new ServiceException(PreLitiErrorCodeEnum.DOC_TO_PDF_ERROR);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new ServiceException(PreLitiErrorCodeEnum.DOC_TO_PDF_ERROR);
                }
            }
        }

    }

//    public static void main(String[] args) {
//        doc2pdf("/usr/local/yidao/attachment/case/temp/测试doc.docx","/usr/local/yidao/attachment/case/temp/测试doc.pdf");
//    }
}
