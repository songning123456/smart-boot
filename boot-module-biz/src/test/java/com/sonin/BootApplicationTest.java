package com.sonin;

import com.sonin.ssh.command.SshTemplate;
import com.sonin.ssh.exception.SshException;
import com.sonin.ssh.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <pre>
 * Spring Test
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/25 16:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class BootApplicationTest {

    @Autowired
    private SshTemplate sshTemplate;

    @Test
    public void testSsh() throws SshException {
        String ip = "192.168.2.121";
        int port = 22;
        String username = "cloud";
        String password = "cloud";

        Result result = sshTemplate.execute(ip, port, username, password, sshSession -> sshSession.executeCommand("ls"));
        System.out.println(result.getResult());
    }

}
