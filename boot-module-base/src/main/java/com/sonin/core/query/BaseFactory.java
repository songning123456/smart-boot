package com.sonin.core.query;

/**
 * @author sonin
 * @date 2021/12/5 8:16
 * Base工厂
 */
public class BaseFactory {

    public static Join join() {
        return new Join();
    }

    public static Where where() {
        return new Where();
    }

    public static Result result() {
        return new Result();
    }

}
