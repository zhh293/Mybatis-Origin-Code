package com.zhh.handsome.DataSource;

import com.zhh.handsome.Utils.Environment;
import com.zhh.handsome.Utils.MappedStatement;
import com.zhh.handsome.Utils.TransactionFactory;
import com.zhh.handsome.Utils.TypeHandlerRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Configuration {
    private DataSource dataSource;
    private Map<String, MappedStatement> mappedStatements;
    private TypeHandlerRegistry typeHandlerRegistry;
    private ObjectFactory objectFactory;
    private Environment environment;
    private TransactionFactory transactionFactory;
    
    public Configuration() {
        this.mappedStatements = new HashMap<>();
        this.typeHandlerRegistry = new TypeHandlerRegistry();
    }
    
    // 添加映射语句
    public void addMappedStatement(String id, MappedStatement mappedStatement) {
        mappedStatements.put(id, mappedStatement);
    }
    
    // 获取映射语句
    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }
    
    // 设置环境
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.dataSource = environment.getDataSource();
        this.transactionFactory = environment.getTransactionFactory();
    }
}

