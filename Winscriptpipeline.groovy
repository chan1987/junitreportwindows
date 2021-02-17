class Example {
	static def pipelinescript () {
	node('master') {
		stage("Test") {
		 try {
		  slackSend (color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
		  sh 'mvn surefire:test surefire-report:report'
		  //sh  'cd /var/lib/jenkins/workspace/junitreportgeneration/target/surefire-reports'
		  //sh 'touch *.xml'
		 }
		 catch (exc) {
		  echo 'something failed'
		  throw exc
		 }
		}  
		stage("build") {
		 try {
		  //slackSend channel: 'junittesting', message: 'Build Phase running'
		   sh  'mvn -Dmaven.test.skip=true surefire-report:report'
		   sh  'sudo cp /var/lib/jenkins/workspace/junitreportgeneration/target/site/surefire-report.html /var/lib/jenkins/workspace/junitreportgeneration/surefire-report.html'
		   sh  'sudo chmod 777 /var/lib/jenkins/workspace/junitreportgeneration/surefire-report.html'
		   sh  'ls -l /var/lib/jenkins/workspace/junitreportgeneration/surefire-report.html'
		}
		catch (exc) {
		 echo 'build failed'
		 throw  exc
		}
		
		}
	  
		stage("Slack notification") {
		 
		 try {
			 
		   //slackSend baseUrl: 'https://hooks.slack.com/services/', channel: '#junittesting', color: 'good', message: 'Jenkins Build Completed', teamDomain: 'a1devopsconsulting', tokenCredentialId: 'slackunit', username: 'Chandrakanth'
			 lastSuccess {
				
				  archiveArtifacts "target/**/*"
				 junit '**/surefire-reports/*.xml'
				 slackSend (color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
				 slackUploadFile channel: '#junittesting', credentialId: 'slackunit', filePath: '*.html', initialComment: 'Test Reports'
		   }
		   
		   LastFailure {
			   
			   slackSend (color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
			   emailext (
				   subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
				   body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
				  <p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
			  	  recipientProviders: [[$class: 'DevelopersRecipientProvider']]
			   )
		   }
		  
		 }
		  catch (exc) {
		   echo 'notification error'
		   throw exc
		   
		  }
		  
		}
	  }
	  return this;
	}
}
