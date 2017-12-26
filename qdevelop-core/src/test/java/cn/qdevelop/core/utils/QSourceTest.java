package cn.qdevelop.core.utils;

import java.io.File;
import java.io.InputStream;

import cn.qdevelop.common.files.QSource;
import cn.qdevelop.common.files.SearchFileFromJars;
import cn.qdevelop.common.files.SearchFileFromProject;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class QSourceTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public QSourceTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( QSourceTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testGetProjectName()
    {
    	assertEquals(QSource.getProjectName(), "qd.core");
    }
    
    
    public void testSearchAllFiles(){
    	new SearchFileFromProject(){
			@Override
			protected void disposeFile(File f) {
				System.out.println(f.getAbsolutePath());
			}

			@Override
			protected void disposeFileDirectory(File f) {
				
			}
    	}.searchProjectFiles("*.jar");
    	
    	
    	new SearchFileFromJars(){

			@Override
			public void desposeFile(String jarName,String fileName, InputStream is) {
				System.out.println(jarName+" "+fileName);
			}
    		
    	}.searchAllJarsFiles("*.xml");;
    }
}
