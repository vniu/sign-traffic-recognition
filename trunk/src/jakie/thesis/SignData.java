package jakie.thesis;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class SignData {
	Document doc;
	
	public SignData(InputStream is){
		try{
    		doc=Util.readXml(is);
    	}
    	catch(Exception e)
    	{
    		Log.i("SignData.Class",e.toString());
    	}
	}
	
	public String getSignName(String id){
		NodeList nl = doc.getElementsByTagName("sign");
		XMLParser parser = new XMLParser();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String code = parser.getValue(e, "id");
			if(code.equals(id))
				return parser.getValue(e, "name");
		}
		return null;
	}
	
	public String getImage(String id){
		NodeList nl = doc.getElementsByTagName("sign");
		XMLParser parser = new XMLParser();
		
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String code = parser.getValue(e, "id");
			if(code.equals(id))
				return parser.getValue(e, "image");
		}
		return null;
	}
}
