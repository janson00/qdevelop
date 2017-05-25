/**
 * 
 */
package cn.qdevelop.common.event;

import java.io.Serializable;

/**
 * @author square
 * 
 */
public interface IRequest extends Serializable {
	
	public void run() throws Exception;
	public void successCallBack(Object info);
	public String toString();

}
