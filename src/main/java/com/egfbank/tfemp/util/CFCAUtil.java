package com.egfbank.tfemp.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import scala.tools.jline_embedded.internal.Log;
import cfca.safeguard.api.bank.ClientEnvironment;
import cfca.safeguard.api.bank.Constants;
import cfca.safeguard.api.bank.SGBusiness;
import cfca.safeguard.api.bank.SGRollBusiness;
import cfca.safeguard.api.bank.bean.VerifyResult;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100102;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100104;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100106;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100202;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100204;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100206;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100302;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100304;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100306;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100308;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100310;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100401;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100403;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100404;
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100405;
import cfca.safeguard.api.bank.util.Base64;
import cfca.safeguard.Result;
import cfca.safeguard.api.bank.util.ResultUtil;
import cfca.safeguard.api.bank.util.StringUtil;
import cfca.safeguard.tx.TxBase;

/**
 * Communicate with Front_end
 * 
 * @author andy
 */

public class CFCAUtil {
	static {
		try {
			ClientEnvironment.initTxClientEnvironment("cfca");
			ClientEnvironment.initRollClientEnvironment("cfca");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Vector<TxBase> Tx000000() throws Exception {
		SGBusiness sgBusiness = new SGBusiness();
		String responseXml = sgBusiness.getMessage();

		List<String> messageList = null;
		String code = StringUtil.getNodeText(responseXml, Constants.CODE);
		if (Constants.SUCCESS_CODE_VALUE.equals(code)) {
			messageList = StringUtil.getNodeTextList(responseXml,
					Constants.MESSAGE);
		} else {
			throw new Exception(
					"Get message error! Error code: "
							+ code
							+ ". Error descriptin: "
							+ StringUtil.getNodeText(responseXml,
									Constants.DESCRIPTION));
		}
		if (messageList == null || messageList.size() == 0) {
			Log.info("Front_End do not hava message to handle ");
			return null;
		}
		Log.info("Get message from Front_End, size=" + messageList.size());
		Vector<TxBase> txList = new Vector<TxBase>();
		if (messageList == null || messageList.size() > 0) {
			for (int i = 0; i < messageList.size(); i++) {
				String xml = Base64.decode(messageList.get(i),
						Constants.DEFAULT_CHARSET);
				TxBase tx = ResultUtil.convertTxFromMessageXML(xml);
				txList.add(tx);
			}
		}
		return txList;
	}

	public static Vector<String> Tx000000Xml() throws Exception {
		SGBusiness sgBusiness = new SGBusiness();
		String responseXml = sgBusiness.getMessage();

		List<String> messageList = null;
		String code = StringUtil.getNodeText(responseXml, Constants.CODE);
		if (Constants.SUCCESS_CODE_VALUE.equals(code)) {
			messageList = StringUtil.getNodeTextList(responseXml,
					Constants.MESSAGE);
		} else {
			throw new Exception(
					"Get message error! Error code: "
							+ code
							+ ". Error descriptin: "
							+ StringUtil.getNodeText(responseXml,
									Constants.DESCRIPTION));
		}
		if (messageList == null || messageList.size() == 0) {
			Log.info("前置机没有收到需要处理的业务消息 ");
			return new Vector<String>();
		}
		Log.info("前置机收到需要处理的业务消息数量=" + messageList.size());
		Vector<String> txList = new Vector<String>();
		if (messageList == null || messageList.size() > 0) {
			for (int i = 0; i < messageList.size(); i++) {
				String xml = Base64.decode(messageList.get(i),
						Constants.DEFAULT_CHARSET);
				// TxBase tx = ResultUtil.convertTxFromMessageXML(xml);
				txList.add(xml);
			}
		}
		return txList;
	}

	public static Map<String, String> Tx100102(Tx100102 tx100102, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100102(tx100102, to);
			Log.info("Tx100102:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100102Xml(Tx100102 tx100102, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100102(tx100102, to);
			insertIntoUpQueue(tx100102.getApplicationID(), requestXML);
			Log.info("Tx100102:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100102.getApplicationID());
	}

	public static Map<String, String> Tx100104(Tx100104 tx100104, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100104(tx100104, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100104Xml(Tx100104 tx100104, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100104(tx100104, to);
			insertIntoUpQueue(tx100104.getApplicationID(), requestXML);
			Log.info("Tx100104:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100104.getApplicationID());
	}

	public static Map<String, String> Tx100106(Tx100106 tx100106, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100106(tx100106, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100106Xml(Tx100106 tx100106, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100106(tx100106, to);
			insertIntoUpQueue(tx100106.getApplicationID(), requestXML);
			Log.info("Tx100106:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100106.getApplicationID());
	}

	public static Map<String, String> Tx100202(Tx100202 tx100202, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100202(tx100202, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100202Xml(Tx100202 tx100202, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100202(tx100202, to);
			insertIntoUpQueue(tx100202.getApplicationID(), requestXML);
			Log.info("Tx100202:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100202.getApplicationID());
	}

	public static Map<String, String> Tx100204(Tx100204 tx100204, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100204(tx100204, to);
			Log.info("tx100204:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100204Xml(Tx100204 tx100204, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100204(tx100204, to);
			insertIntoUpQueue(tx100204.getApplicationID(), requestXML);
			Log.info("tx100204:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100204.getApplicationID());
	}

	public static Map<String, String> Tx100206(Tx100206 tx100206, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100206(tx100206, to);
			Log.info("Tx100206:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100206Xml(Tx100206 tx100206, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100206(tx100206, to);
			insertIntoUpQueue(tx100206.getApplicationID(), requestXML);
			Log.info("Tx100206:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100206.getApplicationID());
	}

	public static Map<String, String> Tx100302(Tx100302 tx100302, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100302(tx100302, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100302Xml(Tx100302 tx100302, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100302(tx100302, to);
			insertIntoUpQueue(tx100302.getApplicationID(), requestXML);
			Log.info("Tx100302:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100302.getApplicationID());
	}

	public static Map<String, String> Tx100304(Tx100304 tx100304, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100304(tx100304, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100304Xml(Tx100304 tx100304, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100304(tx100304, to);
			insertIntoUpQueue(tx100304.getApplicationID(), requestXML);
			Log.info("Tx100304:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100304.getApplicationID());
	}

	public static Map<String, String> Tx100306(Tx100306 tx100306, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100306(tx100306, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100306Xml(Tx100306 tx100306, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100306(tx100306, to);
			insertIntoUpQueue(tx100306.getApplicationID(), requestXML);
			Log.info("Tx100306:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100306.getApplicationID());
	}

	public static Map<String, String> Tx100308(Tx100308 tx100308, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100308(tx100308, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100308Xml(Tx100308 tx100308, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100308(tx100308, to);
			insertIntoUpQueue(tx100308.getApplicationID(), requestXML);
			Log.info("Tx100308:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100308.getApplicationID());
	}

	public static Map<String, String> Tx100310(Tx100310 tx100310, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100310(tx100310, to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100310Xml(Tx100310 tx100310, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100310(tx100310, to);
			insertIntoUpQueue(tx100310.getApplicationID(), requestXML);
			Log.info("Tx100310:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.info("tx100310:" + requestXML);
		return getResponseXml(sgBusiness, requestXML,
				tx100310.getApplicationID());
	}

	public static Map<String, String> Tx100401(Tx100401 tx100401,
			String fromTGOrganizationId, String mode, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100401(tx100401, fromTGOrganizationId,
					mode, to);
			insertIntoUpQueue(tx100401.getApplicationID(), requestXML);
			Log.info("Tx100401:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100401Xml(Tx100401 tx100401,
			String fromTGOrganizationId, String mode, String to) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100401(tx100401, fromTGOrganizationId,
					mode, to);
			insertIntoUpQueue(tx100401.getApplicationID(), requestXML);
			Log.info("Tx100401:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100401.getApplicationID());
	}

	public static Map<String, String> Tx100403(Tx100403 tx100403,
			String fromTGOrganizationId) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100403(tx100403, fromTGOrganizationId);
			insertIntoUpQueue(tx100403.getApplicationID(), requestXML);
			Log.info("Tx100403:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100403Xml(Tx100403 tx100403,
			String fromTGOrganizationId) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100403(tx100403, fromTGOrganizationId);
			insertIntoUpQueue(tx100403.getApplicationID(), requestXML);
			Log.info("Tx100403:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100403.getApplicationID());
	}

	public static Map<String, String> Tx100404(Tx100404 tx100404,
			String fromTGOrganizationId) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100404(tx100404, fromTGOrganizationId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100404Xml(Tx100404 tx100404,
			String fromTGOrganizationId) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100404(tx100404, fromTGOrganizationId);
			insertIntoUpQueue(tx100404.getApplicationID(), requestXML);
			Log.info("Tx100404:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100404.getApplicationID());
	}

	public static Map<String, String> Tx100405(Tx100405 tx100405,
			String fromTGOrganizationId) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100405(tx100405, fromTGOrganizationId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse(sgBusiness, requestXML);
	}

	public static String Tx100405Xml(Tx100405 tx100405,
			String fromTGOrganizationId) {
		SGBusiness sgBusiness = new SGBusiness();
		String requestXML = "";
		try {
			requestXML = sgBusiness.tx100405(tx100405, fromTGOrganizationId);
			insertIntoUpQueue(tx100405.getApplicationID(), requestXML);
			Log.info("Tx100405:" + requestXML);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponseXml(sgBusiness, requestXML,
				tx100405.getApplicationID());
	}

	public static String Tx100501Xml(String listSource, String dataType,
			String organizationID, String data, String accountName) {

		SGRollBusiness sgRollBusiness = new SGRollBusiness();
		try {
			return sgRollBusiness.tx100501(listSource, dataType,
					organizationID, data, accountName);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_MSG;
		}
	}

	public static String Tx100503Xml(String listSource1, String dataType1,
			String organizationID1, String data1, String accountName1,
			String listSource2, String dataType2, String organizationID2,
			String data2, String accountName2) {

		SGRollBusiness sgRollBusiness = new SGRollBusiness();
		try {
			return sgRollBusiness.tx100503(listSource1, dataType1,
					organizationID1, data1, accountName1, listSource2,
					dataType2, organizationID2, data2, accountName2);
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_MSG;
		}
	}

	private static Map<String, String> getResponse(SGBusiness sgBusiness,
			String requestXML) {
		Map<String, String> beanMap = null;
		try {
			String responseXML = sgBusiness.sendPackagedRequestXML(requestXML);
			Result result = ResultUtil.chageXMLToResult(responseXML);
			beanMap = BeanUtils.describe(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		beanMap.remove("class");
		return beanMap;
	}

	public static String getResponseXml(SGBusiness sgBusiness,
			String requestXML, String id) {
		try {
			String response = sgBusiness.sendPackagedRequestXML(requestXML);
			String status = getStatus(response);
			// insert the response into the cfca_up table
			updateUpQueue(id, response, status);
			// update the status
			if (status.equals(CFCA_RESP_SUCC))
				updateDownQueue(id, CFCA_TASK_SUCC);
			else
				updateDownQueue(id, CFCA_TASK_ERROR);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getResponseXml(String requestXML, String id) {
		try {
			SGBusiness sgBusiness = new SGBusiness();
			String response = sgBusiness.sendPackagedRequestXML(requestXML);
			String status = getStatus(response);

			// insert the response into the cfca_up table
			if (status.equals(CFCA_RESP_SUCC))
				updateUpQueue(id, response, status);

			// update the status
			if (status.equals(CFCA_RESP_SUCC))
				updateDownQueue(id, CFCA_TASK_SUCC);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param id
	 * @param xmlcontent
	 * @return
	 */
	private static int insertIntoUpQueue(String id, String xmlcontent) {
		String sql = "update tfemp_cfca_up set xmlcontent = ? where APPLICATIONID = ?";
		Vector<String> data = new Vector<String>();
		data.add(xmlcontent);
		data.add(id);
		OracleServiceImp op = new OracleServiceImp();
		return op.executeUpdate(sql, data);
	}

	/**
	 * @param id
	 * @param response
	 * @param status
	 * @return
	 */
	public static int updateUpQueue(String id, String response, String status) {
		String sql = "update tfemp_cfca_up set response = ?,status = ?,respTime=? where APPLICATIONID = ?";
		Vector<String> data = new Vector<String>();
		data.add(response);
		data.add(status);
		data.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		data.add(id);
		OracleServiceImp op = new OracleServiceImp();
		return op.executeUpdate(sql, data);
	}

	private static int updateDownQueue(String id, String status) {
		String sql = "update tfemp_cfca_down set status = ? where APPLICATIONID = ?";
		Vector<String> data = new Vector<String>();
		data.add(status);
		data.add(id);
		OracleServiceImp op = new OracleServiceImp();
		return op.executeUpdate(sql, data);
	}

	private static String ERROR_MSG = "<error>error</error>";

	/**
	 * @param response
	 * @return
	 */
	private static String getStatus(String response) {
		int indexS = response.indexOf("<Code>");
		int indexE = response.indexOf("</Code>");
		String code = response.substring(indexS + 6, indexE);
		if (CFCA_GET_FEEDBACK.equals(code))
			return CFCA_RESP_SUCC;
		else
			return CFCA_RESP_ERROR;
	}

	/**
	 * Front_End Receive Message Successful-Code in the XML
	 */
	private static String CFCA_GET_FEEDBACK = "100000";
	/**
	 * Front_End Receive Message Successful
	 */
	private static String CFCA_RESP_SUCC = "1";
	/**
	 * Front_End Receive Message Fail
	 */
	private static String CFCA_RESP_ERROR = "2";
	/**
	 * CFCA task execute successful
	 */
	private static String CFCA_TASK_SUCC = "4";
	/**
	 * CFCA task execute fail,because Front_End receive message fail
	 */
	private static String CFCA_TASK_ERROR = "7";
}
