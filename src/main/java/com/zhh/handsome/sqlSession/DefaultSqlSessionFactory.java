package com.zhh.handsome.sqlSession;

import com.zhh.handsome.DataSource.Configuration;
import com.zhh.handsome.Utils.DefaultTransactionFactory;
import com.zhh.handsome.Utils.TransactionFactory;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        // 创建事务
        TransactionFactory transactionFactory = configuration.getTransactionFactory();
        if (transactionFactory == null) {
            transactionFactory = new DefaultTransactionFactory();
        }
        
        try {
            // 创建事务对象
            TransactionFactory.Transaction transaction = transactionFactory.newTransaction(configuration.getDataSource());
            
            // 创建执行器
            Executor executor = new Executor.SimpleExecutor(configuration, transaction);
            
            // 创建并返回SqlSession
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            throw new RuntimeException("创建SqlSession失败: " + e.getMessage(), e);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}