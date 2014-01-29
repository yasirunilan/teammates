package teammates.logic.automated;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.Emails;
import teammates.logic.core.EvaluationsLogic;

public class EvaluationClosingMailAction extends EmailAction {

	private String evaluationName;
	private String courseId;
	
	public EvaluationClosingMailAction(HttpServletRequest request) {
		super(request);
		initializeNameAndDescription();
		
		evaluationName = HttpRequestHelper
				.getValueFromRequestParameterMap(request, ParamsNames.EMAIL_EVAL);
		Assumption.assertNotNull(evaluationName);
		
		courseId = HttpRequestHelper
				.getValueFromRequestParameterMap(request, ParamsNames.EMAIL_COURSE);
		Assumption.assertNotNull(courseId);
	}
	
	public EvaluationClosingMailAction(HashMap<String, String> paramMap) {
		super(paramMap);
		initializeNameAndDescription();
		
		evaluationName = paramMap.get(ParamsNames.EMAIL_EVAL);
		Assumption.assertNotNull(evaluationName);
		
		courseId = paramMap.get(ParamsNames.EMAIL_COURSE);
		Assumption.assertNotNull(courseId);
	}
	
	@Override
	protected void doPostProcessingForSuccesfulSend() {
		/* 
		 * Empty because no action is required on successful
		 * sending of evaluation closing mails
		 */
	}

	@Override
	protected List<MimeMessage> prepareMailToBeSent() 
			throws MessagingException, IOException, EntityDoesNotExistException {
		Emails emailManager = new Emails();
		List<MimeMessage> preparedEmails = null;
		
		EvaluationAttributes evalObject = EvaluationsLogic.inst()
				.getEvaluation(courseId, evaluationName);
		
		if(evalObject != null) {
			 /*
			  * Check if evaluation was deleted between scheduling
			  * and the actual sending of emails
			  */
			preparedEmails = emailManager.generateEvaluationClosingEmailsForEval(evalObject);
		}
		
		return preparedEmails;
	}
	
	private void initializeNameAndDescription() {
		actionName = "evaluationClosingMailAction";
		actionDescription = "send closing reminders";
	}
}