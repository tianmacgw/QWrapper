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

public class Wrapper_gjsairku001 implements QunarCrawler{

//	private static long strtime = System.currentTimeMillis();	
	
	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		//DEL-CAI 2014-08-10
	    //BKK-CDG 2014-08-19
		//KWI-DXB
		searchParam.setDep("KWI");
		searchParam.setArr("DXB");
		searchParam.setDepDate("2014-08-10");
		searchParam.setRetDate("2014-08-20");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		searchParam.setWrapperid("gjsairku001");
		
		String html = new  Wrapper_gjsairku001().getHtml(searchParam);
//		long getTime = System.currentTimeMillis()-strtime;
//		System.out.println("获取html用时："+getTime);		

		ProcessResultInfo result = new ProcessResultInfo();
		result = new  Wrapper_gjsairku001().process(html,searchParam);
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

		String bookingUrlPre = "http://fly.kuwaitairways.com/SessionHandler.aspx?target=/IBE.aspx&pub=/kw/English&Tab=1&s=&h=&header=true&footer=true";
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("resultby", "0");
		map.put("selacity1", arg0.getArr());
		map.put("seladate1", arg0.getRetDate());
		map.put("seladults", "1");	
		map.put("selcabinclass", "0");	
		map.put("selchildren", "0");	
		map.put("seldcity1", arg0.getDep());	
		map.put("selddate1", arg0.getDepDate());
		map.put("selinfants", "0");	
		map.put("tid", "SB");
		map.put("promocode", "");
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
		    NameValuePair seladate1 = new NameValuePair("seladate1", arg0.getRetDate());
		    NameValuePair seladults = new NameValuePair("seladults", "1");
		    NameValuePair selcabinclass = new NameValuePair("selcabinclass", "0");
		    NameValuePair selchildren = new NameValuePair("selchildren", "0");
		    NameValuePair seldcity1 = new NameValuePair("seldcity1", arg0.getDep());
		    NameValuePair selddate1 = new NameValuePair("selddate1", arg0.getDepDate());
		    NameValuePair selinfants = new NameValuePair("selinfants", "0");
		    NameValuePair tid = new NameValuePair("tid", "SB");
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
		
		String temptable = "";

		try {	
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			
			int index0 = 0;			
			while(tablehtml.contains("class=\"BlueHeaderTBL\">")){				
				OneWayFlightInfo baseFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();
				FlightSegement seg = new FlightSegement();
				List<String> flightNoList = new ArrayList<String>();
				boolean depDate = true;

				temptable = org.apache.commons.lang.StringUtils.substringBetween(tablehtml, "class=\"BlueHeaderTBL\">", "</table>\r\n</td>\r\n</tr>\r\n</table>");
				boolean flag = true;
				for(int index1 = 0;flag;index1++){
					for(int i = 1;;i++){						
						String flightinfo = org.apache.commons.lang.StringUtils.substringBetween(temptable,"ctl00_c_CtrlFltResult_ctl0"+index0+"_ctl0"+index1+"_ctl0"+i+"_tdFCode", "</tr>");
						
						if(null == flightinfo){
							if(i==1){
								flag = false;
							}
							break;
						}
						
						String[] strs = flightinfo.split("\r\n");
						
						for(String str : strs){		
							
							Matcher matcherFlightNo = Pattern.compile("Flight_Info.*>\\b(\\w*\\d*)\\b<").matcher(str);
							if(matcherFlightNo.find()){
//								System.out.println("flightNo: " + matcherFlightNo.group(1));
								String flightNo = matcherFlightNo.group(1);
								flightNoList.add(flightNo);
								seg.setFlightno(flightNo);
							}
											
							Matcher matcherPlace = Pattern.compile("\\((.*)\\)<br />").matcher(str);
							if(matcherPlace.find()){
								if(str.contains("deptairport")){
//									System.out.println("始发地："+matcherPlace.group(1));
									seg.setDepairport(matcherPlace.group(1));
								}else if(str.contains("arrairport")){
//									System.out.println("目的地："+matcherPlace.group(1));
									seg.setArrairport(matcherPlace.group(1));
								}
							}
											
							Matcher matcherDate = Pattern.compile("\\b(\\d{2}:\\d{2})\\b.*\\b(\\w{3})\\b.*\\b(\\d{2}-\\w{3}-\\d{2})\\b").matcher(str);
							if(matcherDate.find()){
								if(depDate){
//									System.out.print("出发时间：");
									seg.setDeptime(matcherDate.group(1));
									seg.setDepDate(matcherDate.group(3));
									flightDetail.setDepdate(new Date(matcherDate.group(3)));
									depDate = false;
								}else{
//									System.out.print("到达时间：");
									seg.setArrtime(matcherDate.group(1));
									seg.setArrDate(matcherDate.group(3));
								}
//								System.out.println(matcherDate.group(1)+" "+matcherDate.group(2)+" "+matcherDate.group(3));
							}
											
						}					
						segs.add(seg);					
//						System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					}
//					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}

				String priceinfo = org.apache.commons.lang.StringUtils.substringBetween(temptable,"for 1 passenger(s)", "</td>");					
				Matcher matcherPrice = Pattern.compile("Fare (.\\w*) (\\d*\\.\\d*) \\+").matcher(priceinfo);
				if(matcherPrice.find()){
//					System.out.println("单价："+matcherPrice.group(2));
					flightDetail.setPrice(Double.parseDouble(matcherPrice.group(2)));
				}
				
				Matcher taxes = Pattern.compile("Taxes and Fees (.\\w*) (\\d*\\.\\d*)\\)").matcher(priceinfo);
				if(taxes.find()){
//					System.out.println("税费："+taxes.group(2));
					flightDetail.setTax(Double.parseDouble(taxes.group(2)));
				}
				
				flightDetail.setFlightno(flightNoList);								
				flightDetail.setDepcity(arg1.getDep());
				flightDetail.setArrcity(arg1.getArr());
				flightDetail.setWrapperid(arg1.getWrapperid());				
				baseFlight.setDetail(flightDetail);
				baseFlight.setInfo(segs);
				flightList.add(baseFlight);
				
//				System.out.println("\r\n==================================================================================\r\n");
								
				tablehtml = tablehtml.replaceFirst("class=\"BlueHeaderTBL\">", "");
//				System.out.println(tablehtml);
				index0++;
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
