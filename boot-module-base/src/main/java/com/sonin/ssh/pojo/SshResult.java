package com.sonin.ssh.pojo;

import lombok.Data;

/**
 * @author sonin
 * @date 2020/10/1 10:42
 */
@Data
public class SshResult {

    private Boolean success;

    private String result;

    private Exception exception;

    public SshResult(Boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public SshResult(Boolean success) {
        this.success = success;
    }

    public SshResult(Exception e) {
        this.success = false;
        this.exception = e;
    }

}
