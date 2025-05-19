package com.mangoyoo.selfaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// 取消注释即可在 SpringBoot 项目启动时执行
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;
    @Resource
    record ActorFilms(String actor, List<String> movies) {}
    @Override
    public void run(String... args) throws Exception {
//        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是mangoyoo"))
//                .getResult()
//                .getOutput();
//        System.out.println(output.getText());
        // 高级用法(ChatClient)
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
                .build();

//        String response = chatClient.prompt().user("你好").call().content();
//        System.out.println(response);
//        ChatResponse chatResponse = chatClient.prompt()
//                .user("Tell me a joke")
//                .call()
//                .chatResponse();
//        System.out.println(chatResponse.getResult().getOutput().getText());
        String voice ="cat";
        String result = chatClient.prompt()
                .system(sp -> sp.param("voice", voice))
                .user("Generate the filmography for a random actor.")
                .call()
                .content();
        System.out.println(result);
//        var chatClient2 = ChatClient.builder(chatModel)
//                .defaultAdvisors(
//                        new MessageChatMemoryAdvisor(chatMemory), // 对话记忆 advisor
//                        new QuestionAnswerAdvisor(vectorStore)    // RAG 检索增强 advisor
//                )
//                .build();

    }
}
