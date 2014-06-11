import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
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
import com.qunar.qfwrapper.util.QFPostMethod;

public class Wrapper_gjdairku001 implements QunarCrawler{

	private static long strtime = System.currentTimeMillis();	
	
	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		//DEL-CAI 2014-08-10
	    //BKK-CDG 2014-08-19
		//KWI-DXB
		searchParam.setDep("KWI");
		searchParam.setArr("DXB");
		searchParam.setDepDate("2014-08-10");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		searchParam.setWrapperid("gjdairku001");
		
		String html = new  Wrapper_gjdairku001().getHtml(searchParam);
		long getTime = System.currentTimeMillis()-strtime;
		System.out.println("获取html用时："+getTime);		

		ProcessResultInfo result = new ProcessResultInfo();
		result = new  Wrapper_gjdairku001().process(html,searchParam);
		System.out.println("解析html用时："+(System.currentTimeMillis()-getTime-strtime));
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

		String bookingUrlPre = "http://fly.kuwaitairways.com/IBE/SearchResult.aspx";
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("ro", "0");
		map.put("from", arg0.getDep());
		map.put("to", arg0.getArr());
		map.put("cur", "KWD");
		map.put("sdate", arg0.getDepDate().replaceAll("-", "/"));
		map.put("edate", arg0.getDepDate().replaceAll("-", "/"));
		map.put("adult", "1");
		map.put("child", "0");
		map.put("infant", "0");
		map.put("view", "0");
		map.put("btnsubmit", "Flight Search");
		bookingInfo.setInputs(map);		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;

	}

	public String getHtml(FlightSearchParam arg0) {
		QFGetMethod get = null;	
		QFPostMethod post = null;	
		try {	
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			
			/*对于需要cookie的网站，请自己处理cookie（必须）
			* 例如：
			* httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			*/
			
		    String postUrl = "http://fly.kuwaitairways.com/SessionHandler.aspx?target=/IBE.aspx&pub=/kw/English&Tab=1&s=&h=&header=true&footer=true";
		    post = new QFPostMethod(postUrl);
		    NameValuePair resultby = new NameValuePair("resultby", "0");
		    NameValuePair selacity1 = new NameValuePair("selacity1", arg0.getArr());
		    NameValuePair seladate1 = new NameValuePair("seladate1", "");
		    NameValuePair seladults = new NameValuePair("seladults", "1");
		    NameValuePair selcabinclass = new NameValuePair("selcabinclass", "0");
		    NameValuePair selchildren = new NameValuePair("selchildren", "0");
		    NameValuePair seldcity1 = new NameValuePair("seldcity1", arg0.getDep());
		    NameValuePair selddate1 = new NameValuePair("selddate1", arg0.getDepDate());
		    NameValuePair selinfants = new NameValuePair("selinfants", "0");
		    NameValuePair tid = new NameValuePair("tid", "OW");
		    NameValuePair promocode = new NameValuePair("promocode", "");
		    post.setRequestBody(new NameValuePair[]{resultby,selacity1,seladate1,seladults,selcabinclass,selchildren,seldcity1,selddate1,selinfants,tid,promocode});
		    int status = httpClient.executeMethod(post);
		    String html = post.getResponseBodyAsString();
//		    System.out.println("2. "+status+" ~~~~~~~~~~~~~ "+html);
		    
		    String getUrl = "http://fly.kuwaitairways.com/IBE.aspx?j=t";
		    get = new QFGetMethod(getUrl);		
		    
		    //1、对于通过多次get|post请求才能得到包含机票信息的网站，需要注意对status的判断
			//2、对于通过多次get|post请求才能得到包含机票信息的网站，如果需要cookie，则在每一次get|post请求前都处理好cookie
			//3、如果网站需要使用cookie，GetMethod 遇到 302 时默认会自动跳转，不留机会给 开发处理Cookie，这个时候要特别小心， 需要使用 get.setFollowRedirects(false); 阻止自动跳转，然后自己处理302 以及Cookie。
		    try {
				get.setFollowRedirects(false);
				get.getParams().setContentCharset("utf-8");
				status = httpClient.executeMethod(get);
			    html = get.getResponseBodyAsString();
//			    System.out.println("3. "+status+" ~~~~~~~~~~~~~ "+html);
			
				if(get.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY || get.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY){
					Header location = get.getResponseHeader("Location");
					String url = "";
					if(location !=null){
						url = location.getValue();
						if(!url.startsWith("http")){
							url = get.getURI().getScheme() + "://" + get.getURI().getHost() + (get.getURI().getPort()==-1?"":(":"+get.getURI().getPort())) + url;
						}
					}else{
						return null;
					}
					String cookie = StringUtils.join(httpClient.getState().getCookies(),"; ");
					get = new QFGetMethod(url);
					httpClient.getState().clearCookies();
					get.addRequestHeader("Cookie",cookie);
					status = httpClient.executeMethod(get);
				    html = get.getResponseBodyAsString();
//				    System.out.println("4. "+status+" ~~~~~~~~~~~~~ "+html);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(get!=null){
					get.releaseConnection();
				}
			}
		    
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
		String tablehtml = org.apache.commons.lang.StringUtils.substringBetween(html, "class=\"MainTBL2\">", "<td>&nbsp;</td>\r\n</tr>\r\n\r\n<tr>\r\n<td>&nbsp;</td>");
		
		String pricetable = tablehtml;
		double lowprice = 0.00;
		String lowpricestr = null;
		while(pricetable.contains("for 1 passenger(s) (Fare")){
			Matcher matcherLowPrice = Pattern.compile("Fare .\\w* (\\d*\\.\\d*) \\+").matcher(pricetable);
			if(matcherLowPrice.find()){
				System.out.println("单价："+matcherLowPrice.group(1));
				double tempprice = Double.parseDouble(matcherLowPrice.group(1));
				if(lowprice == 0.00 || lowprice > tempprice){
					lowprice = tempprice;
					lowpricestr = matcherLowPrice.group(0);
				}
				pricetable = pricetable.replaceFirst(lowpricestr, "");
			}
		}
		System.out.println("最低价："+lowprice);
		
		String temptable = "";

		try {	
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			
			
			while(tablehtml.contains("class=\"BlueHeaderTBL\">")){
				
				OneWayFlightInfo baseFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();
				FlightSegement seg = new FlightSegement();
				List<String> flightNoList = new ArrayList<String>();
				boolean depDate = true;

				temptable = org.apache.commons.lang.StringUtils.substringBetween(tablehtml, "class=\"BlueHeaderTBL\">", "</table>\r\n</td>\r\n</tr>\r\n</table>");
				if(!temptable.contains(lowpricestr)){
					break;
				}else{
					tablehtml = tablehtml.replaceAll(lowpricestr, "");
				}
				
				String[] strs = temptable.split("\r\n");
				
				for(String str : strs){		
					
					Matcher matcherFlightNo = Pattern.compile("Flight_Info.*>\\b(\\w*\\d*)\\b<").matcher(str);
					if(matcherFlightNo.find()){
						System.out.println("flightNo: " + matcherFlightNo.group(1));
						String flightNo = matcherFlightNo.group(1);
						flightNoList.add(flightNo);
					}
									
//					Matcher matcherPlace = Pattern.compile("\\((.*)\\)<br />").matcher(str);
//					if(matcherPlace.find()){
//						if(matcherPlace.group(1).contains(arg1.getDep())){
//							System.out.println("始发地："+matcherPlace.group(1));
//							seg.setDepairport(matcherPlace.group(1));
//						}else if(matcherPlace.group(1).contains(arg1.getArr())){
//							System.out.println("目的地："+matcherPlace.group(1));
//							seg.setArrairport(matcherPlace.group(1));
//						}else{
//							System.out.println("中转地："+matcherPlace.group(1));
//						}
//					}
									
					Matcher matcherDate = Pattern.compile("\\b(\\d{2}:\\d{2})\\b.*\\b(\\w{3})\\b.*\\b(\\d{2}-\\w{3}-\\d{2})\\b").matcher(str);
					if(matcherDate.find()){
						if(depDate){
							System.out.print("出发时间：");
							seg.setDeptime(matcherDate.group(1));
							seg.setDepDate(matcherDate.group(3));
							flightDetail.setDepdate(new Date(matcherDate.group(3)));
							depDate = false;
						}else{
							System.out.print("到达时间：");
							seg.setArrtime(matcherDate.group(1));
							seg.setArrDate(matcherDate.group(3));
						}
						System.out.println(matcherDate.group(1)+" "+matcherDate.group(2)+" "+matcherDate.group(3));
					}
					
					Matcher sumprice = Pattern.compile("OrangeText_Bold.>\\b(.\\w*) (\\d*\\.\\d*)\\b<").matcher(str);
					if(sumprice.find()){
						System.out.println("总价："+sumprice.group(2));
						flightDetail.setMonetaryunit(sumprice.group(1));
					}
					
					Matcher matcherPrice = Pattern.compile("Fare (.\\w*) (\\d*\\.\\d*) \\+").matcher(str);
					if(matcherPrice.find()){
						System.out.println("单价："+matcherPrice.group(2));
						flightDetail.setPrice(Double.parseDouble(matcherPrice.group(2)));
					}
					
					Matcher taxes = Pattern.compile("Taxes and Fees (.\\w*) (\\d*\\.\\d*)\\)").matcher(str);
					if(taxes.find()){
						System.out.println("税费："+taxes.group(2));
						flightDetail.setTax(Double.parseDouble(taxes.group(2)));
					}				
				}
				
				seg.setDepairport(arg1.getDep());
				seg.setArrairport(arg1.getArr());
				seg.setFlightno(flightNoList.toString());
				flightDetail.setFlightno(flightNoList);								
				flightDetail.setDepcity(arg1.getDep());
				flightDetail.setArrcity(arg1.getArr());
				flightDetail.setWrapperid(arg1.getWrapperid());
				segs.add(seg);
				baseFlight.setDetail(flightDetail);
				baseFlight.setInfo(segs);
				flightList.add(baseFlight);
				
				System.out.println("\r\n==================================================================================\r\n");
				
				tablehtml = tablehtml.replaceFirst("class=\"BlueHeaderTBL\">", "");
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
