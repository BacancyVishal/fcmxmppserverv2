package com.wedevol.xmpp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import com.wedevol.xmpp.bean.CcsOutMessage;
import com.wedevol.xmpp.server.CcsClient;
import com.wedevol.xmpp.server.MessageHelper;
import com.wedevol.xmpp.util.Util;

/**
 * Entry Point class for the XMPP Server
 */
public class EntryPoint {

	public static final Logger logger = Logger.getLogger(EntryPoint.class.getName());

	public static void main(String[] args) throws SmackException, IOException {
		final String fcmProjectSenderId = args[0];
		final String fcmServerKey = args[1];
		final String toRegId = args[2];

		final CcsClient ccsClient = CcsClient.prepareCcsClient(fcmProjectSenderId, fcmServerKey, true); // true for debugging

		try {
			ccsClient.connect();
		} catch (XMPPException | InterruptedException e) {
			logger.log(Level.SEVERE, "Error trying to connect.", e);
		}

		// Send a sample downstream message to a device
		final String messageId = Util.getUniqueMessageId();
		final Map<String, String> dataPayload = new HashMap<String, String>();
		dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, "This is the simple sample message");
		final CcsOutMessage message = new CcsOutMessage(toRegId, messageId, dataPayload);
		final String jsonRequest = MessageHelper.createJsonOutMessage(message);
		ccsClient.sendPacket(jsonRequest);

		try {
			final CountDownLatch latch = new CountDownLatch(1);
			latch.await();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "An error occurred while latch was waiting.", e);
		}
	}
}
