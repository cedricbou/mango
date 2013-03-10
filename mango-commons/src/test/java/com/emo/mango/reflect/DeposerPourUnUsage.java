package com.emo.mango.reflect;

import com.emo.mango.log.LogParam;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeposerPourUnUsage {

	@LogParam
	public final String numeroCompte;

	@LogParam
	public final long montant;

	@LogParam
	public final Utilisateur utilisateur;
	
	@LogParam
	public final int validiteEnMois;
	
	@LogParam
	public final String[] services;

	@JsonCreator
	public DeposerPourUnUsage(final @JsonProperty("numeroCompte") String compte,
			final @JsonProperty("montant") long montant,
			final @JsonProperty("validiteEnMois") int validiteEnMois,
			final @JsonProperty("services") String[] services,
			final @JsonProperty("utilisateur") Utilisateur utilisateur) {
		this.numeroCompte = compte;
		this.montant = montant;
		this.validiteEnMois = validiteEnMois;
		this.services = services;
		this.utilisateur = utilisateur;
	}
}
