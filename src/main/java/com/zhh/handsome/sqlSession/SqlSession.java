package com.zhh.handsome.sqlSession;

import com.zhh.handsome.DataSource.Configuration;

import java.util.List;

public interface SqlSession {
    // 查询单个结果
    <T> T selectOne(String statement, Object... parameter);
    
    // 查询多个结果
    <E> List<E> selectList(String statement, Object... parameter);
    
    // 插入操作
    int insert(String statement, Object parameter);
    
    // 更新操作
    int update(String statement, Object parameter);
    
    // 删除操作
    int delete(String statement, Object parameter);
    
    // 提交事务
    void commit();
    
    // 回滚事务
    void rollback();
    
    // 获取Mapper接口代理
    <T> T getMapper(Class<T> type);
    
    // 关闭会话
    void close();
    
    // 获取配置
    Configuration getConfiguration();
}
