package com.newland;

/**
 * Created by wot_zhengshenming on 2021/3/24.
 */
public enum ByteCodeType {
    B("Byte"), C("Char"), D("Double"), F("Float"), I("Int"), J("Long"), S("Short"), Z("Boolean"), V("Void"), L("Object");
    private String type;

    ByteCodeType(String type) {
        this.type = type;
    }

    public static ByteCodeType getByteCodeType(String byteCode) {
        for (ByteCodeType byteCodeType : values()) {
            if (ByteCodeType.valueOf(byteCode).equals(byteCodeType)) {
                return byteCodeType;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }
}
