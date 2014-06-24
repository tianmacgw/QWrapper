import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;

public class Wrapper_gjsairi2001 implements QunarCrawler{

	public static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//	private static long strtime = System.currentTimeMillis();
	
	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		
		//ALC-CPH 2014-06-30
		//AMS-ATH 2014-09-13
		//ALC-DUB 2014-06-30
		searchParam.setDep("MAD");
		searchParam.setArr("ALC");
		searchParam.setDepDate("2014-06-26");
		searchParam.setRetDate("2014-06-30");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		searchParam.setWrapperid("gjsairi2001");
		
		String html = new Wrapper_gjsairi2001().getHtml(searchParam);
		System.out.println(html);
//		long getTime = System.currentTimeMillis()-strtime;
//		System.out.println("获取html用时："+getTime);		

		ProcessResultInfo result = new ProcessResultInfo();
		result = new  Wrapper_gjsairi2001().process(html,searchParam);
//		System.out.println("解析html用时："+(System.currentTimeMillis()-getTime-strtime));
		if(result.isRet() && result.getStatus().equals(Constants.SUCCESS))
		{
			List<OneWayFlightInfo> flightList = (List<OneWayFlightInfo>) result.getData();
			for (OneWayFlightInfo in : flightList){
				System.out.println("************" + in.getInfo().toString());
				System.out.println("++++++++++++" + in.getDetail().toString());
			}
		}
		else
		{
			System.out.println(result.getStatus());
		}
	}
	
	public BookingResult getBookingInfo(FlightSearchParam arg0) {

		String depDate = arg0.getDepDate().replace("-", "");
		String depDay = depDate.substring(6,8);
		String depMonth = depDate.substring(0,6);		
		
		String retDate = arg0.getRetDate().replace("-", "");
		String retDay = retDate.substring(6,8);
		String retMonth = retDate.substring(0,6);
		
		String bookingUrlPre = "http://www.iberiaexpress.com/airlines/web/bookingForm.do?BV_EngineID=ccckadgdfmligjgcfngcfkmdfhmdfln.0&tabId=0&prgOid=&chOid="
							+"&menuId=01000000000000&quadrigam=&isPopup=&menuRP=&firstLoad=1&OID=0&BEGIN_DATE_OFFER=&END_DATE_OFFER=&originCountry=ES"
							+"&BEGIN_CITY_01="+arg0.getDep()+"&END_CITY_01="+arg0.getArr()+"&TRIP_TYPE=2&BEGIN_DAY_01="+depDay+"&BEGIN_MONTH_01="+depMonth
							+"&BEGIN_HOUR_01=0000&BEGIN_HOUR_SPECIFIED=false&BEGIN_YEAR_01=&END_DAY_01="+retDay+"&END_MONTH_01="+retMonth+"&END_HOUR_01=0000&END_HOUR_SPECIFIED=false&END_YEAR_01=&flexible=false&ADT=1&CHD=0&INF=0&FARE_TYPE=R";
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;

	}

	public String getHtml(FlightSearchParam arg0) {
		QFGetMethod get = null;	
		try {	
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			
			String depDate = arg0.getDepDate().replace("-", "");
			String depDay = depDate.substring(6,8);
			String depMonth = depDate.substring(0,6);
			
			String retDate = arg0.getRetDate().replace("-", "");
			String retDay = retDate.substring(6,8);
			String retMonth = retDate.substring(0,6);

			String getUrl = "http://www.iberiaexpress.com/airlines/web/bookingForm.do?BV_EngineID=ccckadgdfmligjgcfngcfkmdfhmdfln.0&tabId=0&prgOid=&chOid="
		    				+"&menuId=01000000000000&quadrigam=&isPopup=&menuRP=&firstLoad=1&OID=0&BEGIN_DATE_OFFER=&END_DATE_OFFER=&originCountry=ES"
		    				+"&BEGIN_CITY_01="+arg0.getDep()+"&END_CITY_01="+arg0.getArr()+"&TRIP_TYPE=2&BEGIN_DAY_01="+depDay+"&BEGIN_MONTH_01="+depMonth
		    				+"&BEGIN_HOUR_01=0000&BEGIN_HOUR_SPECIFIED=false&BEGIN_YEAR_01=&END_DAY_01="+retDay+"&END_MONTH_01="+retMonth+"&END_HOUR_01=0000&END_HOUR_SPECIFIED=false&END_YEAR_01=&flexible=false&ADT=1&CHD=0&INF=0&FARE_TYPE=R";		    
		    
		    get = new QFGetMethod(getUrl);	
		    httpClient.executeMethod(get);
		    
		    return get.getResponseBodyAsString();

		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			if (null != get){
				get.releaseConnection();
			}
		}
		return "Exception";
	}


	public ProcessResultInfo process(String arg0, FlightSearchParam arg1) {
		String html = arg0;
		/* ProcessResultInfo中，
		 * ret为true时，status可以为：SUCCESS(抓取到机票价格)|NO_RESULT(无结果，没有可卖的机票)
		 * ret为false时，status可以为:CONNECTION_FAIL|INVALID_DATE|INVALID_AIRLINE|PARSING_FAIL|PARAM_ERROR
		 */
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {	
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;			
		}		
		//需要有明显的提示语句，才能判断是否INVALID_DATE|INVALID_AIRLINE|NO_RESULT
		if (html.contains("Sorry, we were unable to process your request due to either no operating flight or no seats available. Please select a different search option.")) {
			result.setRet(false);
			result.setStatus(Constants.INVALID_DATE);
			return result;			
		}
		
		//获取到展示数据的table
		String tablehtml = org.apache.commons.lang.StringUtils.substringBetween(html, "<tbody>", "</tbody>");		
		
		String temptable = "";

		try {	
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			
			while(tablehtml.contains("<tr>")){
				
				OneWayFlightInfo baseFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();				
				List<String> flightNoList = new ArrayList<String>();
				
				temptable = org.apache.commons.lang.StringUtils.substringBetween(tablehtml, "<tr>", "</tr>");
				temptable = temptable.replaceAll("\n", "");
				
				while(temptable.contains("<a href=")){
					FlightSegement seg = new FlightSegement();
					String flightinfo = org.apache.commons.lang.StringUtils.substringBetween(temptable,"<a href=", "<div");						
					if(null == flightinfo){
						break;
					}
					flightinfo = flightinfo.replaceAll("\t", "");
					flightinfo = flightinfo.replaceAll(" ", "");
					
					Matcher matcherFlightNo = Pattern.compile("id=\"idNumFlight_\\w*\\d*\"value=\"(\\w*\\d*)\"").matcher(flightinfo);
					if(matcherFlightNo.find()){
						System.out.println("flightNo: " + matcherFlightNo.group(1));
						String flightNo = matcherFlightNo.group(1);
						flightNoList.add(flightNo);
						seg.setFlightno(flightNo);
					}
					
					Matcher matcher = Pattern.compile("bDate=(\\d*)&amp;beginLocation=(\\w{3})&amp;endLocation=(\\w{3}).*<strong>(\\d*:\\d*)</strong>.*\\)(\\d*h:\\d*m)").matcher(flightinfo);
					if(matcher.find()){
						System.out.println("------------------------------------------------------------------------------");														
							Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(matcher.group(1));
							System.out.println("出发时间："+matcher.group(4)+"\t"+sf.format(date));
							seg.setDeptime(matcher.group(4));
							seg.setDepDate(sf.format(date));
							System.out.println("始发地："+matcher.group(2));
							
							//获取返回时间
							String time = matcher.group(5);
							int hh = Integer.parseInt(time.substring(0, time.indexOf("h")));
							int mm = Integer.parseInt(time.substring(time.indexOf(":")+1,time.indexOf("m")));
							int flightTime = (hh*3600)+(mm*60);
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							cal.add(Calendar.SECOND, flightTime);
							Date arrDate = cal.getTime();
							String arrTime = new SimpleDateFormat("HH:mm").format(arrDate);
							System.out.println("到达时间："+arrTime+"\t"+sf.format(arrDate));
							seg.setArrtime(arrTime);
							seg.setArrDate(sf.format(arrDate));
							seg.setDepairport(matcher.group(2));							
							System.out.println("目的地："+matcher.group(3));
							seg.setArrairport(matcher.group(3));
					}												
					
					segs.add(seg);
					temptable = temptable.replaceFirst("<a href=", "");
				}
						
						/*boolean transfer = false; //是否中转
						if(flightinfo.contains("<inputtype=\"hidden\"value=\"null\"/>")){
							System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&中转：");							
							String transferhtml = tablehtml.replaceFirst("class=\"vuelo_escala\">", "");							
							if(transferhtml.contains("class=\"vuelo_escala\">")){
								transferhtml = org.apache.commons.lang.StringUtils.substringBetween(transferhtml, "class=\"vuelo_escala\">", "</tr>");
								transferhtml = transferhtml.replaceAll("\n", "");
								FlightSegement transferSeg = new FlightSegement();
								boolean transferDepDate = true;
								String transferFlightinfo = org.apache.commons.lang.StringUtils.substringBetween(transferhtml,"<input", "/></td>");						
								if(null == transferFlightinfo){
									break;
								}
								transferFlightinfo = transferFlightinfo.replaceAll("\t", "");
								transferFlightinfo = transferFlightinfo.replaceAll(" ", "");
								
								Matcher transferMatcherFlightNo = Pattern.compile("id=\"idNumFlight_.*\"value=\"(.*)\"").matcher(transferFlightinfo);
								if(transferMatcherFlightNo.find()){
									System.out.println("flightNo: " + transferMatcherFlightNo.group(1));
									String flightNo = transferMatcherFlightNo.group(1);
									flightNoList.add(flightNo);
									transferSeg.setFlightno(flightNo);
								}
								
								Matcher transferMatcher = Pattern.compile("type=\"hidden\"value=\"(\\d*)\"/><strong>(\\d*:\\d*)</strong>\\w*\\((\\w{3})\\)").matcher(transferFlightinfo);
								while(transferMatcher.find()){
									System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");							
									if(transferDepDate){
										System.out.println("出发时间："+transferMatcher.group(1)+"\t"+transferMatcher.group(2));
										transferSeg.setDeptime(transferMatcher.group(2));
										Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(transferMatcher.group(1));
										transferSeg.setDepDate(sf.format(date));
										System.out.println("始发地："+transferMatcher.group(3));
										transferSeg.setDepairport(transferMatcher.group(3));
										transferDepDate = false;
									}else{
										System.out.println("到达时间："+transferMatcher.group(1)+"\t"+transferMatcher.group(2));
										transferSeg.setArrtime(transferMatcher.group(2));
										Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(transferMatcher.group(1));
										transferSeg.setArrDate(sf.format(date));
										System.out.println("目的地："+transferMatcher.group(3));
										transferSeg.setArrairport(transferMatcher.group(3));
									}
								}												
								
								segs.add(transferSeg);
								transfer = true;
							}
						}*/
						
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//					}
//				}

				String priceinfo = org.apache.commons.lang.StringUtils.substringBetween(temptable,"<span>", "</span>");
				priceinfo = priceinfo.replace("\t", "");
				priceinfo = priceinfo.replace(" ", "");
				Matcher matcherPrice = Pattern.compile("(\\d*)").matcher(priceinfo);
				if(matcherPrice.find()){
					System.out.println("单价："+matcherPrice.group(1));
					flightDetail.setPrice(Double.parseDouble(matcherPrice.group(1)));
				}
				
				/*Matcher taxes = Pattern.compile("Taxes and Fees (.\\w*) (\\d*\\.\\d*)\\)").matcher(priceinfo);
				if(taxes.find()){
//					System.out.println("税费："+taxes.group(2));
					flightDetail.setTax(Double.parseDouble(taxes.group(2)));
				}*/
				
				flightDetail.setFlightno(flightNoList);								
				flightDetail.setDepcity(arg1.getDep());
				flightDetail.setDepdate(sf.parse(arg1.getDepDate()));
				flightDetail.setArrcity(arg1.getArr());
				flightDetail.setMonetaryunit("EUR");
				flightDetail.setWrapperid(arg1.getWrapperid());				
				baseFlight.setDetail(flightDetail);
				baseFlight.setInfo(segs);
				flightList.add(baseFlight);
				
				System.out.println("\r\n====================================================================================================================================================\r\n");
				
				tablehtml = tablehtml.replaceFirst("<tr>", "");
//				if(transfer){
//					tablehtml = tablehtml.replaceFirst("<tr>", "");					
//				}
			}

			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(flightList);
			return result;
		} catch(Exception e){
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
			return result;
		}
	}
	
}
