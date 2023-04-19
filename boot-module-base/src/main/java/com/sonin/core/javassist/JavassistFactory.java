package com.sonin.core.javassist;

import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.reflect.Field;

/**
 * @author sonin
 * @date 2021/12/5 19:07
 */
public class JavassistFactory {

    public static Javassist create() {
        return new Javassist();
    }

    public static void create(Class... classArray) {
        Javassist javassist;
        Class tmpClazz;
        for (Class clazz : classArray) {
            try {
                javassist = new Javassist();
                javassist.className(javassist.getPrefixPackage() + clazz.getName());
                // 遍历class对象属性
                tmpClazz = clazz;
                while (!"java.lang.Object".equals(tmpClazz.getName())) {
                    Field[] fields = tmpClazz.getDeclaredFields();
                    for (Field field : fields) {
                        TableField tableFieldAnno = field.getAnnotation(TableField.class);
                        if (tableFieldAnno != null && !tableFieldAnno.exist()) {
                            continue;
                        }
                        javassist.field(field.getName(), field.getType());
                    }
                    tmpClazz = tmpClazz.getSuperclass();
                }
                javassist.buildClass();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
