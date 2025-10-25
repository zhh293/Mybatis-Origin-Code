package com.zhh.handsome.sqlSession;

import com.zhh.handsome.DataSource.Configuration;
import com.zhh.handsome.Utils.MappedStatement;
import com.zhh.handsome.Utils.TransactionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Executor {
    // 查询单个结果
    <T> T query(MappedStatement ms, Object parameter) throws SQLException;
    
    // 查询多个结果
    <E> List<E> queryList(MappedStatement ms, Object parameter) throws SQLException;
    
    // 执行更新操作（INSERT, UPDATE, DELETE）
    int update(MappedStatement ms, Object parameter) throws SQLException;
    
    // 提交事务
    void commit(boolean required) throws SQLException;
    
    // 回滚事务
    void rollback(boolean required) throws SQLException;
    
    // 关闭执行器
    void close(boolean forceRollback);
    
    // 获取事务
    TransactionFactory.Transaction getTransaction();
    
    // 简单执行器实现
    class SimpleExecutor implements Executor {
        //这里面其实不要Configuration也可以，写了反而有误导性，让人容易直接使用Configuration中的数据源去做jdbc操作了
        protected Configuration configuration;
        protected TransactionFactory.Transaction transaction;
        protected boolean closed;
        
        public SimpleExecutor(Configuration configuration, TransactionFactory.Transaction transaction) {
            this.configuration = configuration;
            this.transaction = transaction;
        }
        
        @Override
        public <T> T query(MappedStatement ms, Object parameter) throws SQLException {
            List<T> results = queryList(ms, parameter);
            return results != null && !results.isEmpty() ? results.get(0) : null;
        }
        
        @Override
        public <E> List<E> queryList(MappedStatement ms, Object parameter) throws SQLException {
            // 这里应该实现SQL执行和结果映射的逻辑
            /*try {
                Connection connection = transaction.getConnection();
                System.out.println("获取数据库连接: " + connection);
                System.out.println("执行查询SQL: " + ms.getSql());
                System.out.println("处理结果映射: " + ms.getResultHandler());
                return null;
            }catch (Exception e){
                e.printStackTrace();
                rollback( true);
                close(false);
                throw new RuntimeException(e);
            }finally {
                //提交事务
                commit(true);
                close(false);
            }
            */
            // 简化版本，实际应该使用PreparedStatement执行SQL并处理结果
            System.out.println("执行查询SQL: " + ms.getSql());
            return null; // 简化实现，实际应该返回结果集
        }
        
        @Override
        public int update(MappedStatement ms, Object parameter) throws SQLException {
            // 这里应该实现更新操作的逻辑
            System.out.println("执行更新SQL: " + ms.getSql());
            return 0; // 简化实现，实际应该返回影响的行数
        }
        
        @Override
        public void commit(boolean required) throws SQLException {
            if (transaction != null && required) {
                try {
                    transaction.commit();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        @Override
        public void rollback(boolean required) throws SQLException {
            if (transaction != null && required) {
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        @Override
        public void close(boolean forceRollback) {
            try {
                try {
                    rollback(forceRollback);
                } finally {
                    if (transaction != null) {
                        transaction.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public TransactionFactory.Transaction getTransaction() {
            return transaction;
        }
    }
}
