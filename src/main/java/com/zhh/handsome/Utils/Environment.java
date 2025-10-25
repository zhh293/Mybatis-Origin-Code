package com.zhh.handsome.Utils;

import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Environment {
    // 环境ID
    private String id;
    // 事务工厂
    private TransactionFactory transactionFactory;
    // 数据源
    private DataSource dataSource;
}
