package com.zhh.handsome.Utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource) {
        return new JdbcTransaction(dataSource);
    }

    // JDBC事务实现
    public static class JdbcTransaction implements Transaction {
        private Connection connection;
        private DataSource dataSource;
        private boolean autoCommit;

        public JdbcTransaction(Connection connection) {
            this.connection = connection;
            try {
                this.autoCommit = connection.getAutoCommit();
            } catch (SQLException e) {
                // 默认设置为true
                this.autoCommit = true;
            }
        }

        public JdbcTransaction(DataSource dataSource) {
            this.dataSource = dataSource;
            this.autoCommit = true;
        }

        @Override
        public Connection getConnection() throws Exception {
            if (connection == null) {
                connection = dataSource.getConnection();
                connection.setAutoCommit(autoCommit);
            }
            return connection;
        }

        @Override
        public void commit() throws Exception {
            if (connection != null && !connection.getAutoCommit()) {
                connection.commit();
            }
        }

        @Override
        public void rollback() throws Exception {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
            }
        }

        @Override
        public void close() throws Exception {
            if (connection != null) {
                // 恢复自动提交设置
                try {
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    // 忽略
                }
                connection.close();
            }
        }
    }
}