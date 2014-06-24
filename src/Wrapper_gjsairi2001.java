import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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
//		searchParam.setDep("MAD");
//		searchParam.setArr("ALC");
//		searchParam.setDepDate("2014-06-26");
//		searchParam.setRetDate("2014-06-30");
		searchParam.setDep("DUB");
		searchParam.setArr("ALC");
		searchParam.setDepDate("2014-06-26");
		searchParam.setRetDate("2014-06-30");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		searchParam.setWrapperid("gjsairi2001");
		
		String html = new Wrapper_gjsairi2001().getHtml(searchParam);
//		System.out.println(html);
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
		    int status = httpClient.executeMethod(get);
		    String html = get.getResponseBodyAsString();
//		    System.out.println("1、状态："+status+"\r\n"+html);
		    
		    String ajaxUrl = "http://www.iberiaexpress.com/airlines/web/loadIBFPOW.do?ajax=true&amp;tabId=0&amp;menuId=01000000000000";
		    String cookie = StringUtils.join(httpClient.getState().getCookies(),"; ");
			get = new QFGetMethod(ajaxUrl);
			httpClient.getState().clearCookies();
			get.addRequestHeader("Cookie",cookie);
			status = httpClient.executeMethod(get);
		    html = get.getResponseBodyAsString();
