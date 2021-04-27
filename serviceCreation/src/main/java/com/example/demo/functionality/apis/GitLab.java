package com.example.demo.functionality.apis;

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
	 * Creates a GitLab repository
	 * 
	 * @param projectName The name for the GitLab repository.
	 * 
	 * @return true or false, depending on the repository's creation result
	 * 
	 */
	public boolean createProject(String projectName) throws GitLabApiException {

		if (gitLabApi == null) {
			throw new GitLabApiException("GitLab API not initialized");
		}

		// checking if the is a "jenkins" user at our GtiLab server
		User usr = gitLabApi.getUserApi().getUser("jenkins");
		if (usr == null) {
			// there isnt a "jenkins" user at our GtiLab server
			throw new IllegalArgumentException("User \"jenkins\" does not exist in the GitLab server");
		}

		if (!this.isProjectNameAvailable(projectName)) {
			throw new GitLabApiException("Project name is already taken");
		}

		// Creating a GitLab repository <
		ProjectApi prjApi = gitLabApi.getProjectApi();
		Project prj = prjApi.createProject(getConfigForNewProject(projectName));
		if (prj == null) {
			// the project was not created
			return false;
		}
		// >

		// Adding "jenkins" user as developer to the project <
		Integer jenkinsUserID = usr.getId();
		try {
			prjApi.addMember(prj.getId(), jenkinsUserID, 30);
		} catch (GitLabApiException e) {
			e.printStackTrace();
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
     * @param project The project that the webhook will be added.
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
	private Project getConfigForNewProject(String prjName) {
		Project projectSpec = new Project().withName(prjName).withVisibility(Visibility.PUBLIC)
				.withDescription("My project for API demonstration.").withIssuesEnabled(true)
				.withMergeRequestsEnabled(true).withWikiEnabled(true).withSnippetsEnabled(true).withPublic(true);

		return projectSpec;
	}

	/**
     * Returns a project Object, depending on the project's name and the user that requested it.
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
     * Returns a project's HTTP URL, depending on the project's name and the user that requested it.
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
