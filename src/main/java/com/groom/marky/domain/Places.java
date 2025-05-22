package com.groom.marky.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Places {

	private Long id;

	private String googlePlaceId;
	private String summary; // 자연어 설명

	private float[] embedding; // pgvector 컬럼

}
