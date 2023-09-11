package com.mhz.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread{

    private Socket socket;
    private Map<String, HttpServlet> servletMap;

    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    @Override
    public void run() {
        try {
        InputStream inputStream = socket.getInputStream();
        Request request = null;
        request = new Request(inputStream);
        Response response = new Response(socket.getOutputStream());
        if (null == servletMap.get(request.getUrl())){
            //静态处理
            response.outputHtml(request.getUrl());
        }else {
            //动态处理
            HttpServlet httpServlet = servletMap.get(request.getUrl());
            httpServlet.service(request, response);
        }
        socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
