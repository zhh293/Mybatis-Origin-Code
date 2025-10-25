package com.zhh.handsome.Utils;

import com.zhh.handsome.DataSource.Configuration;
import com.zhh.handsome.annotation.Delete;
import com.zhh.handsome.annotation.Insert;
import com.zhh.handsome.annotation.Mapper;
import com.zhh.handsome.annotation.Select;
import com.zhh.handsome.annotation.Update;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapperScanner {
    private Configuration configuration;

    public MapperScanner(Configuration configuration) {
        this.configuration = configuration;
    }

    // 扫描指定包下的Mapper接口
    public void scan(String packageName) throws Exception {
        // 将包名转换为路径
        String packagePath = packageName.replace('.', '/');
        
        // 获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        // 获取包路径下的所有资源
        URL url = classLoader.getResource(packagePath);
        if (url == null) {
            throw new RuntimeException("找不到包: " + packageName);
        }
        
        // 获取包路径对应的文件目录
        File packageDir = new File(url.getFile());
        
        // 递归扫描目录，找到所有类文件
        List<Class<?>> classes = findClasses(packageDir, packageName);
        
        // 处理找到的类
        for (Class<?> clazz : classes) {
            // 检查是否带有@Mapper注解
            if (clazz.isAnnotationPresent(Mapper.class) && clazz.isInterface()) {
                processMapperInterface(clazz);
            }
        }
    }

    // 递归查找目录下的所有类
    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        
        if (!directory.exists()) {
            return classes;
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归处理子目录
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    // 加载类
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        
        return classes;
    }

    // 处理Mapper接口
    private void processMapperInterface(Class<?> mapperInterface) {
        // 获取接口中的所有方法
        Method[] methods = mapperInterface.getDeclaredMethods();
        
        for (Method method : methods) {
            // 创建MappedStatement对象
            MappedStatement mappedStatement = createMappedStatement(mapperInterface, method);
            if (mappedStatement != null) {
                // 生成statementId
                String statementId = mapperInterface.getName() + "." + method.getName();
                // 注册到Configuration中
                configuration.addMappedStatement(statementId, mappedStatement);
            }
        }
    }

    // 根据方法创建MappedStatement
    private MappedStatement createMappedStatement(Class<?> mapperInterface, Method method) {
        MappedStatement mappedStatement = new MappedStatement();
        mappedStatement.setMethod(method);
        
        // 设置SQL内容和SQL类型
        if (method.isAnnotationPresent(Select.class)) {
            Select select = method.getAnnotation(Select.class);
            mappedStatement.setSql(select.value());
            mappedStatement.setSqlType(MappedStatement.SqlType.SELECT);
        } else if (method.isAnnotationPresent(Insert.class)) {
            Insert insert = method.getAnnotation(Insert.class);
            mappedStatement.setSql(insert.value());
            mappedStatement.setSqlType(MappedStatement.SqlType.INSERT);
        } else if (method.isAnnotationPresent(Update.class)) {
            Update update = method.getAnnotation(Update.class);
            mappedStatement.setSql(update.value());
            mappedStatement.setSqlType(MappedStatement.SqlType.UPDATE);
        } else if (method.isAnnotationPresent(Delete.class)) {
            Delete delete = method.getAnnotation(Delete.class);
            mappedStatement.setSql(delete.value());
            mappedStatement.setSqlType(MappedStatement.SqlType.DELETE);
        } else {
            // 没有SQL注解，跳过
            return null;
        }
        
        // 设置返回类型
        mappedStatement.setResultType(method.getReturnType());
        
        // 设置参数类型（简化处理，假设只有一个参数）
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length > 0) {
            mappedStatement.setParameterType(paramTypes[0]);
        }
        
        return mappedStatement;
    }
}