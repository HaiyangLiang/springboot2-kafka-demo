package com.liang.kafka.excel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lianghaiyang
 * @date 2018/07/18
 */
@Setter
@Getter
@ToString
public class ExcelData {
    /**
     * 表头
     */
    private List<String> titles;
    /**
     * 数据
     */
    private List<List<Object>> rows;
    /**
     * 页签名称
     */
    private String name;
    /**
     * 错误信息, key, 和value分别表示行列
     */
    private Map<Integer, List<Integer>> errorMap = new HashMap<>();

}
