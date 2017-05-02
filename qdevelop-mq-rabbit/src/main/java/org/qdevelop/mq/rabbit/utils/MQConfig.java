package org.qdevelop.mq.rabbit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MQConfig {
  
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
    if ( stream == null ) {
      throw new Exception( resource + " not found!" );
    }
    return stream;
  }
  
  
}
