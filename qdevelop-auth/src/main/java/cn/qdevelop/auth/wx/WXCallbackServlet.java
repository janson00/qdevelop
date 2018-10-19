package cn.qdevelop.auth.wx;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WXCallbackServlet  extends HttpServlet {
  /**
   * 
   */
  private static final long serialVersionUID = -2904373227263803076L;

  public void doGet(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
    ServletOutputStream stream=null;
    String code = request.getParameter("code") == null ? "" : request.getParameter("code");
    try{
      stream = response.getOutputStream();
      if( code.length() > 0){
        byte[] r = QHttpsQuery.post("https://api.weixin.qq.com/sns/oauth2/access_token","appid="+Concact.APP_ID+"&secret="+Concact.APP_ID+"&code="+code+"&grant_type=authorization_code",request);
        String t = new String(r);
        if(t.indexOf("access_token") > -1){
          t = t.substring(t.indexOf("access_token")+15);
          t = t.substring(0,t.indexOf("\""));
          r = QHttpsQuery.post("https://api.weixin.qq.com/sns/userinfo","access_token=" + t + "&openid=wxc2660be46c9b081e&lang=zh_CN",request );
        }
        if(request.getParameter("url") != null ){
          String refer = request.getParameter("url");
          response.sendRedirect(refer.indexOf("?") > -1 ? (refer+"&wx_info="+new String(r)) : (refer+"?wx_info="+new String(r)) );
        }if(request.getParameter("cacheKey") != null){
//          JavaMem.setCache(request.getParameter("cacheKey"), r);
//          System.out.println(request.getParameter("cacheKey")+" => "+new String(r));
          stream.write("页面已登录成功，请关闭当前页面。".getBytes());
        }else{
          stream.write(r);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally{
      if(stream!=null){
        stream.close();
      }
    }
  }
  
//  private String getcookieKey(HttpServletRequest request){
//    Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
//    if(cookies == null){
//      return "";
//    }
//    for(Cookie cookie : cookies){
//      if( "_call_url".equals(cookie.getName())){
//        return String.valueOf(cookie.getValue());
//      }
//    }
//    return "";
//  }
}
