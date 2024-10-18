package com.alwx.backend.service.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TableUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TableUpdateService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyTableUpdate() {
        messagingTemplate.convertAndSend("/topic/tableUpdates", "Данные в таблице обновлены");
    }
}

