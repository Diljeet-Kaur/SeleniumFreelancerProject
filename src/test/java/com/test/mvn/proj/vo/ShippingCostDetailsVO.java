package com.test.mvn.proj.vo;

/**
 * The Class ShippingCostDetailsVO.
 */
public class ShippingCostDetailsVO {
	
	/** The product id. */
	private Long productId;
	
	/** The product name. */
	private String productName;
	
	/** The product type. */
	private String productType;
	
	/** The product price. */
	private String productPrice;
	
	/** The product URL. */
	private String productURL;
	
	/** The product height. */
	private String productHeight;
	
	/** The product width. */
	private String productWidth;
	
	/** The product depth. */
	private String productDepth;
	
	/** The product weight. */
	private Double productWeight;
	
	/** The source city. */
	private String sourceCity;
	
	/** The source state. */
	private String sourceState;
	
	/** The source country. */
	private String sourceCountry;
	
	/** The source zipcode. */
	private String sourceZipcode;
	
	/** The destination city. */
	private String destinationCity;
	
	/** The destination state. */
	private String destinationState;
	
	/** The destination country. */
	private String destinationCountry;
	
	/** The destination zipcode. */
	private String destinationZipcode;
	
	/** The quotation by. */
	private String quotationBy;
	
	/** The quotation price. */
	private String quotationPrice;
	
	/** The recorded at. */
	private String recordedAt;
	
	/** The input row id. */
	private Integer inputRowId;
	
	/**
	 * Instantiates a new shipping cost details VO.
	 */
	public ShippingCostDetailsVO() {
		super();
	}

	/**
	 * Instantiates a new shipping cost details VO.
	 *
	 * @param productId the product id
	 * @param destinationCity the destination city
	 * @param destinationCountry the destination country
	 * @param destinationZipcode the destination zipcode
	 */
	public ShippingCostDetailsVO(Long productId, String destinationCity, String destinationCountry,
			String destinationZipcode) {
		super();
		this.productId = productId;
		this.destinationCity = destinationCity;
		this.destinationCountry = destinationCountry;
		this.destinationZipcode = destinationZipcode;
	}

