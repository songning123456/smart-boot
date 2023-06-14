package com.sonin.core.javassist;

import javassist.*;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sonin
 * @date 2021/12/5 18:44
 */
public class Javassist {

    private static final Map<String, Class> cache = new ConcurrentHashMap<>();

    private String className;

    private String similarClassName;

    private Map<String, Class> fieldMap;

    private String prefixPackage = "javassist.";

    Javassist() {
        fieldMap = new LinkedHashMap<>();
    }

    public String getPrefixPackage() {
        return prefixPackage;
    }

    public void setPrefixPackage(String prefixPackage) {
        this.prefixPackage = prefixPackage;
    }

    /**
     * 待创建的类
     *
     * @param className
     * @return
     */
    public Javassist className(String className) {
        this.className = className;
        return this;
    }

    /**
     * 新创建的类class属性继承自similarClass
     *
     * @param similarClassName
     * @return
     */
    public Javassist similarClassName(String similarClassName) {
        this.similarClassName = similarClassName;
        return this;
    }

    public Javassist similarClassName(Class similarClass) {
        this.similarClassName = prefixPackage + similarClass.getName();
        if (!cache.containsKey(this.similarClassName)) {
            synchronized (cache) {
                if (!cache.containsKey(this.similarClassName)) {
                    JavassistFactory.create(similarClass);
                }
            }
        }
        return this;
    }

    /**
     * 添加字段(会生成get/set方法)
     *
     * @param fieldName
     * @param fieldType
     * @return
     */
    public Javassist field(String fieldName, Class fieldType) {
        fieldMap.put(fieldName, fieldType);
        return this;
    }

    public Class buildClass() throws Exception {
        if (similarClassName == null) {
            initClass(ClassEnum.FIELD);
        } else {
            if (!cache.containsKey(similarClassName)) {
                initClass(ClassEnum.FIELD);
            } else {
                initClass(ClassEnum.SIMILAR_CLASS);
            }
        }
        return cache.get(className);
    }

    public Class getClass(String className) {
        return cache.getOrDefault(className, Object.class);
    }

    /**
     * @param classEnum
     * @throws Exception
     */
    private void initClass(ClassEnum classEnum) throws Exception {
        if (!cache.containsKey(className)) {
            synchronized (cache) {
                if (!cache.containsKey(className)) {
                    // 创建ClassPool
                    ClassPool classPool = ClassPool.getDefault();
                    // 生成类的名称
                    CtClass ctClass = classPool.makeClass(className);
                    if (classEnum == ClassEnum.FIELD) {
                        for (Map.Entry<String, Class> item : fieldMap.entrySet()) {
                            initField(classPool, ctClass, item.getValue().getName(), item.getKey());
                        }
                    } else if (classEnum == ClassEnum.SIMILAR_CLASS) {
                        Class similarClass = cache.get(similarClassName);
                        for (Field field : similarClass.getDeclaredFields()) {
                            initField(classPool, ctClass, field.getType().getName(), field.getName());
                        }
                    }
                    // 创建构造方法，指定了构造方法的参数类型和构造方法所属的类
                    CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
                    // 设置方法体
                    ctConstructor.setBody("{\n}");
                    ctClass.addConstructor(ctConstructor);
                    // 缓存生成的类
                    cache.put(className, ctClass.toClass());
                }
            }
        }
    }

    private void initField(ClassPool classPool, CtClass ctClass, String fieldType, String fieldName) throws Exception {
        // 创建字段，指定了字段类型、字段名称、字段所属的类
        CtField ctField = new CtField(classPool.get(fieldType), fieldName, ctClass);
        // 指定该字段使用private修饰
        ctField.setModifiers(Modifier.PRIVATE);
        // 将字段添加到ctClass中
        ctClass.addField(ctField);
        // 设置ctClass字段的getter/setter方法
        String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        ctClass.addMethod(CtNewMethod.getter(getMethodName, ctField));
        ctClass.addMethod(CtNewMethod.setter(setMethodName, ctField));
    }

}
