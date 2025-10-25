package com.zhh.handsome.sqlSession;

import com.zhh.handsome.DataSource.Configuration;
import com.zhh.handsome.Utils.MappedStatement;
import com.zhh.handsome.Utils.TransactionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;
    private Executor executor;
    private boolean autoCommit;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
        this.autoCommit = false;
    }

    @Override
    public <T> T selectOne(String statement, Object... parameter) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            if (ms == null) {
                throw new IllegalArgumentException("找不到映射语句: " + statement);
            }
            return executor.query(ms, parameter.length > 0 ? parameter[0] : null);
        } catch (SQLException e) {
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object... parameter) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            if (ms == null) {
                throw new IllegalArgumentException("找不到映射语句: " + statement);
            }
            return executor.queryList(ms, parameter.length > 0 ? parameter[0] : null);
        } catch (SQLException e) {
            throw new RuntimeException("查询失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            if (ms == null) {
                throw new IllegalArgumentException("找不到映射语句: " + statement);
            }
            return executor.update(ms, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("更新失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public void commit() {
        try {
            executor.commit(!autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException("提交事务失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void rollback() {
        try {
            executor.rollback(!autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException("回滚事务失败: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        // 使用动态代理创建Mapper接口的实现
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[]{type},
                new MapperProxy(this)
        );
    }

    @Override
    public void close() {
        executor.close(false);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    // Mapper代理处理器
    private static class MapperProxy implements InvocationHandler {
        private final SqlSession sqlSession;

        public MapperProxy(SqlSession sqlSession) {
            this.sqlSession = sqlSession;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 处理Object类的方法
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }

            // 获取方法的SQL注解
            String sql = null;
            MappedStatement.SqlType sqlType = null;

            if (method.isAnnotationPresent(com.zhh.handsome.annotation.Select.class)) {
                sql = method.getAnnotation(com.zhh.handsome.annotation.Select.class).value();
                sqlType = MappedStatement.SqlType.SELECT;
            } else if (method.isAnnotationPresent(com.zhh.handsome.annotation.Insert.class)) {
                sql = method.getAnnotation(com.zhh.handsome.annotation.Insert.class).value();
                sqlType = MappedStatement.SqlType.INSERT;
            } else if (method.isAnnotationPresent(com.zhh.handsome.annotation.Update.class)) {
                sql = method.getAnnotation(com.zhh.handsome.annotation.Update.class).value();
                sqlType = MappedStatement.SqlType.UPDATE;
            } else if (method.isAnnotationPresent(com.zhh.handsome.annotation.Delete.class)) {
                sql = method.getAnnotation(com.zhh.handsome.annotation.Delete.class).value();
                sqlType = MappedStatement.SqlType.DELETE;
            }

            if (sql == null) {
                throw new RuntimeException("方法 " + method.getName() + " 没有配置SQL注解");
            }

            // 构建statementId
            String statementId = method.getDeclaringClass().getName() + "." + method.getName();

            // 根据SQL类型执行相应操作
            if (sqlType == MappedStatement.SqlType.SELECT) {
                // 判断返回类型是否为List
                if (List.class.isAssignableFrom(method.getReturnType())) {
                    return sqlSession.selectList(statementId, args);
                } else {
                    return sqlSession.selectOne(statementId, args);
                }
            } else {
                return sqlSession.update(statementId, args);
            }
        }
    }
}
