package com.GMPV.GMPV.Service;

import com.GMPV.GMPV.DTO.StockAlertNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(StockNotificationService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyLowStock(StockAlertNotification notification) {
        logger.info("Sending WebSocket notification: {}", notification);
        messagingTemplate.convertAndSend("/topic/stock-alerts", notification);
    }
}
