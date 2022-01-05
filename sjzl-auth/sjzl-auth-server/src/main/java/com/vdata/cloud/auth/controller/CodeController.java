package com.vdata.cloud.auth.controller;

import com.vdata.cloud.common.constant.Constants;
import com.vdata.cloud.common.util.CommonUtil;
import com.vdata.cloud.common.vo.DataResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Api(value = "验证码接口", tags = "验证码")
@Controller
@RequestMapping("/code")
public class CodeController {

    /**
     * 随机生成4位字符验证码
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping(value = "/get")
    public void getCodeImages(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpg"); // 通知浏览器返回的是一张图片
        @SuppressWarnings("unused")
        int charNum = 4;
        int width = 30 * 4;
        int height = 30;
        // 1. 创建一张内存图片
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 2.获得绘图对象
        Graphics graphics = bufferedImage.getGraphics();
        // 3、绘制背景颜色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        // 4、绘制图片边框
        graphics.setColor(Color.GRAY);
        graphics.drawRect(0, 0, width - 1, height - 1);
        // 5、输出验证码内容
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("微软雅黑", Font.BOLD, 20));
        // 随机输出4个字符
        Graphics2D graphics2d = (Graphics2D) graphics;
        String s = "ABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String msg = "";
        int x = 5;
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(26);
            String content = String.valueOf(s.charAt(index));
            msg += content;
            double theta = random.nextInt(45) * Math.PI / 180;
            graphics2d.rotate(theta, x, 18);
            graphics2d.drawString(content, x, 18);
            graphics2d.rotate(-theta, x, 18);
            x += 30;
        }
        // // 6、绘制干扰线
        for (int i = 0; i < 4; i++) {
            int r = random.nextInt(160);
            int g = random.nextInt(160);
            int b = random.nextInt(160);
            graphics.setColor(new Color(r, g, b));
            int x1 = random.nextInt(width);
            int x2 = random.nextInt(width);

            int y1 = random.nextInt(height);
            int y2 = random.nextInt(height);
            graphics.drawLine(x1, y1, x2, y2);
        }
        request.getSession().setAttribute("code", msg);
        graphics.dispose();

        ImageIO.write(bufferedImage, "jpg", response.getOutputStream());


    }


    @GetMapping(value = "/v2/get")
    @ResponseBody
    public DataResult getCodeImagesv2(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DataResult result = new DataResult();


        try {
            //        response.setContentType("image/jpg"); // 通知浏览器返回的是一张图片
            @SuppressWarnings("unused")
            int charNum = 4;
            int width = 30 * 4;
            int height = 30;
            // 1. 创建一张内存图片
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            // 2.获得绘图对象
            Graphics graphics = bufferedImage.getGraphics();
            // 3、绘制背景颜色
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            // 4、绘制图片边框
            graphics.setColor(Color.GRAY);
            graphics.drawRect(0, 0, width - 1, height - 1);
            // 5、输出验证码内容
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("微软雅黑", Font.BOLD, 20));
            // 随机输出4个字符
            Graphics2D graphics2d = (Graphics2D) graphics;
            String s = "ABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
            Random random = new Random();
            String msg = "";
            int x = 5;
            for (int i = 0; i < 4; i++) {
                int index = random.nextInt(26);
                String content = String.valueOf(s.charAt(index));
                msg += content;
                double theta = random.nextInt(45) * Math.PI / 180;
                graphics2d.rotate(theta, x, 18);
                graphics2d.drawString(content, x, 18);
                graphics2d.rotate(-theta, x, 18);
                x += 30;
            }
            // // 6、绘制干扰线
            for (int i = 0; i < 4; i++) {
                int r = random.nextInt(160);
                int g = random.nextInt(160);
                int b = random.nextInt(160);
                graphics.setColor(new Color(r, g, b));
                int x1 = random.nextInt(width);
                int x2 = random.nextInt(width);

                int y1 = random.nextInt(height);
                int y2 = random.nextInt(height);
                graphics.drawLine(x1, y1, x2, y2);
            }
            request.getSession().setAttribute("code", msg);
            graphics.dispose();

//        ImageIO.write(bufferedImage, "jpg", response.getOutputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", stream);

            byte[] bytes = stream.toByteArray();//转换成字节
            BASE64Encoder encoder = new BASE64Encoder();
            String jpg_base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
            jpg_base64 = jpg_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
            Map<String, Object> map = new HashMap<>();
            map.put("data", "data:image/jpg;base64," + jpg_base64);
            map.put("code", msg);
            result.setData(map);
            result.setCode(Constants.RETURN_NORMAL);
            result.setMessage("获取验证码成功");
        } catch (Exception e) {
            result.setCode(Constants.RETURN_UNNORMAL);
            result.setMessage("获取验证码失败");
        }
        return result;


    }

    @ApiOperation(value = "验证验证码")
    @GetMapping(value = "/verify")
    @ResponseBody
    public boolean verifyVerificationCode(HttpSession session, @RequestParam String checkcode)
            throws ServletException, IOException {
        String code = (String) session.getAttribute("code");
        if (CommonUtil.isNotEmpty(code)) {
            session.removeAttribute("code");
            if (code.toLowerCase().equals(checkcode.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
