package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SageConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SageConfig.class);
	private static final String PROPERTIES_FILE = "sage.properties";

	public static boolean ENABLE_SAGE_INTEGRATION = true;
	public static String SUPPLIER_GENERAL_ACCOUNT = "40100000";
	public static String CUSTOMER_GENERAL_ACCOUNT = "411";
	public static String CASH_ACCOUNT = "571110";
	public static String INCOME_ACCOUNT = "706102";
	public static String EXPENSE_ACCOUNT = "601100";
	public static String JOURNAL_BUY_CODE = "ACH";
	public static String JOURNAL_PAID_CODE = "VTE";
	public static String JOURNAL_CASHDESK_CODE = "CASH3";
	public static String FILE_PAID_NAME = "exportvente";
	public static String FILE_CASHDESK_NAME = "exportcaisse";
	public static String PAID_PREFIX = "V";
	public static String CASHDESK_PREFIX = "C";
	public static String MEDICALSALESACCOUNT = "700011";
	public static String OTHERSALESACCOUNT = "700002";
	public static String EXAMSALESACCOUNT = "700003";
	public static String OPERATIONSALESACCOUNT = "700004";
	public static String MEDICALEXPENSEACCOUNT = "600001";
	public static String OTHEREXPENSEACCOUNT = "600002";
	public static String EXAMEXPENSEACCOUNT = "600003";
	public static String OPERATIONEXPENSEACCOUNT = "600004";

	static {
		try {
			Properties p = new Properties();
			File file = new File("rsc/" + PROPERTIES_FILE);
			if (!file.exists()) {
				file = new File(PROPERTIES_FILE);
			}
			if (!file.exists()) {
				file = new File("src/main/resources/" + PROPERTIES_FILE);
			}

			if (file.exists()) {
				p.load(new FileInputStream(file));

				ENABLE_SAGE_INTEGRATION = "yes".equalsIgnoreCase(p.getProperty("ENABLE_SAGE_INTEGRATION"));
				SUPPLIER_GENERAL_ACCOUNT = p.getProperty("SUPPLIER_GENERAL_ACCOUNT", SUPPLIER_GENERAL_ACCOUNT);
				CUSTOMER_GENERAL_ACCOUNT = p.getProperty("CUSTOMER_GENERAL_ACCOUNT", CUSTOMER_GENERAL_ACCOUNT);
				CASH_ACCOUNT = p.getProperty("CASH_ACCOUNT", CASH_ACCOUNT);
				INCOME_ACCOUNT = p.getProperty("INCOME_ACCOUNT", INCOME_ACCOUNT);
				EXPENSE_ACCOUNT = p.getProperty("EXPENSE_ACCOUNT", EXPENSE_ACCOUNT);
				JOURNAL_BUY_CODE = p.getProperty("JOURNAL_BUY_CODE", JOURNAL_BUY_CODE);
				JOURNAL_PAID_CODE = p.getProperty("JOURNAL_PAID_CODE", JOURNAL_PAID_CODE);
				JOURNAL_CASHDESK_CODE = p.getProperty("JOURNAL_CASHDESK_CODE", JOURNAL_CASHDESK_CODE);
				FILE_PAID_NAME = p.getProperty("FILE_PAID_NAME", FILE_PAID_NAME);
				FILE_CASHDESK_NAME = p.getProperty("FILE_CASHDESK_NAME", FILE_CASHDESK_NAME);
				PAID_PREFIX = p.getProperty("PAID_PREFIX", PAID_PREFIX);
				CASHDESK_PREFIX = p.getProperty("CASHDESK_PREFIX", CASHDESK_PREFIX);
				MEDICALSALESACCOUNT = p.getProperty("MEDICALSALESACCOUNT", MEDICALSALESACCOUNT);
				OTHERSALESACCOUNT = p.getProperty("OTHERSALESACCOUNT", OTHERSALESACCOUNT);
				EXAMSALESACCOUNT = p.getProperty("EXAMSALESACCOUNT", EXAMSALESACCOUNT);
				OPERATIONSALESACCOUNT = p.getProperty("OPERATIONSALESACCOUNT", OPERATIONSALESACCOUNT);
				MEDICALEXPENSEACCOUNT = p.getProperty("MEDICALEXPENSEACCOUNT", MEDICALEXPENSEACCOUNT);
				OTHEREXPENSEACCOUNT = p.getProperty("OTHEREXPENSEACCOUNT", OTHEREXPENSEACCOUNT);
				EXAMEXPENSEACCOUNT = p.getProperty("EXAMEXPENSEACCOUNT", EXAMEXPENSEACCOUNT);
				OPERATIONEXPENSEACCOUNT = p.getProperty("OPERATIONEXPENSEACCOUNT", OPERATIONEXPENSEACCOUNT);

				LOGGER.info("Sage configuration loaded successfully");
			} else {
				LOGGER.info("File not found: " + PROPERTIES_FILE + ", using default values");
			}
		} catch (FileNotFoundException e) {
			LOGGER.info("File not found: " + PROPERTIES_FILE);
		} catch (IOException e) {
			LOGGER.error(">> Exception reading: " + PROPERTIES_FILE, e);
		}
	}
}