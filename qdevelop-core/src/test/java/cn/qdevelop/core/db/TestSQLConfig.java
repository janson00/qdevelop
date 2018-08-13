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
			assertEquals(cpb.getParseVale(), " (key not like ? and key=?)");
			cpb = scp.parserComplexVales("key", ">1|<2", true);
			assertEquals(cpb.getParseVale(), " (key>? or key<?)");
			
			cpb = scp.parserComplexVales("key", "1|!2&3", true);
			assertEquals(cpb.getParseVale(), " (key=? or key<>? and key=?)");

			cpb = scp.parserComplexVales("key", "1|2|3|4", true);
			assertEquals(cpb.getParseVale(), " key in ('1','2','3','4')");
			
			cpb = scp.parserComplexVales("key", ">='2010-09-09'&<='2010-10-09'", true);
			assertEquals(cpb.getParseVale(), " (key>=? and key<=?)");
//			System.out.println(">>>>>"+cpb.getParseVale());
//			System.out.println(cpb.toString());
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
