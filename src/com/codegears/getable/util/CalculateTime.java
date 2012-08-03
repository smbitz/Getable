package com.codegears.getable.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculateTime {
	
	private static long DATA_ONE_YEAR_SEC = 31104000000L; //12 months
	private static long DATA_ONE_MONTH_SEC = 2592000000L; //30 days
	private static int DATA_ONE_DAY_SEC = 86400000;
	private static int DATA_ONE_HOUR_SEC = 3600000;
	private static int DATA_ONE_MINUTE_SEC = 60000;
	private static int DATA_ONE_SEC_SEC = 1000;
	
	public static String getPostTime(String getPostTime){
		Calendar c = Calendar.getInstance(); 
		long secondsNow = c.getTimeInMillis();
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );
		Date dt = null;
		try {
			dt = sdf.parse( getPostTime );
			c.setTime(dt);
			long secondsPost = c.getTimeInMillis();
			
			long timeDiff = secondsNow - secondsPost;
			if( timeDiff > (DATA_ONE_YEAR_SEC) ){
				int timeNumber = (int) (timeDiff/(DATA_ONE_YEAR_SEC));
				if( timeNumber > 1 ){
					return String.valueOf( timeNumber )+" years ago";
				}else{
					return String.valueOf( timeNumber )+" year ago";
				}
			}else if( timeDiff > (DATA_ONE_MONTH_SEC) ){
				int timeNumber = (int) (timeDiff/(DATA_ONE_MONTH_SEC));
				if( timeNumber > 1 ){
					return String.valueOf( timeNumber )+" months ago";
				}else{
					return String.valueOf( timeNumber )+" month ago";
				}
			}else if( timeDiff > (DATA_ONE_DAY_SEC) ){
				int timeNumber = (int) (timeDiff/(DATA_ONE_DAY_SEC));
				if( timeNumber > 1 ){
					return String.valueOf( timeNumber )+" days ago";
				}else{
					return String.valueOf( timeNumber )+" day ago";
				}
			}else if( timeDiff > (DATA_ONE_HOUR_SEC) ){
				int timeNumber = (int) (timeDiff/(DATA_ONE_HOUR_SEC));
				if( timeNumber > 1 ){
					return String.valueOf( timeNumber )+" hrs ago";
				}else{
					return String.valueOf( timeNumber )+" hr ago";
				}
			}else if( timeDiff > (DATA_ONE_MINUTE_SEC) ){
				int timeNumber = (int) (timeDiff/(DATA_ONE_MINUTE_SEC));
				if( timeNumber > 1 ){
					return String.valueOf( timeNumber )+" mins ago";
				}else{
					return String.valueOf( timeNumber )+" min ago";
				}
			}else if( timeDiff > (DATA_ONE_SEC_SEC) ){
				int timeNumber = (int) (timeDiff/(DATA_ONE_SEC_SEC));
				if( timeNumber > 1 ){
					return String.valueOf( timeNumber )+" secs ago";
				}else{
					return String.valueOf( timeNumber )+" sec ago";
				}
			}else{
				return "NOW";
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
}
