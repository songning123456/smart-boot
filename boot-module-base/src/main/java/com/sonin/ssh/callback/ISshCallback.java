package com.sonin.ssh.callback;


import com.sonin.ssh.pojo.Result;
import com.sonin.ssh.pojo.SshSession;

/**
 * @author sonin
 * @date 2020/10/1 10:44
 */
public interface ISshCallback {

    /**
     * 执行命令回调
     *
     * @param sshSession
     * @return
     */
    Result call(SshSession sshSession);

}
