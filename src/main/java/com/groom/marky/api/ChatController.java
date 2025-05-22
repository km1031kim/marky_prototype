package com.groom.marky.api;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

	private final ChatClient chatClient;
	private final VectorStore vectorStore;

	@Autowired
	public ChatController(ChatClient chatClient, VectorStore vectorStore) {
		this.chatClient = chatClient;
		this.vectorStore = vectorStore;
	}



	@GetMapping("/ai")
	public String chat(@RequestParam String message) {
		return chatClient.prompt()
			.user(message)
			//.advisors(new QuestionAnswerAdvisor(vectorStore))
			.call()
			.content();
	}
}
