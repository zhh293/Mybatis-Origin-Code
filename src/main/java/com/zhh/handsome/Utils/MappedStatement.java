package com.zhh.handsome.Utils;

import lombok.Data;
import java.lang.reflect.Method;

@Data
public class MappedStatement {
    // SQL语句ID（通常是接口名.方法名）
    private String id;
    // SQL语句内容
    private String sql;
    // SQL类型（SELECT, INSERT, UPDATE, DELETE）
    private SqlType sqlType;
    // 返回类型
    private Class<?> resultType;
    // 参数类型
    private Class<?> parameterType;
    // 对应的方法
    private Method method;
    
    // SQL类型枚举
    public enum SqlType {
        SELECT, INSERT, UPDATE, DELETE
    }
}
