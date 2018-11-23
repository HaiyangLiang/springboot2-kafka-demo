package com.liang.kafka.excel;

/**
 * @author lianghaiyang
 * @date 2018/08/02
 */
public enum ExcelColumnTypeEnum {
    /**
     * Excel 单元格数据类型
     */
    STRING(0, "非空字符串"),
    NULL_STRING(1, "可空字符串"),
    INT(2, "非空Int型"),
    NULL_INT(3, "可空Int型"),
    LONG(4, "非空Long型"),
    NULL_LONG(5, "可空Long型"),
    DOUBLE(6, "非空Double型"),
    NULL_DOUBLE(7, "可空Double型"),
    BOOLEAN(8, "非空Boolean型"),
    NULL_BOOLEAN(9, "可空Boolean型"),
    DATE(10, "日期类型"),
    COMMA_LONG(11,"逗号间隔的Long"),
    NULL_COMMA_LONG(12,"可空的逗号间隔的Long");

    ExcelColumnTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private int code;
    private String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public ExcelColumnTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        ExcelColumnTypeEnum[] cellTypeEnums = ExcelColumnTypeEnum.values();
        for (ExcelColumnTypeEnum cellTypeEnum : cellTypeEnums) {
            if (cellTypeEnum.getCode() == code) {
                return cellTypeEnum;
            }
        }
        return null;
    }
}
