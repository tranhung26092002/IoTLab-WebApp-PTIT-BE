pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Checking for existing containers...'
                    sh '''
                        if docker ps -q -f name=$CONTAINER_NAME | grep -q .; then
                            echo "Stopping containers..."
                            docker-compose down
                        else
                            echo "No containers found"
                        fi
                    '''
                }
            }
        }

        stage('Fix Permissions') {
            steps {
                script {
                    echo 'Fixing permissions for mvnw...'
                    sh 'chmod +x ./mvnw'  // Chắc chắn cấp quyền cho tệp mvnw
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    echo 'Building application...'
                    sh './mvnw clean package -DskipTests'  // Build jar file
                }
            }
        }

        stage('List Files in Target Directory') {
            steps {
                script {
                    echo 'Listing files in target directory...'
                    sh 'ls -l target/'  // Kiểm tra các file trong thư mục target
                }
            }
        }

        stage('Verify Jar Existence') {
            steps {
                script {
                    echo 'Verifying if .jar file exists...'
                    sh '''
                    if [ ! -f target/eureka-service.jar ]; then
                        echo "No eureka-service.jar found in target directory! Exiting."
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
                    // Build và deploy docker
                    sh '''
                    docker-compose down
                    docker container prune -f  # Xóa các container đã dừng
                    docker-compose up -d --build
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up Docker system and workspace...'
            sh 'docker system prune -f'  // Dọn dẹp Docker
            cleanWs()  // Dọn dẹp workspace
        }
        success {
            echo 'Pipeline completed successfully!'  // Thành công
        }
        failure {
            echo 'Pipeline failed!'  // Thất bại
        }
    }
}
