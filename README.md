# smartclide-service-creation <br/>
<br/>
A Spring service for the automatic configuration of a **GitLab** repository and a **Jenkins** pipeline <br/>
The service uses the default Spring port 8080. <br/>
<br/>
The service includes two endpoints: <br/>
**Endpoint 1:** "/createStructure" -> creates a GitLab repository. <br/>
		**Request parameters:** <br/>
				**projectName** -> String <br/>
				**projVisibility** -> String <br/>
				**projDescription** -> String <br/>
				**gitLabServerURL** -> String <br/>
				**gitlabToken** -> String <br/>
<br/>
<br/>
**Endpoint 2:** "/createStuctureJenkins" -> creates a GitLab repository and a Jenkins pipeline and finaly configures and pairs them. <br/>
		**Request parameters:** <br/>
				**projectName** -> String <br/>
				**projVisibility** -> String <br/>
				**projDescription** -> String <br/>
				**gitLabServerURL** -> String <br/>
				**gitlabToken** -> String <br/>
				**jenkinsServerUrl** -> String <br/>
				**jenkinsUsername** -> String <br/>
				**jenkinsToken** -> String <br/>
