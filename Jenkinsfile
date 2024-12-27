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
                    // Cấp quyền cho các tệp mvnw
                    echo 'Fixing permissions for mvnw...'
                    sh 'chmod +x ./mvnw'
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    // Build jar file
                    echo 'Building application...'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Verify Jar Existence') {
            steps {
                script {
                    // Kiểm tra sự tồn tại của eureka-service.jar trong thư mục target
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
                    // Xác nhận tên image và container trong Docker Compose
                    echo 'Deploying application with Docker Compose...'
                    sh '''
                    docker-compose down
                    docker container prune -f   # Xóa các container đã dừng
                    docker-compose up -d --build
                    '''
                }
            }
        }
    }

    post {
        always {
            // Dọn dẹp các tài nguyên không cần thiết như container đã dừng và file workspace
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
