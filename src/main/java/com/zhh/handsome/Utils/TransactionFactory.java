package com.zhh.handsome.Utils;

import javax.sql.DataSource;
import java.sql.Connection;

public interface TransactionFactory {
    // 根据数据库连接创建事务
    Transaction newTransaction(Connection conn);
    
    // 根据数据源创建事务
    Transaction newTransaction(DataSource dataSource);
    
    // 事务接口
    interface Transaction {
        // 获取数据库连接
        Connection getConnection() throws Exception;
        // 提交事务
        void commit() throws Exception;
        // 回滚事务
        void rollback() throws Exception;
        // 关闭事务
        void close() throws Exception;
    }
}
