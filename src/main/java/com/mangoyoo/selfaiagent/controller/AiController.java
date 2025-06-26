package com.mangoyoo.selfaiagent.controller;

import com.mangoyoo.selfaiagent.Manus.MyManus;
import com.mangoyoo.selfaiagent.app.MyApp;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private MyApp myApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    @GetMapping("/my_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return myApp.doChat(message, chatId);
    }
    @GetMapping(value = "/my_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return myApp.doChatByStream(message, chatId);
    }
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        MyManus myManus = new MyManus(allTools, dashscopeChatModel);
        return myManus.runStream(message);
    }

}

