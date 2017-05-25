package cn.qdevelop.plugin.link.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

public class KeyWordFinder {

  private static KeyWordFinder _KeyWordFinder = new KeyWordFinder();
  private static DFA dfa;
  public static KeyWordFinder getInstance(){
    return _KeyWordFinder;
  } 

  public  InputStream getSourceAsStream(String resource) throws Exception{
    File tmp = new File(resource);
    if(tmp.exists())return new FileInputStream(tmp);
    boolean hasLeadingSlash = resource.startsWith( "/" );
    String stripped = hasLeadingSlash ? resource.substring(1) : resource;
    InputStream stream = null;
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if ( classLoader != null ) {
      stream = classLoader.getResourceAsStream( resource );
      if ( stream == null && hasLeadingSlash ) {
        stream = classLoader.getResourceAsStream( stripped );
      }
    }
    if ( stream == null ) {
      stream = ClassLoader.getSystemResourceAsStream( resource );
    }
    if ( stream == null && hasLeadingSlash ) {
      stream = ClassLoader.getSystemResourceAsStream( stripped );
    }
    //    if ( stream == null ) {
    //      throw new Exception( resource + " not found!" );
    //    }
    return stream;
  }

  public KeyWordFinder(){
    dfa = new DFA();
    try {
      InputStream is = getSourceAsStream("plugin-config/qdevelop-sensitive-words.properties");
      if(is!=null){
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        Properties props = new Properties();
        props.load(bf);
        Iterator<Object> items =  props.keySet().iterator();
        while( items.hasNext()){
          dfa.addKeyWord(String.valueOf(items.next()));
        }
        props.clear();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void addKeyWord(String ... keyWords){
    try {
      dfa.addKeyWord(keyWords);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public String findKeyWord(String text){
    try {
      return dfa.searchKeyword(text.replaceAll(" ", ""));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String wrapKeyWord(String text,Wrap doit){
    try {
      return dfa.wrapKeyWord(text,doit);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

}
