/*
Multithread math calculations benchmark utility. (C)2019 IC Book Labs.
-----------------------------------------------------------------------
Application info and path to resources in the application JAR archive.
*/

package javabench;

class About 
{
private final static String VERSION_NAME = "v0.13.05";
private final static String VENDOR_NAME  = "(C)2019 IC Book Labs";
private final static String SHORT_NAME   = "Math Benchmark " + VERSION_NAME;
private final static String LONG_NAME    = "Java " + SHORT_NAME;
private final static String WEB_SITE     = "http://icbook.com.ua";
private final static String VENDOR_ICON  = "/javabench/resources/icbook.jpg";

static String getVersionName() { return VERSION_NAME; }
static String getVendorName()  { return VENDOR_NAME;  }
static String getShortName()   { return SHORT_NAME;   }
static String getLongName()    { return LONG_NAME;    }
static String getWebSite()     { return WEB_SITE;     }
static String getVendorIcon()  { return VENDOR_ICON;  }
}
