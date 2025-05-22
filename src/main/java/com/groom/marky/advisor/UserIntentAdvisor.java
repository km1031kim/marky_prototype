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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Description("사용자의 메시지에서 intent, location, mood를 추출하는 어드바이저")
public class UserIntentAdvisor implements CallAdvisor {

	private final ChatModel chatModel;
	private final ObjectMapper objectMapper;

	private static final String INTENT_KEY = "intent";
	private static final String LOCATION_KEY = "location";
	private static final String MOOD_KEY = "mood";

	@Autowired
	public UserIntentAdvisor(ChatModel chatModel, ObjectMapper objectMapper) {
		this.chatModel = chatModel;
		this.objectMapper = objectMapper;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
		String input = request.prompt().getUserMessages().stream()
			.map(UserMessage::getText)
			.reduce("", String::concat);

		// 프롬프트 상세히 쓰기..
		// 지피티한테 프롬프트 짜달라하기
		Prompt prompt = new Prompt(List.of(
			new SystemMessage("""
				다음 사용자 입력에서 intent, location, mood를 JSON 형식으로 추출해줘.
				- intent는 사용자의 목적 (예: 주차장, 카페, 식당, 놀거리 등)
				- location은 장소명 (예: 연남동, 홍대)
				- mood는 분위기나 선호 표현 (예: 조용한, 활기찬 등)
				- 모든 키는 반드시 포함되어야 하며, 해당 정보가 없으면 null을 설정해줘.

				출력 예시:
				{ "intent": "카페", "location": "연남동", "mood": "조용한" }

				주의: 반드시 JSON만 출력해.
				"""),
			new UserMessage(input)
		));

		String json = chatModel.call(prompt).getResult().getOutput().getText();
		log.info("LLM 응답 JSON: {}", json);

		if (!json.trim().startsWith("{")) {
			log.warn("LLM 응답이 JSON이 아님: {}", json);
			return chain.nextCall(request);
		}

		try {
			Map<String, String> extracted = objectMapper.readValue(json, new TypeReference<>() {});
			String intent = extracted.get(INTENT_KEY);
			String location = extracted.get(LOCATION_KEY);
			String mood = extracted.get(MOOD_KEY);

			log.info("의도: {}, 장소: {}, 분위기: {}", intent, location, mood);

			ChatClientRequest modified = ChatClientRequest.builder()
				.prompt(request.prompt())
				.context(request.context())
				.context(INTENT_KEY, intent)
				.context(LOCATION_KEY, location)
				.context(MOOD_KEY, mood)
				.build();

			return chain.nextCall(modified);

		} catch (Exception e) {
			log.warn("JSON 파싱 실패: {}\nLLM 응답: {}", e.getMessage(), json, e);
			return chain.nextCall(request);
		}
	}

	@Override
	public String getName() {
		return "UserIntentAdvisor";
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
