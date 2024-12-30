pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'api-gateway:latest'
        CONTAINER_NAME = 'api-gateway'
    }

    stages {
        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Checking for existing containers...'
                    sh '''
                        docker-compose down || echo "No running containers to stop"
                    '''
                }
            }
        }

        stage('Fix Permissions') {
            steps {
                script {
                    echo 'Fixing permissions for mvnw...'
                    sh '''
                    if [ -f ./mvnw ]; then chmod +x ./mvnw; fi
                    '''
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    echo 'Building application...'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Verify Jar Existence') {
            steps {
                script {
                    echo 'Verifying if .jar file exists...'
                    sh '''
                    if [ ! -f target/api-gateway.jar ]; then
                        echo "No api-gateway.jar found in target directory! Exiting."
                        exit 1
                    fi
                    echo ".jar file found!"
                    '''
                }
            }
        }

        stage('Build and Deploy with Docker') {
            steps {
                script {
                    echo 'Deploying application with Docker Compose...'
                    sh '''
                    docker-compose -f docker-compose.yml down
                    docker container prune -f
                    docker-compose -f docker-compose.yml up -d --build
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up Docker system and workspace...'
            sh 'docker system prune -f'
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
