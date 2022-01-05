package com.vdata.cloud.common.util;

import com.vdata.cloud.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * <p>
 * 文件处理工具类
 * </p>
 *
 * @author xubo
 * @since 2019-12-18
 */
@Slf4j
public class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("FileUtil class");
    }

    /**
     * 保存文件到相应的路径下面
     *
     * @param uploadFile
     * @param filepath
     * @return
     */
    public static String writeFile(MultipartFile uploadFile, String filepath) {

        try {
            String fileName = uploadFile.getOriginalFilename();
            byte[] bytes = uploadFile.getBytes();
            writeFile(filepath, fileName, bytes);
            return fileName;
        } catch (Exception e) {
            log.error("写文件出错！", e);
            return "";
        }
    }

    /**
     * 保存文件到相应的路径下面
     *
     * @param filepath
     * @param fileName
     * @param bytes
     */
    public static void writeFile(String filepath, String fileName, byte[] bytes) {
        try {
            File file = new File(filepath, fileName);
            String suffix = fileName.split("\\.")[1];
            //uploadFile.transferTo(file);
            BufferedOutputStream stream = null;
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();// 新建文件夹
            }
            stream = new BufferedOutputStream(new FileOutputStream(file));//设置文件路径及名字
            stream.write(bytes);// 写入
            stream.close();
        } catch (Exception e) {
            log.error("保存文件出错！", e);
        }
    }

    /**
     * 文件下载
     *
     * @param response
     * @param request
     * @param filePath
     * @return
     */
    public static OutputStream download(HttpServletResponse response, HttpServletRequest request, String filePath) throws IOException {
        //设置文件路径
        File file = new File(filePath);
        return download(response, request, file);
    }

    public static OutputStream download(HttpServletResponse response, HttpServletRequest request, File file) throws IOException {
        if (file.exists()) {
            String fileName = file.getName();
            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
            if (agent.contains("MSIE") || agent.contains("TRIDENT") || (agent.contains("GECKO") && agent.contains("RV:11"))) {  //IE浏览器和Edge浏览器
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {  //其他浏览器
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
            OutputStream os = response.getOutputStream();
            os = writeFileToStream(file, os);
            return os;
        } else {
            log.info("filePath:" + file.getAbsolutePath());
            throw new IOException("文件不存在！");
        }
    }


    /**
     * 将文件写出到输出流
     *
     * @param file
     * @param os
     * @return
     */
    public static OutputStream writeFileToStream(File file, OutputStream os) {
        if (!file.getParentFile().exists()) {//判断文件目录是否存在
            file.getParentFile().mkdirs();
        }
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
            return os;
        } catch (Exception e) {
            log.error("文件下载失败！", e);
        }
        return null;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static File getFile(byte[] bfile, String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {//判断文件目录是否存在
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            file = new File(filePath);
            bos.write(bfile);
        } catch (Exception e) {
            log.error("", e);
        }
        return file;
    }

    /**
     * 删除文件夹里面所有的文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) throws IOException {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                Files.delete(Paths.get(temp.getAbsolutePath()));
            }
            if (temp.isDirectory()) {
                delAllFile(path + Constants.LINE_LEFT + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + Constants.LINE_LEFT + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 删除文件夹
     *
     * @param filePath
     */
    public static void delFolder(String filePath) {
        try {
            delAllFile(filePath); //删除完里面所有内容
            File myFilePath = new File(filePath);
            //删除空文件夹
            Files.delete(Paths.get(myFilePath.getAbsolutePath()));
        } catch (Exception e) {
            log.error("删除文件夹失败", e);
        }
    }

    /**
     * 删除多个文件
     *
     * @param filePathArr
     */
    public static void deleteFile(String... filePathArr) {
        new Thread(() -> {
            try {
                if (CommonUtil.isNotEmpty(filePathArr)) {
                    for (String filePath : filePathArr) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            if (file.isDirectory()) {
                                delFolder(filePath);
                            } else {
                                Files.delete(Paths.get(file.getAbsolutePath()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("删除文件失败", e);
            }
        }).start();
    }

    // 多个附件上传的处理
    public static Map<String, MultipartFile> uploadMulitiFiles(HttpServletRequest request) {
        MultipartHttpServletRequest mrequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = mrequest.getFileMap();
        return fileMap;
    }


    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        int otherPos = fileName.lastIndexOf('.');
        return fileName.substring(otherPos);
    }

    public static String getName(String filePath) {
        if (CommonUtil.isEmpty(filePath)) {
            return filePath;
        }
        int otherPos = filePath.lastIndexOf('/');
        return filePath.substring(otherPos + 1);
    }


    /**
     * @param file     文件
     * @param path     文件存放路径
     * @param fileName 原文件名
     * @return
     */
    public static String upload(MultipartFile file, String path, String fileName) {
        // 生成新的文件名
        String realPath = path + fileName;
        File dest = new File(realPath);
        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        try {
            //保存文件
            file.transferTo(dest);
            return fileName;
        } catch (IOException e) {
            log.error("操作失败", e);
            return null;
        }
    }


    /**
     * 得到文件的扩展名
     *
     * @param fileName 文件名称
     * @return 文件扩展名
     */
    public static String getFileExt(String fileName) {
        int potPos = fileName.lastIndexOf('.') + 1;
        String type = fileName.substring(potPos);
        return type;
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent
        // Pixels
        // boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the
        // screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.TRANSLUCENT;
            /*
             * if (hasAlpha) { transparency = Transparency.BITMASK; }
             */

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        } catch (Exception e) {
            log.error("操作失败", e);
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            // int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
            /*
             * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
             */
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    /**
     * 读取文件内容类
     */
    public static String readFile(String path) {
        BufferedReader reader = null;
        StringBuilder laststr = new StringBuilder();
        File file = new File(path);
        if (!file.getParentFile().exists()) {//判断文件目录是否存在
            file.getParentFile().mkdirs();
        }
        try (FileInputStream fileInputStream = new FileInputStream(path);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                laststr.append(tempString);
            }
            reader.close();
        } catch (Exception e) {
            log.error("操作失败", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("操作失败", e);
                }
            }
        }
        return laststr.toString();
    }

    /*尹健健 2020-7-7 增加下载网络文件工具方法 开始*/

    /**
     * 下载网络文件
     *
     * @param urlPath  url路径
     * @param downPath 文件存储路径
     * @return 文件存储路径
     * @throws MalformedURLException
     */
    public static String downloadNet(String urlPath, String downPath) throws MalformedURLException {
        int bytesum = 0;
        int byteread = 0;
        InputStream inStream = null;
        URL url = new URL(urlPath);
        try {
            URLConnection conn = url.openConnection();
            inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(downPath);
            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            log.info("文件大小为:{}", bytesum);
            inStream.close();
            fs.close();
        } catch (IOException e) {
            downPath = "";
            log.error("下载文件出错", e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error("操作失败", e);
                }
            }
        }
        return downPath;
    }
    /*尹健健 2020-7-7 增加下载网络文件工具方法 结束*/

    public static String getExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
