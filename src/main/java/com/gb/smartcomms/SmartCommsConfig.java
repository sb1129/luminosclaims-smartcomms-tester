package com.gb.smartcomms;

/**
 * Class to store the name of parameters for Smart Comms
 * 
 * @author STHOTAKURA
 *
 */
public final class SmartCommsConfig {

	/** SmartComms connection properties. */
	public static final String SMARTCOMMS_APPLIANCE_URL = "SmartCommsApplianceURL";
	public static final String SMARTCOMMS_AUTH_URL = "SmartCommsAuthURL";
	public static final String SMARTCOMMS_SERVER_URL = "SmartCommsServerURL";
	public static final String SC_OAUTH_CUSTOMER_KEY = "SmartCommsOAuthConsumerKey";
	public static final String SC_OAUTH_CUSTOMER_SECRET = "SmartCommsOAuthConsumerSecret";
	public static final String SC_OAUTH_USERID = "SmartCommsOAuthUserID";
	public static final String SC_PROJECT_ID = "SmartCommsProjectID";
	public static final String SC_SUBMIT_QUEUE = "SmartCommsSubmitQueue";
	public static final String SC_GENERIC_SENDER_EMAIL = "SmartCommsGenericSenderEmail";

	/** Configuration property to switch off smart comms integration. */
	public static final String ENABLE_SMARTCOMMS_INTEGRATION = "EnableSmartCommsIntegration";

	/** Draft Data TaSession Parameter. */
	public static final String DRAFT_DATA_PARAMETER = "DraftDataParameter";

	/** Document Draft Submit Job Status TaSession Parameter. */
	public static final String SUBMIT_JOB_STATUS_PARAMETER = "SubmitJobStatusParameter";

	/** Configuration category for SmartCommunications integration. */
	public static final String SMARTCOMMS_CONFIGURATION_CATEGORY = "SmartCommsIntegration";

	/** Parameter used to pass email response body. */
	public static final String EMAIL_RESPONSE_BODY_PARAM = "EmailResponseBody";

	/** Parameter used to pass email response title. */
	public static final String EMAIL_RESPONSE_TITLE_PARAM = "EmailResponseTitle";

	/** Parameter storing content of document draft. */
	public static final String DOCUMENT_DRAFT_CONTENT = "DRAFT_CONTENT";

	/** Parameter storing content of document draft. */
	public static final String EMAIL_SUBJECT_PARAMETER = "EMAIL_SUBJECT";

	/**
	 * Parameter storing Add Email Action name to be auto selected on Attach
	 * Document Page drop down.
	 */
	public static final String EMAIL_ACTION_NAME_PARAMETER = "EMAIL_ACTION_NAME";

	/** Configuration category for Document Templates. */
	public static final String DOCUMENT_TEMPLATES_CONFIGURATION_CATEGORY = "DocumentTemplates";

	/**
	 * Class for Fixed Numbers.
	 */
	public final class Numbers {
		public static final int MINUS_ONE = -1;
		public static final int SIX = 6;
		public static final int TEN = 10;
		public static final int THIRTY = 30;
		public static final int HUNDRED = 100;
		public static final int TWO_HUNDRED = 200;
		public static final int TWO_HUNDRED_AND_ONE = 201; // Http Response 201 = Created status
		public static final int THOUSAND = 1000;

		public static final int FOUR_KILO = 4096;

		/**
		 * private constructor.
		 */
		private Numbers() {
		}
	}

}
