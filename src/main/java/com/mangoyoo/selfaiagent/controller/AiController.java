package com.mangoyoo.selfaiagent.controller;

import com.mangoyoo.selfaiagent.app.MyApp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.chat.model.ChatModel;
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private MyApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }
}

