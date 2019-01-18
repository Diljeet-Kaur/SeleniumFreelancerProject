package com.test.mvn.proj.vo;

/**
 * The Class ShippingDataVO.
 */
public class ShippingDataVO {
	
	/** The input id. */
	private Integer inputId;
	
	/** The country. */
	private String country;
	
	/** The state. */
	private String state;
	
	/** The city. */
	private String city;
	
	/** The zip code. */
	private String zipCode;
	
	/**
	 * Instantiates a new shippipng data VO.
	 */
	public ShippingDataVO() {
		super();
	}

	/**
	 * Instantiates a new shippipng data VO.
	 *
	 * @param inputId the input id
	 * @param country the country
	 * @param state the state
	 * @param city the city
	 * @param zipCode the zip code
	 */
	public ShippingDataVO(Integer inputId, String country, 
			String state, String city, String zipCode) {
		this.inputId = inputId;
		this.country = country;
		this.state = state;
		this.city = city;
		this.zipCode = zipCode;
	}
	
	/**
	 * Gets the input id.
	 *
	 * @return the input id
	 */
	public Integer getInputId() {
		return inputId;
	}

	/**
	 * Sets the input id.
	 *
	 * @param inputId the new input id
	 */
	public void setInputId(Integer inputId) {
		this.inputId = inputId;
	}


	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the zip code.
	 *
	 * @return the zip code
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the zip code.
	 *
	 * @param zipCode the new zip code
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
}
