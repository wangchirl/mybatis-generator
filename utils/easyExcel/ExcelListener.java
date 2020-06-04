package com.yidao.court.prelitigation.utils.easyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname ExcelListener
 * @Description 解析监听器
 * 每解析一行会回调invoke()方法。
 * 整个excel解析结束会执行doAfterAllAnalysed()方法
 * @Date 2019/8/24 16:33
 * @Created by 伊人
 */

public class ExcelListener extends AnalysisEventListener {

    private Logger logger = LoggerFactory.getLogger(ExcelListener.class);

    // 自定义用于暂时存储data。
    // 可以通过实例获取该值
    private List<Object> data = new ArrayList<Object>();

    @Override
    public void invoke(Object object, AnalysisContext context) {
        data.add(object);// 数据存储到list，供批量处理，或后续自己业务逻辑处理。
        logger.info("当前行===>>> " + context.getCurrentRowNum() + "<<<<>>>>  内容===>>> " + object);
//        doSomething(object);// 根据自己业务做处理
    }

    private void doSomething(Object object) {
        //1、入库调用接口
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // datas.clear();// 解析结束销毁不用的资源
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}

