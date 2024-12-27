pipeline {
    agent any

    environment {
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

        stage('Build and Deploy') {
            steps {
                script {
                    // Chạy lại Docker Compose để tạo container mới từ image đã build
                    echo "Building and deploying the container..."
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'  // Dọn dẹp các container, image không sử dụng
            cleanWs()  // Dọn dẹp workspace Jenkins
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
