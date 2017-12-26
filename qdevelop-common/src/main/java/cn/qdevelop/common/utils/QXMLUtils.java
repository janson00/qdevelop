package cn.qdevelop.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import cn.qdevelop.common.files.FileFilter;

public class QXMLUtils {
	
	public boolean isRootWith(String root,InputStream xml){
		SAXReader saxReader = new SAXReader();
		try {
			Document doc = saxReader.read(xml);
			return doc.getRootElement().getName().equals(root);
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			saxReader = null;
		}
		return false;
	}
	
	public boolean isRootWith(String root,File xml){
		SAXReader saxReader = new SAXReader();
		try {
			Document doc = saxReader.read(xml);
			return doc.getRootElement().getName().equals(root);
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			saxReader = null;
		}
		return false;
	}
	
	/**
	 * 获取XMLDocument
	 * @param filePath
	 * @return
	 * @throws DocumentException
	 */
	public Document getDocument(String filePath) throws DocumentException{
		return getDocument(new File(filePath));
	}
	
	public Document getDocument(String filePath,String encode) throws DocumentException{
		return getDocument(new File(filePath),encode);
	}
	
	public Document getDocument(File xmlFile) throws DocumentException{
		return getDocument(xmlFile,"UTF-8");
	}
	
	public Document getDocument(File xmlFile,String encode) throws DocumentException{
		if(!xmlFile.exists()) return null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding(encode);
		return saxReader.read(xmlFile);
	}
	
	public Document getDocument(InputStream xmlFile) throws DocumentException{
		return getDocument(xmlFile,"UTF-8");
	}
	
	public Document getDocument(InputStream xmlFile,String encode) throws DocumentException{
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding(encode);
		return saxReader.read(xmlFile);
	}
	
	
	@SuppressWarnings("unchecked")
	public void copyElement(Element source,Element target){
		List<Attribute> attributes = source.attributes();
		for(Attribute attr:attributes){
			target.addAttribute(attr.getName(), attr.getValue());
		}
		if(source.getTextTrim().length()>0){
			target.addText(source.getText());
		}
		Iterator<Element> iter = source.elementIterator();
		while(iter.hasNext()){
			Element cr =  iter.next();
			Element ct = target.addElement(cr.getName());
			copyElement(cr,ct);
		}
	}
	
	/**
	 * 保存XML
	 * @param document
	 * @param filePath
	 * @throws IOException
	 */
	public void save(Document document,String filePath) throws IOException{
		save(document,new File(filePath),"UTF-8");
	}
	public void save(Document document,String filePath,String encode) throws IOException{
		save(document,new File(filePath),encode);
	}
	public void save(Document document,File file) throws IOException{
		save(document,file,"UTF-8");
	}
	public void save(Document document,File file,String encode) throws IOException{
		OutputFormat outFmt = OutputFormat.createPrettyPrint(); 
	    outFmt.setEncoding(encode); 
	    outFmt.setExpandEmptyElements(true);
	    outFmt.setTrimText(true);
	    outFmt.setIndent(true);
	    outFmt.getNewLineAfterNTags();
		XMLWriter writer = new XMLWriter(new FileOutputStream(file),outFmt);    
		writer.write(document);            
		writer.close();
		System.out.println(QString.append("SAVE XML:\t",file.getAbsolutePath()));
	}
	
	public File[] getXMLFiles(String path){
		File _f = new File(path);
		if(!_f.exists())return null;
		return _f.listFiles(new FileFilter(".xml"));
	}

}
