package cn.ingenic.updater;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ProductInfoHelper {
	private final static String PRODUCT_ELEMENT = "product";
	private final static String VALUE_MODEL = "model";
	private final static String VALUE_URL = "url";
	private static ProductInfoHelper sHelper;
	private static MyLog klilog = new MyLog(ProductInfoHelper.class);
	
	private ProductInfoHelper(){
		
	}
	
	public static ProductInfoHelper getInstance(){
		if(sHelper == null){
			sHelper = new ProductInfoHelper();
		}
		return sHelper;
	}
	
	public List<ProductInfo> getProductList(String xml){
		ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
		return getProductList(stream);
	}
	
	public List<ProductInfo> getProductList(InputStream is){
		List<ProductInfo> res = new ArrayList<ProductInfo>();
		SAXParserFactory saxParser = SAXParserFactory.newInstance();
		try {
			SAXParser sp = saxParser.newSAXParser();
			XMLReader reader = sp.getXMLReader();
			XmlHandler handler = new XmlHandler(res); 
			reader.setContentHandler(handler);
			reader.parse(new InputSource(is));
			res = handler.getUpdateList();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(ProductInfo info:res){
			info.dump();
		}
		return res;
	}
	
	private class XmlHandler extends DefaultHandler{
		private List<ProductInfo> list = new ArrayList<ProductInfo>();
		private ProductInfo info;
		private String tmp;
		
		public XmlHandler(List<ProductInfo> list){
			this.list = list;
		}
		
		@Override
		public void startElement(String uri, String localName,
				String qName, Attributes attributes)
				throws SAXException {
			if(PRODUCT_ELEMENT.equals(localName)){
				info = new ProductInfo();
			}
			tmp = localName;
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if(info == null){
				return;
			}
			String value = new String(ch, start, length);
			if(VALUE_MODEL.equals(tmp)){
				info.model = value;
			}else if(VALUE_URL.equals(tmp)){
				info.url = value;
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(PRODUCT_ELEMENT.equals(localName)){
				list.add(info);
			}
			tmp = null;
		}
		
		public List<ProductInfo> getUpdateList(){
			return list;
		}

	}
}
