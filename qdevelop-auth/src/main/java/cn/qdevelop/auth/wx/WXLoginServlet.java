package cn.qdevelop.auth.wx;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WXLoginServlet extends HttpServlet  {

  /**
   * 
   */
  private static final long serialVersionUID = -4320363308762325018L;

  public void doGet(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
    String cookie_key = this.getcookieKey(request);
    if(cookie_key.equals("")){
      cookie_key = java.util.UUID.randomUUID().toString();
      response.addHeader("Set-Cookie", "_ckno="+cookie_key+";Path=/; HttpOnly");
    }
    
    String src = request.getParameter("url") == null ? request.getHeader("referer") : request.getParameter("url");
//    ServletOutputStream stream=null;
    String callback  = URLEncoder.encode("http://wx.e-platform.cn/wxLogin/callback?url="+src,"utf-8");
    String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Concact.APP_ID+"&redirect_uri="+callback+"&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect";
    String ua = request.getHeader("user-agent").toLowerCase();
    if(ua.indexOf("micromessenger") > -1){
      response.sendRedirect(url);  
    }else{
      String key = java.util.UUID.randomUUID().toString();
      request.setAttribute("rediect", src);
      request.setAttribute("cacheKey", key);
      request.getRequestDispatcher("/wxLogin.jsp").forward(request, response);
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)    throws ServletException, IOException {
    this.doGet(request, response);
  }


//    private String redirectURL(HttpServletRequest request){
//      StringBuffer sb = new StringBuffer();
//      sb.append(request.getParameter("url") == null ? request.getHeader("referer") : request.getParameter("url"));
////      sb.append(request);
//      Enumeration<String> paramNames = request.getParameterNames();
//      String key;
//      while (paramNames.hasMoreElements()) {
//        key = (String) paramNames.nextElement();
//        sb.append(key).append("=").append(request.getParameter(key)).append("&");
//      }
////      System.out.println(sb.toString());
//      return sb.toString();
//    }
    

  private String getcookieKey(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
    if(cookies == null){
      return "";
    }
    for(Cookie cookie : cookies){
      if( "_ckno".equals(cookie.getName())){
        return cookie.getValue();
      }
    }
    return "";
  }

  public static void main(String[] args) {
    String result = "{\"subscribe\": 1, \"openid\": \"o6_bmjrPTlm6_2sgVt7hMZOPfL2M\", \"nickname\": \"Band\", \"sex\": 1, \"language\": \"zh_CN\", \"city\": \"广州\", \"province\": \"广东\", \"country\": \"中国\", \"headimgurl\":  \"http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4 eMsv84eavHiaiceqxibJxCfHe/0\", \"subscribe_time\": 1382694957, \"unionid\": \" o6_bmasdasdsad6_2sgVt7hMZOPfL\"\"remark\": \"\", \"groupid\": 0, \"tagid_list\":[128,2] }";
    result = result.replaceAll(" ", "");
    String[] keys = new String[]{"openid","username","headimgurl"};
    for(String key : keys){
      if(result.indexOf(key) > -1){
        String t = result.substring(result.indexOf(key)+key.length()+3);
        t = t.substring(0,t.indexOf("\""));
        System.out.println(key + ":"+t);
      }
    }
//    String ss = "http://blog.csdn.net/cynhafa/article/details/7911791";
//    String domain = ss.substring(7);
//    domain = domain.substring(0,domain.indexOf("/"));
//    System.out.println(domain);
  }
  //    StringBuffer sb = new StringBuffer();
  //    sb.append("xxx");
  //    sb.insert(0, "?");
  //    sb.insert(0, "V");
  //    System.out.println(sb);
  //  }
}
