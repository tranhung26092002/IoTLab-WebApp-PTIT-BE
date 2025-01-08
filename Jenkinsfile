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
                    echo 'Checking for existing containers with the same name...'
                    sh '''
                        # Kiểm tra container đang chạy
                        if docker ps -q -f name=$CONTAINER_NAME | grep -q .; then
                            echo "Stopping and removing existing container: $CONTAINER_NAME"
                            docker stop $CONTAINER_NAME
                            docker rm $CONTAINER_NAME
                        fi
                        # Kiểm tra container đã dừng nhưng chưa xóa
                        if docker ps -aq -f name=$CONTAINER_NAME | grep -q .; then
                            echo "Removing stopped container: $CONTAINER_NAME"
                            docker rm $CONTAINER_NAME
                        fi
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
                    docker-compose down || true
                    docker container prune -f || true
                    docker-compose up -d --build
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
