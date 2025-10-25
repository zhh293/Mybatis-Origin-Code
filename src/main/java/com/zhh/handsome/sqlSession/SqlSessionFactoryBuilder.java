package com.zhh.handsome.sqlSession;

import com.zhh.handsome.DataSource.Configuration;
import com.zhh.handsome.Utils.DefaultTransactionFactory;
import com.zhh.handsome.Utils.Environment;
import com.zhh.handsome.Utils.MapperScanner;
import com.zhh.handsome.Utils.TransactionFactory;

import javax.sql.DataSource;

public class SqlSessionFactoryBuilder {
    
    // 根据配置对象构建SqlSessionFactory
    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
    
    // 根据环境构建SqlSessionFactory
    public SqlSessionFactory build(Environment environment) {
        Configuration configuration = new Configuration();
        configuration.setEnvironment(environment);
        return new DefaultSqlSessionFactory(configuration);
    }
    
    // 构建者模式：简化配置过程
    public static class Builder {
        private Configuration configuration;
        
        public Builder() {
            this.configuration = new Configuration();
        }
        
        // 设置数据源
        public Builder dataSource(DataSource dataSource) {
            configuration.setDataSource(dataSource);
            return this;
        }
        
        // 扫描Mapper接口
        public Builder scanMappers(String packageName) {
            try {
                MapperScanner scanner = new MapperScanner(configuration);
                scanner.scan(packageName);
            } catch (Exception e) {
                throw new RuntimeException("扫描Mapper失败: " + e.getMessage(), e);
            }
            return this;
        }
        
        // 设置事务工厂
        public Builder transactionFactory(TransactionFactory transactionFactory) {
            configuration.setTransactionFactory(transactionFactory);
            return this;
        }
        
        // 构建SqlSessionFactory
        public SqlSessionFactory build() {
            // 如果没有设置事务工厂，使用默认的
            if (configuration.getTransactionFactory() == null) {
                configuration.setTransactionFactory(new DefaultTransactionFactory());
            }
            
            return new DefaultSqlSessionFactory(configuration);
        }
    }
}