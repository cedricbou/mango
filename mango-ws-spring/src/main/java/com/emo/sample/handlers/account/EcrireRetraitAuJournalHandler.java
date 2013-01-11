package com.emo.sample.handlers.account;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.emo.mango.cqs.Handler;
import com.emo.sample.commands.account.EcrireRetraitAuJournal;
import com.emo.sample.domain.account.Journal;
import com.emo.sample.domain.account.JournalRepository;
import com.emo.skeleton.annotations.CommandHandler;

@CommandHandler(EcrireRetraitAuJournal.class)
@Component
public class EcrireRetraitAuJournalHandler implements Handler<EcrireRetraitAuJournal>{

	@Inject
	private JournalRepository repo;
	
	@Transactional
	public void handle(EcrireRetraitAuJournal command) {
		final Journal journal = repo.findByForAccountName(command.getForAccountName());
		Assert.notNull(journal);
		
		journal.ecrireRetrait(command.getSomme(), command.getSolde());
	};
}
