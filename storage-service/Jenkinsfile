pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'storage-service:latest'
        CONTAINER_NAME = 'storage-service'
    }

    stages {
        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Stopping and removing old containers...'
                    sh '''
                        docker-compose down || true
                        docker container prune -f || true
                    '''
                }
            }
        }

        stage('Fix Permissions') {
            steps {
                script {
                    echo 'Fixing permissions for mvnw...'
                    sh 'chmod +x ./mvnw'
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
                    echo 'Checking if storage-service.jar exists...'
                    sh '''
                    if [ ! -f target/storage-service.jar ]; then
                        echo "storage-service.jar not found! Exiting..."
                        exit 1
                    fi
                    '''
                }
            }
        }

        stage('Build and Deploy with Docker') {
            steps {
                script {
                    echo 'Building and deploying Docker containers...'
                    sh '''
                    docker-compose down || true
                    docker-compose up -d --build
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace and Docker resources...'
            sh 'docker system prune -f'
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
