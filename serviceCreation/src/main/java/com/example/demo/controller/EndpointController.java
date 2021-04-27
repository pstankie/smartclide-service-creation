package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.functionality.tempName.MainFlow;
import com.example.demo.functionality.tempName.ResultObject;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class EndpointController {

	@GetMapping("/getRepoURL")
	public String getRepoURL() {
		return "Hello, placeholder for Git repo URL";
	}

	@PostMapping("/createStucture")
	public ResultObject createStructure(@RequestHeader String projectName, @RequestHeader String gitLabServerURL,
			@RequestHeader String gitlabToken, @RequestHeader String jenkinsServerUrl,
			@RequestHeader String jenkinsUsername, @RequestHeader String jenkinsToken) {

		if (isNullOrBlank(projectName) || isNullOrBlank(gitLabServerURL) || isNullOrBlank(gitlabToken)
				|| isNullOrBlank(jenkinsServerUrl) || isNullOrBlank(jenkinsUsername) || isNullOrBlank(jenkinsToken)) {
			return new ResultObject(1, "One or more fields are empty or blank");
		}
		ResultObject ret = new ResultObject(1, "Creation Failed");
		ret = MainFlow.createStructure(projectName, gitLabServerURL, gitlabToken, jenkinsServerUrl, jenkinsUsername,
				jenkinsToken);

		return ret;

	}

	private boolean isNullOrBlank(String str) {
		return (str.isBlank() || str == null);
	}
}
