package com.emo.sajou.commands;

import javax.inject.Inject;

import org.joda.time.Period;
import org.springframework.stereotype.Component;

import com.emo.mango.cqs.Handler;
import com.emo.mango.spring.cqs.annotations.CommandHandler;
import com.emo.sajou.application.services.DeposerSurCompte;
import com.emo.sajou.domain.commons.Service;
import com.emo.sajou.domain.commons.Usage;
import com.emo.sajou.domain.compte.NumeroCompte;


class PlaySomethingHandler implements Handler<PlayMove> {

	private @Inject Game game;
	
	@Override
	public void handle(PlayMove playMove) {
		game.playVsComputer(playMove.userMove())
	}
}
