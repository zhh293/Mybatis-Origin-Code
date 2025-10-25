# 说一下基本流程吧，把组件之间的配合说明一下

我们自顶向下来讲述，首先看我的main文件，先配置数据库文件，然后配置Mybatis的Configuration文件，并且创建MapperScan扫描整个项目目录，在configuration的MAP中添加所有被Mapper注解
注释的类中的SQL语句，进行一个预编译和预处理。到这里基本上Mybatis的Configuration配置类已经基本配置好了。下面通过SqlSessionFactoryBuilder创建工厂，通过工厂
再创建SqlSession，这个SqlSession用来获取想要的Mapper接口的代理对象，而这个对象之后需要在调用方法的同时执行Sql语句，所以工厂在创建SqlSession的时候一定要传入Configuration
和Executor执行器以及重要的事务处理类，在之后创建代理对象的方法和执行Sql的方法中有用。最后通过mapper类直接调用方法即可操控数据库了





# 这里大部分的类的使用都在这短短的一句sqlSession.getMapper(UserMapper.class)中，我来详细的讲述一下

这里工厂创建的是sqlsession接口的默认实现类DefaultSqlSession，里面的getMapper实现是这样的

```
   public <T> T getMapper(Class<T> type) {
        // 使用动态代理创建Mapper接口的实现
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[]{type},
                new MapperProxy(this)
        );
    }
```

MapperProxy中会定义这个代理对象中的方法应该怎么执行，如下文


```
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
```

可以看出，这个代理对象在执行原始方法的时候，会扫面方法上面的注解并根据不同类型的数据库操作以及返回类型将statementId和方法上面的参数返回给
sqlsession并让她调用对应的方法执行，这时候有人问了，欸主播主播，为什么不用传写好的SQL语句啊，这里貌似只判断了一下是否为空


那恭喜你，问出了一个特别好的问题
，这里我只传statementId是因为MapperScan已经扫面过了所有被注释的类并且把相关的所有SQL语句进行了预编译和预处理，保存在了Configuration的Map集合中

其中的键为statementId，值为对应的SQL语句，所以这时我们只需要把Id的和方法参数传给SqlSession，让SqlSession从自己内部的Configuration中取出来
SQL语句不就可以了吗，于是乎，现在我们便知道了sqlsession中定义的这些方法是干什么的了



```


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
```


从这里也可以看出，sqlsession在这里的作用，从代理类中拿Id和各种参数，通过Id拿到预编译SQL，通过Executor将SQL中的空缺位置用参数填补并且执行，最后将结果返回给用户那边（main类）
像一个交通枢纽，调控这一切。。。




然后这里来说一下事务的自动回滚是如何实现的，我先定义了一个事务工厂接口，里面还定义了一个事务接口，到时候可以让用户自定义实现工厂的实现类以及工厂会造出来自己想要的事务实现类对象


如下
```
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
```


这里我自己自定义实现了一个
```

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

```

需要在哪里使用呢，当然是执行SQL语句的地方，要传到Executor，我们就要开始想Executor是怎么来的，因为最后是sqlsession负责调用的，我们就再去看sqlsession的执行器哪里来的，到这一步了
我相信大家早就猜出来了
当然是工厂造出来的，没错，在SqlSession sqlSession = factory.openSession();这一步，底层干了一个事

```

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

```


好了，这下着整个框架的组件的作用和运行流程就完全可以搞懂了，我是一枚彩笔，有错恳请大家多多指出









    
