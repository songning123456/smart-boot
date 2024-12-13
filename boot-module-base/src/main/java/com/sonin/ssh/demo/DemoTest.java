package com.sonin.ssh.demo;

import com.sonin.ssh.command.SshTemplate;
import com.sonin.ssh.exception.SshException;
import com.sonin.ssh.pojo.SshResult;

/**
 * @Author：sonin
 * @Date：2024/12/13 9:55
 */
public class DemoTest {

    public static void main(String[] args) {
        SshTemplate sshTemplate = new SshTemplate();
        String curlStr = "curl --request POST --url https://safety.originwater.com:1443/apis/api-admin/sso/saveSyncInfo --header 'X-Access-Token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MzMyOTk4NzYsInVzZXJuYW1lIjoiYWRtaW4ifQ.Ww1e1DPF8gegVN_Yk8RwRcyqz2kpsXrK35IwW_hCTg0' --header 'content-type: application/json' --data '[{\"realname\": \"测试005\", \"username\": \"测试005\", \"phone\": \"13896966999\", \"password\": \"Aa@123456\"}]'\n";
        try {
            SshResult sshResult = sshTemplate.execute("47.104.23.94", 22, "root", "af67SwtF!fdW3", sshSession -> sshSession.executeCommand(curlStr));
            System.out.println(sshResult.getResult());
        } catch (SshException e) {
            e.printStackTrace();
        }
    }

}
