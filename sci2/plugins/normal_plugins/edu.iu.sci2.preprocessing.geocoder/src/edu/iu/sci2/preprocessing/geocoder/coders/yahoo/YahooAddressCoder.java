package edu.iu.sci2.preprocessing.geocoder.coders.yahoo;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.cishell.utilities.network.DownloadHandler.InvalidUrlException;
import org.cishell.utilities.network.DownloadHandler.NetworkConnectionException;

import edu.iu.sci2.preprocessing.geocoder.coders.yahoo.placefinder.PlaceFinderClient;
import edu.iu.sci2.preprocessing.geocoder.coders.yahoo.placefinder.beans.ResultSet;

public class YahooAddressCoder extends AbstractYahooCoder {
	
	public YahooAddressCoder(String applicationId) {
		super(applicationId);
	}

	@Override
	public CODER_TYPE getLocationType() {
		return CODER_TYPE.ADDRESS;
	}

	@Override
	public ResultSet requestYahooService(String location, String applicationId)
			throws IOException, JAXBException, InvalidUrlException,
			NetworkConnectionException {
		return PlaceFinderClient.requestAddress(location, applicationId);
	}
}
