package com.sonin.ssh.pojo;

import ch.ethz.ssh2.StreamGobbler;
import com.sonin.ssh.service.ILineProcessor;
import com.sonin.ssh.service.impl.AbstractDefaultLineProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author sonin
 * @date 2020/10/1 16:23
 */
@Slf4j
public class Stream {

    /**
     * 获取调用命令后的返回结果
     *
     * @param inputStream
     * @return
     */
    public String getResult(InputStream inputStream) {
        final StringBuilder stringBuilder = new StringBuilder();
        ILineProcessor iLineProcessor = new AbstractDefaultLineProcessor() {
            @Override
            public void process(String line, int lineNum) {
                if (lineNum > 1) {
                    stringBuilder.append(System.lineSeparator());
                }
                stringBuilder.append(line);
            }
        };
        this.processStream(inputStream, iLineProcessor);
        return stringBuilder.length() > 0 ? stringBuilder.toString() : null;
    }

    /**
     * 从流中获取内容
     *
     * @param inputStream
     * @param iLineProcessor
     */
    public void processStream(InputStream inputStream, ILineProcessor iLineProcessor) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new StreamGobbler(inputStream)));
            String line;
            int lineNum = 1;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    iLineProcessor.process(line, lineNum);
                } catch (Exception e) {
                    log.error("error line: {}", e.getMessage());
                }
                lineNum++;
            }
            iLineProcessor.finish();
        } catch (Exception e) {
            log.error("从流中获取内容失败: {}", e.getMessage());
        } finally {
            close(bufferedReader);
        }
    }

    /**
     * 关闭流
     *
     * @param bufferedReader
     */
    private void close(BufferedReader bufferedReader) {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                log.error("关闭流失败: {}", e.getMessage());
            }
        }
    }

}
