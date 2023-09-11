package com.mhz.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 封装Response对象  需要依赖于Outputstream
 */
public class Response {

    private OutputStream outputStream;

    public Response(){
    }

    public Response(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    //使用输出流输出指定字符
    public void output(String content)throws Exception{
        outputStream.write(content.getBytes());
    }

    /**
     * 根据url获取绝对路劲
     */
    public void outputHtml(String path) throws Exception {
        //获取静态资源文件的绝对路径
        String absoluteResourcePath = StaticResourceUtil.getAbsolutePath(path);

        //输入静态资源文件
        File file = new File(absoluteResourcePath);
        if (file.exists()){
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        }else {
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }
}
