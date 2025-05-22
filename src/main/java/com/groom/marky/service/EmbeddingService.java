package com.groom.marky.service;

import static com.groom.marky.domain.response.GooglePlacesApiResponse.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.groom.marky.domain.response.GooglePlacesApiResponse;
import com.groom.marky.domain.response.PlaceDescriptionBuilder;

@Service
public class EmbeddingService {

	private final VectorStore vectorStore;

	@Autowired
	public EmbeddingService(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	public void saveEmbeddings(GooglePlacesApiResponse apiResponse) {

		List<Place> places = apiResponse.places();


		// UUID 비교, 업데이트..
		List<Document> documents = places.stream()
			.map(place -> {
				String description = PlaceDescriptionBuilder.buildDescription(place);
				String id = UUID.nameUUIDFromBytes(place.id().getBytes(StandardCharsets.UTF_8)).toString();
				return new Document(
					id,
					description,
					Map.of(
						"original", description,
						"lat", place.location().latitude(),
						"lon", place.location().longitude(),
						"displayName", place.displayName().text(),
						"formattedAddress", place.formattedAddress(),
						"userRatingCount", place.userRatingCount(),
						"rating", place.rating()
					));
			}).toList();

		vectorStore.add(documents);

	}

}
