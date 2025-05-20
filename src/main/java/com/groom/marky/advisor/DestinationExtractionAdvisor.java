package com.groom.marky.advisor;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Description("사용자의 질문으로부터 출발지, 목적지를 추출하는 어드바이저")
public class DestinationExtractionAdvisor implements CallAdvisor {

	// 순환 의존성 방지. 필요할 때 꺼내쓴다.
	private final ChatModel chatModel;
	private final ObjectMapper objectMapper;
	private final static String DEPARTURE_KEY = "departure";
	private final static String DESTINATION_KEY = "destination";

	@Autowired
	public DestinationExtractionAdvisor(ChatModel chatModel, ObjectMapper objectMapper) {
		this.chatModel = chatModel;
		this.objectMapper = objectMapper;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

		String input = request.prompt().getUserMessages().stream()
			.map(UserMessage::getText)
			.reduce("", String::concat);

		// 목적지 키워드 추출
		// 목적지만 추출하도록 LLM에 요청
		Prompt prompt = new Prompt(List.of(
			new SystemMessage("""
				다음 문장에서 출발지와 목적지를 JSON 형식으로 추출해줘.
				형식 예시: { "%s": "강남역", "%s": "성수역" }
				반드시 JSON만 출력해줘.
				""".formatted(DEPARTURE_KEY, DESTINATION_KEY)
			),
			new UserMessage(input)
		));

		String json = chatModel.call(prompt).getResult().getOutput().getText();
		log.info("json : {}", json);


		try {
			Map<String, String> extracted = objectMapper.readValue(json, Map.class);

			String departure = extracted.get(DEPARTURE_KEY);
			String destination = extracted.get(DESTINATION_KEY);

			log.info("출발지 추출 : {}, 목적지 추출: {}" ,departure, destination);

			ChatClientRequest modified = ChatClientRequest.builder()
				.prompt(request.prompt())
				.context(request.context())
				.context(DEPARTURE_KEY, departure)
				.context(DESTINATION_KEY, destination)
				.build();

			return chain.nextCall(modified);

		} catch (Exception e) {
			log.warn("출발지/목적지 파싱 실패: {}", e.getMessage());
			return chain.nextCall(request);
		}
	}

	@Override
	public String getName() {
		return "DestinationExtractionAdvisor";
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
