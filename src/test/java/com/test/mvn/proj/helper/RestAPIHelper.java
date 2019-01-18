package com.test.mvn.proj.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.test.mvn.proj.exception.EmergartSeleniumException;
import com.test.mvn.proj.util.TestUtility;
import com.test.mvn.proj.vo.ShippingCostDetailsVO;

/**
 * The Class RestAPIHelper.
 */
public class RestAPIHelper {
	
	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(RestAPIHelper.class); 
	
	/**
	 * Update product and artist details.
	 *
	 * @param productId the product id
	 * @param props the props
	 * @return the shipping cost details VO
	 * @throws EmergartSeleniumException the emergart selenium exception
	 */
	public static ShippingCostDetailsVO updateProductAndArtistDetails(String productId, 
			Properties props) throws EmergartSeleniumException {
		LOG.debug("updateProductAndArtistDetails : start");
		ShippingCostDetailsVO returnShpCostVO = null;
		String webserviceURL = new StringBuilder().append(props.getProperty("restAPIURL"))
				.append("/getProductAndArtistDetails.php?productid=")
				.append(productId).toString();
		try {
			URL url = new URL(webserviceURL);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");
			int responseCode= conn.getResponseCode();
			if(HttpURLConnection.HTTP_OK != responseCode){
				LOG.error("Webservice : "+ webserviceURL + " not returning HTTP error code : " + responseCode);
				throw new RuntimeException("Product Id : "+ productId+ 
						" : WebService Call failed : HTTP error code : "
						+ responseCode);
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			returnShpCostVO = parseOutputForService(br);
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			LOG.error("Webservice URL not correct : "+webserviceURL, e);
			throw new EmergartSeleniumException(e);
		} catch (IOException e) {
			LOG.error("Webservice : "+webserviceURL + " throwing IOException", e);
			throw new EmergartSeleniumException(e);
		} catch (ParseException e) {
			LOG.error("Exception parsing response for Webservice : "+webserviceURL, e);
			throw new EmergartSeleniumException(e);
		}
		
		LOG.debug("updateProductAndArtistDetails : end");
		return returnShpCostVO;
	}
	
	/**
	 * Find shipping cost for product.
	 *
	 * @param outputCostVO the output cost VO
	 * @param props the props
	 * @return the string
	 * @throws EmergartSeleniumException the emergart selenium exception
	 */
	public static String findShippingCostForProduct(ShippingCostDetailsVO outputCostVO,
			Properties props) throws EmergartSeleniumException {
		LOG.debug("findShippingCostForProduct : start");
		String shippingCost = TestUtility.SHP_COST_NOT_FOUND;
		String webserviceURL = StringUtils.replaceAll(new StringBuilder().append(props.getProperty("restAPIURL"))
				.append("/findFedexRateV2.php?productid=")
				.append(outputCostVO.getProductId())
				.append("&destCity=").append(outputCostVO.getDestinationCity())
				.append("&destCountry=").append(outputCostVO.getDestinationCountry())
				.append("&destZIP=").append(outputCostVO.getDestinationZipcode())
				.append("&destState=").append(outputCostVO.getDestinationState())
				.toString(),StringUtils.SPACE,"%20");
		try {
			System.out.println(webserviceURL);
			final HttpGet request = new HttpGet(webserviceURL);
			final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
			final CloseableHttpClient httpClient = clientBuilder.build();
			CloseableHttpResponse response = httpClient.execute(request);
			String outputResult = EntityUtils.toString(response.getEntity());

			System.out.println(outputResult);
			
			if(StringUtils.isNotEmpty(outputResult)){
				shippingCost = outputResult.toString();
			}
			
		} catch (IOException e) {
			LOG.error("Webservice : "+webserviceURL + " throwing IOException", e);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("findShippingCostForProduct : end");
		return shippingCost;
	}

	/**
	 * Parses the output for service.
	 *
	 * @param br the br
	 * @return the shipping cost details VO
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	private static ShippingCostDetailsVO parseOutputForService(BufferedReader br) throws IOException, ParseException {
		LOG.debug("parseOutputForService : start");
		JSONParser parser = new JSONParser();
		ShippingCostDetailsVO returnShpCostVO = new ShippingCostDetailsVO();
		JSONObject jsonObject = (JSONObject) parser.parse(br);
		returnShpCostVO.setProductHeight((String) jsonObject.get("ProductHeight"));
		returnShpCostVO.setProductWidth((String) jsonObject.get("ProductWidth"));
		returnShpCostVO.setProductDepth((String) jsonObject.get("ProductDepth"));
		returnShpCostVO.setProductWeight(Double.valueOf(String.valueOf(jsonObject.get("ProductWeight"))));
		returnShpCostVO.setSourceCity((String) jsonObject.get("ArtistCity"));
		returnShpCostVO.setSourceState((String) jsonObject.get("ArtistState"));
		returnShpCostVO.setSourceCountry((String) jsonObject.get("ArtistCountry"));
		returnShpCostVO.setSourceZipcode((String) jsonObject.get("ArtistZIP"));
		returnShpCostVO.setProductPrice((String) jsonObject.get("ProductPrice"));
		LOG.debug("parseOutputForService : end");
		return returnShpCostVO;
	}
}
