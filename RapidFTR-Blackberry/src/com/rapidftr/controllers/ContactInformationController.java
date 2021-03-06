package com.rapidftr.controllers;

import com.rapidftr.controllers.internal.Controller;
import com.rapidftr.screens.ContactInformationScreen;
import com.rapidftr.screens.internal.UiStack;
import com.rapidftr.services.ContactInformationSyncService;

public class ContactInformationController extends Controller {
	private final ContactInformationSyncService service;

	public ContactInformationController(ContactInformationScreen screen,
			UiStack uiStack, ContactInformationSyncService service) {
		super(screen, uiStack);
		this.service = service;
		service.setScreenCallback(screen);
	}

	public void fetchContactInformation() {
		service.downloadContactInformation();
	}

}
