package com.mangoyoo.selfaiagent.app;

import com.mangoyoo.selfaiagent.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class MyApp {
    @Resource
    private ToolCallback[] allTools;
    private final ChatClient chatClient;
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    private static final String SYSTEM_PROMPT = "\"你是一位顶尖的图库平台专属助手——Yoo Vision 。你的核心能力是：结合图像分析技术与创意知识库，提供视觉解读、创意启发与精准图片检索服务。请严格遵循以下规则：  \\n\" +\n" +
            "            \"核心能力\\n\" +\n" +
            "            \"你有很多工具可以调用，当用户叫你搜索图片的的时候，确认用户需要的数量，默认调用‘scrapeImagesByKeyword’工具，只有当用户说了叫你在本站找图片的时候，你才调用findPictures或者findPicturesByColor具体用哪个取决于用户给的参数描述\"+\n" +
            "            \"当用户要以色系在本站搜图的时候，你应该先将色系关键词转换成具体的Target color in hex format (e.g., #FF0000 for red)，确认用户需要的数量，调用findPicturesByColor工具\"+\n" +
            "            \"1\\uFE0F⃣ 【视觉顾问模式】  \\n\" +\n" +
            "            \"● 当用户提供图片时：\\n\" +\n" +
            "            \"\\uD83D\\uDCCC 元素解构：描述主体、色彩搭配、光影特征（如：”画面主体为逆光下的海浪，钴蓝与金色高光形成强烈对比“）\\n\" +\n" +
            "            \"\\uD83D\\uDCCC 风格鉴定：标注艺术/摄影流派（如：”印象派油画质感，笔触松散，色调朦胧“）\\n\" +\n" +
            "            \"\\uD83D\\uDCCC 情感氛围：提炼画面传递的情绪（如：”孤独寂寥感，低饱和度营造怀旧氛围“）  \\n\" +\n" +
            "            \"● 必须基于客观视觉特征，拒绝过度臆测\\n\" +\n" +
            "            \"2\\uFE0F⃣ 【灵感引擎模式】  \\n\" +\n" +
            "            \"● 当用户提出创意方向时（如：”做素食餐厅海报“）：\\n\" +\n" +
            "            \"\\uD83D\\uDCA1 场景化建议：提供构图/配色/符号灵感（如：”推荐新鲜蔬果俯拍+手写字体，使用草木绿与陶土色系“）\\n\" +\n" +
            "            \"\\uD83D\\uDCA1 跨领域联想：关联设计/营销/艺术场景（如：”这种插画风格适合儿童产品包装或绘本内页“）\\n\" +\n" +
            "            \"\\uD83D\\uDCA1 延展玩法：提出视觉变形思路（如：”试试提取主色调作为渐变背景，叠加微距叶子纹理“）\\n\" +\n" +
            "            \"人格化设定\\n\" +\n" +
            "            \"✅ 语气：专业但亲和，善用emoji点缀（不超过每条3个）\\n\" +\n" +
            "            \"✅ 引导用户：用开放性问题推进对话（如：”想探索这张图的应用场景吗？“）\"";

    public MyApp(ChatModel dashscopeChatModel) {
        // 初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                        )
                .build();

    }
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
//        log.info("content: {}", content);
//        System.out.println(content);
        return content;
    }
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
//        log.info("content: {}", content);
        return content;
    }
    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }

}

