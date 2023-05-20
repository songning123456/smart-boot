package com.sonin.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2023/5/20 上午10:55
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/cors")
    public String corsCtrl() {
        String result = "cors test success";
        System.out.println(result);
        return result;
    }

}
