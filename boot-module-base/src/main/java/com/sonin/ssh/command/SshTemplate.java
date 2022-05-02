package com.sonin.ssh.command;

import ch.ethz.ssh2.Connection;
import com.sonin.ssh.pojo.SshResult;
import com.sonin.ssh.pojo.SshSession;
import com.sonin.ssh.enums.ConstantEnum;
import com.sonin.ssh.exception.SshException;
import com.sonin.ssh.callback.ISshCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author sonin
 * @date 2020/10/1 10:33
 */
@Slf4j
@Component
public class SshTemplate {

    /**
     * 通过回调执行命令
     *
     * @param ip
     * @param iSshCallback
     * @return
     * @throws SshException
     */
    public SshResult execute(String ip, ISshCallback iSshCallback) throws SshException {
        return execute(ip, ConstantEnum.getValue("ssh", "defaultPort"), ConstantEnum.getValue("ssh", "username"), ConstantEnum.getValue("ssh", "password"), iSshCallback);
    }

    public SshResult execute(String ip, int port, String username, String password, ISshCallback iSshCallback) throws SshException {
        SshResult sshResult;
        Connection connection = null;
        try {
            connection = getConnection(ip, port, username, password);
            sshResult = iSshCallback.call(new SshSession(connection, ip + ":" + port));
        } catch (Exception e) {
            log.error("Ssh Error: {}", e.getMessage());
            throw new SshException("SSH error: " + e.getMessage(), e);
        } finally {
            close(connection);
        }
        return sshResult;
    }

    /**
     * 获取连接并校验
     *
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    private Connection getConnection(String ip, int port, String username, String password) throws Exception {
        Connection connection = new Connection(ip, port);
        connection.connect(null, ConstantEnum.getValue("ssh", "connectionTimeout"), ConstantEnum.getValue("ssh", "connectionTimeout"));
        boolean isAuthenticated = connection.authenticateWithPassword(username, password);
        if (!isAuthenticated) {
            log.error("ssh登录失败; username {}, password {}", username, password);
            throw new Exception("SSH authentication failed with [ userName: " + username + ", password: " + password + "]");
        }
        return connection;
    }

    /**
     * 关闭ssh connection连接
     *
     * @param connection
     */
    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                log.error("connection close fail: {}", e.getMessage());
            }
        }
    }

}
