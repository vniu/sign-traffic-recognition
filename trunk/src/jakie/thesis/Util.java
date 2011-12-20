package jakie.thesis;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Util {
	public static double[] toData(Bitmap bm) throws Exception
	{
		if(bm.getHeight()!=30||bm.getWidth()!=30)
			throw new Exception("Image size must be 30*30");
		
		List<Double> Data = new ArrayList<Double>();
		
        double[][] temp=new double[30][30];
        int pixel;
        double total_R=0, total_G=0, total_B=0, total_temp=0;
		int r,g,b;
		
        for (int i = 0; i < 30; i++){
            for (int j = 0; j < 30; j++){
            	pixel= bm.getPixel(i, j);
            	
            	r=(int)Color.red(pixel);
            	g=(int)Color.green(pixel);
            	b=(int)Color.blue(pixel);
            	
                total_R += r;
                total_G += g;
                total_B += b;

                temp[i][j] = r * .3 + g * .59 + b * .11;
                total_temp += temp[i][j];
            }
        }
        
      //Calculate the average value;
        total_R = (total_R / 900) / 256; Data.add(total_R);
        total_G = (total_G / 900) / 256; Data.add(total_G);
        total_B = (total_B / 900) / 256; Data.add(total_B);

        double Threshold=(total_temp / 900);
        
      //Calculate the horizontal parameters
        for (int i = 0; i < 30; i++){
            total_temp = 0;
            for (int j = 0; j < 30; j++)
                if (temp[i][j] > Threshold) total_temp += temp[i][j];
            Data.add(total_temp / 30);
        }
        
      //Calculate the vertical parameters
        for (int j = 0; j < 30; j++){
            total_temp = 0;
            for (int i = 0; i < 30; i++)
                if (temp[i][j] > Threshold) total_temp += temp[i][j];
            Data.add(total_temp / 30);
        }
        
		double result[]=new double[63];
		for (int i = 0; i < 63; i++)
        {
            result[i] = (double)Data.get(i);
        }
		
		return result;
	}
	
	public static Document readXml(InputStream is) throws SAXException, IOException,
	    ParserConfigurationException {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	    dbf.setValidating(false);
	    dbf.setIgnoringComments(false);
	    dbf.setIgnoringElementContentWhitespace(true);
	    dbf.setNamespaceAware(true);
	    // dbf.setCoalescing(true);
	    // dbf.setExpandEntityReferences(true);
	
	    DocumentBuilder db = null;
	    db = dbf.newDocumentBuilder();
	    db.setEntityResolver(new NullResolver());
	
	    // db.setErrorHandler( new MyErrorHandler());
	
	    return db.parse(is);
	}
}

class NullResolver implements EntityResolver {
	  public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
	      IOException {
	    return new InputSource(new StringReader(""));
	  }
}
