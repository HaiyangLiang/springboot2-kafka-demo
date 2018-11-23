package com.liang.kafka;

import com.liang.kafka.excel.ExcelColumnTypeEnum;
import com.liang.kafka.excel.ExcelData;
import com.liang.kafka.excel.ExcelUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author lianghaiyang
 * @date 2018/11/08
 */
public class KafkaTest {
    @Test
    public void test(){
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            System.out.println(random.nextInt(2));
        }
    }
    @Test
    public void test1() throws ClassNotFoundException, FileNotFoundException {
        File file = new File("F:\\WorkSpace\\idea\\bonc\\kafka-sender\\src\\main\\resources\\电子一所正式上报数据1.xlsx");
        InputStream inputStream = new FileInputStream(file);
        // 从excel中获取数据, 封装入ExcelData
        List<ExcelColumnTypeEnum> cellTypes = Arrays.asList(ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING,
                ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING,
                ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING,
                ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING);
        ExcelData excelData = ExcelUtils.readExcel(inputStream, ".xlsx", cellTypes);
//        System.out.println(excelData.getRows());
        List<List<Object>> rows = excelData.getRows();
        List<String> strings = new ArrayList<>();
        for (List<Object> row : rows) {
            String collect = row.stream().map(String::valueOf).collect(Collectors.joining(","));
            String dateString = "'"+collect.substring(collect.lastIndexOf(",")+1)+"'";
            strings.add("("+collect.substring(0,collect.lastIndexOf(","))+","+dateString+")");
        }
        System.out.println(strings);
    }
}
