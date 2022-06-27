/*******************************************************************************
 * Copyright (C) 2021-2022 University of Macedonia
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package uom.smartclide.structurecreation.functionality.mainFlow;

import java.io.IOException;
import java.net.URISyntaxException;

import org.dom4j.DocumentException;
import org.gitlab4j.api.GitLabApiException;

import uom.smartclide.structurecreation.functionality.apis.GitLab;
import uom.smartclide.structurecreation.functionality.apis.Jenkins;
import uom.smartclide.structurecreation.functionality.utils.ResultObject;

public class MainFlow {
		
    /**
     * Create a GitLab repository and a Jenkins pipeline, and configure a webhook between them
     * 
     * @param projectName The name for the GitLab repository.
     * @param gitURL The URL of the GitLab server.
     * @param gitToken The access token of the GitLab user that will create the GitLab repository.
     * @param jekninsURL The URL of the Jenkins server.
     * @param jenkinsUsername The username of the Jenkins user that is going to create the Jenkins pipeline.
     * @param jenkinsToken The access token of the Jenkins user that will create the Jenkins pipeline
     * 
     * @return a ResultObject with a status and a message, depending on the result
     * 
     */
	public static ResultObject createStructureJenkins(String projectName, String projVisibility, String projDescription, String gitURL, String gitToken, String jekninsURL, String jenkinsUsername, String jenkinsToken) {
		
		GitLab gitApi = new GitLab(gitToken, gitURL);
		boolean result = true;
		try {
			if(gitApi.isProjectNameAvailable(projectName)) {
				try {
					result = gitApi.createProjectWithJenkinsCI(projectName, projVisibility, projDescription);
				} catch (GitLabApiException e) {
					e.printStackTrace();
					return new ResultObject(1, "Error during GitLab repository creation");
				}
			}else {
				return new ResultObject(1, "A GitLab repository with the name \'"+projectName+"\' already exists");
			}
		} catch (GitLabApiException e) {
			e.printStackTrace();
			return new ResultObject(1, "Problem with GitLab api");
		}
		
		if(!result) return new ResultObject(1, "Error during GitLab repository creation");
		
		String projectHttpURL;
		try {
			projectHttpURL = gitApi.getProjectUrl(projectName);
		} catch (GitLabApiException e2) {
			e2.printStackTrace();
			return deleteRepository(gitApi, projectName, "Failed to find the URL of the GitLab repository");
		}
		
		
		String pipelineName = null;
		try {
			pipelineName = Jenkins.createPipeline(projectHttpURL, jekninsURL, jenkinsUsername, jenkinsToken);
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
			return deleteRepository(gitApi, projectName, "There was an error with the pipeline configuration template");
		} catch (DocumentException e1) {
			e1.printStackTrace();
			return deleteRepository(gitApi, projectName, "The pipeline configuration template cannot be found");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return deleteRepository(gitApi, projectName, "Error converting Jenkins Server URL to URI");
		} catch (IOException e1) {
			e1.printStackTrace();
			return deleteRepository(gitApi, projectName, "Error while retrieving jobs from the Jenkins Server");
		}
		
		if(pipelineName==null) {
			//failed to create a pipeline, so the GitLab repository must be deleted
			return deleteRepository(gitApi, projectName, "Failed to create a Jenkins pipeline");
		}
		
		String jenkinsHookUrl = jekninsURL+"/project/"+pipelineName;
		try {
			gitApi.addHookToProject(gitApi.getProjectFromName(projectName), jenkinsHookUrl);
		} catch (GitLabApiException e) {
			//failed to add webhook to GitLab repository so it is deleted
			e.printStackTrace();
			return deleteRepository(gitApi, projectName, "Failed to add webhook to GitLab repository");
		}
		
		String gitRepoURL;
		try {
			gitRepoURL = gitApi.getProjectUrl(projectName);
		} catch (GitLabApiException e) {
			e.printStackTrace();
			return deleteRepository(gitApi, projectName, "Failed to find the URL of the GitLab repository");
		}
		
		gitApi.close();
		
		return new ResultObject(0, gitRepoURL);
	}
	
	public static ResultObject createStructure(String projectName, String projVisibility, String projDescription, String gitURL, String gitToken) {
		
		GitLab gitApi = new GitLab(gitToken, gitURL);
		boolean result = true;
		try {
			if(gitApi.isProjectNameAvailable(projectName)) {
				try {
					result = gitApi.createProjectWithGitlabCI(projectName, projVisibility, projDescription);
				} catch (GitLabApiException e) {
					e.printStackTrace();
					return new ResultObject(1, "Error during GitLab repository creation");
				}
			}else {
				return new ResultObject(1, "A GitLab repository with the name \'"+projectName+"\' already exists");
			}
		} catch (GitLabApiException e) {
			e.printStackTrace();
			return new ResultObject(1, "Problem with GitLab api");
		}
		
		if(!result) return new ResultObject(1, "Error during GitLab repository creation");
		
		String projectHttpURL;
		try {
			projectHttpURL = gitApi.getProjectUrl(projectName);
		} catch (GitLabApiException e2) {
			e2.printStackTrace();
			return deleteRepository(gitApi, projectName, "Failed to find the URL of the GitLab repository");
		}
		
		String gitRepoURL;
		try {
			gitRepoURL = gitApi.getProjectUrl(projectName);
		} catch (GitLabApiException e) {
			e.printStackTrace();
			return deleteRepository(gitApi, projectName, "Failed to find the URL of the GitLab repository");
		}
		
		gitApi.close();
		
		return new ResultObject(0, gitRepoURL);
	}
	
	private static ResultObject deleteRepository(GitLab gitApi, String projectName, String successMessage) {
		try {
			gitApi.deleteProject(projectName);
		} catch (GitLabApiException e) {
			e.printStackTrace();
			return new ResultObject(1, "Service Creation had an error and GitLab repository deletion failed");
		}
		return new ResultObject(1, successMessage);
	}
	
}
