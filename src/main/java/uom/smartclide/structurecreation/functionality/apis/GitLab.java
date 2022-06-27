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

import java.util.Iterator;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.gitlab4j.api.models.Visibility;

public class GitLab {

	private String gitLabServerURL;
	private GitLabApi gitLabApi;

	public GitLab(String userToken, String gitLabServerURL) {
		super();
		this.gitLabServerURL = gitLabServerURL;
		gitLabApi = new GitLabApi(gitLabServerURL, userToken);
	}

	// TODO improve isProjectNameAvailable()
	public boolean isProjectNameAvailable(String projectName) throws GitLabApiException {

		ProjectApi prjApi = gitLabApi.getProjectApi();
		User usr = gitLabApi.getUserApi().getCurrentUser();
		try {
			Project prj = prjApi.getProject(usr.getUsername() + "/" + projectName, false);
			if (prj == null) {
				return true;
			}
			return false;
		} catch (GitLabApiException e) {
			if (e.getMessage().equals("404 Project Not Found")) {
				return true;
			}
			System.out.println(e.getMessage());
			return false;
		}
	}

	/**
	 * Creates a GitLab repository using Jenkins CI/CD
	 * 
	 * @param projectName The name for the GitLab repository.
	 * 
	 * @return true or false, depending on the repository's creation result
	 * 
	 */
	public boolean createProjectWithJenkinsCI(String projectName, String projVisibility, String projDescription)
			throws GitLabApiException {
		if (createProject(projectName, projVisibility, projDescription)) {
			return addJenkinsUserAsProjectDev(projectName);
		}
		return false;
	}

	/**
	 * Creates a GitLab repository using GitLab CI/CD
	 * 
	 * @param projectName The name for the GitLab repository.
	 * 
	 * @return true or false, depending on the repository's creation result
	 * 
	 */
	public boolean createProjectWithGitlabCI(String projectName, String projVisibility, String projDescription)
			throws GitLabApiException {

		return createProject(projectName, projVisibility, projDescription);

	}

	/**
	 * Creates a GitLab repository
	 * 
	 * @param projectName The name for the GitLab repository.
	 * 
	 * @return true or false, depending on the repository's creation result
	 * 
	 */
	private boolean createProject(String projectName, String projVisibility, String projDescription)
			throws GitLabApiException {

		if (gitLabApi == null) {
			throw new GitLabApiException("GitLab API not initialized");
		}

		if (!this.isProjectNameAvailable(projectName)) {
			throw new GitLabApiException("Project name is already taken");
		}

		// Creating a GitLab repository <
		ProjectApi prjApi = gitLabApi.getProjectApi();
		Project prj = prjApi.createProject(getConfigForNewProject(projectName, projVisibility, projDescription));
		if (prj == null) {
			// the project was not created
			return false;
		}
		// >

		return true;
	}

	// Adding Gitlab "jenkins" user as a developer to the project, for use in the CI
	// pipeline
	private boolean addJenkinsUserAsProjectDev(String projectName) throws GitLabApiException {

		// checking if the is a "jenkins" user at our GtiLab server
		User usrJenkins = gitLabApi.getUserApi().getUser("jenkins");
		if (usrJenkins == null) {
			// there isnt a "jenkins" user at our GtiLab server
			throw new IllegalArgumentException("User \"jenkins\" does not exist in the GitLab server");
		}

		ProjectApi prjApi = gitLabApi.getProjectApi();
		User usr = gitLabApi.getUserApi().getCurrentUser();
		Project prj = prjApi.getProject(usr.getUsername() + "/" + projectName, false);
		if (prj == null) {
			throw new IllegalArgumentException("Project \"" + projectName + "\" does not exist");
		}
		// Adding "jenkins" user as developer to the project <
		Integer jenkinsUserID = usr.getId();
		try {
			prjApi.addMember(prj.getId(), jenkinsUserID, 30);
		} catch (GitLabApiException e) {
			e.printStackTrace();
			deleteProject(projectName);
			return false;
		}

		return true;
	}

	/**
	 * Deletes a GitLab repository
	 * 
	 * @param projectName The name for the GitLab repository.
	 * 
	 */
	public void deleteProject(String prjName) throws GitLabApiException {
		ProjectApi prjApi = gitLabApi.getProjectApi();
		Project prj = getProjectFromName(prjName);
		prjApi.deleteProject(prj.getId());
	}

	/**
	 * Adds a webhook to the GitLab repository
	 * 
	 * @param project        The project that the webhook will be added.
	 * @param jenkinsHookURL The Jenkins webhook URL.
	 * 
	 */
	public void addHookToProject(Project project, String jenkinsHookURL) throws GitLabApiException {
		ProjectApi prjApi = gitLabApi.getProjectApi();
		prjApi.addHook(project.getId(), jenkinsHookURL, true, true, true);
	}

	public void close() {
		gitLabApi.close();
	}

	// TODO more dynamic
	private Project getConfigForNewProject(String prjName, String projVisibility, String projDescription) {
		Project projectSpec;
		
		int projectVisib = Integer.valueOf(projVisibility).intValue();
		
		Visibility visibility;
		switch (projectVisib) {
		case 0:
			visibility = Visibility.PUBLIC;
			break;
		case 1:
			visibility = Visibility.INTERNAL;
			break;
		case 2:
			visibility = Visibility.PRIVATE;
			break;
		default:
			visibility = Visibility.PRIVATE;
			break;
		}
		projectSpec = new Project().withName(prjName).withVisibility(visibility)
				.withDescription(projDescription).withIssuesEnabled(true)
				.withMergeRequestsEnabled(true).withWikiEnabled(true).withSnippetsEnabled(true);

		return projectSpec;
	}

	/**
	 * Returns a project Object, depending on the project's name and the user that
	 * requested it.
	 * 
	 * @param projectName The project's name.
	 * 
	 * @return project
	 * 
	 */
	public Project getProjectFromName(String projectName) throws GitLabApiException {
		ProjectApi prjApi = gitLabApi.getProjectApi();
		User usr = gitLabApi.getUserApi().getCurrentUser();

		return prjApi.getProject(usr.getUsername() + "/" + projectName, false);
	}

	/**
	 * Returns a project's HTTP URL, depending on the project's name and the user
	 * that requested it.
	 * 
	 * @param projectName The project's name.
	 * 
	 * @return The project's HTTP URL
	 * 
	 */
	public String getProjectUrl(String projectName) throws GitLabApiException {
		ProjectApi prjApi = gitLabApi.getProjectApi();
		User usr = gitLabApi.getUserApi().getCurrentUser();

		Project prj = prjApi.getProject(usr.getUsername() + "/" + projectName, false);
		if (prj == null) {
			return null;
		}

		return prj.getHttpUrlToRepo();
	}

}
