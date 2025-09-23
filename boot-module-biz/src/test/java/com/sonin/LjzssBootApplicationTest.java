package com.sonin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sonin.modules.mpp.service.IMPPService;
import com.sonin.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
@ActiveProfiles("ljzss")
@SpringBootTest(classes = BootApplication.class)
public class LjzssBootApplicationTest {

    @Autowired
    private IMPPService mppService;

    /**
     * 递归读取 图片
     */
    @Test
    public void recursionReadImageTest() {
        // 要搜索的文件夹路径，可以根据需要修改
        String folderPath = "E:\\Company\\kingtrol\\035-丽江再生水\\20250923资料";
        File folder = new File(folderPath);
        // 检查文件夹是否存在且是一个目录
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定的路径不存在或不是一个文件夹: " + folderPath);
            return;
        }
        System.out.println("在 " + folderPath + " 中找到的照片:");
        System.out.println("-----------------------------------");
        List<String> imageNameList = new ArrayList<>();
        // 递归查找所有图片文件
        findImageFiles(folder, imageNameList);
        // 存入照片信息
        for (String imageName : imageNameList) {
            String tmpImageName = "files/pipe/" + imageName;
            String[] imageNameArr = imageName.split("_");
            // 管井ID
            String tmpTubewellId = imageNameArr[0];
            // 查询当前管井照片
            Map<String, Object> tubewellMap = mppService.queryForMap("select * from pipenetwork_tubewell", new QueryWrapper<>().eq("id", tmpTubewellId));
            if (tubewellMap == null) {
                continue;
            }
            String tmpImage = ConvertUtils.getString(tubewellMap.get("other_imgs"));
            Set<String> tmpImageSet = new HashSet<>();
            if (!StringUtils.isEmpty(tmpImage)) {
                tmpImageSet.addAll(new HashSet<>(Arrays.asList(tmpImage.split(","))));
            }
            tmpImageSet.add(tmpImageName);
            UpdateWrapper<?> tmpUpdateWrapper = new UpdateWrapper<>();
            tmpUpdateWrapper.set("other_imgs", String.join(",", tmpImageSet))
                    .eq("id", tmpTubewellId);
            mppService.update("pipenetwork_tubewell", tmpUpdateWrapper);
        }
    }

    /**
     * 递归查找文件夹中的所有图片文件
     *
     * @param file 要搜索的文件或文件夹
     */
    private void findImageFiles(File file, List<String> imageNameList) {
        // 如果是目录，则递归查找其子文件和子目录
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            // 确保目录可访问
            if (files != null) {
                for (File subFile : files) {
                    findImageFiles(subFile, imageNameList);
                }
            }
        } else if (file.isFile() && isImageFile(file)) {
            // 如果是文件，检查是否为图片
            // 输出图片文件的名称
            System.out.println(file.getName());
            imageNameList.add(file.getName());
        }
    }

    /**
     * 判断文件是否为图片文件
     *
     * @param file 要判断的文件
     * @return 如果是图片文件则返回true，否则返回false
     */
    private static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        // 定义常见的图片文件扩展名
        List<String> imageSuffixList = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".tiff");
        for (String extension : imageSuffixList) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


}
