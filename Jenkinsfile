pipeline {
  agent { label "master" }
  stages {
    stage("Build") {
      steps {
        sh """
          docker-compose up -d --build
        """
      }
    }
    stage("Remove Old Container") {
      steps {
        sh "echo 'y' |docker container prune"
      }
    }
    stage("Remove Old Image") {
      steps {
        sh "echo 'y' |docker container prune"
      }
    }
  }
}
