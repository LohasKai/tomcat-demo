package com.mhz.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticResourceUtil {

    /**
     * 获取静态资源文件的绝对路劲
     */
    public static String getAbsolutePath(String path){
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replace("\\\\", "/") + path;
    }

    /**
     * 读取静态资源输出流  根据输出流输出
     */
    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {
        int count = 0;
        while (count == 0){
            count = inputStream.available();
        }
        int resourceSize = count;
        //输出http请求头  然后输出具体内容
        long written = 0;
        int byteSize = 1024;
        byte[] bytes = new byte[byteSize];
        while (written < resourceSize){
            //说明剩余字节数不足1024
            if (written + byteSize > resourceSize){
                byteSize = (int)(resourceSize - written);
                bytes = new byte[byteSize];
            }
            inputStream.read(bytes);
            System.out.println(new String(bytes));
            String str =  HttpProtocolUtil.getHttpHeader200(bytes.length) + new String(bytes);
            outputStream.write(str.getBytes());
            outputStream.flush();
            written += byteSize;
        }

    }
}
