package com.zhh.handsome.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Transaction {
    // 事务隔离级别
    IsolationLevel isolation() default IsolationLevel.DEFAULT;
    
    // 事务传播行为
    Propagation propagation() default Propagation.REQUIRED;
    
    // 是否只读
    boolean readOnly() default false;
    
    // 事务超时时间（秒）
    int timeout() default -1;
    
    // 隔离级别枚举
    enum IsolationLevel {
        DEFAULT, READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE
    }
    
    // 传播行为枚举
    enum Propagation {
        REQUIRED, SUPPORTS, MANDATORY, REQUIRES_NEW, NOT_SUPPORTED, NEVER, NESTED
    }
}
