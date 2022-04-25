package com.sonin.ssh.pojo;

import lombok.Data;

/**
 * @author sonin
 * @date 2020/10/1 10:42
 */
@Data
public class Result {

    private Boolean success;

    private String result;

    private Exception exception;

    public Result(Boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public Result(Boolean success) {
        this.success = success;
    }

    public Result(Exception e) {
        this.success = false;
        this.exception = e;
    }

}
