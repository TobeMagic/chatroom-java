package cn.com.util;

import java.io.*;

/**
 * @description: read Resource process
 * @author:  magic
 * @time: 2022/12/5
 */
public class ResourcesUtils {

    public static File getResource(String path, String prefix, String suffix) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            File f = File.createTempFile(prefix, suffix); // 得到file对象
            f.deleteOnExit();
            bos = new BufferedOutputStream(new FileOutputStream(f));  // 转换为流对象
            int length;
            byte[] bytes = new byte[1024];
            is = ResourcesUtils.class.getResourceAsStream(path); // 使用Class对象将文件转化为流
//            bis = new BufferedInputStream(ResourcesUtils.class.getResourceAsStream(path));
            while ((length = is.read(bytes)) > 0) { // 将读取到的字节输入到bos输出对象
                bos.write(bytes, 0, length);
                bos.flush(); // 全部缓存区输出
            }
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {    // 关闭输入输出流
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

}
