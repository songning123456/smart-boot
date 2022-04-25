package com.sonin.ssh.service;

/**
 * @author sonin
 * @date 2020/10/1 13:05
 */
public interface ILineProcessor {

    /**
     * @param line    内容
     * @param lineNum 行号:1开始
     * @throws Exception
     */
    void process(String line, int lineNum) throws Exception;

    /**
     * 所有的行处理完毕回调该方法
     */
    void finish();
}