//		    System.out.println("2、状态："+status+"\r\n"+html);
		    
		    return html;

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
		if (html.contains("No se han encontrado vuelos disponibles en la fecha solicitada.") || html.contains("No fue posible encontrar disponibilidad.")) {
			result.setRet(false);
			result.setStatus(Constants.INVALID_DATE);
			return result;			
		}
		
		//获取到展示数据的table
		String tablehtml = org.apache.commons.lang.StringUtils.substringBetween(html, "<table id=\"tabla_ida\"", "</tbody>");
		String rethtml = org.apache.commons.lang.StringUtils.substringBetween(html, "<table id=\"tabla_vuelta\"", "</tbody>");
		
		String temptable = "";

		try {	
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			
			while(tablehtml.contains("class=\"vuelo_escala\">")){
				
				OneWayFlightInfo baseFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();				
				List<String> flightNoList = new ArrayList<String>();
				
				temptable = org.apache.commons.lang.StringUtils.substringBetween(tablehtml, "class=\"vuelo_escala\">", "</tr>");
				temptable = temptable.replaceAll("\n", "");
//				boolean flag = true;
//				for(int index1 = 0;flag;index1++){
//					for(int i = 1;;i++){
						FlightSegement seg = new FlightSegement();
						boolean depDate = true;
						String flightinfo = org.apache.commons.lang.StringUtils.substringBetween(temptable,"<input", "<td class");						
						if(null == flightinfo){
//							if(i==1){
//								flag = false;
//							}
							break;
						}
						flightinfo = flightinfo.replaceAll("\t", "");
						flightinfo = flightinfo.replaceAll(" ", "");
						
						Matcher matcherFlightNo = Pattern.compile("id=\"idNumFlight_.*\"value=\"(.*)\"").matcher(flightinfo);
						if(matcherFlightNo.find()){
							System.out.println("flightNo: " + matcherFlightNo.group(1));
							String flightNo = matcherFlightNo.group(1);
							flightNoList.add(flightNo);
							seg.setFlightno(flightNo);
						}
						
						Matcher matcher = Pattern.compile("type=\"hidden\"value=\"(\\d*)\"/><strong>(\\d*:\\d*)</strong>\\w*\\((\\w{3})\\)").matcher(flightinfo);
						while(matcher.find()){
							System.out.println("------------------------------------------------------------------------------");							
							if(depDate){
								System.out.println("出发时间："+matcher.group(1)+"\t"+matcher.group(2));
								seg.setDeptime(matcher.group(2));
								Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(matcher.group(1));
								seg.setDepDate(sf.format(date));
								System.out.println("始发地："+matcher.group(3));
								seg.setDepairport(matcher.group(3));
								depDate = false;
							}else{
								System.out.println("到达时间："+matcher.group(1)+"\t"+matcher.group(2));
								seg.setArrtime(matcher.group(2));
								Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(matcher.group(1));
								seg.setArrDate(sf.format(date));
								System.out.println("目的地："+matcher.group(3));
								seg.setArrairport(matcher.group(3));
							}
						}												
						
						segs.add(seg);
						
						boolean transfer = false; //是否中转
						if(flightinfo.contains("<inputtype=\"hidden\"value=\"null\"/>")){
							System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&中转：");							
							String transferhtml = tablehtml.replaceFirst("class=\"vuelo_escala\">", "");							
							if(transferhtml.contains("class=\"vuelo_escala\">")){
								transferhtml = org.apache.commons.lang.StringUtils.substringBetween(transferhtml, "class=\"vuelo_escala\">", "<tr class");
								transferhtml = transferhtml.replaceAll("\n", "");
								transferhtml = transferhtml.replaceAll("\t", "");
								transferhtml = transferhtml.replaceAll(" ", "");
								FlightSegement transferSeg = new FlightSegement();
								boolean transferDepDate = true;
								String transferFlightinfo = org.apache.commons.lang.StringUtils.substringBetween(transferhtml,"<input", "</td></tr>");						
								if(null == transferFlightinfo){
									break;
								}
								
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
						}
						
						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//					}
//				}

				String priceinfo = org.apache.commons.lang.StringUtils.substringBetween(temptable,"class=\"cajaPrecioTarifa\"", "<p class=\"txt_ult_plazas\">");
				priceinfo = priceinfo.replace("\t", "");
				priceinfo = priceinfo.replace(" ", "");
				Matcher matcherPrice = Pattern.compile("id=\"hidden_ida-(\\d*)-(\\d*)-color(\\d*)\"value=\"(\\d*\\.\\d*)/(\\d*\\.\\d*)\".*<span>(\\d*)</span>&(.*);").matcher(priceinfo);
				if(matcherPrice.find()){
					System.out.println("单价："+matcherPrice.group(4));
					flightDetail.setPrice(Double.parseDouble(matcherPrice.group(4)));
				}
				
				/*Matcher taxes = Pattern.compile("Taxes and Fees (.\\w*) (\\d*\\.\\d*)\\)").matcher(priceinfo);
				if(taxes.find()){
//					System.out.println("税费："+taxes.group(2));
					flightDetail.setTax(Double.parseDouble(taxes.group(2)));
				}*/
				
//				flightDetail.setFlightno(flightNoList);								
//				flightDetail.setDepcity(arg1.getDep());
//				flightDetail.setDepdate(sf.parse(arg1.getDepDate()));
//				flightDetail.setArrcity(arg1.getArr());
//				flightDetail.setMonetaryunit("EUR");
//				flightDetail.setWrapperid(arg1.getWrapperid());				
//				baseFlight.setDetail(flightDetail);
//				baseFlight.setInfo(segs);
//				flightList.add(baseFlight);
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				String retTempTable = "";
				
				while(rethtml.contains("class=\"vuelo_escala\">")){
					System.out.println("\r\n返程：\r\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					OneWayFlightInfo retBaseFlight = new OneWayFlightInfo();
					List<FlightSegement> retSegs = new ArrayList<FlightSegement>();
					FlightDetail retFlightDetail = new FlightDetail();				
					List<String> retFlightNoList = new ArrayList<String>();
					retSegs = segs;
					retFlightNoList = flightNoList;
					retFlightDetail = flightDetail;
					
					
					
					retTempTable = org.apache.commons.lang.StringUtils.substringBetween(rethtml, "class=\"vuelo_escala\">", "</tr>");
//					System.out.println(retTempTable);
					retTempTable = retTempTable.replaceAll("\n", "");
//					boolean flag = true;
//					for(int index1 = 0;flag;index1++){
//						for(int i = 1;;i++){
							FlightSegement retSeg = new FlightSegement();
							boolean retDepDate = true;
							String retFlightinfo = org.apache.commons.lang.StringUtils.substringBetween(retTempTable,"<input", "<td class");						
							if(null == retFlightinfo){
								break;
							}
							retFlightinfo = retFlightinfo.replaceAll("\t", "");
							retFlightinfo = retFlightinfo.replaceAll(" ", "");
							
							Matcher retMatcherFlightNo = Pattern.compile("id=\"idNumFlight_.*\"value=\"(.*)\"").matcher(retFlightinfo);
							if(retMatcherFlightNo.find()){
								System.out.println("flightNo: " + retMatcherFlightNo.group(1));
								String flightNo = retMatcherFlightNo.group(1);
								retFlightNoList.add(flightNo);
								retSeg.setFlightno(flightNo);
							}
							
							Matcher retMatcher = Pattern.compile("type=\"hidden\"value=\"(\\d*)\"/><strong>(\\d*:\\d*)</strong>\\w*\\((\\w{3})\\)").matcher(retFlightinfo);
							while(retMatcher.find()){
								System.out.println("------------------------------------------------------------------------------");							
								if(retDepDate){
									System.out.println("出发时间："+retMatcher.group(1)+"\t"+retMatcher.group(2));
									retSeg.setDeptime(retMatcher.group(2));
									Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(retMatcher.group(1));
									retSeg.setDepDate(sf.format(date));
									System.out.println("始发地："+retMatcher.group(3));
									retSeg.setDepairport(retMatcher.group(3));
									retDepDate = false;
								}else{
									System.out.println("到达时间："+retMatcher.group(1)+"\t"+retMatcher.group(2));
									retSeg.setArrtime(retMatcher.group(2));
									Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(retMatcher.group(1));
									retSeg.setArrDate(sf.format(date));
									System.out.println("目的地："+retMatcher.group(3));
									retSeg.setArrairport(retMatcher.group(3));
								}
							}												
							
							retSegs.add(retSeg);
							
							boolean retTransfer = false; //是否中转
							if(retFlightinfo.contains("<inputtype=\"hidden\"value=\"null\"/>")){
								System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&中转：");							
								String retTransferhtml = rethtml.replaceFirst("class=\"vuelo_escala\">", "");							
								if(retTransferhtml.contains("class=\"vuelo_escala\">")){
									retTransferhtml = org.apache.commons.lang.StringUtils.substringBetween(retTransferhtml, "class=\"vuelo_escala\">", "<tr class");
									retTransferhtml = retTransferhtml.replaceAll("\n", "");
									retTransferhtml = retTransferhtml.replaceAll("\t", "");
									retTransferhtml = retTransferhtml.replaceAll(" ", "");
									FlightSegement retTransferSeg = new FlightSegement();
									boolean retTransferDepDate = true;
									String retTransferFlightinfo = org.apache.commons.lang.StringUtils.substringBetween(retTransferhtml,"<input", "</td></tr>");						
									if(null == retTransferFlightinfo){
										break;
									}
									
									Matcher retTransferMatcherFlightNo = Pattern.compile("id=\"idNumFlight_.*\"value=\"(.*)\"").matcher(retTransferFlightinfo);
									if(retTransferMatcherFlightNo.find()){
										System.out.println("flightNo: " + retTransferMatcherFlightNo.group(1));
										String flightNo = retTransferMatcherFlightNo.group(1);
										retFlightNoList.add(flightNo);
										retTransferSeg.setFlightno(flightNo);
									}
									
									Matcher retTransferMatcher = Pattern.compile("type=\"hidden\"value=\"(\\d*)\"/><strong>(\\d*:\\d*)</strong>\\w*\\((\\w{3})\\)").matcher(retTransferFlightinfo);
									while(retTransferMatcher.find()){
										System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");							
										if(retTransferDepDate){
											System.out.println("出发时间："+retTransferMatcher.group(1)+"\t"+retTransferMatcher.group(2));
											retTransferSeg.setDeptime(retTransferMatcher.group(2));
											Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(retTransferMatcher.group(1));
											retTransferSeg.setDepDate(sf.format(date));
											System.out.println("始发地："+retTransferMatcher.group(3));
											retTransferSeg.setDepairport(retTransferMatcher.group(3));
											retTransferDepDate = false;
										}else{
											System.out.println("到达时间："+retTransferMatcher.group(1)+"\t"+retTransferMatcher.group(2));
											retTransferSeg.setArrtime(retTransferMatcher.group(2));
											Date date = new SimpleDateFormat("yyyyMMddHHmm").parse(retTransferMatcher.group(1));
											retTransferSeg.setArrDate(sf.format(date));
											System.out.println("目的地："+retTransferMatcher.group(3));
											retTransferSeg.setArrairport(retTransferMatcher.group(3));
										}
									}												
									
									retSegs.add(retTransferSeg);
									retTransfer = true;
								}
							}
							
							System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//						}
//					}

					String retPriceinfo = org.apache.commons.lang.StringUtils.substringBetween(retTempTable,"class=\"cajaPrecioTarifa\"", "<p class=\"txt_ult_plazas\">");
					retPriceinfo = retPriceinfo.replace("\t", "");
					retPriceinfo = retPriceinfo.replace(" ", "");
					Matcher retMatcherPrice = Pattern.compile("id=\"hidden_ida-(\\d*)-(\\d*)-color(\\d*)\"value=\"(\\d*\\.\\d*)/(\\d*\\.\\d*)\".*<span>(\\d*)</span>&(.*);").matcher(priceinfo);
					if(retMatcherPrice.find()){
						System.out.println("单价："+retMatcherPrice.group(4) + "去程：" + retFlightDetail.getPrice());
						retFlightDetail.setPrice(retFlightDetail.getPrice() + Double.parseDouble(retMatcherPrice.group(4)));
					}
					
					/*Matcher taxes = Pattern.compile("Taxes and Fees (.\\w*) (\\d*\\.\\d*)\\)").matcher(priceinfo);
					if(taxes.find()){
//						System.out.println("税费："+taxes.group(2));
						retFlightDetail.setTax(Double.parseDouble(taxes.group(2)));
					}*/
					
					retFlightDetail.setFlightno(retFlightNoList);								
					retFlightDetail.setDepcity(arg1.getDep());
					retFlightDetail.setDepdate(sf.parse(arg1.getDepDate()));
					retFlightDetail.setArrcity(arg1.getArr());
					retFlightDetail.setMonetaryunit("EUR");
					retFlightDetail.setWrapperid(arg1.getWrapperid());				
					retBaseFlight.setDetail(retFlightDetail);
					retBaseFlight.setInfo(retSegs);
					flightList.add(retBaseFlight);
					
					
					
					System.out.println("\r\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\r\n");
					
					rethtml = rethtml.replaceFirst("class=\"vuelo_escala\">", "");
					if(retTransfer){
						rethtml = rethtml.replaceFirst("class=\"vuelo_escala\">", "");					
					}
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				System.out.println("\r\n====================================================================================================================================================\r\n");
				
				tablehtml = tablehtml.replaceFirst("class=\"vuelo_escala\">", "");
				if(transfer){
					tablehtml = tablehtml.replaceFirst("class=\"vuelo_escala\">", "");					
				}
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
