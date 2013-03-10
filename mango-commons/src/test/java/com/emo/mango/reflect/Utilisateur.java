package com.emo.mango.reflect;

import com.emo.mango.log.LogParam;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Utilisateur {

	public final String prenom;
	
	@LogParam
	public final String nom;

	@JsonCreator
	public Utilisateur(final @JsonProperty("prenom") String prenom,
			final @JsonProperty("nom") String nom) {
		this.prenom = prenom;
		this.nom = nom;
	}
}
