package cn.qdevelop.core.db;

import java.sql.SQLException;

import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.db.bean.ComplexParserBean;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSQLConfig  extends TestCase{
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestSQLConfig( String testName ){
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite(){
        return new TestSuite( TestSQLConfig.class );
    }

    /**
     * Rigourous Test :-)
     * @throws SQLException 
     */
    public void testParserComplexVales() throws SQLException{
    	SQLConfigParser scp = 	new SQLConfigParser();
    	ComplexParserBean  cpb;
    	try {
    		cpb = scp.parserComplexVales("key", "1", true);
			assertEquals(cpb.getParseVale(), " key=?");
			
			cpb = scp.parserComplexVales("key", ">1", true);
			assertEquals(cpb.getParseVale(), " key>?");
			
			cpb = scp.parserComplexVales("key", "!1", true);
			assertEquals(cpb.getParseVale(), " key<>?");
			
			cpb = scp.parserComplexVales("key", "%1%", true);
			assertEquals(cpb.getParseVale(), " key like ?");
			
			cpb = scp.parserComplexVales("key", "!1%", true);
			assertEquals(cpb.getParseVale(), " key not like ?");
			
			cpb = scp.parserComplexVales("key", "!1%&1", true);
//			System.out.println(">>>>>"+cpb.getParseFullValue());
			assertEquals(cpb.getParseFullValue(), " (key not like '1%' and key='1')");
			
			cpb = scp.parserComplexVales("key", ">1|<2", true);
//			System.out.println(">>>>>"+cpb.getParseFullValue());
			assertEquals(cpb.getParseFullValue(), " (key>1 or key<2)");
			
			cpb = scp.parserComplexVales("key", "1|!2&3", true);
//			System.out.println(">>>>>"+cpb.getParseFullValue());
			assertEquals(cpb.getParseFullValue(), " (key='1' or key<>'2' and key='3')");

			cpb = scp.parserComplexVales("key", "1|2|3|4", true);
			assertEquals(cpb.getParseFullValue(), " key in ('1','2','3','4')");
			
			cpb = scp.parserComplexVales("key", ">='2010-09-09'&<='2010-10-09'", true);
//			System.out.println(">>>>>"+cpb.getParseFullValue());
			assertEquals(cpb.getParseFullValue(), " (key>='2010-09-09' and key<='2010-10-09')");
			
			cpb = scp.parserComplexVales("key", "139%***4927", true);
//			System.out.println(">>>>>"+cpb.getParseFullValue());
//			System.out.println(cpb.toString());
			assertEquals(cpb.getParseFullValue(), " key like '139%4927'");
			
		} catch (QDevelopException e) {
			e.printStackTrace();
		}
    }
	
//	public void testLeftQueryAutoSearch(){
//		Map<String,String> query = new HashMap<String,String>();
//		query.put("index", "products_left_test");
//		query.put("a.uid", "7358|7343|7387");
//		query.put("b.action", "insert");
//		query.put("b.ctime", ">2010-10-11&<2010-11-11");
//		try {
//			Connection conn = DatabaseFactory.getInstance().getConnectByQuery(query);
//			IDBQuery qb = SQLConfigParser.getInstance().getDBQueryBean(query,conn);
//			System.out.println(qb.getPreparedSql());
//			for(int i=0;i<qb.getPreparedColumns().length;i++){
//				System.out.println("param\t "+qb.getPreparedColumns()[i]+" : "+qb.getPreparedValues()[i]);
//			}
//			DatabaseFactory.getInstance().closeConnection(conn);
//		} catch (QDevelopException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
