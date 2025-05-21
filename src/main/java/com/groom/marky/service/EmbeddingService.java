package com.groom.marky.service;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

	private final EmbeddingModel embeddingModel;

	@Autowired
	public EmbeddingService(EmbeddingModel embeddingModel) {
		this.embeddingModel = embeddingModel;
	}

	public float[] embed(String message) {
		return embeddingModel.embed(message);
	}

	public List<float[]> embed(List<String> messages) {
		return embeddingModel.embed(messages);
	}

}
