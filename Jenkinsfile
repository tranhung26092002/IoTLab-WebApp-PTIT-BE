pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Clean Old Containers') {
            steps {
                script {
                    bat """
                        docker ps -aq -f name=${CONTAINER_NAME} && (
                            echo Stopping and removing old container...
                            docker stop ${CONTAINER_NAME}
                            docker rm ${CONTAINER_NAME}
                        )
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    bat 'docker build -t %DOCKER_IMAGE% .'
                }
            }
        }

        stage('Deploy New Container') {
            steps {
                script {
                    bat 'docker-compose down'
                    bat 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}