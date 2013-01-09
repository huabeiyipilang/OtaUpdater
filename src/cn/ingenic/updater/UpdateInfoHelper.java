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

public class UpdateInfoHelper {
	private final static String LIST_VERSION = "update_list_version";
	private final static String UPDATE_ELEMENT = "update";
	private final static String VALUE_INDEX = "index";
	private final static String VALUE_VERSION = "version";
	private final static String VALUE_DESCRIPTION = "description";
	private final static String VALUE_URL = "url";
	private final static String VALUE_SIZE = "size";
	private final static String VALUE_MD5 = "md5";
	private final static String VALUE_NEXT = "next_version";
	private final static String VALUE_PRE = "pre_version";
	
	private static UpdateInfoHelper sHelper;
	private static MyLog klilog = new MyLog(UpdateInfoHelper.class);
	
	private UpdateInfoHelper(){
		
	}
	
	public static UpdateInfoHelper getInstance(){
		if(sHelper == null){
			sHelper = new UpdateInfoHelper();
		}
		return sHelper;
	}
	
	public List<UpdateInfo> getUpdateList(String xml){
		ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
		return getUpdateList(stream);
	}
	
	public List<UpdateInfo> getUpdateList(InputStream is){
		List<UpdateInfo> res = new ArrayList<UpdateInfo>();
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
		for(UpdateInfo info:res){
			info.dump();
		}
		return res;
	}
	
	private class XmlHandler extends DefaultHandler{
		private List<UpdateInfo> list = new ArrayList<UpdateInfo>();
		private UpdateInfo info;
		private String tmp;
		
		public XmlHandler(List<UpdateInfo> list){
			this.list = list;
		}
		
		@Override
		public void startElement(String uri, String localName,
				String qName, Attributes attributes)
				throws SAXException {
			if(UPDATE_ELEMENT.equals(localName)){
				info = new UpdateInfo();
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
			if(VALUE_INDEX.equals(tmp)){
				klilog.i("ch = "+ String.valueOf(ch)+"value = "+value);
				info.index = value;
			}else if(VALUE_VERSION.equals(tmp)){
				info.version = value;
			}else if(VALUE_DESCRIPTION.equals(tmp)){
				info.description = value;
			}else if(VALUE_URL.equals(tmp)){
				info.url = value;
			}else if(VALUE_SIZE.equals(tmp)){
				info.size = value;
			}else if(VALUE_MD5.equals(tmp)){
				info.md5 = value;
			}else if(VALUE_NEXT.equals(tmp)){
				info.next_version = new ArrayList<String>(Arrays.asList(value.split(",")));
			}else if(VALUE_PRE.equals(tmp)){
				info.pre_version = new ArrayList<String>(Arrays.asList(value.split(",")));
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(UPDATE_ELEMENT.equals(localName)){
				list.add(info);
			}
			tmp = null;
		}
		
		public List<UpdateInfo> getUpdateList(){
			return list;
		}

		private int[] s2i(String[] s){
			int[] res = new int[s.length];
			for(int i = 0; i<s.length; i++){
				res[i] = Integer.valueOf(s[i]);
			}
			return res;
		}
	}
}
