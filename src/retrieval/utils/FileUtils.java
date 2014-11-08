package retrieval.utils;

import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import org.apache.http.Header;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import retrieval.storage.exception.PictureNotFoundException;

/**
 * This class store util method for IO
 * @author Rollus Loic
 */
public class FileUtils {
    private static Logger logger = Logger.getLogger(FileUtils.class);
    
    public static String[] listDirectories(File basePath) {
        File[] files = basePath.listFiles();
        List<String> dir = new ArrayList<String>();
        
        for(int i=0;i<files.length;i++) {
            if(files[i].isDirectory())
             dir.add(files[i].getName());
        } 
        return dir.toArray(new String[dir.size()]);
    }    
    
    /**
     * List all file from file/directory f (and its subdirectories) in list v
     * Don't list picture in cbirthumb directory (thumbnail directory)
     * @param f File f
     * @param v Destination list
     */
    public static void listFiles(File f, List<String> v) {

        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                //directory which contain no thumb, list its files
                if (!fs[i].getName().equals("cbirthumb")) {
                    listFiles(fs[i], v);
                }
            } else {
                //its a file
                v.add(fs[i].getAbsolutePath());
            }
        }

    }
    
    /**
     * List all file from file/directory f (and its subdirectories) in list v
     * Don't list picture in cbirthumb directory (thumbnail directory)
     * @param f File f
     * @param v Destination list
     * @param format Accepted format
     */
    public static void listFiles(File f, List<String> v, String[] format) {
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                //directory which contain no thumb, list its files
                if (!fs[i].getName().equals("cbirthumb")) {
                    listFiles(fs[i], v, format);
                }
            } else {
                //its a file
                if(isValidFormat(fs[i],format)) {
                    v.add(fs[i].getAbsolutePath());
                }               
            }
        }
    } 
    
    private static boolean isValidFormat(File f,String[] format) {
       boolean valid = false;
       String formatFile = getUpperCaseFormat(f);
       for(String allowedFormat : format) {
           if(formatFile.equals(allowedFormat.toUpperCase())) {
               valid = true;
           }
       }
       return valid;  
    }
    
    private static String getUpperCaseFormat(File f) {
        String name = f.getName().toUpperCase();
        int pos = name.lastIndexOf('.');
        if(pos==-1 || name.length()<(pos+1)) {
            return "";
        }
        String ext = name.substring(pos+1); 
        return ext;
    }

    /**
     * List all file from file/directory (not subdirectories) f in list v
     * @param f File f
     * @param v Destination list
     */
    public static void listFilesNoRecursively(File f, List<String> v) {

        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (!fs[i].isDirectory()) {
                v.add(fs[i].getAbsolutePath());
            }
        }

    }

    /**
     * Get file name from File f
     * @param f File f
     * @return Name (with extension) of f
     */
    public static String getFileName(File f) {
        return f.getAbsolutePath();
    }

    /**
     * Dlete all files and directory from directory
     * @param directory Directory
     */
    public static void deleteAllFiles(File directory) {
        File[] fs = directory.listFiles();
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                fs[i].delete();
            }
        }
    }
    
    public static void deleteAllFilesRecursively(File directory) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(directory);
    }
    
    public static void deleteAllSubFilesRecursively(File directory) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(directory);
        directory.mkdirs();
    }    

    public static boolean isValidResource(String path) {
        File f = new File(path);
        if (!f.canRead()) {
            try {
                if (!FileUtils.isValidURL(path)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
    

    public static BufferedImage readPicture(String uri) throws IOException {
        
        File f = new File(uri);
        boolean isURL = false;

        if (!f.canRead()) {isURL = true;}

        BufferedImage img = null;
        
        if (isURL) {
//            logger.info(uri + " is an URL ("+uri+")" + authorization);
//            if(authorization.isNoAuth()) {
                img = readBufferedImageFromURLWithoutAuth(uri);
//            } else if(authorization.isBasicAuth()) {
//                Authenticator.setDefault(new RetrievalAuthenticator(authorization.LOGIN,authorization.PASSWORD));
//                img = readBufferedImageFromURLWithBasicAuth(uri,authorization.LOGIN,authorization.PASSWORD);
//            } else if(authorization.isKeyAuth()) {
//               Authenticator.setDefault(new RetrievalAuthenticator(authorization.PUBLICKEY,authorization.PRIVATEKEY));
//               img = readBufferedImageFromURLWithKeysAuth(uri,authorization.PUBLICKEY,authorization.PRIVATEKEY,authorization.HOST);
//            }
        } else {
            logger.info(uri + " is a local file");
            img = ImageIO.read(new File(uri));
        }
        return img;

    }
    
    public static BufferedImage readPictureFromPath(File file) throws PictureNotFoundException {
        try {
            return ImageIO.read(file);
        } catch(IOException e) {
            throw new PictureNotFoundException("Cannot read: " + file.getAbsolutePath() + ": " + e.toString());
        }
    }
    
     public static BufferedImage readPictureFromUrl(URL url) throws PictureNotFoundException {
        try {
            return readBufferedImageFromURLWithoutAuth(url.toString());
        } catch(IOException e) {
            throw new PictureNotFoundException("Cannot read: " + url.toString() + ": " + e.toString());
        }
    }   
    

    public static boolean isValidURL(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            boolean isOK = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            boolean isFound = (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP);
            boolean isErrorServer = (con.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR);
            logger.info("url="+URLName + " is " + con.getResponseCode() + "(OK="+HttpURLConnection.HTTP_OK +",MOVED="+HttpURLConnection.HTTP_MOVED_TEMP+")");
            return isOK || isFound || isErrorServer;
        } catch (Exception e) {
            logger.error("Exception:"+e.toString());
            return false;
        }
    }

    public static BufferedImage readBufferedImageFromURLWithoutAuth(String url) throws MalformedURLException, IOException {
        return ImageIO.read(new URL(url));
    }

    public static BufferedImage readBufferedImageFromURLWithBasicAuth(String url,String loginHTTP, String passHTTP) throws MalformedURLException, IOException {
        //logger.info("readBufferedImageFromURLWithBasicAuth:"+url +" login="+loginHTTP);
        URL URL = new URL(url);
        HttpHost targetHost = new HttpHost(URL.getHost(), URL.getPort());
        DefaultHttpClient client = new DefaultHttpClient();
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local
        // auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        // Set credentials
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(loginHTTP, passHTTP);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

        BufferedImage img = null;
        HttpGet httpGet = new HttpGet(URL.getPath());
        HttpResponse response = client.execute(targetHost, httpGet, localcontext);
        int code = response.getStatusLine().getStatusCode();
        logger.info("url="+url + " is " + code + "(OK="+HttpURLConnection.HTTP_OK +",MOVED="+HttpURLConnection.HTTP_MOVED_TEMP+")");

        boolean isOK = (code == HttpURLConnection.HTTP_OK);
        boolean isFound = (code == HttpURLConnection.HTTP_MOVED_TEMP);
        boolean isErrorServer = (code == HttpURLConnection.HTTP_INTERNAL_ERROR);

        if(!isOK && !isFound & !isErrorServer) {
            throw new IOException(url + " cannot be read: "+code);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            img = ImageIO.read(entity.getContent());
        }
        return img;

    }

    public static BufferedImage readBufferedImageFromURLWithKeysAuth(String url,String publicKey, String privateKey, String host) throws MalformedURLException, IOException, Exception {
        logger.debug("readBufferedImageFromURL:"+url);
        URL URL = new URL(url);
        HttpHost targetHost = new HttpHost(URL.getHost(), URL.getPort());
        logger.debug("targetHost:"+targetHost);
        DefaultHttpClient client = new DefaultHttpClient();
        logger.debug("client:"+client);
        // Add AuthCache to the execution context
        BasicHttpContext localcontext = new BasicHttpContext();
        logger.debug("localcontext:"+localcontext);
        Header[] headers = authorize("GET", URL.toString(),"","",publicKey,privateKey,host);
        logger.debug("headers:"+headers.length);

        BufferedImage img = null;
        HttpGet httpGet = new HttpGet(URL.toString());
        httpGet.setHeaders(headers);
        HttpResponse response = client.execute(targetHost, httpGet, localcontext);
        int code = response.getStatusLine().getStatusCode();
        logger.info("url="+url + " is " + code + "(OK="+HttpURLConnection.HTTP_OK +",MOVED="+HttpURLConnection.HTTP_MOVED_TEMP+")");

        boolean isOK = (code == HttpURLConnection.HTTP_OK);
        boolean isFound = (code == HttpURLConnection.HTTP_MOVED_TEMP);
        boolean isErrorServer = (code == HttpURLConnection.HTTP_INTERNAL_ERROR);

        if(!isOK && !isFound & !isErrorServer) {
            throw new IOException(url + " cannot be read: "+code);
        }
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            img = ImageIO.read(entity.getContent());
        }
        return img;

    }


    public static  Header[] authorize(String action, String urlFullStr, String contentType, String accept, String publicKey, String privateKey, String host) throws Exception {
        logger.debug("authorize: action="+action+", url="+urlFullStr +", contentType=" + contentType + ",accept=" + accept);
        String url = urlFullStr.replace(host, "");
        logger.debug("authorize: url short="+url);
        Header[] headers = new Header[3];
        //conn = httplib.HTTPConnection(self._host)
        headers[0] = new BasicHeader("accept", accept);
        headers[1] = new BasicHeader("date", getActualDateStr());

        String canonicalHeaders = action + "\n\n" + contentType + "\n" + headers[1].getValue() + "\n";
        String messageToSign = canonicalHeaders + url;
        SecretKeySpec privateKeySign = new SecretKeySpec(privateKey.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(privateKeySign);
        byte[] rawHmac = mac.doFinal(new String(messageToSign.getBytes(), "UTF-8").getBytes());
        //byte[] signatureBytes = Base64.encodeToByte(rawHmac,false);
        byte[] signatureBytes = org.apache.commons.codec.binary.Base64.encodeBase64(rawHmac,false);
        String authorization = "CYTOMINE " + publicKey + ":" + new String(signatureBytes);
        headers[2]= new BasicHeader("authorization", authorization);
        return headers;
    }

    public static String getActualDateStr() {
        Date today = Calendar.getInstance().getTime();
        Strftime formatter = new Strftime("%a, %d %b %Y %H:%M:%S +0000",Locale.ENGLISH);
        return  formatter.format(today);
    }
}
