package com.junitPoc.hello_junit;

import javax.annotation.PostConstruct;

 

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.MessageCorrelationResultWithVariables;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import com.junitPoc.hello_junit.*;
 

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

 


/**
 * Test case starting an in-memory database-backed Process Engine.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessUnitTest {

 

  @Autowired
  private ProcessEngine processEngine;

 

  static {
    LogFactory.useSlf4jLogging(); // MyBatis
  }

 

  @ClassRule
  @Rule
  public static ProcessEngineRule rule;

 

  @PostConstruct
  void initRule() {
    rule = TestCoverageProcessEngineRuleBuilder.create(processEngine).build();
  }

 

  @Before
  public void setup() {
    init(processEngine);
  }

 
  @Test
  public void DefaultPath() {	
	 
	 InputMock info = new InputMock();
	 info.setImplementationType("Default Path");
	 Map<String,Object> variables = new HashMap<>();
	 variables.put("info", info);
	 
	 ProcessInstance processInstance = processEngine().getRuntimeService().startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY, variables);	
	 
	 	 assertThat(processInstance).hasNotPassed("MockScriptTask").isEnded(); 	 
	 
	 

  }

  
  @Test
  public void AlternativePath() {	
	 RuntimeService runtimeService = rule.getRuntimeService();
	 TaskService taskService = rule.getTaskService();
	 
	 InputMock info = new InputMock();
	 info.setImplementationType("Alternative Path");
	 info.setImplementationValue("mockActivity");
	 boolean msgListenerSet = true;
  	 Map<String,Object> variables = new HashMap<>();
	 variables.put("info", info);
	 variables.put("msgListenerSet", msgListenerSet);
	 
	 
	 ProcessInstance processInstance = processEngine().getRuntimeService()
			 .startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY, ProcessConstants.PROCESS_BUSINESS_KEY , variables);
	
			 
	 assertThat(processInstance).isStarted().isWaitingAt("CompleteReceiveTaskTask");
	 
	
//	 //1st Approach
//	 runtimeService.createMessageCorrelation("completeTask")
//	 				.processInstanceBusinessKey(ProcessConstants.PROCESS_BUSINESS_KEY)
//	 				.correlate();
//	 
//
//	 //2nd Approach
//	 EventSubscription subscription = runtimeService.createEventSubscriptionQuery()
//			  .processInstanceId(processInstance.getId()).eventType("message").singleResult();
//	 runtimeService.messageEventReceived("completeTask", subscription.getExecutionId());
//	
//	 //3rd Approach
//	 runtimeService.createExecutionQuery()
//			  .messageEventSubscriptionName("completeTask")
//			  .singleResult();
//	 
//	 //4th Approach	 
//	 org.camunda.bpm.engine.MismatchingMessageCorrelationException: Cannot correlate message 'completeTask': No process definition or execution matches the parameters
//	 MessageCorrelationResult result = runtimeService
//			  .createMessageCorrelation("completeTask")
//			  .processInstanceBusinessKey(ProcessConstants.PROCESS_DEFINITION_KEY)
//			  .correlateWithResult();
//			 
	
 	//Finally asserting the end
 	assertThat(processInstance).isStarted().isNotWaitingAt("CompleteReceiveTaskTask");
	assertThat(processInstance).isEnded();

	 
  }
   
  
 

}
 