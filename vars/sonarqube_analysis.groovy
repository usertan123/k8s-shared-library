def call(String SonarQubeAPI, String Projectname, String ProjecKey){
  withSonarQubeEnv("${SonarQubeAPI}") {
    sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=${Projectname} -Dsonar.projectKey=${ProjecKey} -X"
  }
}