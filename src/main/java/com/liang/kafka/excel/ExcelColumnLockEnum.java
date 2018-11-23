package com.liang.kafka.excel;

/**
 * @author lianghaiyang
 * @date 2018/08/22
 */
public enum ExcelColumnLockEnum {
    /**
     * 是否锁定该列
     */
    UN_LOCKED(false, "不锁定该列"),
    LOCKED(true, "锁定该列");

    private boolean isLocked;
    private String description;

    ExcelColumnLockEnum(boolean isLocked, String description) {
        this.isLocked = isLocked;
        this.description = description;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getDescription() {
        return description;
    }

    public static ExcelColumnLockEnum getByCode(Boolean isLocked) {
        if (isLocked == null) {
            return null;
        }
        ExcelColumnLockEnum[] values = ExcelColumnLockEnum.values();
        for (ExcelColumnLockEnum value : values) {
            if (isLocked.equals(value.isLocked())) {
                return value;
            }
        }
        return null;
    }
}
