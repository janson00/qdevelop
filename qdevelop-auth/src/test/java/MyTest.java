import cn.qdevelop.common.exception.QDevelopException;
import cn.qdevelop.core.DatabaseFactory;
import cn.qdevelop.core.bean.DBArgs;
import cn.qdevelop.core.standard.IDBResult;

public class MyTest {

	public static void main(String[] args) {
		try {
			IDBResult rb = DatabaseFactory.getInstance().queryDatabase(new DBArgs("qd_auth_menu_closest").put("menu_id", 7));
			System.out.println(rb.toString());
		} catch (QDevelopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
