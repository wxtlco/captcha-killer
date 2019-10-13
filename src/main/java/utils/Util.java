package utils;

import burp.BurpExtender;
import burp.IResponseInfo;
import sun.misc.BASE64Encoder;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static boolean isImage(byte[] img){
        // Reference: https://www.cnblogs.com/shihaiming/p/10404700.html
        boolean isImg = false;
        InputStream buffin = new ByteArrayInputStream(img);
        try {
            //两种判断方式只能选中一种
            //第一种方式
//            ImageInputStream iis = ImageIO.createImageInputStream(buffin);
//            Iterator iter = ImageIO.getImageReaders(iis);
//            if (!iter.hasNext()) {
//                isImg = false;
//            }else {
//                isImg = true;
//            }
            //第二方式
            BufferedImage image = ImageIO.read(buffin);
            if(image == null){
                isImg = false;
            }else {
                isImg = true;
            }
        } catch (IOException e) {
            BurpExtender.stderr.println(e.getMessage());
            isImg = false;
        }
        return isImg;
    }

    public static ImageIcon byte2img(byte[] img) {
        InputStream buffin = new ByteArrayInputStream(img);
        Image image = null;
        ImageIcon icon = null;
        try {
            image = ImageIO.read(buffin);
            icon = new ImageIcon(image);
        } catch (IOException e) {
            BurpExtender.stderr.println(e.getMessage());
            icon = null;
        }
        return icon;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }

    public static String matchByStartEndPosition(String str,String rule){
        int nStart = 0;
        int nEnd = 0;
        if(nStart > 0 && nStart < nEnd){
            return "Rules of the error: start should >0 and <end";
        }

        if(nEnd <= str.length()){
            return String.format("Rules of the error: end should < response.length(%s)",str.length());
        }
        return str.substring(nStart,nEnd);
    }

    public static String URLEncode(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            BurpExtender.stdout.println(e.getMessage());
        }
        return result;
    }

    public static String URLDecode(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            BurpExtender.stdout.println(e.getMessage());
        }
        return result;
    }

    public static String base64Encode(byte[] byteArray){
        //https://www.cnblogs.com/alter888/p/9140732.html
        final BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(byteArray);
    }

    public static String base64Encode(String str){
        final BASE64Encoder encoder = new BASE64Encoder();
        byte[] b = new byte[]{};
        try {
            b = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoder.encode(b);
    }

    public static boolean isURL(String url){
        if (url ==  null ){
            return   false ;
        }
        String regEx =  "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                +  "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                +  "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                +  "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                +  "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                +  "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                +  "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                +  "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$" ;
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(url);
        return  matcher.matches();
    }

    /**
     * 转义正则特殊字符 $()*+.[]?\^{},|
     * Reference:https://www.cnblogs.com/lovehansong/p/7874337.html
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!keyword.equals(null) && !keyword.trim().equals("")) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|",":"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
            keyword = keyword.replace("\r","\\r");
            keyword = keyword.replace("\n","\\n");
        }
        return keyword;
    }

    /**
     * 转移json特殊字符 $()*+.[]?{}/^-|"
     * Renference: https://www.cnblogs.com/javalanger/p/10913838.html
     * @param keyword
     * @return
     */
    public static String escapeJsonString(String keyword){
        if (!keyword.equals(null) && !keyword.trim().equals("")) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|",":","-"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
            keyword = keyword.replace("\r","\\r");
            keyword = keyword.replace("\n","\\n");
        }
        return keyword;
    }


    public static byte[] getRspBody(byte[] response){
        IResponseInfo responseInfo = BurpExtender.helpers.analyzeResponse(response);
        int bodyOffset = responseInfo.getBodyOffset();
        int body_length = response.length - bodyOffset;
        return subBytes(response,bodyOffset,body_length);
    }

    public static void main(String[] args) {
        System.out.println("\\n");
        String str1 = "\r\nsdsdsd";
        System.out.println(str1.replace("\r","\\r"));
        String str = "<html><body style=\"sss\">sssss</body></html>\\sdsdsd";
        //generateRegular(str,4,6);
    }

}
