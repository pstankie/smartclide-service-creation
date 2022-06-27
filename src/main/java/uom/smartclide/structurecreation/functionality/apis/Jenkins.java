/*******************************************************************************
 * Copyright (C) 2021-2022 University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package uom.smartclide.structurecreation.functionality.apis;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.HttpResponseException;
import org.dom4j.DocumentException;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;

import uom.smartclide.structurecreation.functionality.utils.PipelineConfigXML;

public class Jenkins {
	
    /**
     * Create a Jenkins pipeline on the server using the required fields
     * 
     * @param projectUrl The http url of the GitLab repository.
     * @param serverUrl The URL of the Jenkins server.
     * @param username The username of the Jenkins user that is going to create the Jenkins pipeline.
     * @param token The access token of the user that will create the Jenkins pipeline
     * 
     * @return the name of the new Jenkins pipeline. If the pipeline name is taken, returns null
     * 
     * @throws DocumentException & NoSuchFieldException in case of an error @PipelineConfigXML.
     * @throws URISyntaxException & IOException in case of an error @JenkinsServer.
     * 
     */
	public static String createPipeline(String projectUrl, String serverUrl, String username, String token) throws DocumentException, NoSuchFieldException, URISyntaxException, IOException {

		String[] temp = projectUrl.split("/");
		String jobName = temp[temp.length-2]+"."+temp[temp.length-1];
		jobName = replaceLast(jobName, ".git", "");
		
		JenkinsServer jenkins = new JenkinsServer(new URI(serverUrl), username, token);
		
		//checking if the Pipeline name is already taken
		Map<String, Job> map = jenkins.getJobs();
		if(map.get(jobName)!=null) {
			//pipeline name is already taken
			return null;
		}

		PipelineConfigXML jobConfigXML = new PipelineConfigXML();
		String field1 = replaceLast(projectUrl, ".git", "");

		jobConfigXML.changeNode(
				"//flow-definition/properties/com.coravy.hudson.plugins.github.GithubProjectProperty/projectUrl",
				field1 + "/-/blob/master/Jenkinsfile/");

		jobConfigXML.changeNode(
				"//flow-definition/definition/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/url",
				projectUrl);

		String xmlToString = jobConfigXML.getNewDoc();

		try {
			jenkins.createJob(jobName, xmlToString);
			jenkins.close();
			return jobName;
		}catch (HttpResponseException exc) {
			exc.printStackTrace();
			return null;
		}
	}

	private static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

}