	/**
	 * Instantiates a new shipping cost details VO.
	 *
	 * @param productId the product id
	 * @param productName the product name
	 * @param productType the product type
	 * @param productPrice the product price
	 * @param productURL the product URL
	 * @param productHeight the product height
	 * @param productWidth the product width
	 * @param productDepth the product depth
	 * @param productWeight the product weight
	 * @param sourceCity the source city
	 * @param sourceState the source state
	 * @param sourceCountry the source country
	 * @param sourceZipcode the source zipcode
	 * @param destinationCity the destination city
	 * @param destinationState the destination state
	 * @param destinationCountry the destination country
	 * @param destinationZipcode the destination zipcode
	 * @param inputRowId the input row id
	 */
	public ShippingCostDetailsVO(Long productId, String productName, String productType, String productPrice, 
			String productURL, String productHeight, String productWidth, String productDepth, 
			Double productWeight, String sourceCity, String sourceState,
			String sourceCountry, String sourceZipcode, String destinationCity, String destinationState,
			String destinationCountry, String destinationZipcode, Integer inputRowId) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.productPrice = productPrice;
		this.productURL = productURL;
		this.productHeight = productHeight;
		this.productWidth = productWidth;
		this.productDepth = productDepth;
		this.productWeight = productWeight;
		this.sourceCity = sourceCity;
		this.sourceState = sourceState;
		this.sourceCountry = sourceCountry;
		this.sourceZipcode = sourceZipcode;
		this.destinationCity = destinationCity;
		this.destinationState = destinationState;
		this.destinationCountry = destinationCountry;
		this.destinationZipcode = destinationZipcode;
		this.inputRowId = inputRowId;
	}

	/**
	 * Gets the product id.
	 *
	 * @return the product id
	 */
	public Long getProductId() {
		return productId;
	}

	/**
	 * Sets the product id.
	 *
	 * @param productId the new product id
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}

	/**
	 * Gets the product name.
	 *
	 * @return the product name
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * Sets the product name.
	 *
	 * @param productName the new product name
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * Gets the product type.
	 *
	 * @return the product type
	 */
	public String getProductType() {
		return productType;
	}

	/**
	 * Sets the product type.
	 *
	 * @param productType the new product type
	 */
	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	

	/**
	 * Gets the product price.
	 *
	 * @return the product price
	 */
	public String getProductPrice() {
		return productPrice;
	}

	/**
	 * Sets the product price.
	 *
	 * @param productPrice the new product price
	 */
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	/**
	 * Gets the product URL.
	 *
	 * @return the product URL
	 */
	public String getProductURL() {
		return productURL;
	}

	/**
	 * Sets the product URL.
	 *
	 * @param productURL the new product URL
	 */
	public void setProductURL(String productURL) {
		this.productURL = productURL;
	}

	/**
	 * Gets the product height.
	 *
	 * @return the product height
	 */
	public String getProductHeight() {
		return productHeight;
	}

	/**
	 * Sets the product height.
	 *
	 * @param productHeight the new product height
	 */
	public void setProductHeight(String productHeight) {
		this.productHeight = productHeight;
	}

	/**
	 * Gets the product width.
	 *
	 * @return the product width
	 */
	public String getProductWidth() {
		return productWidth;
	}

	/**
	 * Sets the product width.
	 *
	 * @param productWidth the new product width
	 */
	public void setProductWidth(String productWidth) {
		this.productWidth = productWidth;
	}

	/**
	 * Gets the product depth.
	 *
	 * @return the product depth
	 */
	public String getProductDepth() {
		return productDepth;
	}

	/**
	 * Sets the product depth.
	 *
	 * @param productDepth the new product depth
	 */
	public void setProductDepth(String productDepth) {
		this.productDepth = productDepth;
	}

	/**
	 * Gets the product weight.
	 *
	 * @return the product weight
	 */
	public Double getProductWeight() {
		return productWeight;
	}

	/**
	 * Sets the product weight.
	 *
	 * @param productWeight the new product weight
	 */
	public void setProductWeight(Double productWeight) {
		this.productWeight = productWeight;
	}

	/**
	 * Gets the source city.
	 *
	 * @return the source city
	 */
	public String getSourceCity() {
		return sourceCity;
	}

	/**
	 * Sets the source city.
	 *
	 * @param sourceCity the new source city
	 */
	public void setSourceCity(String sourceCity) {
		this.sourceCity = sourceCity;
	}

	/**
	 * Gets the source state.
	 *
	 * @return the source state
	 */
	public String getSourceState() {
		return sourceState;
	}

	/**
	 * Sets the source state.
	 *
	 * @param sourceState the new source state
	 */
	public void setSourceState(String sourceState) {
		this.sourceState = sourceState;
	}

	/**
	 * Gets the source country.
	 *
	 * @return the source country
	 */
	public String getSourceCountry() {
		return sourceCountry;
	}

	/**
	 * Sets the source country.
	 *
	 * @param sourceCountry the new source country
	 */
	public void setSourceCountry(String sourceCountry) {
		this.sourceCountry = sourceCountry;
	}

	/**
	 * Gets the source zipcode.
	 *
	 * @return the source zipcode
	 */
	public String getSourceZipcode() {
		return sourceZipcode;
	}

	/**
	 * Sets the source zipcode.
	 *
	 * @param sourceZipcode the new source zipcode
	 */
	public void setSourceZipcode(String sourceZipcode) {
		this.sourceZipcode = sourceZipcode;
	}

	/**
	 * Gets the destination city.
	 *
	 * @return the destination city
	 */
	public String getDestinationCity() {
		return destinationCity;
	}

	/**
	 * Sets the destination city.
	 *
	 * @param destinationCity the new destination city
	 */
	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}

	/**
	 * Gets the destination state.
	 *
	 * @return the destination state
	 */
	public String getDestinationState() {
		return destinationState;
	}

	/**
	 * Sets the destination state.
	 *
	 * @param destinationState the new destination state
	 */
	public void setDestinationState(String destinationState) {
		this.destinationState = destinationState;
	}

	/**
	 * Gets the destination country.
	 *
	 * @return the destination country
	 */
	public String getDestinationCountry() {
		return destinationCountry;
	}

	/**
	 * Sets the destination country.
	 *
	 * @param destinationCountry the new destination country
	 */
	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}

	/**
	 * Gets the destination zipcode.
	 *
	 * @return the destination zipcode
	 */
	public String getDestinationZipcode() {
		return destinationZipcode;
	}

	/**
	 * Sets the destination zipcode.
	 *
	 * @param destinationZipcode the new destination zipcode
	 */
	public void setDestinationZipcode(String destinationZipcode) {
		this.destinationZipcode = destinationZipcode;
	}

	/**
	 * Gets the quotation by.
	 *
	 * @return the quotation by
	 */
	public String getQuotationBy() {
		return quotationBy;
	}

	/**
	 * Sets the quotation by.
	 *
	 * @param quotationBy the new quotation by
	 */
	public void setQuotationBy(String quotationBy) {
		this.quotationBy = quotationBy;
	}

	/**
	 * Gets the quotation price.
	 *
	 * @return the quotation price
	 */
	public String getQuotationPrice() {
		return quotationPrice;
	}

	/**
	 * Sets the quotation price.
	 *
	 * @param quotationPrice the new quotation price
	 */
	public void setQuotationPrice(String quotationPrice) {
		this.quotationPrice = quotationPrice;
	}

	/**
	 * Gets the recorded at.
	 *
	 * @return the recorded at
	 */
	public String getRecordedAt() {
		return recordedAt;
	}

	/**
	 * Sets the recorded at.
	 *
	 * @param recordedAt the new recorded at
	 */
	public void setRecordedAt(String recordedAt) {
		this.recordedAt = recordedAt;
	}

	/**
	 * Gets the input row id.
	 *
	 * @return the input row id
	 */
	public Integer getInputRowId() {
		return inputRowId;
	}

	/**
	 * Sets the input row id.
	 *
	 * @param inputRowId the new input row id
	 */
	public void setInputRowId(Integer inputRowId) {
		this.inputRowId = inputRowId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder().append(productId)
				.append(destinationZipcode).append(destinationCity)
				.append(destinationCountry);
		return strBuilder.toString();
	}
}