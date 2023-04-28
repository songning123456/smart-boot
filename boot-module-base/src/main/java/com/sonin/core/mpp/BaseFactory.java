package com.sonin.core.mpp;

/**
 * @author sonin
 * @date 2021/12/5 8:16
 * Base工厂
 */
public class BaseFactory {

    public static Join JOIN() {
        return new Join();
    }

    public static Where WHERE() {
        return new Where();
    }

    public static Result RESULT() {
        return new Result();
    }

    public static Dict DICT() {
        return new Dict();
    }

}
