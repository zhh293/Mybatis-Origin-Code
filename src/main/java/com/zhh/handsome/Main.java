package com.zhh.handsome;

import com.zhh.handsome.DataSource.MysqlConfiguration;
import com.zhh.handsome.entity.User;
import com.zhh.handsome.mapper.UserMapper;
import com.zhh.handsome.sqlSession.SqlSession;
import com.zhh.handsome.sqlSession.SqlSessionFactory;
import com.zhh.handsome.sqlSession.SqlSessionFactoryBuilder;
import com.zhh.handsome.Utils.MapperScanner;

public class Main {
    public static void main(String[] args) {
        try {
            // 创建MySQL配置
            MysqlConfiguration mysqlConfig = new MysqlConfiguration();
            mysqlConfig.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC");
            mysqlConfig.setUsername("root");
            mysqlConfig.setPassword("password");

            // 构建数据源
            mysqlConfig.setDataSource(mysqlConfig.buildDataSource());

            // 扫描Mapper接口
            MapperScanner scanner = new MapperScanner(mysqlConfig);
            scanner.scan("com.zhh.handsome.mapper");

            // 创建SqlSessionFactory
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(mysqlConfig);
            SqlSession sqlSession = factory.openSession();
            // 使用SqlSession操作数据库
            try {
                System.out.println("MyBatis框架初始化成功！");

                // 获取UserMapper代理
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

                // 示例操作
                System.out.println("===== 测试查询所有用户 =====");
                // List<User> users = userMapper.selectAll();
                // users.forEach(System.out::println);

                System.out.println("\n===== 测试根据ID查询用户 =====");
                // User user = userMapper.selectById(1L);
                // System.out.println(user);

                System.out.println("\n===== 测试插入用户 =====");
                // User newUser = new User(null, "test", "123456", "test@example.com", "13800138000", 25);
                // int insertResult = userMapper.insert(newUser);
                // System.out.println("插入结果: " + insertResult);
                // sqlSession.commit(); // 提交事务

                System.out.println("\n注意：以上操作需要先创建数据库和表。要启用实际操作，请取消注释相应代码。");
            } catch (Exception e) {
                e.printStackTrace();
            }
            sqlSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}