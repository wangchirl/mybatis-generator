package com.yidao.court.prelitigation.utils.excel;

import com.yidao.court.prelitigation.exception.FileFieldNullException;
import com.yidao.court.prelitigation.exception.FileResolveException;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Excel File Handler
 * <p>
 * Created by zizhan on 2019/7/4.
 */
public class ExcelHelper {

    protected Logger logger = LoggerFactory.getLogger(ExcelHelper.class);

    private String fileUploadPath;

    public ExcelHelper(String fileUpload) {
        this.fileUploadPath = fileUpload;
    }

    /**
     * Read upload file from http request
     *
     * @param request
     * @return
     * @throws FileUploadException
     */
    public List<FileItem> readUploadFile(HttpServletRequest request) throws FileUploadException {
        try {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
            servletFileUpload.setHeaderEncoding("UTF-8");
            List<FileItem> fileItems = servletFileUpload.parseRequest(request);
            return fileItems;
        } catch (FileUploadException ex) {
            logger.error("read upload file from http request failed : " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Save file to the disk and return the disk path
     *
     * @param file
     * @param savePath
     * @return
     * @throws FileUploadException save file failed
     */
    public String saveFile(MultipartFile file, String savePath) throws FileUploadException {
        try {
            String fileName = file.getOriginalFilename();
            if(StringUtils.isEmpty(fileName)) {
                fileName = file.getName(); //这里为什么会取到空的名字呢？
            }

            if (fileName != null) {

                String fileRealPath = savePath + "/" + fileName;

                File directory = new File(savePath);
                if (!directory.exists()) {
                    FileUtils.forceMkdir(directory);
                }

                //获取文件输入流
                BufferedInputStream bufferedInputStream = new BufferedInputStream(file.getInputStream());

                //获得文件输出流
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileRealPath)));
                Streams.copy(bufferedInputStream, bufferedOutputStream, true);
                logger.info(this.getClass().getSimpleName() + " save file, filepath is " + fileRealPath);
                return fileRealPath;
            }
            return null;
        } catch (IOException e) {
            logger.error("save file filed:", e);
            throw new FileUploadException("save file failed : " + e.getMessage());
        }
    }

    /**
     * Open Excel return the workbook
     *
     * @param fileRealPath
     * @return
     * @throws FileUploadException open excel failed.
     */
    public Workbook open(String fileRealPath) throws FileUploadException {
        try {
            Workbook workbook = null;
            FileInputStream fileInputStream = new FileInputStream(fileRealPath);
            if (fileRealPath.endsWith("xlsx")) {
                // TODO: 2016/7/12 excel2007格式
//                logger.warn(this.getClass().getSimpleName() + " open excel file, but xlsx format file is not implemented.");
                workbook = new XSSFWorkbook(fileInputStream);
            } else {
                workbook = new HSSFWorkbook(fileInputStream);
            }
            return workbook;
        } catch (IOException e) {
            logger.error("open file failed: " + e.getMessage());
            throw new FileUploadException("open file failed : " + e.getMessage());
        }
    }

