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
  post{
    always{
      sh """curl 'https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage?chat_id=${CHAT_ID}&text=[${env.ENVIRONMENT}] ${env.JOB_NAME} – Build number ${env.BUILD_NUMBER} – ${currentBuild.currentResult}!'"""
    }
  }
  
}
