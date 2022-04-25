package com.sonin.ssh.exception;

/**
 * @author sonin
 * @date 2020/10/1 10:47
 */
public class SshException extends Exception {

    private static final long serialVersionUID = -6213665149000064880L;

    public SshException() {
        super();
    }

    public SshException(String message) {
        super(message);
    }

    public SshException(String message, Throwable cause) {
        super(message, cause);
    }

    public SshException(Throwable cause) {
        super(cause);
    }

}
