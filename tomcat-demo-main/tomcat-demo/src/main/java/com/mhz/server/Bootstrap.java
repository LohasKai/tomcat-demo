package com.mhz.server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bootstrap {

    /**
     * 定义socket监听的端口号
     */
    private int port = 8080;

    private Map<String, HttpServlet> servletMap = new HashMap<>();

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * minicat启动需要展开的操作
     */
    public  void start() throws Exception {
        //1.0版本  浏览器请求local host:8080 返回 hello tomcat
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("===========>>>tomcat is Server on port:" + port);

        /**
         * tomcat 1.0版本
         */
//        while (true){
//            Socket socket = serverSocket.accept();
//            OutputStream outputStream = socket.getOutputStream();
//            String data = "hello tomcat";
//            String responseText = HttpProtocolUtil.getHttpHeader200(data.getBytes().length) + data;
//            outputStream.write(responseText.getBytes());
//            serverSocket.close();
//        }

        /**
         * tomcat 2.0版本 请求静态资源
         */
//        while (true){
//            Socket socket = serverSocket.accept();
//            InputStream inputStream = socket.getInputStream();
//
//            Request request = new Request(inputStream);
//            Response response = new Response(socket.getOutputStream());
//            response.outputHtml(request.getUrl());
//            socket.close();
//        }

        loadServlet();

        /**
         * tomcat 3.0版本 请求动态资源
         */
//        while (true){
//            Socket socket = serverSocket.accept();
//            InputStream inputStream = socket.getInputStream();
//
//            Request request = new Request(inputStream);
//            Response response = new Response(socket.getOutputStream());
//            if (null == servletMap.get(request.getUrl())){
//                //静态处理
//                response.outputHtml(request.getUrl());
//            }else {
//                //动态处理
//                HttpServlet httpServlet = servletMap.get(request.getUrl());
//                httpServlet.service(request, response);
//            }
//            socket.close();
//        }
        /**
         * 使用线程池改造
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,
                50,
                100L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(50),
                new ThreadPoolExecutor.AbortPolicy());

        while (true){
            Socket socket = serverSocket.accept();
            RequestProcessor processor = new RequestProcessor(socket, servletMap);
            threadPoolExecutor.execute(processor);
        }

    }





    public void loadServlet(){
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletNameElement.getStringValue();
                Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletClassElement.getStringValue();

                //根据servlet-name找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();


                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.start();
    }
}
