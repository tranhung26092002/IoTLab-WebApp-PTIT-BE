pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Build Maven Project') {
            steps {
                script {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Clean Old Containers') {
            steps {
                script {
                    bat '''
                        FOR /f "tokens=*" %%i IN ('docker ps -aq -f name=%CONTAINER_NAME%') DO (
                            echo Stopping and removing old container...
                            docker stop %%i 2>NUL || exit /b 0
                            docker rm %%i 2>NUL || exit /b 0
                        )
                    '''
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
                    bat 'docker-compose down || exit /b 0'
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
    }
}