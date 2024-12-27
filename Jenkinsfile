pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Maven Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Checking for existing containers...'
                    bat '''
                        docker ps -q -f name=%CONTAINER_NAME% && (
                            echo Stopping containers...
                            docker-compose down
                        ) || echo No containers found
                    '''
                }
            }
        }

        stage('Build and Deploy') {
            steps {
                script {
                    bat 'docker-compose build'
                    bat 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            bat 'docker system prune -f'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}