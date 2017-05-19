package cn.qdevelop.dubbo;

import com.google.common.base.Splitter;

/**
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        System.out.println( "Hello World!" );
        Iterable<String> tmps =  Splitter.on(",").trimResults().omitEmptyStrings().split("asd, ad,s,,asda");
        for(String s : tmps){
        	System.out.println(s);
        }
    }
}
