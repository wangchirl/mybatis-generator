package com.yidao.court.prelitigation.utils.easyExcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Classname ExcelUtils
 * @Description 公共导出excel工具 - 无格式
 * @Date 2019/8/19 19:31
 * @Created by 伊人
 */
public class ExcelUtils {


    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);


    private static final int INDEX_ZERO = 0;

    private static final int INDEX_ONE = 1;

    private static final int INDEX_TWO = 2;

    private ExcelUtils() {

    }

    /**
     * 导出列表 poi
     *
     * @param headers     表头
     * @param contentKeys 内容Map 的 key
     * @param contents    内容
     * @param sheetName   表单名称
     * @param fileName    文件名称
     * @param res
     */
    public static void exportContent(String[] headers, String[] contentKeys, List<Map<String, Object>> contents,
                                     String sheetName, String fileName, HttpServletRequest request, HttpServletResponse res) {
        //写入表头
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        //背景颜色
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        style.setWrapText(true);//换行
        //创建表头
        row = sheet.createRow(0);
        //第一列为序号
        HSSFCell content = row.createCell(0);
        content.setCellStyle(style);
        content.setCellValue(new HSSFRichTextString("序号"));

        int e;
        for (e = 1; e < headers.length + 1; ++e) {
            content = row.createCell(e);
            content.setCellStyle(style);
            HSSFRichTextString is = new HSSFRichTextString(headers[e - 1]);
            content.setCellValue(is);
        }

        for (e = 0; e < contents.size(); ++e) {
            row = sheet.createRow(e + 1);
            //第一列为序号
            HSSFCell out = row.createCell(0);
            out.setCellStyle(style);
            out.setCellValue(new HSSFRichTextString(String.valueOf(e + 1)));//序号

            //内容写入
            Map temp = contents.get(e);
            for (int i = 1; i < contentKeys.length + 1; i++) {
                HSSFCell tempout = row.createCell(i);//从第二列开始
                tempout.setCellStyle(style);
                String val = temp.get(contentKeys[i - 1]) == null ? "" : temp.get(contentKeys[i - 1]).toString();
                tempout.setCellValue(val);
            }
        }
        BufferedInputStream var32 = null;
        BufferedOutputStream var33 = null;
        try {
            ByteArrayOutputStream var26 = new ByteArrayOutputStream();
            wb.write(var26);
            byte[] var27 = var26.toByteArray();
            ByteArrayInputStream var29 = new ByteArrayInputStream(var27);
            res.reset();
            res.setContentType("application/vnd.ms-excel;charset=utf-8");
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0 || request.getHeader("User-Agent").contains("Trident")) { // IE浏览器
                res.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(new String(fileName + ".xls"), "UTF-8"));// 设置文件名
            } else {
                res.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
            }


            res.setHeader("Content-Length", String.valueOf(var29.available())); // Content-Length 防止导出的文件报错 【发现不可读取内容】
            ServletOutputStream var30 = res.getOutputStream();

            var32 = new BufferedInputStream(var29);
            var33 = new BufferedOutputStream(var30);
            byte[] var34 = new byte[2048];

            int var35;
            while (-1 != (var35 = var32.read(var34, 0, var34.length))) {
                var33.write(var34, 0, var35);
            }

        } catch (Exception e2) {
            logger.error("文件处理异常 {}", e2);
        } finally {
            if (var32 != null) {
                try {
                    var32.close();
                } catch (IOException e1) {
                    logger.error("流关闭异常 {}", e1);
                }
            }
            if (var33 != null) {
                try {
                    var33.close();
                } catch (IOException e1) {
                    logger.error("流关闭异常 {}", e1);
                }
            }
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    logger.error("流关闭异常 {}", e1);
                }
            }
        }

    }


    /**
     *  使用easyExcel解析excel文件
     */

    /**
     * =======================================>>>        EXCEL 读取方法 START...
     */

    // 定义默认的sheet对象 - 读取使用
    private static Sheet initSheet;

    static {
        // 第一个1代表sheet1, 第二个0代表表头占几行
        initSheet = new Sheet(INDEX_ONE, 0);
        // 设置sheet名称
        initSheet.setSheetName("sheet");
        // 设置自适应宽度
        initSheet.setAutoWidth(Boolean.TRUE);
    }


    /**
     * 使用自定义的sheet对象读取文件流
     *
     * @param in       文件输入流 注意使用 BufferedInputStream-不然不支持2003版
     * @param sheet    自定义sheet对象
     * @param more1000 是否超过1000行
     * @return List<Object>
     */
    private static List<Object> read(InputStream in, Sheet sheet, boolean more1000) {
        sheet = sheet == null ? initSheet : sheet;
        if (more1000) {
            ExcelListener excelListener = new ExcelListener();
            EasyExcelFactory.readBySax(in, sheet, excelListener);
            return excelListener.getData();
        }
        return EasyExcelFactory.read(in, sheet);
    }


    /**
     * 使用默认的sheet读取文件流
     *
     * @param in       文件输入流 注意使用 BufferedInputStream-不然不支持2003版
     * @param more1000 是否超过1000行
     * @return List<Object>
     */
    public static List<Object> read(InputStream in, boolean more1000) {
        return read(in, null, more1000);
    }


    /**
     * 使用默认的sheet读取文件流 默认未超过1000行
     *
     * @param in 文件输入流 注意使用 BufferedInputStream-不然不支持2003版
     * @return List<Object>
     */
    public static List<Object> read(InputStream in) {
        return read(in, false);
    }


    /**
     * 读取文件流
     *
     * @param in          文件输入流 注意使用 BufferedInputStream-不然不支持2003版
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @param more1000    是否超过1000行
     * @return
     */
    public static List<Object> read(InputStream in, Integer sheetNo, Integer headLineMun, boolean more1000) {
        Sheet sheet = new Sheet(sheetNo, headLineMun);
        return read(in, sheet, more1000);
    }


    /**
     * 读取文件流 默认未超过1000行
     *
     * @param in          文件输入流 注意使用 BufferedInputStream-不然不支持2003版
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @return
     */
    public static List<Object> read(InputStream in, Integer sheetNo, Integer headLineMun) {
        return read(in, sheetNo, headLineMun, false);
    }


    /**
     * 使用自定义的sheet读取指定路径的excel文件
     *
     * @param filePath excel文件路径
     * @param sheet    自定义sheet对象
     * @param more1000 是否超过1000行
     * @return 读取成功返回List<List>， 否则返回null
     */
    private static List<Object> read(String filePath, Sheet sheet, boolean more1000) {
        if (StringUtils.isEmpty(filePath)) {
            return new ArrayList<>();
        }
        sheet = sheet == null ? initSheet : sheet;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return read(inputStream, sheet, more1000);
        } catch (FileNotFoundException e) {
            logger.error("文件未找到");
        }
        return new ArrayList<>();
    }


    /**
     * 使用默认的sheet读取指定路径的excel文件
     *
     * @param filePath excel文件路径
     * @param more1000 是否超过1000行
     * @return 读取成功返回List<List>，否则返回null
     */
    public static List<Object> read(String filePath, boolean more1000) {
        return read(filePath, null, more1000);
    }


    /**
     * 使用默认的sheet读取指定路径的excel文件 默认未超过1000行
     *
     * @param filePath excel文件路径
     * @return 读取成功返回List<List>，否则返回null
     */
    public static List<Object> read(String filePath) {
        return read(filePath, false);
    }


    /**
     * 读取文件
     *
     * @param filePath    文件路径
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @param more1000    是否超过1000行
     * @return
     */
    public static List<Object> read(String filePath, Integer sheetNo, Integer headLineMun, boolean more1000) {
        Sheet sheet = new Sheet(sheetNo, headLineMun);
        return read(filePath, sheet, more1000);
    }


    /**
     * 读取文件 默认未超过1000行
     *
     * @param filePath    文件路径
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @return
     */
    public static List<Object> read(String filePath, Integer sheetNo, Integer headLineMun) {
        return read(filePath, sheetNo, headLineMun, false);
    }


    /**
     * 读取文件流映射为对象
     *
     * @param in          文件流 - 注意使用 BufferedInputStream-不然不支持2003版
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @param clazz       需要映射类 需要extends BaseRowModel
     * @param more1000    是否超过1000行
     * @return
     */
    public static List<Object> readStream2Obj(InputStream in, Integer sheetNo, Integer headLineMun, Class<? extends BaseRowModel> clazz, boolean more1000) {
        Sheet sheet = new Sheet(sheetNo, headLineMun, clazz);
        return read(in, sheet, more1000);
    }


    /**
     * 读取文件流映射为对象 默认未超过1000行
     *
     * @param in          文件流 注意使用 BufferedInputStream-不然不支持2003版
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @param clazz       需要映射类 需要extends BaseRowModel
     * @return
     */
    public static List<Object> readStream2Obj(InputStream in, Integer sheetNo, Integer headLineMun, Class<? extends BaseRowModel> clazz) {
        return readStream2Obj(in, sheetNo, headLineMun, clazz, false);
    }


    /**
     * 读取文件映射为对象
     *
     * @param in       文件流 注意使用 BufferedInputStream-不然不支持2003版
     * @param clazz    需要映射类 需要extends BaseRowModel
     * @param more1000 是否超过1000行
     * @return
     */
    public static List<Object> readStream2Obj(InputStream in, Class<? extends BaseRowModel> clazz, boolean more1000) {
        return readStream2Obj(in, INDEX_ONE, INDEX_ONE, clazz, more1000);
    }


    /**
     * 读取文件映射为对象
     *
     * @param in    文件流 注意使用 BufferedInputStream-不然不支持2003版
     * @param clazz 需要映射类 需要extends BaseRowModel
     * @return
     */
    public static List<Object> readStream2Obj(InputStream in, Class<? extends BaseRowModel> clazz) {
        return readStream2Obj(in, clazz, false);
    }


    /**
     * 读取文件映射为对象
     *
     * @param filePath    文件路径
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @param clazz       需要映射类 需要extends BaseRowModel
     * @param more1000    是否超过1000行
     * @return
     */
    public static List<Object> readFile2Obj(String filePath, Integer sheetNo, Integer headLineMun, Class<? extends BaseRowModel> clazz, boolean more1000) {
        Sheet sheet = new Sheet(sheetNo, headLineMun, clazz);
        return read(filePath, sheet, more1000);
    }


    /**
     * 读取文件映射为对象 默认未超过1000行
     *
     * @param filePath    文件路径
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 表头所在行号 从 0 开始
     * @param clazz       需要映射类 需要extends BaseRowModel
     * @return
     */
    public static List<Object> readFile2Obj(String filePath, Integer sheetNo, Integer headLineMun, Class<? extends BaseRowModel> clazz) {
        return readFile2Obj(filePath, sheetNo, headLineMun, clazz, false);
    }


    /**
     * 读取文件映射为对象 默认未超过1000行
     *
     * @param filePath 文件路径
     * @param clazz    需要映射类 需要extends BaseRowModel
     * @param more1000 是否超过1000行
     * @return
     */
    public static List<Object> readFile2Obj(String filePath, Class<? extends BaseRowModel> clazz, boolean more1000) {
        return readFile2Obj(filePath, INDEX_ONE, INDEX_ONE, clazz, false);
    }


    /**
     * 读取文件映射为对象 默认未超过1000行
     *
     * @param filePath 文件路径
     * @param clazz    需要映射类 需要extends BaseRowModel
     * @return
     */
    public static List<Object> readFile2Obj(String filePath, Class<? extends BaseRowModel> clazz) {
        return readFile2Obj(filePath, clazz, false);
    }


    /**
     *  =======================================>>>        EXCEL 读取方法 END...
     */

    /**
     *      华丽的分割线  \(^o^)/~ 华丽的分割线
     */

    /**
     *  =======================================>>>        EXCEL 写入方法 START...
     */


    /**
     * 生成excel,数据以集合的方式进行写入,默认的sheet方式
     *
     * @param filePath 需要生成的文件路径
     * @param data     数据
     * @param head     表头
     */
    public static void write(String filePath, List<List<Object>> data, List<String> head) {
        write(filePath, data, head, "sheet1");
    }

    /**
     * 生成excel,数据以集合的方式进行写入,自定义sheet名称
     *
     * @param filePath  需要生成的文件路径
     * @param data      数据
     * @param head      表头
     * @param sheetName sheet名称
     */
    public static void write(String filePath, List<List<Object>> data, List<String> head, String sheetName) {
        write(filePath, data, head, 1, 0, sheetName);
    }

    /**
     * 生成excel,数据以集合的方式进行写入,自定义sheet
     *
     * @param filePath    需要生成的文件路径
     * @param data        数据
     * @param head        表头
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 开始行号 从 0 开始
     */
    public static void write(String filePath, List<List<Object>> data, List<String> head, Integer sheetNo, Integer headLineMun) {
        write(filePath, data, head, sheetNo, headLineMun, "sheet1");
    }

    public static void write(String filePath, List<List<Object>> data, List<String> head, Integer sheetNo, Integer headLineMun, String sheetName) {
        Sheet sheet = new Sheet(sheetNo, headLineMun);
        sheet.setSheetName(sheetName);
        sheet.setAutoWidth(true);
        write(filePath, data, head, sheet);
    }


    /**
     * 生成excel,数据以集合的方式进行写入,自定义sheet方式
     *
     * @param filePath 需要生成的文件路径
     * @param data     数据
     * @param head     表头
     * @param sheet    sheet对象
     */
    private static void write(String filePath, List<List<Object>> data, List<String> head, Sheet sheet) {
        if (!CollectionUtils.isEmpty(head)) {
            List<List<String>> list = new ArrayList<>();
            head.forEach(h -> list.add(Collections.singletonList(h)));
            sheet.setHead(list);
        }
        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            outputStream = new FileOutputStream(filePath);
            writer = EasyExcelFactory.getWriter(outputStream);
            writer.write1(data, sheet);
        } catch (FileNotFoundException e) {
            logger.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {

            if (writer != null) {
                writer.finish();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("excel文件生成失败, 失败原因：{}", e);
                }
            }
        }
    }


    /**
     * 生成excel,数据以模型对象的方式进行写入,默认sheet方式
     *
     * @param filePath 需要生成的文件路径
     * @param data     模型数据对象
     */
    public static void write(String filePath, List<? extends BaseRowModel> data) {
        write(filePath, data, "sheet1");
    }


    /**
     * 生成excel,数据以模型对象的方式进行写入,自定义sheet名称
     *
     * @param filePath  需要生成的文件路径
     * @param data      模型数据对象
     * @param sheetName sheet名称
     */
    public static void write(String filePath, List<? extends BaseRowModel> data, String sheetName) {
        write(filePath, data, 1, 0, sheetName);
    }


    /**
     * 生成excel,数据以模型对象的方式进行写入,自定义sheet下标及行号
     *
     * @param filePath    需要生成的文件路径
     * @param data        模型数据对象
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 开始行号 从 0 开始
     */
    public static void write(String filePath, List<? extends BaseRowModel> data, Integer sheetNo, Integer headLineMun) {
        write(filePath, data, sheetNo, headLineMun, null);
    }


    /**
     * 生成excel,数据以模型对象的方式进行写入,自定义sheet下标及行号
     *
     * @param filePath    需要生成的文件路径
     * @param data        模型数据对象
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 开始行号 从 0 开始
     * @param sheetName   sheet名称
     */
    public static void write(String filePath, List<? extends BaseRowModel> data, Integer sheetNo, Integer headLineMun, String sheetName) {
        Sheet sheet = new Sheet(sheetNo, headLineMun);
        sheet.setAutoWidth(true);
        sheet.setSheetName(StringUtils.isEmpty(sheetName) ? "sheet1" : sheetName);
        write(filePath, data, sheet);
    }

    /**
     * 生成excel,数据以模型对象的方式进行写入,默认sheet方式
     *
     * @param filePath 需要生成的文件路径
     * @param data     模型数据对象
     * @param sheet    sheet对象
     */
    private static void write(String filePath, List<? extends BaseRowModel> data, Sheet sheet) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        /**
         *  @Warning 如果使用的是默认的sheet情况需要设置class, 对象类型可以根据data获取data.get(0).getClass()
         */
        sheet.setClazz(data.get(0).getClass());
        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            outputStream = new FileOutputStream(filePath);
            writer = EasyExcelFactory.getWriter(outputStream);
            writer.write(data, sheet);
        } catch (FileNotFoundException e) {
            logger.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {

            if (writer != null) {
                writer.finish();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("excel文件生成失败, 失败原因：{}", e);
                }
            }

        }
    }


    /**
     * 生成多sheet的excel文件，数据以模型对象的方式进行写入  TODO
     *
     * @param filePath              需要生成的文件路径
     * @param multipleSheelPropetys
     */
    private static void writeMultiple(String filePath, List<MultipleSheelPropety> multipleSheelPropetys) {
        if (CollectionUtils.isEmpty(multipleSheelPropetys)) {
            return;
        }
        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            outputStream = new FileOutputStream(filePath);
            writer = EasyExcelFactory.getWriter(outputStream);
            // 循环每一个sheet需要写入的内容
            int sheetNo = 1;
            for (MultipleSheelPropety multipleSheelPropety : multipleSheelPropetys) {
                // 设置sheet对象 这里貌似不能使用默认的initSheet
                Sheet sheet = multipleSheelPropety.getSheet() != null ? multipleSheelPropety.getSheet() : new Sheet(sheetNo);
                if (!CollectionUtils.isEmpty(multipleSheelPropety.getData())) {
                    // 这里绑定每个sheet内容绑定的模型
                    sheet.setClazz(multipleSheelPropety.getData().get(0).getClass());
                }
                sheet.setSheetNo(sheetNo);
                sheet.setSheetName("sheet" + sheetNo);
                writer.write(multipleSheelPropety.getData(), sheet);
                sheetNo++;
            }
        } catch (FileNotFoundException e) {
            logger.error("找不到文件或文件路径错误, 文件：{}", filePath);
        } finally {

            if (writer != null) {
                writer.finish();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("excel文件生成失败, 失败原因：{}", e);
                }
            }
        }
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response HttpServletResponse
     * @param data     List<List<Object>> 数据
     * @param head     List<String> 表头
     */
    public static void export(HttpServletResponse response, List<List<Object>> data, List<String> head) {
        export(response, data, head, UUID.randomUUID().toString(), "sheet1");
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     * 默认第一个sheet，第一行开始
     *
     * @param response HttpServletResponse
     * @param data     List<List<Object>> 数据
     * @param head     List<String> 表头
     * @param fileName 文件名称
     */
    public static void export(HttpServletResponse response, List<List<Object>> data, List<String> head, String fileName) {
        export(response, data, head, fileName, "sheet1");
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response  HttpServletResponse
     * @param data      List<List<Object>> 数据
     * @param head      List<String> 表头
     * @param fileName  文件名称
     * @param sheetName sheet名称
     */
    public static void export(HttpServletResponse response, List<List<Object>> data, List<String> head, String fileName, String sheetName) {
        export(response, data, head, fileName, sheetName, INDEX_ONE, INDEX_ZERO);
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response    HttpServletResponse
     * @param data        List<List<Object>>
     * @param head        List<String> 表头
     * @param fileName    文件名称
     * @param sheetNo     表格sheet下标 从 1 开始
     * @param headLineMun 开始行号 从 0 开始
     */
    public static void export(HttpServletResponse response, List<List<Object>> data, List<String> head, String fileName, Integer sheetNo, Integer headLineMun) {
        export(response, data, head, fileName, "sheet1", sheetNo, headLineMun);
    }

    /**
     * 导出excel  单个sheet
     *
     * @param response    HttpServletResponse
     * @param data        List<List<Object>>
     * @param head        List<String> 表头
     * @param fileName    文件名称
     * @param sheetName   sheet名称
     * @param sheetNo     sheet下标 从 1 开始
     * @param headLineMun 行号 从 0 开始
     */
    public static void export(HttpServletResponse response, List<List<Object>> data, List<String> head, String fileName, String sheetName, Integer sheetNo, Integer headLineMun) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        Sheet sheet = new Sheet(sheetNo, headLineMun);
        sheet.setSheetName(StringUtils.isEmpty(sheetName) ? "sheet1" : sheetName);
        sheet.setAutoWidth(true);
        if (!CollectionUtils.isEmpty(head)) {
            List<List<String>> list = new ArrayList<>();
            head.forEach(h -> list.add(Collections.singletonList(h)));
            sheet.setHead(list);
        }
        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(new String(fileName + ".xlsx"),"UTF8"));
            // 输出流对象
            outputStream = response.getOutputStream();

            writer = EasyExcelFactory.getWriter(outputStream);
            writer.write1(data, sheet);
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的编码异常");
        } catch (IOException e) {
            logger.error("IO异常");
        } finally {
            if (writer != null) {
                writer.finish();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("IO异常");
                }
            }
        }
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response HttpServletResponse
     * @param data     List<? extends BaseRowModel> 数据
     */
    public static void exportBaseRowModel(HttpServletResponse response, List<? extends BaseRowModel> data) {
        exportBaseRowModel(response, data, UUID.randomUUID().toString());
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response HttpServletResponse
     * @param data     List<? extends BaseRowModel> 数据
     * @param fileName 文件名称
     */
    public static void exportBaseRowModel(HttpServletResponse response, List<? extends BaseRowModel> data, String fileName) {
        exportBaseRowModel(response, data, fileName, "sheet1");
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response  HttpServletResponse
     * @param data      List<? extends BaseRowModel> 数据
     * @param fileName  文件名称
     * @param sheetName sheet名称
     */
    public static void exportBaseRowModel(HttpServletResponse response, List<? extends BaseRowModel> data, String fileName, String sheetName) {
        exportBaseRowModel(response, data, fileName, sheetName, INDEX_ONE, INDEX_ZERO);
    }


    /**
     * 导出excel 默认sheet名称 单个sheet
     *
     * @param response    HttpServletResponse
     * @param data        List<? extends BaseRowModel> 数据
     * @param fileName    文件名称
     * @param sheetNo     sheet下标 从 1 开始
     * @param headLineMun 行号 从 0 开始
     */
    public static void exportBaseRowModel(HttpServletResponse response, List<? extends BaseRowModel> data, String fileName, Integer sheetNo, Integer headLineMun) {
        exportBaseRowModel(response, data, fileName, "sheet1", sheetNo, headLineMun);
    }


    /**
     * 导出excel  单个sheet
     *
     * @param response    HttpServletResponse
     * @param data        List<? extends BaseRowModel> 数据
     * @param fileName    文件名称
     * @param sheetName   sheet名称
     * @param sheetNo     sheet下标 从 1 开始
     * @param headLineMun 行号 从 0 开始
     */
    public static void exportBaseRowModel(HttpServletResponse response, List<? extends BaseRowModel> data, String fileName, String sheetName, Integer sheetNo, Integer headLineMun) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        // @Warning 设置 sheet 对象 必须设置class
//        Sheet sheet = new Sheet(sheetNo, headLineMun, data.get(0).getClass());
//        sheet.setSheetName(StringUtils.isEmpty(sheetName) ? "sheet1" : sheetName);
//        sheet.setAutoWidth(true);
//        OutputStream outputStream = null;
//        ExcelWriter writer = null;
        try {
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(new String(fileName + ".xlsx"),"UTF8"));
            // 输出流对象
//            outputStream = response.getOutputStream();
//            writer = EasyExcelFactory.getWriter(outputStream);
//            writer.write(data, sheet);
//            outputStream.flush();

            EasyExcel.write(response.getOutputStream(), data.get(0).getClass()).sheet(StringUtils.isEmpty(sheetName) ? "sheet1" : sheetName).doWrite(data);

        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的编码异常");
        } catch (IOException e) {
            logger.error("IO异常");
        } finally {
//            if (writer != null) {
//                writer.finish();
//            }
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    logger.error("IO异常");
//                }
//            }
        }
    }



    // TODO

    /**
     * 导出excel - 根据模型对象 - 支持 多 sheet导出
     *
     * @param response              HttpServletResponse
     * @param fileName              文件名称
     * @param multipleSheelPropetys
     */
    public static void exportMultiSheet(HttpServletResponse response, String fileName, List<MultipleSheelPropety> multipleSheelPropetys) {
        exportMultiSheet(response, fileName, multipleSheelPropetys, null);
    }


    /**
     * 导出excel - 根据模型对象 - 支持 多 sheet导出
     *
     * @param response              HttpServletResponse
     * @param fileName              文件名称
     * @param sheetNames            sheet名称集合
     * @param multipleSheelPropetys
     */
    public static void exportMultiSheet(HttpServletResponse response, String fileName, List<MultipleSheelPropety> multipleSheelPropetys, List<String> sheetNames) {
        if (CollectionUtils.isEmpty(multipleSheelPropetys)) {
            return;
        }
        List<String> tempSheetNames = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sheetNames)) {
            tempSheetNames = sheetNames;
        }
        if (tempSheetNames.size() < multipleSheelPropetys.size()) {
            for (int i = tempSheetNames.size(); i <= multipleSheelPropetys.size(); i++) {
                tempSheetNames.add("sheet" + (i + 1));
            }
        }
        OutputStream outputStream = null;
        ExcelWriter writer = null;
        try {
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(new String(fileName + ".xlsx"),"UTF8"));

            outputStream = response.getOutputStream();
            writer = EasyExcelFactory.getWriter(outputStream);
            // 循环每一个sheet需要写入的内容
            int sheetNo = 1;
            for (MultipleSheelPropety multipleSheelPropety : multipleSheelPropetys) {
                // 设置sheet对象 这里貌似不能使用默认的initSheet
                Sheet sheet = multipleSheelPropety.getSheet() != null ? multipleSheelPropety.getSheet() : new Sheet(sheetNo);
                if (!CollectionUtils.isEmpty(multipleSheelPropety.getData())) {
                    // 这里绑定每个sheet内容绑定的模型
                    sheet.setClazz(multipleSheelPropety.getData().get(0).getClass());
                }
                sheet.setSheetNo(sheetNo);
                sheet.setSheetName(tempSheetNames.get(sheetNo - 1));
                writer.write(multipleSheelPropety.getData(), sheet);
                sheetNo++;
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的编码异常");
        } catch (IOException e) {
            logger.error("IO异常");
        } finally {
            try {
                if (writer != null) {
                    writer.finish();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.error("excel文件导出失败, 失败原因：{}", e);
            }
        }
    }


    /**
     *  =======================================>>>        EXCEL 写入方法 END...
     */
}
