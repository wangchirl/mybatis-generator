package com.yidao.court.prelitigation.utils.excel;

import com.yidao.court.prelitigation.exception.FileFieldNullException;
import com.yidao.court.prelitigation.exception.FileResolveException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report File Handler
 * <p>
 * Created by zizhan on 2019/7/4.
 */
public class ReportHelper {

    /**
     * Checked fields
     * if set the checked field, must catch the FieldNullException
     */
    private List<String> checkNotEmptyField = new ArrayList<String>();

    /**
     * Set Default Value to field
     */
    private Map<String, Object> defaultValueField = new HashMap<>();

    /**
     * Set not allow filed.
     *
     * @param filedsName
     */
    public void setNotEmptyField(String... filedsName) {
        for (String filedName : filedsName) {
            checkNotEmptyField.add(filedName);
        }
    }

    /**
     * file tmp upload path.
     */
    private String fileUpload;

    public String getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(String fileUpload) {
        this.fileUpload = fileUpload;
    }

    /**
     * Get not allow null filed
     *
     * @return
     */
    public List<String> getNotEmptyField() {
        return this.checkNotEmptyField;
    }


    public Map<String, Object> getDefaultValueField() {
        return defaultValueField;
    }

    public void setDefaultValueField(Map<String, Object> defaultValueField) {
        this.defaultValueField = defaultValueField;
    }

    /**
     * Convert line excel file to specify the type object, can specify more than one object, current
     * only supports single file resolve.
     *
     * @param fileItems
     * @param clazz
     * @return 转换的对象
     * @throws FileUploadException    resolve upload files from http request failed, or save upload file error, or
     *                                read upload file from disk error.
     * @throws FileResolveException   resolve excel file to clazz error.
     * @throws FileFieldNullException the checked fileds is null.
     */
    public Map<String, List<?>> importFile(MultipartFile[] fileItems, Class<?>... clazz) throws
            FileUploadException, FileResolveException, FileFieldNullException {
        // 1.Resolve files from http request.
        ExcelHelper excelHelper = new ExcelHelper(fileUpload);
        // 2.Get the file save path.
        String savePath = excelHelper.getPath();
        // 3.Iterator files
        for (int i = 0; i < fileItems.length; i++) {
            MultipartFile fileItem = fileItems[i];
            // 4.Save file to the path.
            String fileRealPath = excelHelper.saveFile(fileItem, savePath);
            if (fileRealPath == null) continue;
            // 5.Read file from the disk. if excel format is .xlsx then workbook is null
            Workbook workbook = excelHelper.open(fileRealPath);
            if (workbook == null) return null;
            Sheet sheet = workbook.getSheetAt(0); // get the first sheet
            int startRow = 2; // the first row is field name, so from the second row to read.
            // 6.Convert file to the clazz
            Map<String, List<?>> map = excelHelper.resolve(sheet, startRow, checkNotEmptyField, defaultValueField, clazz);
            return map;
        }
        return null;
    }
}