    /**
     * Read excel
     *
     * @param sheet
     * @param startRow
     * @param checkedFieldsName the checked fileds are not allow null.
     * @param clazz
     * @return
     * @throws FileFieldNullException the checked fileds is null
     * @throws FileResolveException   read failed, maybe the field format error, etc.
     */
    public Map<String, List<? extends Object>> resolve(Sheet sheet, int startRow, List<String> checkedFieldsName,
                                                       Map<String, Object> defaultValueField, Class<?>... clazz) throws FileFieldNullException, FileResolveException {

        if (sheet == null) return null;

        Iterator<Row> rowIterator = sheet.rowIterator();
        int currentRow = 0;
        // starting from the specified line
        while (rowIterator.hasNext() && currentRow < startRow) {
            rowIterator.next();
            currentRow++;
        }

        Map<String, List<?>> listMap = new HashedMap();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            for (Class<?> aClass : clazz) {
                try {
                    List<Object> list;
                    if (listMap.containsKey(aClass.getName())) {
                        list = (List<Object>) listMap.get(aClass.getName());
                    } else {
                        list = new ArrayList<>();
                        listMap.put(aClass.getName(), list);
                    }

                    Object obj = aClass.newInstance(); // InstantiationException

                    for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
                        Cell cellData = row.getCell(i);
                        Cell cellHeader = sheet.getRow(1).getCell(i);
                        if (cellHeader == null) {
                            continue;
                        }
                        String cellField = cellHeader.toString(); // cellHeader.getStringCellValue();
                        Field declaredField = getDeclaredField(cellField, aClass);
                        if (declaredField == null) {
                            continue;
                        }
                        declaredField.setAccessible(true);
                        // get the field type
                        String fieldType = declaredField.getType().toString().substring(declaredField.getType().toString().lastIndexOf(".") + 1);
                        if (cellData == null) continue;
                        // set the field value
                        try {
                            switch (fieldType) {
                                case "String":
                                    cellData.setCellType(Cell.CELL_TYPE_STRING);
                                    declaredField.set(obj, cellData.getStringCellValue());
                                    break;
                                case "Integer":
                                    cellData.setCellType(Cell.CELL_TYPE_STRING);
                                    declaredField.set(obj, Integer.parseInt("".equals(cellData.getStringCellValue()) ? "0" : cellData.getStringCellValue()));
                                    break;
                                case "Double":
                                    cellData.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    declaredField.set(obj, cellData.getNumericCellValue());
                                    break;
                                case "BigDecimal":
                                    cellData.setCellType(Cell.CELL_TYPE_STRING);
                                    declaredField.set(obj, new BigDecimal("".equals(cellData.getStringCellValue()) ? "0" : cellData.getStringCellValue()));
                                    break;
                                case "Long":
                                    cellData.setCellType(Cell.CELL_TYPE_STRING);
                                    declaredField.set(obj, Long.parseLong("".equals(cellData.getStringCellValue()) ? "0" : cellData.getStringCellValue()));
                                    break;
                                case "Date": // 2019-11-07 添加
                                    cellData.setCellType(CellType.STRING);
                                    try {
                                        Double dateDouble = Double.parseDouble(cellData.getStringCellValue());
                                        declaredField.set(obj, HSSFDateUtil.getJavaDate(dateDouble));
                                    } catch (NumberFormatException e) {
                                    }
                                    break;
                                default:
                                    logger.warn(this.getClass().getSimpleName() + " not found type, fieldType = [" + fieldType + "], cellField = [" + cellField + "].");
                                    break;
                            }
                        } catch (Exception ex) {
                            throw new FileResolveException(cellField + " 字段格式错误：" + cellData.toString());
                        }
                    }

                    // set default value to field
                    if (defaultValueField != null && !defaultValueField.isEmpty()) {
                        for (String key : defaultValueField.keySet()) {
                            try {
                                Field field = aClass.getDeclaredField(key);
                                field.setAccessible(true);
                                field.set(obj, defaultValueField.get(key));
                            } catch (NoSuchFieldException e) {
//                                logger.warn(this.getClass().getSimpleName() + " set default value to {} make error, no such field [{}]", aClass.getName(), key);
                            } catch (Exception ex) {
//                                logger.warn(this.getClass().getSimpleName() + " set default value to {} make error {}.", aClass.getName(), ex.getMessage());
                            }
                        }
                    }

                    // check whether the specified field value is null
                    if (checkedFieldsName != null) {
                        for (int i = 0; i < checkedFieldsName.size(); i++) {
                            try {
                                Field field = aClass.getDeclaredField(checkedFieldsName.get(i));
                                field.setAccessible(true);
                                Class<?> type = field.getType();
                                Object fieldValue = field.get(obj);
                                if (type.isAssignableFrom(String.class)) {
                                    if (StringUtils.isEmpty((String) fieldValue)) {
                                        logger.error(this.getClass().getSimpleName() + " {0} value is null.", checkedFieldsName.get(i));
                                        throw new FileFieldNullException(checkedFieldsName.get(i));
                                    }
                                }
                            } catch (NoSuchFieldException e) {
                                logger.warn(this.getClass().getSimpleName() + " check field to {} make error, no such field [{}]", aClass.getName(), checkedFieldsName.get(i));
                            }
                        }
                    }

                    list.add(obj);
                } catch (FileFieldNullException ex) {
                    throw ex;
                } catch (InstantiationException | IllegalAccessException | IllegalStateException ex) {
                    logger.error(this.getClass().getSimpleName() + " resolve failed : " + ex.getMessage());
                    // file format error
                    throw new FileResolveException(ex.getMessage());
                }
            }
        }
        return listMap;
    }

    /**
     * Check the cell whether is blank
     *
     * @param cell
     * @return
     */
    private boolean isBlankCell(Cell cell) {
        if (cell == null) return true;
        boolean result = true;
        String value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf((int) cell.getNumericCellValue());
                break;
            default:
                break;
        }
        if (!"".equals(value.trim())) {
            result = false;
        }
        return result;
    }

    /**
     * Get file default save path
     *
     * @return
     */
    public String getPath() {
        // TODO: 2016/12/28 this path must be can edit in the future
        String savePath;
        if (StringUtils.isEmpty(fileUploadPath)) {
            savePath = this.getClass().getResource("/").getPath() + "uploads";
        } else {
            savePath = fileUploadPath;
        }
        logger.info(this.getClass().getSimpleName() + " get save path : " + savePath);
        File directory = new File(savePath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return savePath;
    }

    /**
     * 循环向上转型，获取对象的Field
     *
     * @param cellField
     * @param clazz
     * @return
     */
    private Field getDeclaredField(String cellField, Class<?> clazz) {
        Field declaredField = null;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                // only resolve fields in the clazz
                declaredField = clazz.getDeclaredField(cellField);
            } catch (NoSuchFieldException e) {
                try {
                    cellField = FieldFormatConvert.underlineToCamel(cellField);
                    declaredField = clazz.getDeclaredField(cellField);
                } catch (NoSuchFieldException e1) {

                }
            }
        }

        return declaredField;
    }
}
