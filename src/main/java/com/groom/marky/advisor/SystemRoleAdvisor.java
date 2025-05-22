package com.groom.marky.advisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

public class SystemRoleAdvisor implements CallAdvisor {

	private static final String SYSTEM_MESSAGE = "넌 ai 마키(Marky)야. 너는 사용자 질문에 대해 친절하게 답변해야해. ";


	@Override
	public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

		// 1. 시스템 메세지 생성
		SystemMessage newSystemMessage = new SystemMessage(SYSTEM_MESSAGE);

		// 2. 기존 유저 메세지 가져오기
		List<UserMessage> userMessages = request.prompt().getUserMessages();

		// 3. 병합 목적의 리스트 생성
		ArrayList<Message> allMessages = new ArrayList<>();

		// 4. 기존의 시스템 메세지 확인. 존재하는 경우 시스템 메세지를 유저메세지로 바꿔서 저장
		SystemMessage oldSystemMessage = request.prompt().getSystemMessage();

		Optional.ofNullable(oldSystemMessage)
			.filter(msg -> !SYSTEM_MESSAGE.equals(msg.getText()))
			.ifPresent(msg -> allMessages.add(new UserMessage(msg.getText())));

		// 5. 시스템 메세지와 유저메세지 병합
		allMessages.add(newSystemMessage);
		allMessages.addAll(userMessages);

		// 6. 병합된 메세지로 새로운 프롬프트 생성
		Prompt newPrompt = new Prompt(allMessages);

		ChatClientRequest modifiedRequest = ChatClientRequest.builder()
			.prompt(newPrompt)
			.context(request.context()) // 기존 context 유지 : 지금 내가 처리한 중간 결과, 이전 상태, 외부 조회 결과
			.build();

		// 다음 체인 호출
		return chain.nextCall(modifiedRequest);
	}

	@Override
	public String getName() {
		return "SystemRoleAdvisor";
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
