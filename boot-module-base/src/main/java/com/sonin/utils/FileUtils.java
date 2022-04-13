package com.sonin.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author sonin
 * @date 2021/12/15 17:05
 */
public class FileUtils {

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static Boolean mkDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 合并分片
     *
     * @param chunkFileList
     * @param mergeFile
     */
    public static void shardMerge(List<File> chunkFileList, File mergeFile) {
        try {
            // 有删 无创建
            if (mergeFile.exists()) {
                mergeFile.delete();
            } else {
                mergeFile.createNewFile();
            }
            // 排序
            chunkFileList.sort((o1, o2) -> {
                if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                    return 1;
                }
                return -1;
            });
            byte[] b = new byte[1024];
            RandomAccessFile writeFile = new RandomAccessFile(mergeFile, "rw");
            for (File chunkFile : chunkFileList) {
                RandomAccessFile readFile = new RandomAccessFile(chunkFile, "r");
                int len;
                while ((len = readFile.read(b)) != -1) {
                    writeFile.write(b, 0, len);
                }
                readFile.close();
            }
            writeFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

}
