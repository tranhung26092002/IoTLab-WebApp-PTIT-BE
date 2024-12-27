pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Maven Build') {
            steps {
                script {
                    // Build project using Maven
                    sh 'mvn clean package -DskipTests'
                    // Kiểm tra file .jar đã được tạo ra trong thư mục target
                    sh 'ls target/*.jar'
                }
            }
        }

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
                    // Kiểm tra xem file .jar có tồn tại hay không trước khi chạy Docker build
                    sh 'ls target/*.jar'
                    sh 'docker-compose build'
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
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
