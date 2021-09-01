package uom.smartclide.structurecreation.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import uom.smartclide.structurecreation.functionality.mainFlow.MainFlow;
import uom.smartclide.structurecreation.functionality.utils.ResultObject;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class EndpointController {

	@GetMapping("/getRepoURL")
	public String getRepoURL() {
		return "Hello, placeholder for Git repo URL";
	}

	@PostMapping("/createStuctureJenkins")
	public ResultObject createStuctureJenkins(@RequestHeader String projectName, @RequestHeader String projVisibility,
			@RequestHeader String projDescription, @RequestHeader String gitLabServerURL,
			@RequestHeader String gitlabToken, @RequestHeader String jenkinsServerUrl,
			@RequestHeader String jenkinsUsername, @RequestHeader String jenkinsToken) {

		if (isNullOrBlank(projectName) || isNullOrBlank(gitLabServerURL) || isNullOrBlank(gitlabToken)
				|| isNullOrBlank(jenkinsServerUrl) || isNullOrBlank(jenkinsUsername) || isNullOrBlank(jenkinsToken)) {
			return new ResultObject(1, "One or more fields are empty or blank");
		}
		ResultObject ret = new ResultObject(1, "Creation Failed");
		ret = MainFlow.createStructureJenkins(projectName, projVisibility, projDescription, gitLabServerURL, gitlabToken,
				jenkinsServerUrl, jenkinsUsername, jenkinsToken);

		return ret;
	}

	@PostMapping("/createStucture")
	public ResultObject createStructure(@RequestHeader String projectName, @RequestHeader String projVisibility,
			@RequestHeader String projDescription, @RequestHeader String gitLabServerURL,
			@RequestHeader String gitlabToken) {

		if (isNullOrBlank(projectName) || isNullOrBlank(gitLabServerURL) || isNullOrBlank(gitlabToken)) {
			return new ResultObject(1, "One or more fields are empty or blank");
		}
		ResultObject ret = new ResultObject(1, "Creation Failed");
		ret = MainFlow.createStructure(projectName, projVisibility, projDescription, gitLabServerURL, gitlabToken);

		return ret;

	}

	private boolean isNullOrBlank(String str) {
		return (str.isBlank() || str == null);
	}
}
