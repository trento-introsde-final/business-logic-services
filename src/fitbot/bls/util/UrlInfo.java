package fitbot.bls.util;

public class UrlInfo {
	
	public UrlInfo() {}
	
	static final String businessLogicServicesURL = "https://business-logic-services.herokuapp.com";
	static final String storageServicesURL = "https://storage-services.herokuapp.com";
	
	public String getStorageServicesURL() {
		return storageServicesURL;
	}
	
	public String getBusinessLogicServicesURL() {
		return businessLogicServicesURL;
	}
}