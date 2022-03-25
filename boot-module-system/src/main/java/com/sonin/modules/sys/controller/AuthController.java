package com.sonin.modules.sys.controller;

import cn.hutool.core.lang.UUID;
import com.google.code.kaptcha.Producer;
import com.sonin.api.vo.Result;
import com.sonin.constant.Const;
import com.sonin.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    Producer producer;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/captcha")
    public Result<Object> captchaCtrl() throws IOException {
        Result<Object> result = new Result<>();
        String key = UUID.randomUUID().toString();
        String code = producer.createText();
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";
        String base64Img = str + encoder.encode(outputStream.toByteArray());
        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);
        Map<String, Object> resMap = new HashMap<String, Object>() {{
            put("token", key);
            put("captchaImg", base64Img);
        }};
        result.setResult(resMap);
        return result;
    }

}
