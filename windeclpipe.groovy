pipeline {
	agent any
	stages {
		stage("Test") {
			steps {
							
				bat 'mvn -Dmaven.test.failure.ignore=true surefire-report:report'
				
				//bat  'cd /var/lib/jenkins/workspace/junitreportgeneration/target/surefire-reports'
				//bat  'touch *.xml'
			}
		}
		stage("build") {
			steps {
				//slackSend channel: 'junittesting', message: 'Build Phase running'
				 bat  'mvn site -DgenerateReports=false'
				 //bat  'copy $(pwd)/target/site/surefire-report.html $(pwd)/surefire-report.html'
				 //bat  'sudo chmod 777 $(pwd)/surefire-report.html'
				 //bat  'ls -l $(pwd)/surefire-report.html'
			}
		}
		stage("Slack notification") {
			steps {
				
				bat 'echo "do nothing"'
			}

		}

	}
	post {
		success {
			archiveArtifacts "target/**/*"
			junit '**/surefire-reports/*.xml'
			
		 }
		 failure {
			echo "Failed"
		 }
		}
	}