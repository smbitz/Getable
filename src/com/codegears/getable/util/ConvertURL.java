package com.codegears.getable.util;

public class ConvertURL {
	
	public static String getConvertURL( String getURLString ){
		getURLString.trim().replace(" ", "%20").replace("&", "%26")
        .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
        .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
        .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
        .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
        .replace(".", "%2E").replace("/", "%2F").replace(":", "%3A")
        .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
        .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
        .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
        .replace("|", "%7C").replace("}", "%7D");
        
		return getURLString;
	}
	
}
