# smartclide-service-creation   
  
A Spring service for the automatic configuration of a **GitLab** repository and a **Jenkins** pipeline   
The service uses the default Spring port 8080.   
  
The service includes two endpoints:    
**Endpoint 1:** "/createStructure" -> creates a GitLab repository.    
		**Request parameters:**    
				**projectName** -> String    
				**projVisibility** -> String    
				**projDescription** -> String    
				**gitLabServerURL** -> String    
				**gitlabToken** -> String    
   
   
**Endpoint 2:** "/createStuctureJenkins" -> creates a GitLab repository and a Jenkins pipeline and finaly configures and pairs them.    
		**Request parameters:**    
				**projectName** -> String    
				**projVisibility** -> String    
				**projDescription** -> String    
				**gitLabServerURL** -> String    
				**gitlabToken** -> String    
				**jenkinsServerUrl** -> String    
				**jenkinsUsername** -> String    
				**jenkinsToken** -> String    
