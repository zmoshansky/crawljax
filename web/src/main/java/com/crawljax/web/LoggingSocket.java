package com.crawljax.web;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class LoggingSocket extends WebSocketAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingSocket.class);
	private SocketLogAppender appender;

	@Override
	public void onWebSocketConnect(Session session) {
		LOG.info("Socket connected!");
		super.onWebSocketConnect(session);
		sendText("Welcome to this socket @ " + new Date());
	}

	private void sendLogFile(String crawlId) {
		File log =
		        new File(System.getProperty("user.home") + File.separator + "crawljax"
		                + File.separator + crawlId + File.separator + "crawl.log");
		if (log.exists()) {
			try {
				LOG.debug("Reading the logfile");
				String asString =
				        Files.toString(log, Charsets.UTF_8).replace(
				                System.getProperty("line.separator"), "<br><br>");
				LOG.debug("Sending the log file");
				sendText(asString);
			} catch (IOException e) {
				LOG.warn("Could not read the log file");
			}
		}

	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		LOG.info("Socket disconnected with status code {} reason: {}", statusCode, reason);
		appender.stop();
	}

	@Override
	public void onWebSocketText(String message) {
		LOG.info("Received text: {}", message);

		if (message.startsWith("startlog")) {
			sendLogFile(message.split("-")[1]);
			appender = new SocketLogAppender(this);
		}
		if (message.startsWith("stoplog")) {
			appender.stop();
		}
	}

	public void sendText(String text) {
		try {
			getSession().getRemote().sendString(text);
		} catch (IOException e) {
			LOG.error("Could not send message {}", text, e);
		}
	}
}
