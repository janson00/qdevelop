package cn.qdevelop.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class XMLUtil {
	// private static final transient Logger LOG = LoggerFactory
	// .getLogger(XMLUtil.class);
	private static String listPrefix = "L-";

	public static Map<String, Object> Xml2Map(File f) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(f);
		Element root = document.getRootElement();
		return Dom2Map(root);
	}

	private static Map Dom2Map(Element e) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Attribute> attrList = e.attributes();
		for (Attribute attribute : attrList) {
			map.put(attribute.getName(), attribute.getValue());
		}
		if (!QString.isNull(e.getTextTrim())) {
			map.put("value", e.getTextTrim());
		}
		List<Element> childElements = e.elements();
		for (Element childElement : childElements) {
			if (childElement.getName().startsWith(listPrefix)) {
				List<Map<String, Object>> childMaps = (List<Map<String, Object>>) map
						.get(childElement.getName());
				if (childMaps != null) {
					childMaps.add(Dom2Map(childElement));
				} else {
					childMaps = new ArrayList<Map<String, Object>>();
					childMaps.add(Dom2Map(childElement));
					map.put(childElement.getName(), childMaps);
				}
			} else {
				map.put(childElement.getName(), Dom2Map(childElement));
			}
		}
		return map;
	}

}
