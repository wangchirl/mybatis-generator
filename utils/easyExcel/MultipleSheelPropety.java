package com.yidao.court.prelitigation.utils.easyExcel;

import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;

import java.util.List;

/**
 * @Classname MultipleSheelPropety
 * @Description easyExcel 多sheet生成excel文件模型
 * @Date 2019/8/25 17:02
 * @Created by 伊人
 */
public class MultipleSheelPropety {

    private List<? extends BaseRowModel> data;

    private Sheet sheet;

    public List<? extends BaseRowModel> getData() {
        return data;
    }

    public void setData(List<? extends BaseRowModel> data) {
        this.data = data;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
}
