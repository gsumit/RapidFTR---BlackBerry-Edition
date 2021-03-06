package com.rapidftr;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.ui.UiApplication;

import com.rapidftr.controllers.ViewChildController;
import com.rapidftr.controllers.ChildHistoryController;
import com.rapidftr.controllers.ContactInformationController;
import com.rapidftr.controllers.HomeController;
import com.rapidftr.controllers.LoginController;
import com.rapidftr.controllers.ManageChildController;
import com.rapidftr.controllers.ResetDeviceController;
import com.rapidftr.controllers.SearchChildController;
import com.rapidftr.controllers.SyncController;
import com.rapidftr.controllers.ViewChildPhotoController;
import com.rapidftr.controllers.ViewChildrenController;
import com.rapidftr.controllers.internal.Dispatcher;
import com.rapidftr.datastore.ChildrenRecordStore;
import com.rapidftr.datastore.FormStore;
import com.rapidftr.net.HttpServer;
import com.rapidftr.net.HttpService;
import com.rapidftr.screens.ChildHistoryScreen;
import com.rapidftr.screens.ChildPhotoScreen;
import com.rapidftr.screens.ContactInformation;
import com.rapidftr.screens.ContactInformationScreen;
import com.rapidftr.screens.HomeScreen;
import com.rapidftr.screens.LoginScreen;
import com.rapidftr.screens.ManageChildScreen;
import com.rapidftr.screens.SearchChildScreen;
import com.rapidftr.screens.SyncScreen;
import com.rapidftr.screens.ViewChildScreen;
import com.rapidftr.screens.ViewChildrenScreen;
import com.rapidftr.screens.internal.UiStack;
import com.rapidftr.services.ChildSyncService;
import com.rapidftr.services.ContactInformationSyncService;
import com.rapidftr.services.FormService;
import com.rapidftr.services.LoginService;
import com.rapidftr.services.LoginSettings;
import com.rapidftr.utilities.DefaultStore;
import com.rapidftr.utilities.HttpSettings;
import com.rapidftr.utilities.Settings;

public class Main extends UiApplication {

	public static void main(String[] args) {
		Main application = new Main();
		application.enterEventDispatcher();
	}

	public Main() {

		enablePermission(ApplicationPermissions.PERMISSION_INPUT_SIMULATION);
		enablePermission(ApplicationPermissions.PERMISSION_FILE_API);

		DefaultStore defaultStore = new DefaultStore(new Key(
				"com.rapidftr.utilities.ftrstore"));

		ChildrenRecordStore childrenStore = new ChildrenRecordStore(
				new DefaultStore(
						new Key("com.rapidftr.utilities.childrenstore")));

		FormStore formStore = new FormStore();

		Settings settings = new Settings(defaultStore);

		HttpServer httpServer = new HttpServer(new HttpSettings(settings));

		HttpService httpService = new HttpService(httpServer, settings);

		LoginService loginService = new LoginService(httpService,
				new LoginSettings(settings));

		FormService formService = new FormService(httpService, formStore);

		ChildSyncService childSyncService = new ChildSyncService(httpService,
				childrenStore);

		UiStack uiStack = new UiStack(this);

		HomeScreen homeScreen = new HomeScreen(settings);

		ContactInformationScreen contactScreen = new ContactInformationScreen(
				new ContactInformation(defaultStore));

		LoginScreen loginScreen = new LoginScreen(new HttpSettings(settings));

		ViewChildScreen viewChildScreen = new ViewChildScreen();

		ViewChildrenScreen viewChildrenScreen = new ViewChildrenScreen();

		SearchChildScreen searchChildScreen = new SearchChildScreen();

		ManageChildScreen newChildScreen = new ManageChildScreen(settings);

		SyncScreen syncScreen = new SyncScreen(settings);

		LoginController loginController = new LoginController(loginScreen,
				uiStack, loginService);

		ResetDeviceController restController = new ResetDeviceController(
				formService, childSyncService, loginService);

		HomeController homeScreenController = new HomeController(
				homeScreen, uiStack);

		ContactInformationController contactScreenController = new ContactInformationController(
				contactScreen, uiStack, new ContactInformationSyncService(
						httpService, new ContactInformation(defaultStore)));

		ChildPhotoScreen childPhotoScreen = new ChildPhotoScreen();

		ChildHistoryScreen childHistoryScreen = new ChildHistoryScreen();

		ViewChildController childController = new ViewChildController(viewChildScreen,
				uiStack, formStore);

		SyncController syncController = new SyncController(syncScreen, uiStack,
				childSyncService, formService);

		Dispatcher dispatcher = new Dispatcher(homeScreenController,
				loginController, childController, syncController,
				restController, contactScreenController,
				new ManageChildController(newChildScreen, uiStack, formStore,
						childrenStore), new ViewChildrenController(
						viewChildrenScreen, uiStack, childrenStore),
				new ViewChildPhotoController(childPhotoScreen, uiStack),
				new ChildHistoryController(childHistoryScreen, uiStack),
				new SearchChildController(searchChildScreen, uiStack,
						childrenStore));

		dispatcher.homeScreen();

	}

	private void enablePermission(int permission) {
		ApplicationPermissionsManager manager = ApplicationPermissionsManager
				.getInstance();

		if (manager.getApplicationPermissions().getPermission(permission) == ApplicationPermissions.VALUE_ALLOW) {
			System.out.println("Got correct permissions");
			return;
		}

		ApplicationPermissions requestedPermissions = new ApplicationPermissions();

		if (!requestedPermissions.containsPermissionKey(permission)) {
			requestedPermissions.addPermission(permission);
		}

		ApplicationPermissionsManager.getInstance().invokePermissionsRequest(
				requestedPermissions);
	}
}
