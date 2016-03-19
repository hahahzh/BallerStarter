package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import play.mvc.Controller;

public class Application extends Controller {

	private static String token = "FSBAwx";
	
    public static void index() {
    	 // ΢�ż���ǩ��  
        String signature = params.get("signature");
        // ʱ���  
        String timestamp = params.get("timestamp");  
        // �����  
        String nonce = params.get("nonce");  
        // ����ַ�  
        String echostr = params.get("echostr");  
    play.Logger.info("signature="+signature+" timestamp="+timestamp+" nonce="+nonce+" echostr="+echostr);
        if (checkSignature(signature, timestamp, nonce)) {
        	renderText(echostr);
        }
    }
    
    public static void oauth2() {
    	Map<Integer, Integer> map = new HashMap<Integer, Integer>();  
    	 String str="";
    	for (Map.Entry<Integer, Integer> entry : map.entrySet()) {  
    	  
    	    str += "Key = " + entry.getKey() + ", Value = " + entry.getValue();  
    	  
    	} 
    	renderText(str);
   }

    public static void list() {
        render();
    }
    
    public static void setLang(String lang) {    
        
    	index();
    }
    
    /** 
     * ��֤ǩ�� 
     *  
     * @param signature 
     * @param timestamp 
     * @param nonce 
     * @return 
     */  
    public static boolean checkSignature(String signature, String timestamp, String nonce) {  
        String[] arr = new String[] { token, timestamp, nonce };  
        // ��token��timestamp��nonce�����������ֵ�������  
        Arrays.sort(arr);  
        StringBuilder content = new StringBuilder();  
        for (int i = 0; i < arr.length; i++) {  
            content.append(arr[i]);  
        }  
        MessageDigest md = null;  
        String tmpStr = null;  
  
        try {  
            md = MessageDigest.getInstance("SHA-1");  
            // ����������ַ�ƴ�ӳ�һ���ַ����sha1����  
            byte[] digest = md.digest(content.toString().getBytes());  
            tmpStr = byteToStr(digest);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
  
        content = null;  
        // ��sha1���ܺ���ַ����signature�Աȣ���ʶ��������Դ��΢��  
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;  
    }  
  
    /** 
     * ���ֽ�����ת��Ϊʮ������ַ� 
     *  
     * @param byteArray 
     * @return 
     */  
    private static String byteToStr(byte[] byteArray) {  
        String strDigest = "";  
        for (int i = 0; i < byteArray.length; i++) {  
            strDigest += byteToHexStr(byteArray[i]);  
        }  
        return strDigest;  
    }  
  
    /** 
     * ���ֽ�ת��Ϊʮ������ַ� 
     *  
     * @param mByte 
     * @return 
     */  
    private static String byteToHexStr(byte mByte) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
        char[] tempArr = new char[2];  
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];  
        tempArr[1] = Digit[mByte & 0X0F];  
  
        String s = new String(tempArr);  
        return s;  
    }  
}