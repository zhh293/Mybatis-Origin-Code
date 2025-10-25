package com.zhh.handsome.Utils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TypeHandlerRegistry {
    // 存储Java类型到类型处理器的映射
    private final Map<Class<?>, TypeHandler<?>> typeHandlerMap = new HashMap<>();
    
    public TypeHandlerRegistry() {
        // 注册基本类型处理器
        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(String.class, new StringTypeHandler());
        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());
        register(Double.class, new DoubleTypeHandler());
        register(double.class, new DoubleTypeHandler());
        register(Float.class, new FloatTypeHandler());
        register(float.class, new FloatTypeHandler());
        register(Boolean.class, new BooleanTypeHandler());
        register(boolean.class, new BooleanTypeHandler());
    }
    
    // 注册类型处理器
    public <T> void register(Class<T> type, TypeHandler<? extends T> handler) {
        typeHandlerMap.put(type, handler);
    }
    
    // 获取类型处理器
    @SuppressWarnings("unchecked")
    public <T> TypeHandler<T> getTypeHandler(Class<T> type) {
        return (TypeHandler<T>) typeHandlerMap.get(type);
    }
    
    // 类型处理器接口
    public interface TypeHandler<T> {
        // 设置参数
        void setParameter(PreparedStatement ps, int i, T parameter, Class<?> parameterType) throws SQLException;
        // 获取结果（从结果集）
        T getResult(ResultSet rs, String columnName) throws SQLException;
        // 获取结果（从结果集，通过索引）
        T getResult(ResultSet rs, int columnIndex) throws SQLException;
        // 获取结果（从CallableStatement）
        T getResult(CallableStatement cs, int columnIndex) throws SQLException;
    }
    
    // Integer类型处理器实现
    public static class IntegerTypeHandler implements TypeHandler<Integer> {
        @Override
        public void setParameter(PreparedStatement ps, int i, Integer parameter, Class<?> parameterType) throws SQLException {
            if (parameter == null) {
                ps.setNull(i, java.sql.Types.INTEGER);
            } else {
                ps.setInt(i, parameter);
            }
        }
        
        @Override
        public Integer getResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getInt(columnName);
        }
        
        @Override
        public Integer getResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getInt(columnIndex);
        }
        
        @Override
        public Integer getResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getInt(columnIndex);
        }
    }
    
    // String类型处理器实现
    public static class StringTypeHandler implements TypeHandler<String> {
        @Override
        public void setParameter(PreparedStatement ps, int i, String parameter, Class<?> parameterType) throws SQLException {
            if (parameter == null) {
                ps.setNull(i, java.sql.Types.VARCHAR);
            } else {
                ps.setString(i, parameter);
            }
        }
        
        @Override
        public String getResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getString(columnName);
        }
        
        @Override
        public String getResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getString(columnIndex);
        }
        
        @Override
        public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getString(columnIndex);
        }
    }
    
    // 其他基本类型处理器实现（简化版）
    public static class LongTypeHandler implements TypeHandler<Long> {
        @Override
        public void setParameter(PreparedStatement ps, int i, Long parameter, Class<?> parameterType) throws SQLException {
            if (parameter == null) {
                ps.setNull(i, java.sql.Types.BIGINT);
            } else {
                ps.setLong(i, parameter);
            }
        }
        
        @Override
        public Long getResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getLong(columnName);
        }
        
        @Override
        public Long getResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getLong(columnIndex);
        }
        
        @Override
        public Long getResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getLong(columnIndex);
        }
    }
    
    public static class DoubleTypeHandler implements TypeHandler<Double> {
        @Override
        public void setParameter(PreparedStatement ps, int i, Double parameter, Class<?> parameterType) throws SQLException {
            if (parameter == null) {
                ps.setNull(i, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(i, parameter);
            }
        }
        
        @Override
        public Double getResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getDouble(columnName);
        }
        
        @Override
        public Double getResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getDouble(columnIndex);
        }
        
        @Override
        public Double getResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getDouble(columnIndex);
        }
    }
    
    public static class FloatTypeHandler implements TypeHandler<Float> {
        @Override
        public void setParameter(PreparedStatement ps, int i, Float parameter, Class<?> parameterType) throws SQLException {
            if (parameter == null) {
                ps.setNull(i, java.sql.Types.FLOAT);
            } else {
                ps.setFloat(i, parameter);
            }
        }
        
        @Override
        public Float getResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getFloat(columnName);
        }
        
        @Override
        public Float getResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getFloat(columnIndex);
        }
        
        @Override
        public Float getResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getFloat(columnIndex);
        }
    }
    
    public static class BooleanTypeHandler implements TypeHandler<Boolean> {
        @Override
        public void setParameter(PreparedStatement ps, int i, Boolean parameter, Class<?> parameterType) throws SQLException {
            if (parameter == null) {
                ps.setNull(i, java.sql.Types.BOOLEAN);
            } else {
                ps.setBoolean(i, parameter);
            }
        }
        
        @Override
        public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
            return rs.getBoolean(columnName);
        }
        
        @Override
        public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getBoolean(columnIndex);
        }
        
        @Override
        public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
            return cs.getBoolean(columnIndex);
        }
    }
}
