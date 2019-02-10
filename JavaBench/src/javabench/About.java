/*
 *
 * Multithread math calculations benchmark. (C)2019 IC Book Labs.
 * Application info and path to resources in the application JAR archive.
 * Private data with public getters, 
 * this is optimal for object oriented programming recommendations compliance.
 *
 */

package javabench;

public class About 
{

private final static String VERSION_NAME = "v0.13.02";
private final static String VENDOR_NAME  = "(C)2019 IC Book Labs";
private final static String SHORT_NAME   = "Math Benchmark " + VERSION_NAME;
private final static String LONG_NAME    = "Java " + SHORT_NAME;
private final static String WEB_SITE     = "http://icbook.com.ua";
private final static String VENDOR_ICON  = "/javabench/resources/icbook.jpg";

public static String getVersionName() { return VERSION_NAME; }
public static String getVendorName()  { return VENDOR_NAME;  }
public static String getShortName()   { return SHORT_NAME;   }
public static String getLongName()    { return LONG_NAME;    }
public static String getWebSite()     { return WEB_SITE;     }
public static String getVendorIcon()  { return VENDOR_ICON;  }
    
}
