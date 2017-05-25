package org.qdevelop.mq.rabbit;

import java.io.Serializable;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
  
  String nodeKey = "test.test";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
       
            
    }
    
    protected void setUp(){
      MQCustomer.getInstance().register(new ICustomer(){
        @Override
        public String getQueueName() {
          return nodeKey;
        }

        @Override
        public boolean handleDelivery(String consumerTag, Envelope envelope,
            BasicProperties properties, Serializable body) {
          System.out.println(body);
          assertEquals(body.toString(), "TEST");
          return true;
        }
        
      });
//      try {
//        Thread.sleep(1000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
    }
    
    protected void tearDown() throws Exception {
      MQCustomer.getInstance().shutdown();
    }
    
    

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
//        assertTrue( true );
        
     
        MQProvider.getInstance().publish(nodeKey, "TEST");
        
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        MQProvider.getInstance().publish(nodeKey, "TEST");
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }
}
