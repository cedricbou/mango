package com.emo.sajou.commands;

import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.MinLength;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayRock {

	@NotNull
	@MinLength(3)
	public final String compte;

	@Min(1)
	public final long montant;

	@Min(1)
	public final int validiteEnMois;
	
	@NotNull
	@MinSize(1)
	public final String[] services;

	@JsonCreator
	public DeposerPourUnUsage(final @JsonProperty("compte") String compte,
			final @JsonProperty("montant") long montant,
			final @JsonProperty("validiteEnMois") int validiteEnMois,
			final @JsonProperty("services") String[] services) {
		this.compte = compte;
		this.montant = montant;
		this.validiteEnMois = validiteEnMois;
		this.services = services;
	}
}
